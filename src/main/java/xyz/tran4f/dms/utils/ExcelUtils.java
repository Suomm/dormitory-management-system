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

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import lombok.Cleanup;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import xyz.tran4f.dms.handler.ColorCellWriteHandler;
import xyz.tran4f.dms.pojo.Note;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * <p>
 * 2021/1/7
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
public final class ExcelUtils {

    static {
        PdfFontFactory.registerSystemDirectories();
    }

    private ExcelUtils() {
    }

    public static void writeWithTemplate(String filename, Object data) {
        EasyExcel.write(filename, Note.class)
                .registerWriteHandler(new ColorCellWriteHandler())
                .withTemplate("./template/化学学院宿舍卫生检查表样表.xlsx")
                .sheet()
                .doFill(data);
    }

    @SuppressWarnings("rawtypes")
    public static void writeWithTemplate(String filename, Collection first, Collection last) {
        @Cleanup("finish") ExcelWriter excelWriter = EasyExcel.write(filename)
                .withTemplate("D:\\Document\\新闻稿表格样表.xlsx").build();
        WriteSheet writeSheet = EasyExcel.writerSheet().build();
        excelWriter.fill(new FillWrapper("first", first), writeSheet);
        excelWriter.fill(new FillWrapper("last", last), writeSheet);
    }

    private static final Color HEADER   = new DeviceRgb(68,114,196); // 表头
    private static final Color ODD_ROW  = new DeviceRgb(217,225,242); // 奇数行
    private static final Color EVEN_ROW = new DeviceRgb(180,198,231); // 偶数行

    @SuppressWarnings("rawtypes")
    public static void data2Image(String imageFile, Collection first, Collection last) throws IOException {
        String excelFile = "D:\\Document\\data2Excel.xlsx";
        writeWithTemplate(excelFile, first, last);
        String pdfFile = "D:\\Document\\excel2Pdf.pdf";
        excel2Pdf(excelFile, pdfFile, Math.max(first.size(), last.size()) + 2);
        pdf2Image(pdfFile, imageFile);
    }

    public static void excel2Pdf(String excelFile, String pdfFile, int lastRow) throws IOException {
        // 读取 Excel 文件内容
        @Cleanup Workbook workbook = new XSSFWorkbook(excelFile);
        Sheet sheet = workbook.getSheetAt(0);

        Table table = new Table(4);

        table.setFont(PdfFontFactory.createRegisteredFont("等线", PdfEncodings.IDENTITY_H));

        Row firstRow = sheet.getRow(0);

        table.addCell(createPdfHeader(firstRow.getCell(0).getStringCellValue(),
                sheet.getColumnWidthInPixels(0)));
        table.addCell(createPdfHeader(firstRow.getCell(2).getStringCellValue(),
                sheet.getColumnWidthInPixels(2)));

        for (int i = 1; i < lastRow; i++) {
            Row row = sheet.getRow(i);
            boolean isOdd = i % 2 == 0;
            for (int j = 0; j < 4; j++) {
                org.apache.poi.ss.usermodel.Cell cell = row.getCell(j);
                float width = sheet.getColumnWidthInPixels(j);
                if (cell == null) {
                    table.addCell(fillEmptyCell(isOdd, width));
                } else {
                    table.addCell(createPdfBody(cell.getStringCellValue(), isOdd, width));
                }
            }
        }

        // 创建 PDF 文档
        @Cleanup PdfWriter pdfWriter = new PdfWriter(pdfFile);
        @Cleanup PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        @Cleanup Document document = new Document(pdfDocument);
        // 设置从左上角开始填写内容，方便后期生成图片裁剪
        document.setLeftMargin(0f);
        document.setTopMargin(0f);
        document.add(table);
    }

    public static void pdf2Image(String pdfFile, String imageFile) throws IOException {
        @Cleanup PDDocument pdDocument = PDDocument.load(new File(pdfFile));
        PDFRenderer pdfRenderer = new PDFRenderer(pdDocument);
        BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 300);

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        int white = java.awt.Color.WHITE.getRGB();

        while (true) {
            if (bufferedImage.getRGB(10, --height) != white) {
                while (true) {
                    if (bufferedImage.getRGB(--width, height) != white) {
                        break;
                    }
                }
                break;
            }
        }

        BufferedImage source = new BufferedImage(width + 10, height + 10, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = source.getGraphics();
        graphics.drawImage(bufferedImage, 0, 0, null);
        graphics.dispose();
        ImageIO.write(source, "png", new File(imageFile));
    }

    private static Cell createPdfCell(String text, int colspan, float borderWidth, Color bgColor, float colWidth) {
        Cell pdfCell = new Cell(1, colspan);
        pdfCell.setBorder(new SolidBorder(DeviceRgb.WHITE, borderWidth));
        pdfCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
        pdfCell.setTextAlignment(TextAlignment.CENTER);
        pdfCell.setBackgroundColor(bgColor);
        pdfCell.add(new Paragraph(text));
        pdfCell.setWidth(colWidth);
        return pdfCell;
    }

    private static Cell createPdfBody(String text, boolean isOdd, float colWidth) {
        return createPdfCell(text, 1, 1f, isOdd ? ODD_ROW : EVEN_ROW, colWidth);
    }

    private static Cell createPdfHeader(String text, float colWidth) throws IOException {
        Cell pdfCell = createPdfCell(text, 2, 3f, HEADER, colWidth);
        pdfCell.setFontColor(DeviceRgb.WHITE);
        PdfFont font = PdfFontFactory.createRegisteredFont("等线 bold", PdfEncodings.IDENTITY_H);
        pdfCell.setFont(font);
        return pdfCell;
    }

    private static Cell fillEmptyCell(boolean isOdd, float colWidth) {
        return createPdfBody(StringUtils.EMPTY, isOdd, colWidth);
    }

}
