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

package xyz.tran4f.dms.utils;

import lombok.Cleanup;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * <p>
 * 提供 Word 2007 级以上版本的相关操作。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
public final class WordUtils {

    private WordUtils() {
    }

    /**
     * <p>
     * 生成一个新闻稿文档。
     * </p>
     *
     * @param filename 文件名称
     * @param title 文档标题
     * @param paragraphs 文档段落
     */
    public static void create(String filename, String title, String... paragraphs) throws IOException {
        // 创建文档
        @Cleanup XWPFDocument document = new XWPFDocument();
        // 创建文档标题
        XWPFParagraph titleParagraph = document.createParagraph();
        titleParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText(title);
        titleRun.setFontSize(16);
        titleRun.setFontFamily("方正小标宋简体");
        titleRun.addCarriageReturn();
        // 创建文档段落
        for (String text : paragraphs) {
            XWPFParagraph paragraph = document.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            paragraph.setSpacingBetween(1.5);
            // 设置首行缩进 0.99cm（仿宋四号字正文首行缩进 2 字符）
            paragraph.setFirstLineIndent(28 * 10 * 2);
            XWPFRun run = paragraph.createRun();
            run.setText(text);
            run.setFontSize(14);
            run.setFontFamily("仿宋GB2312");
        }
        // 保存文档
        @Cleanup FileOutputStream fos = new FileOutputStream(filename);
        @Cleanup BufferedOutputStream bos = new BufferedOutputStream(fos);
        document.write(bos);
    }

}
