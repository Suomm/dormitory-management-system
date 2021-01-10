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
import lombok.SneakyThrows;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import xyz.tran4f.dms.handler.ColorCellWriteHandler;
import xyz.tran4f.dms.pojo.Note;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * <p>
 * 提供对 Excel 2007 及以后文档的相关操作。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
public final class ExcelUtils {

    static {
        // 注册系统字体文件用于生成的 PDF 文件使用
        PdfFontFactory.registerSystemDirectories();
    }

    private ExcelUtils() {
    }

    /**
     * <p>
     * 根据模板填充查宿成绩记录。
     * </p>
     *
     * @param filename 文件名称
     * @param data 要进行填充的数据
     */
    public static void writeWithTemplate(String filename, Object data) {
        EasyExcel.write(filename, Note.class)
                // 优差宿舍成绩背景色处理
                .registerWriteHandler(new ColorCellWriteHandler())
                .withTemplate("./template/化学学院宿舍卫生检查表样表.xlsx")
                .sheet()
                .doFill(data);
    }

    /**
     * <p>
     * 根据模板填充新闻稿表格。
     * </p>
     *
     * @param filename 文件名称
     * @param first 优秀宿舍信息
     * @param last 脏乱宿舍信息
     */
    @SuppressWarnings("rawtypes")
    private static void writeWithTemplate(File filename, Collection first, Collection last) {
        @Cleanup("finish") ExcelWriter excelWriter = EasyExcel.write(filename)
                .withTemplate("D:\\Document\\新闻稿表格样表.xlsx").build();
        WriteSheet writeSheet = EasyExcel.writerSheet().build();
        excelWriter.fill(new FillWrapper("first", first), writeSheet);
        excelWriter.fill(new FillWrapper("last", last), writeSheet);
    }

    /**
     * <p>
     * 新闻稿表格表头的颜色。
     * </p>
     */
    private static final Color HEADER   = new DeviceRgb(68,114,196);

    /**
     * <p>
     * 新闻稿表格奇数行的颜色（不包括表头）。
     * </p>
     */
    private static final Color ODD_ROW  = new DeviceRgb(217,225,242);

    /**
     * <p>
     * 新闻稿表格偶数行的颜色。
     * </p>
     */
    private static final Color EVEN_ROW = new DeviceRgb(180,198,231);

    /**
     * <p>
     * 使用优差宿的信息，生成新闻稿所需要的图片内容。
     * </p>
     *
     * @param imageFile 图片的路径
     * @param first 优秀宿舍信息
     * @param last 脏乱宿舍信息
     */
    @SuppressWarnings("rawtypes")
    public static void data2Image(String imageFile, Collection first, Collection last) throws IOException {
        // 创建相应的临时文件，并在该方法执行（无论是否出现异常）之后删除这些临时文件。
        @Cleanup("delete") File excelFile = File.createTempFile("data2Excel", ".xlsx");
        writeWithTemplate(excelFile, first, last);
        @Cleanup("delete") File pdfFile = File.createTempFile("excel2Pdf", ".pdf");
        // 以长度最长的集合为标准，并加上表头所需的两行，作为需要转换的最后行数。
        excel2Pdf(excelFile, pdfFile, Math.max(first.size(), last.size()) + 2);
        pdf2Image(pdfFile, imageFile);
    }

    /**
     * <p>
     * 将新闻稿表格 Excel 文件转成 PDF 文件。
     * </p>
     *
     * @param excelFile 输入 Excel 文件路径
     * @param pdfFile 输出 PDF 文件路径
     * @param lastRow 需要转换的最后一行
     */
    @SneakyThrows(InvalidFormatException.class)
    public static void excel2Pdf(File excelFile, File pdfFile, int lastRow) throws IOException {
        // 读取 Excel 文件内容
        @Cleanup Workbook workbook = new XSSFWorkbook(excelFile);
        Sheet sheet = workbook.getSheetAt(0);
        // 创建一个四列的 PDF 表格
        Table table = new Table(4);
        // 设置表格的字体为 ”等线“
        table.setFont(PdfFontFactory.createRegisteredFont("等线", PdfEncodings.IDENTITY_H));
        // 获取表头行（需要合并单元格的行）
        Row firstRow = sheet.getRow(0);
        // 添加表头信息
        table.addCell(createPdfHeader(firstRow.getCell(0).getStringCellValue(),
                sheet.getColumnWidthInPixels(0)));
        table.addCell(createPdfHeader(firstRow.getCell(2).getStringCellValue(),
                sheet.getColumnWidthInPixels(2)));
        // 添加内容信息
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

    /**
     * <p>
     * 将 PDF 文档渲染成一张图片。
     * </p>
     *
     * @param pdfFile 输入 PDF 文档路径
     * @param imageFile 输出 PNG 图片路径
     */
    public static void pdf2Image(File pdfFile, String imageFile) throws IOException {
        @Cleanup PDDocument pdDocument = PDDocument.load(pdfFile);
        PDFRenderer pdfRenderer = new PDFRenderer(pdDocument);
        BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 300);
        // 裁剪右方和下方的空白区域
        int border = 10;
        int width  = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int white  = java.awt.Color.WHITE.getRGB();
        // 获取内容区域的长和宽
        while (true) {
            if (bufferedImage.getRGB(border, --height) != white) {
                while (true) {
                    if (bufferedImage.getRGB(--width, height) != white) {
                        break;
                    }
                }
                break;
            }
        }
        // 生成仅含有内容区域的 PNG 图片
        BufferedImage source = new BufferedImage(width + border, height + border, BufferedImage.TYPE_INT_RGB);
        source.getGraphics().drawImage(bufferedImage, 0, 0, null);
        ImageIO.write(source, "png", new File(imageFile));
    }

    /**
     * <p>
     * 创建 PDF 文件中表格元素中的单元格。
     * </p>
     */
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

    /**
     * <p>
     * 创建 PDF 文档表格元素中的表格体。
     * </p>
     */
    private static Cell createPdfBody(String text, boolean isOdd, float colWidth) {
        return createPdfCell(text, 1, 1f, isOdd ? ODD_ROW : EVEN_ROW, colWidth);
    }

    /**
     * <p>
     * 创建 PDF 文档表格元素中的表头。
     * </p>
     */
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
