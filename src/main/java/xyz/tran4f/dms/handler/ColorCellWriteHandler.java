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

package xyz.tran4f.dms.handler;

import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.handler.AbstractCellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 填充成绩模板时，根据分数设置单元格背景色。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
public class ColorCellWriteHandler extends AbstractCellWriteHandler {

    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder,
                                 WriteTableHolder writeTableHolder,
                                 List<CellData> cellDataList,
                                 Cell cell,
                                 Head head,
                                 Integer relativeRowIndex,
                                 Boolean isHead) {
        // 根据列的索引判断是不是成绩那一列
        if (cell.getColumnIndex() == 4) {
            // 获取填入的成绩，并转换为 int 格式
            int cellValue = Integer.parseInt(cell.getStringCellValue());
            if (cellValue >= 90) {
                // 大于等于 90 分设置背景色为绿色
                setFillForegroundColor(writeSheetHolder, cell, IndexedColors.GREEN);
            } else if (cellValue < 60) {
                // 小于 60 设置背景色为红色
                setFillForegroundColor(writeSheetHolder, cell, IndexedColors.RED);
            }
        }
    }

    /**
     * <p>
     * 设置单元格背景色。
     * </p>
     *
     * @param writeSheetHolder 用于生成单元格样式
     * @param cell 需要设置样式的单元格
     * @param color 单元格背景颜色
     */
    private void setFillForegroundColor(WriteSheetHolder writeSheetHolder, Cell cell, IndexedColors color) {
        CellStyle cellStyle = writeSheetHolder.getSheet().getWorkbook().createCellStyle();
        // 克隆原来单元格的样式，保证其他样式不变的情况下添加背景色
        cellStyle.cloneStyleFrom(cell.getCellStyle());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(color.index);
        cell.setCellStyle(cellStyle);
    }

}
