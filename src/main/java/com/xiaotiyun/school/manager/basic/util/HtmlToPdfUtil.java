package com.xiaotiyun.school.manager.basic.util;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import com.xiaotiyun.school.manager.config.FileConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class HtmlToPdfUtil {

    @Resource
    private TemplateEngine templateEngine;


    @Resource
    private FileConfig fileConfig;
    /**
     * 使用Thymeleaf模板生成PDF字节数组
     *
     * @param templateName 模板名称
     * @param data         模板数据
     * @return PDF字节数组
     * @throws IOException       IO异常
     * @throws DocumentException PDF文档异常
     */
    public byte[] generatePdfBytes(String templateName, Map<String, Object> data) throws IOException, DocumentException {
        // 创建Thymeleaf上下文
        Context context = new Context();
        context.setVariables(data);

        // 渲染HTML内容
        String htmlContent = templateEngine.process(templateName, context);

        // 将HTML转换为PDF
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ITextRenderer renderer = new ITextRenderer();
            // 设置中文字体支持，添加异常处理避免字体文件缺失导致PDF生成失败
            String path = fileConfig.getTemplateUrl() + "simsun.ttc";
            try {
                renderer.getFontResolver().addFont(path, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            }catch (Exception e)
            {
                log.error("警告: 找不到中文字体文件 simsun.ttc，PDF可能无法正确显示中文{}",e);
                renderer.getFontResolver().addFont("simsun", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            }

            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            outputStream.flush();
            return outputStream.toByteArray();
        }catch (Exception e)
        {
            log.error("PDF生成失败", e);
        }finally {
            outputStream.close();
        }
        return null;
    }
}