package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.dao.SchoolWeixinRelevanceDao;
import com.xiaotiyun.school.manager.model.entity.SchoolWeixinRelevanceEntity;
import com.xiaotiyun.school.manager.service.SchoolWeixinRelevanceService;
import org.springframework.stereotype.Service;

/**
 * 学校绑定企微表服务实现类
 */
@Service
public class SchoolWeixinRelevanceServiceImpl extends ServiceImpl<SchoolWeixinRelevanceDao, SchoolWeixinRelevanceEntity> implements SchoolWeixinRelevanceService {

    @Override
    public boolean exist(Long schoolId) {
        if (schoolId != null) {
            LambdaQueryWrapper<SchoolWeixinRelevanceEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SchoolWeixinRelevanceEntity::getSchoolId, schoolId)
                    .eq(SchoolWeixinRelevanceEntity::getDeleted, 0);
            long count = this.count(wrapper);
            return count > 0;
        }
        return false;
    }
}