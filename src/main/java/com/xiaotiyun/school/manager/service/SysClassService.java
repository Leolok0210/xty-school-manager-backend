package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.entity.StudentEntity;
import com.xiaotiyun.school.manager.model.entity.SysClass;
import com.xiaotiyun.school.manager.model.req.SysClassQueryReqModel;
import com.xiaotiyun.school.manager.model.res.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface SysClassService extends IService<SysClass> {
    Result<Boolean> createSysClasses(List<SysClass> sysClasses, long schoolId);
    void updateSysClass(SysClass sysClass);
    void deleteSysClass(Long id);
    SysClass getSysClassById(Long id);
    PageInfo<SysClassDetailResModel> getSysClassList(SysClassQueryReqModel reqModel);

    PageInfo<SysClassDetailResModel> getSysClassListByStudent(SysClassQueryReqModel reqModel);

    /**
     * 根据学校ID查询班级列表
     */
    List<SysClassListResModel> getSysClassListBySchoolId(Long schoolId);

    // 新增方法：根据学校ID和sid查询班级列表
    List<SysClassListResModel> getSysClassListBySchoolIdAndSid(Long schoolId, String sid,Integer department);

    // 新增方法：根据学校ID和sid和机组id查询班级列表
    List<SysClassListResModel> getSysClassListBySchoolIdAndSidAndGradeGroupId(Long schoolId, Long gradeGroupId, String sid);

    List<String> promoteClasses(long schoolId);

    // 新增方法：将当前班级下的全部学生状态修改为"毕业"
    void graduateStudentsInClass(Long classId);

    List<SysClassListResModel> getSysClassListByGradeGroupNames(List<String> gradeGroupNames, String sid, long schoolId);

    Long importClass(MultipartFile file, Long schoolId, String sid);

    //查询学年 级组下的最大班级序号
    Integer getMaxClassSerialNumberByGradeGroupAndSid(Long gradeGroupId, String sid, long schoolId);

    Boolean checkGradeGroupCanUpdate(Long gradeGroupId);

    Map<Long, String> getNamesByIds(List<Long> ids);

    //根据学校id和班级名称和学年和级组查询
    List<SysClass> getSysClassBySchoolIdAndClassNameAndSidAndGradeGroupId(Long schoolId, String className, String sid, Long gradeGroupId);

    //根據學校ID和班級名稱查詢班級（模糊匹配）
    List<SysClassListResModel> getSysClassListBySchoolIdAndClassName(Long schoolId, String className);


    String exportClassList(SysClassQueryReqModel reqModel);

    /**
     * 批量查询班级列表
     *
     * @param schoolId 学校ID
     * @param schoolYear 学年
     * @param gradeGroupId 级组ID
     * @return 班级列表
     */
    List<SysClassListResModel> listClasses(Long schoolId, String schoolYear, Long gradeGroupId,Integer department,Long userId);

    /**
     * 获取小程序用户详情
     *
     * @param resModel
     * @param student
     */
    public void getUserDetail(LoginResModel resModel, StudentEntity student);

    void getUserDetail(MinigrogramUserResModel resModel, StudentEntity student);

    void getUserDetail(MinigrogramAuthResModel resModel, StudentEntity student);
}