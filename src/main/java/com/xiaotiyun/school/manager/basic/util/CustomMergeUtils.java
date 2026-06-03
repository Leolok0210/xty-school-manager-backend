package com.xiaotiyun.school.manager.basic.util;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.merge.AbstractMergeStrategy;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.List;

/**
 * 单元格拼接工具类
 */
public class CustomMergeUtils extends AbstractMergeStrategy {
    private final List<CellRangeAddress> mergeRegions;

    public CustomMergeUtils(List<CellRangeAddress> mergeRegions) {
        this.mergeRegions = mergeRegions;
    }

    @Override
    protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {
        for (CellRangeAddress region : mergeRegions) {
            if (cell.getRowIndex() == region.getFirstRow() && cell.getColumnIndex() == region.getFirstColumn()) {
                // 检查区域是否包含至少两个单元格
                if (region.getNumberOfCells() > 1) {
                    sheet.addMergedRegion(region);
                }
            }
        }
    }
}
