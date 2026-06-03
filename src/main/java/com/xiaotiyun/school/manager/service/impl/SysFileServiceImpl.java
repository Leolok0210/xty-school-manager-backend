package com.xiaotiyun.school.manager.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.FileTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.util.FileUtils;
import com.xiaotiyun.school.manager.config.FileConfig;
import com.xiaotiyun.school.manager.dao.SysFileDao;
import com.xiaotiyun.school.manager.model.entity.StudentEntity;
import com.xiaotiyun.school.manager.model.entity.SysFileEntity;
import com.xiaotiyun.school.manager.model.entity.UserEntity;
import com.xiaotiyun.school.manager.service.SysFileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

@Service
public class SysFileServiceImpl extends ServiceImpl<SysFileDao, SysFileEntity> implements SysFileService {

    @Resource
    private FileConfig fileConfig;

    @Override
    public Long addByStudent(MultipartFile file) {
        StudentEntity nowStudent = (StudentEntity) StpUtil.getSession().get("student");
        if (nowStudent == null){
            throw new BusinessException(LanguageConstants.SYSTEM_TOKEN_INVALID);
        }
        return saveFile(file,nowStudent.getSchoolId(),nowStudent.getId());
    }

    @Override
    public Long add(MultipartFile file, Long schoolId) {
        UserEntity userInfo = (UserEntity) StpUtil.getSession().get("userInfo");
        if (userInfo == null) {
            throw new BusinessException(LanguageConstants.SYSTEM_TOKEN_INVALID);
        }
        return saveFile(file,schoolId,userInfo.getId());
    }

    private Long saveFile(MultipartFile file,Long schoolId, Long id) {
        String fileName = file.getOriginalFilename();
        String suffix = FileUtils.getFileSuffix(fileName);
        fileName = FileUtils.getFileNameNoSuffix(fileName);
        if ("jpg".equalsIgnoreCase(suffix) || "png".equalsIgnoreCase(suffix)
                || "JPG".equalsIgnoreCase(suffix) || "PNG".equalsIgnoreCase(suffix)) {
            long fileSize = file.getSize();
            if (fileSize > fileConfig.getMaxLeaveFileSize()) {
                throw new BusinessException(LanguageConstants.IMAGE_TOO_LARGE);
            } else {
                try {
                    //照片保存位置
                    String saveFileName = fileName + System.currentTimeMillis() + "." + suffix;
                    String saveFilePath = fileConfig.getFileRootPath() + File.separator + schoolId + File.separator + FileTypeEnum.LEAVE.getTypePath() + File.separator;
                    String imageUrl = saveFilePath + saveFileName;
                    FileUtil.writeFromStream(file.getInputStream(), imageUrl);
                    imageUrl = imageUrl.replace(fileConfig.getFileRootPath(), "");
                    SysFileEntity sysFileEntity = new SysFileEntity();
                    sysFileEntity.setName(saveFileName);
                    sysFileEntity.setPath(imageUrl);
                    sysFileEntity.setSuffix(suffix);
                    sysFileEntity.setOperatorId(id);
                    this.save(sysFileEntity);
                    return sysFileEntity.getId();
                } catch (IOException e) {
                    throw new BusinessException(LanguageConstants.IMAGE_SAVE_ERROR);
                }
            }
        } else {
            throw new BusinessException(LanguageConstants.IMAGE_FORMAT_ERROR_JPG_PNG);
        }
    }
}