package com.xiaotiyun.school.manager.service;

import com.xiaotiyun.school.manager.model.entity.SysClassUpgradeRel;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Akame
* @description 针对表【sys_class_upgrade_rel(升班学生表)】的数据库操作Service
* @createDate 2025-02-13 14:08:21
*/
public interface SysClassUpgradeRelService extends IService<SysClassUpgradeRel> {

    //根据班级id查询全部数据
    List<SysClassUpgradeRel> getSysClassUpgradeRelByClassId(Long classId);
}
