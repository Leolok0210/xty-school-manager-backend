package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.SysClass;
import com.xiaotiyun.school.manager.model.res.SysClassListResModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysClassDao extends BaseMapper<SysClass> {

    List<SysClassListResModel> selectSysClassListBySchoolId(Long schoolId);

    List<SysClassListResModel> selectSysClassListBySchoolIdAndSid(Long schoolId, String sid);

    List<SysClassListResModel> selectSysClassList(Long schoolId, String sid,Integer department);
    List<SysClassListResModel> selectSysClassListByGradeGroupNames(List<String> gradeGroupNames, String sid, long schoolId);

    Integer getMaxClassSerialNumberByGradeGroupAndSid(Long gradeGroupId, String sid, long schoolId);

    List<SysClassListResModel> selectSysClassListBySchoolIdAndSidAndGradeGroupId(Long schoolId, Long gradeGroupId, String sid);

    Integer getClassNameByGradeGroupAndSid(String gradeGroup, String className, Long schoolId,String sid);

    int getClassSerialNumberByGradeGroupAndSid(String gradeGroup, String classSerialNumber, Long schoolId,String sid);

    /**
     * 批量查询班级列表
     *
     * @param schoolId 学校ID
     * @param schoolYear 学年
     * @param gradeGroupId 级组ID
     * @return 班级列表
     */
    List<SysClassListResModel> selectClassList(@Param("schoolId") Long schoolId,
                                             @Param("schoolYear") String schoolYear,
                                             @Param("gradeGroupId") Long gradeGroupId,
                                               @Param("department") Integer department,
                                               @Param("classIds") List<Long> classIds);
}