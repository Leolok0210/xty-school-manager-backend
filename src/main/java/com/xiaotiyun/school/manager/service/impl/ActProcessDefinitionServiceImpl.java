package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.dao.ActProcessDefinitionDao;
import com.xiaotiyun.school.manager.model.entity.ActProcessDefinitionEntity;
import com.xiaotiyun.school.manager.service.ActProcessDefinitionService;
import org.springframework.stereotype.Service;

@Service
public class ActProcessDefinitionServiceImpl extends ServiceImpl<ActProcessDefinitionDao, ActProcessDefinitionEntity> implements ActProcessDefinitionService {
}