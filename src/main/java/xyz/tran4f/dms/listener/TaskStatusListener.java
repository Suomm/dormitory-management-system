/*
 * Copyright (C) 2020-2021 the original author or authors.
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

package xyz.tran4f.dms.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import xyz.tran4f.dms.attribute.RabbitAttribute;
import xyz.tran4f.dms.pojo.Dormitory;
import xyz.tran4f.dms.pojo.Note;
import xyz.tran4f.dms.utils.DateUtils;
import xyz.tran4f.dms.utils.ExcelUtils;
import xyz.tran4f.dms.utils.RedisUtils;
import xyz.tran4f.dms.utils.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static xyz.tran4f.dms.attribute.RedisAttribute.*;
import static xyz.tran4f.dms.attribute.WebAttribute.WEB_PORTFOLIO_ASSETS;
import static xyz.tran4f.dms.attribute.WebAttribute.WEB_PORTFOLIO_STORES;

/**
 * <p>
 * 2021/1/24
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@Slf4j
@Component
public class TaskStatusListener {

    // 初始化常量 使用 String#concat 方法代替 “+” 只是为了效率高一些

    /**
     * <p>
     * 本周任务的临时资源文件夹中的 ”脏乱宿舍“ 文件夹。
     * </p>
     */
    private static final String DIRTY_DIR = WEB_PORTFOLIO_ASSETS.concat("脏乱宿舍");

    /**
     * <p>
     * 本周任务的临时资源文件夹中的 ”优秀宿舍“ 文件夹。
     * </p>
     */
    private static final String CLEAN_DIR = WEB_PORTFOLIO_ASSETS.concat("优秀宿舍");

    /**
     * <p>
     * 生成并保存的新闻稿图片名称。
     * </p>
     */
    private static final String IMAGE_FILE = WEB_PORTFOLIO_STORES.concat("{0,number,#}/新闻稿图片.png");

    /**
     * <p>
     * 生成并保存的宿舍卫生检查表（本科生或研究生）。
     * </p>
     */
    private static final String NOTES_FILE = WEB_PORTFOLIO_STORES.concat("化学学院宿舍卫生检查表{0}{1}.xlsx");

    /**
     * <p>
     * 生成并保存的化学学院宿舍卫生检查压缩文件。
     * </p>
     */
    private static final String CHART_ZIP = WEB_PORTFOLIO_STORES.concat("{0,number,#}/{1}化学学院宿舍卫生检查.zip");

    /**
     * <p>
     * 生成并保存的优差宿舍照片压缩文件。
     * </p>
     */
    private static final String PHOTO_ZIP = WEB_PORTFOLIO_STORES.concat("{0,number,#}/优差宿图片.zip");

    /**
     * <p>
     * 生成并保存的新闻稿照片和表格压缩文件。
     * </p>
     */
    private static final String DRAFT_ZIP = WEB_PORTFOLIO_STORES.concat("{0,number,#}/新闻稿照片和表格.zip");

    // 注入 RedisUtils 依赖

    private final RedisUtils redisUtils;

    public TaskStatusListener(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }

    /**
     * <p>
     * 监听队列中完成的任务。
     * </p>
     *
     * @param taskId 任务 ID
     * @throws IOException 发生 IO 异常
     */
    @RabbitListener(queues = {RabbitAttribute.QUEUE_TASK})
    public void accomplish(Integer taskId) throws IOException {
        // 获取所有要检查的宿舍
        List<String> buildings = redisUtils.lRange(KEY_BUILDING_LIST);
        // 将获得的数组转换成流的形式
        Stream<String> stream = buildings.stream();
        // 获取所有的查宿记录信息
        Set<Note> notes = union(stream.map(PREFIX_TASK_RECORD::concat));
        // 生成检查时间
        String time = DateUtils.now();
        // 需要打包的文件
        List<String> files = Arrays.asList(DIRTY_DIR, CLEAN_DIR);
        // 生成本周化学学院宿舍卫生检查表
        for (Type v : Type.values()) {
            Stream<Note> s = notes.stream().filter(e -> e.getType() == v.type);
            // 没有该类型宿舍跳过生成
            if (s.count() == 0) { break; }
            String filename = MessageFormat.format(NOTES_FILE, time, v.decl);
            files.add(filename);
            ExcelUtils.writeWithTemplate(filename, s.sorted(Comparator.comparing(Note::getRoom)).collect(Collectors.toList()));
        }
        // 脏乱宿舍信息
        Set<Dormitory> dirty = union(stream.map(PREFIX_DIRTY_SET::concat));
        // 优秀宿舍信息
        Set<Dormitory> clean = union(stream.map(PREFIX_CLEAN_SET::concat));
        // 分类归纳上传的图片
        copyDirectory(dirty, "脏乱宿舍");
        copyDirectory(clean, "优秀宿舍");
        // 生成新闻稿图片
        ExcelUtils.data2Image(MessageFormat.format(IMAGE_FILE, taskId), dirty, clean);
        // 生成压缩文件
        ZipUtils.compress(MessageFormat.format(CHART_ZIP, taskId, time), files);
        ZipUtils.compress(MessageFormat.format(PHOTO_ZIP, taskId), DIRTY_DIR, CLEAN_DIR);
        ZipUtils.compress(MessageFormat.format(DRAFT_ZIP, taskId), IMAGE_FILE, DIRTY_DIR, CLEAN_DIR);
    }

    /**
     * <p>
     * 求 Redis Set 集合的交集。
     * </p>
     *
     * @param stream 数据流
     * @param <T> 数据类型
     * @return 各集合的交集
     */
    private <T> Set<T> union(Stream<String> stream) {
        return redisUtils.sUnion(stream.collect(Collectors.toList()));
    }

    /**
     * <p>
     * 将上传的优差宿舍的照片分类复制到 ”优秀宿舍“ 和 ”脏乱宿舍“ 文件夹中。
     * </p>
     *
     * @param dormitories 宿舍信息
     * @param name 文件夹名称
     */
    private void copyDirectory(Set<Dormitory> dormitories, String name) {
        File destFile = new File(WEB_PORTFOLIO_ASSETS + name);
        FileUtils.deleteQuietly(destFile);
        dormitories.stream().map(Dormitory::getRoom).forEach(e -> {
            try {
                FileUtils.copyDirectory(new File(WEB_PORTFOLIO_ASSETS + e), destFile);
            } catch (IOException ignored) {
                // 忽略异常的抛出
            }
        });
    }

    /**
     * <p>
     * 宿舍类型的枚举类。
     * </p>
     */
    private enum Type {

        POSTGRADUATE(0, "（研究生）"),
        UNDERGRADUATE(1, "（本科生）");

        private final int type;
        private final String decl;

        Type(int type, String decl) {
            this.type = type;
            this.decl = decl;
        }

    }

}
