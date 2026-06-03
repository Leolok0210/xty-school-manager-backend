package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.dao.ExternalCompetitionGroupDao;
import com.xiaotiyun.school.manager.model.entity.ExternalCompetitionGroupEntity;
import com.xiaotiyun.school.manager.service.ExternalCompetitionGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExternalCompetitionGroupServiceImpl extends ServiceImpl<ExternalCompetitionGroupDao, ExternalCompetitionGroupEntity> implements ExternalCompetitionGroupService {

}
