package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.dao.SysClassUpgradeRelMapper;
import com.xiaotiyun.school.manager.model.entity.SysClassUpgradeRel;
import com.xiaotiyun.school.manager.service.SysClassUpgradeRelService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Akame
 * @description 针对表【sys_class_upgrade_rel(升班学生表)】的数据库操作Service实现
 * @createDate 2025-02-13 14:08:21
 */
@Service
public class SysClassUpgradeRelServiceImpl extends ServiceImpl<SysClassUpgradeRelMapper, SysClassUpgradeRel>
        implements SysClassUpgradeRelService {

    @Override
    public List<SysClassUpgradeRel> getSysClassUpgradeRelByClassId(Long classId) {
        LambdaQueryWrapper<SysClassUpgradeRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysClassUpgradeRel::getClassId, classId)
                .eq(SysClassUpgradeRel::getDeleted, 0);
        return this.list(wrapper);
    }
}




