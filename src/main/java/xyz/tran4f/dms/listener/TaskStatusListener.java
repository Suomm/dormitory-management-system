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
import xyz.tran4f.dms.utils.ExcelUtils;
import xyz.tran4f.dms.utils.RedisUtils;
import xyz.tran4f.dms.utils.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static xyz.tran4f.dms.attribute.RedisAttribute.*;

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

    private static final String BASE_PATH = "./portfolio/picture/";
    private static final String DIRTY_PATH = "./portfolio/picture/脏乱宿舍";
    private static final String CLEAN_PATH = "./portfolio/picture/优秀宿舍";

    private final RedisUtils redisUtils;

    public TaskStatusListener(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }

    @RabbitListener(queues = {RabbitAttribute.QUEUE_TASK})
    public void accomplish(String time) throws IOException {
        // 获取所有要检查的宿舍
        List<String> buildings = redisUtils.listGet(KEY_BUILDING_LIST);
        // 将获得的数组转换成流的形式
        Stream<String> stream = buildings.stream();
        // 获取所有的查宿记录信息
        Set<Note> notes = union(stream.map(e -> PREFIX_TASK_RECORD + e));
        // 生成本周化学学院宿舍卫生检查表
        for (Type v : Type.values()) {
            Stream<Note> s = notes.stream().filter(e -> e.getType() == v.type);
            // 没有该类型宿舍跳过生成
            if (s.count() == 0) { break; }
            String filename = "化学学院宿舍卫生检查表" + time + v.decl + ".xlsx";
            ExcelUtils.writeWithTemplate(filename, s.sorted().collect(Collectors.toList()));
        }
        // 脏乱宿舍信息
        Set<Dormitory> dirty = union(stream.map(e -> PREFIX_DIRTY_SET + e));
        // 优秀宿舍信息
        Set<Dormitory> clean = union(stream.map(e -> PREFIX_CLEAN_SET + e));
        // 分类归纳上传的图片
        moveDirectory(dirty, "脏乱宿舍");
        moveDirectory(clean, "优秀宿舍");
        // 生成新闻稿图片
        ExcelUtils.data2Image("picture/新闻稿图片.png", dirty, clean);
        // 生成压缩文件
        ZipUtils.compress("./portfolio/compress/" + time + "化学学院宿舍卫生检查.zip",
                "./portfolio/document/化学学院宿舍卫生检查表" + time +"（本科生）.xlsx",
                "./portfolio/document/化学学院宿舍卫生检查表" + time +"（研究生）.xlsx",
                DIRTY_PATH, CLEAN_PATH);
        ZipUtils.compress("./portfolio/compress/新闻稿照片+表格.zip",
                "./portfolio/picture/新闻稿图片.png", DIRTY_PATH, CLEAN_PATH);
    }

    private <T> Set<T> union(Stream<String> stream) {
        return redisUtils.setUnion(stream.collect(Collectors.toList()));
    }

    private void moveDirectory(Set<Dormitory> dormitories, String name) {
        File destFile = new File(BASE_PATH + name);
        FileUtils.deleteQuietly(destFile);
        dormitories.stream().map(Dormitory::getRoom).forEach(e -> {
            try {
                FileUtils.moveDirectory(new File(BASE_PATH + e), destFile);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
    }

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
