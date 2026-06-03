package com.xiaotiyun.school.manager.basic.util;

import com.xiaotiyun.school.manager.config.FileConfig;
import com.xiaotiyun.school.manager.basic.enums.FileTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;

@Slf4j
@Component
public class LocalUploaderUtil {
    @Resource
    private FileConfig fileConfig;

    public String putStreamToOSS(byte[] fileResources, String originName, FileTypeEnum fileTypeEnum, Long schoolId) {
        try {
            //获取文件名后缀
            String fileSuffix = FileUtils.getFileSuffix(originName);
            //获取无扩展名的文件名
            String fileNameNotExt = FileUtils.getFileNameNoSuffix(originName);
            //上传新文件名-对于没有文件扩展名的无需再添加后缀
            String randomId = UUID.randomUUID().toString();
            // 保存的文件夹路径
            String uploadFileFolder = fileConfig.getFileRootPath() + File.separator + schoolId;
            if (StringUtils.isNotBlank(fileTypeEnum.getTypePath())) {
                uploadFileFolder += File.separator + fileTypeEnum.getTypePath();
            }
            // 输出的文件流保存到本地文件
            File tempFile = new File(uploadFileFolder);
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }
            //文件全路径
            String uploadFileName = StringUtils.isBlank(fileSuffix) ? MD5Util.md5Encode(fileNameNotExt + randomId) : MD5Util.md5Encode(fileNameNotExt + randomId) + "." + fileSuffix;
            String uploadFilePath = uploadFileFolder + File.separator + uploadFileName;
            OutputStream os = new FileOutputStream(uploadFilePath);
            os.write(fileResources);
            String requestURL = uploadFilePath.replace(fileConfig.getFileRootPath(), "");
            log.debug("【上传文件】【上传文件<" + originName + ">到目录<" + fileTypeEnum.getTypePath() + ">】【请求全路径是:" + requestURL + "】");
            os.close();
            return requestURL;
        } catch (Exception e) {
            log.error("【上传文件】【上传文件<" + originName + ">到目录<" + fileTypeEnum.getTypePath() + ">】出现异常：", e);
        }
        return null;
    }
}
