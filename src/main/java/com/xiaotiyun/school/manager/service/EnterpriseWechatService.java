package com.xiaotiyun.school.manager.service;

import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.enums.MiniWxAppMessageType;
import com.xiaotiyun.school.manager.basic.enums.WechatBusinessTypeEnum;
import com.xiaotiyun.school.manager.basic.exception.AesException;
import com.xiaotiyun.school.manager.model.dto.*;
import com.xiaotiyun.school.manager.model.req.WXAppRegisterReq;
import com.xiaotiyun.school.manager.model.req.WXBindAndLoginReq;
import com.xiaotiyun.school.manager.model.req.WXStuBindAndLoginReq;
import com.xiaotiyun.school.manager.model.res.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

public interface EnterpriseWechatService {

    void saveSuiteTicket(HttpServletRequest request,
                         @RequestParam(value = "msg_signature") String msgSignature,
                         @RequestParam(value = "timestamp") String timestamp, @RequestParam(value = "nonce") String nonce,
                         @RequestBody String requestBody, boolean isTeacher) throws AesException;

    Result<SchoolWeixinRelevanceCheckResModel> checkCorpId(String openCorpId, Long schoolId, Integer type);

    Result<SchoolWeixinRelevanceCheckResModel> bindCorpId(String id, Long schoolId);

    WechatMessageResDTO sendMiniWxAPPMessage(Long schoolId, int msgType, MiniWxAppMessageType miniWxAppMessageType, File file, WechatMessageDTO reqDTO) throws IOException;

    String getApplicationRegistration(WXAppRegisterReq reqModel, boolean isTeacher) throws AesException;

    void getUserInfo(String code, boolean isTeacher, HttpServletResponse response) throws IOException;

    void getUserInfoInternal(String code, String state, HttpServletResponse response) throws IOException;

    LoginResModel bindAndLogin(WXBindAndLoginReq req);

    LoginResModel stuBindAndLogin(WXStuBindAndLoginReq req);

    String studentUnbind(Long studentId, Long schoolId);

    String studentGetBind(Long studentId);

    List<StudentWeCharSchoolResModel> switchSchool();

    String changeWeCharStudentSchool(Long studentId);

    /**
     * 小程序登入用户
     *
     * @param code
     * @param studentId
     */
    MinigrogramUserResModel getUserInfo(String code, Long studentId);

    void authChannelGetUserInfo(String openId, MinigrogramAuthResModel resModel);

    /**
     * 创建/更新部门
     *
     * @param id
     * @param type             (1表示班级，2表示年级，3表示学段，4表示校区)
     * @param businessTypeEnum
     */
    void createOrUpdateDepartment(Long schoolId, CreateOrUpdateBatchDepartmentDTO id, Integer type, WechatBusinessTypeEnum businessTypeEnum,String schoolYear);



    /**
     * 创建/更新部门 批量
     *
     * @param id
     * @param type             (1表示班级，2表示年级，3表示学段，4表示校区)
     * @param businessTypeEnum
     */
    void createOrUpdateBatchDepartment(Long schoolId, List<CreateOrUpdateBatchDepartmentDTO> id, Integer type, WechatBusinessTypeEnum businessTypeEnum,String schoolYear);

    /**
     * 删除部门
     *
     * @param id
     */
    void deleteDepartment(Long schoolId, Long id,Integer  type,String schoolYear);

    /**
     * 初始化部门信息
     *
     * @param schoolId
     */
    void initDepartment(Long schoolId);

    /**
     * 批量创建/更新学生
     *
     * @param ids
     * @param businessTypeEnum
     */
    void createOrUpdateStudents(Long schoolId, List<SynWxChatStatusUpdateDTO> ids, WechatBusinessTypeEnum businessTypeEnum,String schoolYear);

    /**
     * 批量删除学生
     *
     * @param ids
     */
    void deleteStudents(Long schoolId, List<Long> ids,String schoolYear);

    /**
     * 初始化学生信息
     *
     * @param schoolId
     */
    void initStudents(Long schoolId);

    /**
     * 批量创建/更新家长
     *
     * @param parentInfoDTOList 家长信息列表
     * @param type 业务类型
     */
    void createOrUpdateParents(Long schoolId, List<WechatParentInfoDTO> parentInfoDTOList, WechatBusinessTypeEnum type,String schoolYear);

    /**
     * 批量删除家长
     *
     * @param ids 家长id列表
     */
    void deleteParents(Long schoolId, List<String> ids,String schoolYear);

    /**
     * 获取部门列表
     *
     * @param schoolId 学校id 不能为空
     * @param deptId 部门id 不填默认获取所有部门
     * @return 部门列表
     */
    List<DepartmentResModelDTO> getDepartmentList(Long schoolId, String deptId);

    /**
     * 获取部门学生列表
     *
     * @param schoolId 学校id 不能为空
     * @param deptId 部门id 不能为空
     * @return 部门列表
     */
    List<StudentInfoResModelDTO> getDepartmentUser(Long schoolId, String deptId);
}
