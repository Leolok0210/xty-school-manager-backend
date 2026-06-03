package com.xiaotiyun.school.manager.service;

import java.io.IOException;
import java.util.Map;

public interface PdfService {

    /**
     * 根据模板生成PDF字节数组
     *
     * @param templateName 模板名称 templates/下面的文件名称
     * @param data         模板数据
     * @return PDF字节数组
     */
    String generatePdfBytes(String templateName, Map<String, Object> data,Long schoolId,String fileName);


    /**
     * 根据模板生成PDF字节数组
     *
     * @param templateName 模板名称 templates/下面的文件名称
     * @param data         模板数据
     * @return PDF字节数组
     */
    String generatePdfInDir(String templateName, Map<String, Object> data,String fileName);
}