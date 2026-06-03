package com.xiaotiyun.school.manager.model.res;

import com.xiaotiyun.school.manager.model.entity.UserDeptRelEntity;
import lombok.Data;

@Data
public class UserDeptRelResModel {

    private Long id;
    private Long schoolId;
    private Long userId;
    private Long deptId;
    private Integer isAdmin;
    private Integer isMaster;

    public static UserDeptRelResModel convertFromEntity(UserDeptRelEntity entity) {
        UserDeptRelResModel resModel = new UserDeptRelResModel();
        resModel.setId(entity.getId());
        resModel.setSchoolId(entity.getSchoolId());
        resModel.setUserId(entity.getUserId());
        resModel.setDeptId(entity.getDeptId());
        resModel.setIsAdmin(entity.getIsAdmin());
        resModel.setIsMaster(entity.getIsMaster());
        return resModel;
    }
}