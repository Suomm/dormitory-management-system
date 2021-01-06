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
 * 2021/1/6
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@Component
public class ColorCellWriteHandler extends AbstractCellWriteHandler {

    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder,
                                 WriteTableHolder writeTableHolder,
                                 List<CellData> cellDataList,
                                 Cell cell,
                                 Head head,
                                 Integer relativeRowIndex,
                                 Boolean isHead) {
        if (cell.getColumnIndex() != 4) {
            return;
        }
        int cellValue = Integer.parseInt(cell.getStringCellValue());
        if (cellValue >= 90) {
            setFillForegroundColor(writeSheetHolder, cell, IndexedColors.GREEN);
        } else if (cellValue < 60) {
            setFillForegroundColor(writeSheetHolder, cell, IndexedColors.RED);
        }
    }

    private void setFillForegroundColor(WriteSheetHolder writeSheetHolder, Cell cell, IndexedColors color) {
        CellStyle cellStyle = writeSheetHolder.getSheet().getWorkbook().createCellStyle();
        cellStyle.cloneStyleFrom(cell.getCellStyle());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(color.index);
        cell.setCellStyle(cellStyle);
    }

}
