package com.xiaotiyun.school.manager.handler;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.xiaotiyun.school.manager.basic.enums.FileTypeEnum;
import com.xiaotiyun.school.manager.model.excel.ExcelSheetDataDTO;
import com.xiaotiyun.school.manager.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExportFileHandler {
    private final FileUploadService fileUploadService;

    /**
     * 导出Excel
     *
     * @param data
     * @param fileName
     * @param clazz
     * @param fileType
     * @param schoolId
     * @return
     */
    public String doExportExcel(Collection<?> data, String fileName, Class<?> clazz, FileTypeEnum fileType, Long schoolId) {
        //导出
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ExcelWriter excelWriter = null;
        try {
            excelWriter = EasyExcel.write(out).build();
            WriteSheet writeSheet1 = EasyExcel.writerSheet().head(clazz).build();
            excelWriter.write(data, writeSheet1);
        } finally {
            // 千万别忘记finish 会帮忙关闭流
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
        //上传文件，返回文件上传后的URL
        return fileUploadService.upload(out.toByteArray(), fileType, fileName, schoolId);
    }

    public String doExportExcelCommon(List<List<String>> data, String tempFileName, List<List<String>> heads, FileTypeEnum fileType, Long schoolId) {
        System.out.println(heads);
        //导出
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ExcelWriter excelWriter = null;
        try {
            excelWriter = EasyExcel.write(out).build();
            WriteSheet writeSheet = EasyExcel.writerSheet().head(heads).build();
            excelWriter.write(data, writeSheet);
        } finally {
            // 千万别忘记finish 会帮忙关闭流
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
        //上传文件到OSS，返回文件上传后的URL
        String url = fileUploadService.upload(out.toByteArray(), fileType, tempFileName, schoolId);
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * 导出多Sheet Excel，支持自定义sheet名称
     *
     * @param sheetDataList 多个sheet的数据列表
     * @return 文件URL
     */
    public byte[] exportMultiSheetExcel(List<ExcelSheetDataDTO> sheetDataList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ExcelWriter excelWriter = null;
        try {
            // 创建ExcelWriter
            excelWriter = EasyExcel.write(out).build();
            
            // 设置样式
            WriteCellStyle headWriteCellStyle = new WriteCellStyle();
            // 设置表头居中对齐
            headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
            headWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            // 设置表头自动换行
            headWriteCellStyle.setWrapped(true);
            
            WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
            // 设置内容居中对齐
            contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
            contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            // 设置内容自动换行
            contentWriteCellStyle.setWrapped(true);
            
            HorizontalCellStyleStrategy horizontalCellStyleStrategy = 
                new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);

            // 遍历处理每个sheet的数据
            for (int i = 0; i < sheetDataList.size(); i++) {
                ExcelSheetDataDTO sheetData = sheetDataList.get(i);
                
                // 创建新的sheet，设置sheet名称和表头
                WriteSheet writeSheet = EasyExcel.writerSheet(i, sheetData.getSheetName())
                        .head(sheetData.getHeaders())
                        .registerWriteHandler(horizontalCellStyleStrategy)
                        // 注册列宽自适应处理器
                        .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                        .build();

                // 写入数据
                excelWriter.write(sheetData.getData(), writeSheet);
            }
        } finally {
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
        return out.toByteArray();
    }
}
