package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.config.FileConfig;
import com.xiaotiyun.school.manager.dao.CampusPhotoDao;
import com.xiaotiyun.school.manager.model.entity.CampusPhotoEntity;
import com.xiaotiyun.school.manager.service.CampusPhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CampusPhotoServiceImpl extends ServiceImpl<CampusPhotoDao, CampusPhotoEntity>
        implements CampusPhotoService {

    private final FileConfig fileConfig;

    @Override
    public CampusPhotoEntity upload(MultipartFile file, Long schoolId) {
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;

        File dir = new File(fileConfig.getFileRootPath(), schoolId + "/campus");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File dest = new File(dir, fileName);
        try {
            file.transferTo(dest);
        } catch (Exception e) {
            throw new RuntimeException("文件保存失败", e);
        }

        CampusPhotoEntity entity = new CampusPhotoEntity();
        entity.setFileName(fileName);
        entity.setOriginalName(originalName);
        entity.setFileSize(file.getSize());
        entity.setSchoolId(schoolId);
        save(entity);
        return entity;
    }

    @Override
    public List<CampusPhotoEntity> listPhotos(Long schoolId) {
        LambdaQueryWrapper<CampusPhotoEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CampusPhotoEntity::getSchoolId, schoolId);
        wrapper.orderByDesc(CampusPhotoEntity::getCreateTime);
        return list(wrapper);
    }

    @Override
    public boolean deletePhoto(Long id) {
        CampusPhotoEntity entity = getById(id);
        if (entity == null) return false;
        File file = new File(fileConfig.getFileRootPath(),
                entity.getSchoolId() + "/campus/" + entity.getFileName());
        if (file.exists()) {
            file.delete();
        }
        return removeById(id);
    }
}
