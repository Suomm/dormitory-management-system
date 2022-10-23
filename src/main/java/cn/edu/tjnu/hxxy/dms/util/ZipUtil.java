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

package cn.edu.tjnu.hxxy.dms.util;

import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.NoArgsConstructor;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;

/**
 * <p>
 * 生成 ZIP 压缩文件的工具类。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ZipUtil {

    /**
     * 生成一个压缩文件，并向其中添加需要压缩的文件。
     *
     * @param filename 生成的压缩文件名
     * @param resources 要进行压缩的资源
     * @throws IOException 压缩失败抛出此异常
     */
    public static void compress(String filename, String... resources) throws IOException {
        compress(filename, Arrays.asList(resources));
    }

    /**
     * 生成一个压缩文件，并向其中添加需要压缩的文件。
     *
     * @param filename 生成的压缩文件名
     * @param resources 要进行压缩的资源
     * @throws IOException 压缩失败抛出此异常
     */
    public static void compress(String filename, Collection<String> resources) throws IOException {
        @Cleanup FileOutputStream fos = new FileOutputStream(filename);
        @Cleanup BufferedOutputStream bos = new BufferedOutputStream(fos);
        @Cleanup ZipArchiveOutputStream out = new ZipArchiveOutputStream(bos);
        // 向压缩文件中添加指定的资源文件
        for (String resource : resources) {
            File file = new File(resource);
            if (!file.exists()) { continue; }
            copy(file, out, "");
        }
    }

    private static void copy(File source, ZipArchiveOutputStream out, String base) throws IOException {
        // 递归方式寻找文件
        if (source.isDirectory()) {
            for (File listFile : source.listFiles()) {
                copy(listFile, out, base + source.getName() + "/");
            }
        } else {
            // 添加文件到 ZIP 压缩包
            ZipArchiveEntry entry = new ZipArchiveEntry(base + source.getName());
            out.putArchiveEntry(entry);
            @Cleanup FileInputStream fis = new FileInputStream(source);
            @Cleanup BufferedInputStream bis = new BufferedInputStream(fis);
            IOUtils.copy(bis, out);
            out.closeArchiveEntry();
        }
    }

}
