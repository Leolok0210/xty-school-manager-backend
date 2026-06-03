package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.dao.StudentLeaveCourseDao;
import com.xiaotiyun.school.manager.model.entity.StudentLeaveCourseEntity;
import com.xiaotiyun.school.manager.service.StudentLeaveCourseService;
import org.springframework.stereotype.Service;

@Service
public class StudentLeaveCourseServiceImpl extends ServiceImpl<StudentLeaveCourseDao, StudentLeaveCourseEntity> implements StudentLeaveCourseService {
}