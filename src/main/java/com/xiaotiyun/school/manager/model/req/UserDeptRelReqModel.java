package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.model.entity.UserDeptRelEntity;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserDeptRelReqModel {

    private Long id;

    @NotNull(message = "学校ID不能为空")
    private Long schoolId;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "部门ID不能为空")
    private Long deptId;

    private Integer isAdmin;

    private Integer isMaster;

    public UserDeptRelEntity convertToEntity() {
        UserDeptRelEntity entity = new UserDeptRelEntity();
        entity.setId(id);
        entity.setSchoolId(schoolId);
        entity.setUserId(userId);
        entity.setDeptId(deptId);
        entity.setIsAdmin(isAdmin);
        entity.setIsMaster(isMaster);
        return entity;
    }
}