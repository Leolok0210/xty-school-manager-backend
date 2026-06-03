package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.dto.UserSchoolRelDTO;
import com.xiaotiyun.school.manager.model.entity.UserSchoolRelEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

@Mapper
public interface UserSchoolRelDao extends BaseMapper<UserSchoolRelEntity> {
    /**
     * 批量插入
     *
     * @param list 用户-学校关联列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<UserSchoolRelEntity> list);

    /**
     * 根据手机号查询用户关联关系
     *
     * @param schoolId
     * @param phones
     * @return
     */
    List<UserSchoolRelDTO> getListByPhones(@Param("schoolId") Long schoolId, @Param("phones") Collection<String> phones);
}