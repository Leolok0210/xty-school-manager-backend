package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.entity.CampusPhotoEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CampusPhotoService extends IService<CampusPhotoEntity> {

    CampusPhotoEntity upload(MultipartFile file, Long schoolId);

    List<CampusPhotoEntity> listPhotos(Long schoolId);

    boolean deletePhoto(Long id);
}
