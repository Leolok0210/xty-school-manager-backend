package com.xiaotiyun.school.manager.basic.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.xiaotiyun.school.manager.basic.enums.DepartmentEnum;
import com.xiaotiyun.school.manager.basic.enums.GradeEnum;
import com.xiaotiyun.school.manager.model.dto.DepartmentConfigDTO;
import com.xiaotiyun.school.manager.model.entity.SystemSettingEntity;
import com.xiaotiyun.school.manager.service.SystemSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClassUtils {

    @Autowired
    private SystemSettingService systemSettingService;

    //年级编号 + 年级 +班级序号
    //年级编号 取"系统设置"中每个学部的编号代码（K、P、S、F）
    //年级：对应的一年级、二年级
    //班级序号： A、B、C、D（对应1、2、3、4）
    public String getClassNumber(Long schoolId, String gradeGroup, Integer classSerialNumber, Integer department) {
        StringBuilder name = new StringBuilder();

        // 从系统配置中获取部门代码
        String departmentCode = getDepartmentCode(schoolId, department);
        if (departmentCode != null) {
            name.append(departmentCode);
        }

        GradeEnum gradeEnum = GradeEnum.getByDesc(gradeGroup);
        if (gradeEnum != null) {
            name.append(gradeEnum.getGrade());
        }
        //班级序号： A、B、C、D（对应1、2、3、4）,根据24个字母对应1-24
        if (classSerialNumber != null) {
            if (classSerialNumber > 0 && classSerialNumber <= 24) {
                name.append((char) ('A' + classSerialNumber - 1));
            }
        }
        return name.toString();
    }

    /**
     * 从系统配置中获取部门代码
     *
     * @param schoolId   学校ID
     * @param department 部门编号（1、2、3对应不同的部门）
     * @return 部门代码
     */
    private String getDepartmentCode(Long schoolId, Integer department) {
        try {
            // 获取departments配置
            SystemSettingEntity setting = systemSettingService.getLatestConfig(schoolId, "departments");
            if (setting == null || setting.getSettingValue() == null) {
                // 如果配置不存在，使用默认值
                return getDefaultDepartmentCode(department);
            }

            // 解析JSON配置
            List<DepartmentConfigDTO> departmentConfigs = JSON.parseObject(
                    setting.getSettingValue(),
                    new TypeReference<List<DepartmentConfigDTO>>() {}
            );

            if (departmentConfigs == null || departmentConfigs.isEmpty()) {
                return getDefaultDepartmentCode(department);
            }

            // 根据部门编号获取对应的部门名称
            DepartmentEnum departmentEnum = DepartmentEnum.getByCode(department);
            if (departmentEnum == null) {
                return getDefaultDepartmentCode(department);
            }

            // 根据部门名称在配置中查找对应的配置
            for (DepartmentConfigDTO config : departmentConfigs) {
                if (departmentEnum.getDesc().equals(config.getName())) {
                    // 如果有type字段则使用type，否则使用code
                    return config.getType() != null ? config.getType() : config.getCode();
                }
            }

            return getDefaultDepartmentCode(department);
        } catch (Exception e) {
            // 解析失败时使用默认值
            return getDefaultDepartmentCode(department);
        }
    }

    /**
     * 获取默认的部门代码（当配置不存在或解析失败时使用）
     *
     * @param department 部门编号
     * @return 默认部门代码
     */
    private String getDefaultDepartmentCode(Integer department) {
        switch (department) {
            case 1:
                return "K";
            case 2:
                return "P";
            case 3:
                return "S";
            default:
                return "";
        }
    }
}
