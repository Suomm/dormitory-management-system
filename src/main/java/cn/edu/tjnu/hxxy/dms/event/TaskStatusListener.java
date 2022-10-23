/*
 * Copyright (C) 2020-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.edu.tjnu.hxxy.dms.event;

import cn.edu.tjnu.hxxy.dms.constant.RabbitConst;
import cn.edu.tjnu.hxxy.dms.entity.Dormitory;
import cn.edu.tjnu.hxxy.dms.entity.Note;
import cn.edu.tjnu.hxxy.dms.util.DateUtil;
import cn.edu.tjnu.hxxy.dms.util.ExcelUtil;
import cn.edu.tjnu.hxxy.dms.util.RedisUtil;
import cn.edu.tjnu.hxxy.dms.util.ZipUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.edu.tjnu.hxxy.dms.constant.RedisConst.*;
import static cn.edu.tjnu.hxxy.dms.constant.WebConst.WEB_PORTFOLIO_ASSETS;
import static cn.edu.tjnu.hxxy.dms.constant.WebConst.WEB_PORTFOLIO_STORES;

/**
 * 任务状态监听器。监听消息队列中的任务消息，接受任务完成的消息，并且整理资源。
 *
 * @author 王帅
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskStatusListener {

    // 初始化常量 使用 String#concat 方法代替 “+” 只是为了效率高一些

    /**
     * 本周任务的临时资源文件夹中的 ”脏乱宿舍“ 文件夹。
     */
    private static final String DIRTY_DIR = WEB_PORTFOLIO_ASSETS.concat("脏乱宿舍");

    /**
     * 本周任务的临时资源文件夹中的 ”优秀宿舍“ 文件夹。
     */
    private static final String CLEAN_DIR = WEB_PORTFOLIO_ASSETS.concat("优秀宿舍");

    /**
     * 生成并保存的新闻稿图片名称。
     */
    private static final String IMAGE_FILE = WEB_PORTFOLIO_STORES.concat("{0,number,#}/新闻稿图片.png");

    /**
     * 生成并保存的宿舍卫生检查表（本科生或研究生）。
     */
    private static final String NOTES_FILE = WEB_PORTFOLIO_STORES.concat("{0,number,#}/化学学院宿舍卫生检查表{1}{2}.xlsx");

    /**
     * 生成并保存的化学学院宿舍卫生检查压缩文件。
     */
    private static final String CHART_ZIP = WEB_PORTFOLIO_STORES.concat("{0,number,#}/{1}化学学院宿舍卫生检查.zip");

    /**
     * 生成并保存的优差宿舍照片压缩文件。
     */
    private static final String PHOTO_ZIP = WEB_PORTFOLIO_STORES.concat("{0,number,#}/优差宿图片.zip");

    /**
     * 生成并保存的新闻稿照片和表格压缩文件。
     */
    private static final String DRAFT_ZIP = WEB_PORTFOLIO_STORES.concat("{0,number,#}/新闻稿照片和表格.zip");

    /**
     * 需要上传到服务器的图片的数量，用于检查上传的图片数量是否正确。
     */
    private static final int UPLOAD_IMAGE_COUNT = 3;

    // 注入 RedisUtils 依赖

    private final RedisUtil redisUtil;

    /**
     * 监听队列中完成的任务。
     *
     * @param taskId 任务 ID
     * @throws IOException 发生 IO 异常
     */
    @RabbitListener(queues = {RabbitConst.QUEUE_TASK})
    public void accomplish(Integer taskId) throws IOException {
        // 获取所有的查宿记录信息
        List<Note> notes = redisUtil.values(KEY_TASK_RECORD);
        // 需要提示给前端的注意信息
        List<String> message = new ArrayList<>();
        // 删除未打分的宿舍
        notes.stream()
                .collect(Collectors.groupingBy(Note::getBuilding))
                .forEach((k, v) -> {
                    if (v.stream().anyMatch(e -> e.getScore() == null)) {
                        notes.removeAll(v);
                        message.add(k + "未检查就结束了任务");
                    }
                });
        // 生成检查时间
        String time = DateUtil.now();
        // 需要打包的文件
        List<String> files = new ArrayList<>();
        files.add(DIRTY_DIR);
        files.add(CLEAN_DIR);
        // 生成本周化学学院宿舍卫生检查表
        for (Type v : Type.values()) {
            List<Note> collect = notes.stream()
                    .filter(e -> e.getType() == v.type)
                    .sorted()
                    .collect(Collectors.toList());
            // 没有该类型宿舍跳过生成
            if (collect.isEmpty()) {
                continue;
            }
            String filename = MessageFormat.format(NOTES_FILE, taskId, time, v.decl);
            files.add(filename);
            ExcelUtil.writeWithTemplate(filename, collect);
        }
        // 脏乱宿舍信息
        List<Dormitory> dirty = redisUtil.values(KEY_DIRTY);
        // 优秀宿舍信息
        List<Dormitory> clean = redisUtil.values(KEY_CLEAN);
        // 将优秀宿舍和脏乱宿舍按照年级和宿舍号排序
        Collections.sort(dirty);
        Collections.sort(clean);
        // 分类归纳上传的图片
        copyDirectory(dirty, "脏乱宿舍", message);
        copyDirectory(clean, "优秀宿舍", message);
        // 存入需要注意的消息到缓存
        redisUtil.hash(KEY_WARNINGS, taskId.toString(), message);
        // 新闻稿图片名
        String name = MessageFormat.format(IMAGE_FILE, taskId);
        // 生成新闻稿图片
        ExcelUtil.data2Image(name, clean, dirty);
        // 生成压缩文件
        ZipUtil.compress(MessageFormat.format(CHART_ZIP, taskId, time), files);
        ZipUtil.compress(MessageFormat.format(PHOTO_ZIP, taskId), DIRTY_DIR, CLEAN_DIR);
        ZipUtil.compress(MessageFormat.format(DRAFT_ZIP, taskId), name, CLEAN_DIR);
    }

    /**
     * 将上传的优差宿舍的照片分类复制到 ”优秀宿舍“ 和 ”脏乱宿舍“ 文件夹中。
     *
     * @param dormitories 宿舍信息
     * @param name        文件夹名称
     * @param message     存放警告的集合
     */
    private void copyDirectory(List<Dormitory> dormitories, String name, List<String> message) {
        File destFile = new File(WEB_PORTFOLIO_ASSETS.concat(name));
        FileUtils.deleteQuietly(destFile);
        dormitories.stream().map(Dormitory::getRoom).forEach(e -> {
            File file = new File(WEB_PORTFOLIO_ASSETS.concat(e));
            if (!file.exists()) {
                message.add("宿舍" + e + "被标记为" + name + "，但未上传任何图片");
            } else {
                int length = Objects.requireNonNull(file.list()).length;
                if (length < UPLOAD_IMAGE_COUNT) {
                    message.add("宿舍" + e + "被标记为" + name + "，但只上传了" + length + "张图片");
                }
                try {
                    FileUtils.copyDirectoryToDirectory(file, destFile);
                } catch (IOException ignored) {
                    // 忽略异常的抛出
                }
            }
        });
        if (!destFile.exists()) {
            message.add("本次宿舍检查没有上传任何" + name + "照片");
        }
    }

    /**
     * 宿舍类型的枚举类。
     */
    private enum Type {

        /**
         * 研究生
         */
        POSTGRADUATE(0, "（研究生）"),
        /**
         * 本科生
         */
        UNDERGRADUATE(1, "（本科生）");

        private final int type;
        private final String decl;

        Type(int type, String decl) {
            this.type = type;
            this.decl = decl;
        }

    }

}
