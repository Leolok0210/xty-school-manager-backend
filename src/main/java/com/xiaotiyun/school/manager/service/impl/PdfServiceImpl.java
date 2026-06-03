package com.xiaotiyun.school.manager.service.impl;

import com.lowagie.text.DocumentException;
import com.xiaotiyun.school.manager.basic.enums.FileTypeEnum;
import com.xiaotiyun.school.manager.service.FileUploadService;
import com.xiaotiyun.school.manager.service.PdfService;
import com.xiaotiyun.school.manager.basic.util.HtmlToPdfUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class PdfServiceImpl implements PdfService {

    @Resource
    private HtmlToPdfUtil htmlToPdfUtil;


    @Resource
    private FileUploadService fileUploadService;

    @Override
    public String generatePdfBytes(String templateName, Map<String, Object> data,Long schoolId,String fileName) {
        try {
            byte[] pdfBytes = htmlToPdfUtil.generatePdfBytes(templateName, data);
            log.info("PDF字节数组生成成功，模板名称：{}", templateName);
            return fileUploadService.upload(pdfBytes, FileTypeEnum.PDF, fileName, schoolId);
        } catch (IOException | DocumentException e) {
            log.error("PDF字节数组生成失败", e);
            return null;
        }
    }

    @Override
    public String generatePdfInDir(String templateName, Map<String, Object> data, String fileName) {
        FileOutputStream fos = null;
        try {
            byte[] pdfBytes = htmlToPdfUtil.generatePdfBytes(templateName, data);
            log.info("PDF字节数组生成成功，模板名称：{}", templateName);
            // 将PDF字节数组写入到指定的绝对路径文件中
            fos = new FileOutputStream(fileName);
            fos.write(pdfBytes);
            fos.flush();
            log.info("PDF文件写入成功，路径：{}", fileName);
            return fileName; // 返回写入成功的文件路径
        } catch (IOException | DocumentException e) {
            log.error("PDF字节数组生成或文件写入失败", e);
            return null;
        }finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    log.error("关闭文件输出流异常", e);
                }
            }
        }
    }
}