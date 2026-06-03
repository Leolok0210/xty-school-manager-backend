package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.entity.PatrolRegistrationEntity;
import com.xiaotiyun.school.manager.model.req.PatrolRegistrationAddReqModel;
import com.xiaotiyun.school.manager.model.req.PatrolRegistrationQueryReqModel;
import com.xiaotiyun.school.manager.model.req.PatrolRegistrationUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.PatrolRegistrationResModel;
import org.springframework.http.ResponseEntity;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 巡堂登记服务接口
 */
public interface PatrolRegistrationService extends IService<PatrolRegistrationEntity> {
    /**
     * 根据请求参数查询巡堂登记列表
     *
     * @param reqModel 请求参数对象
     * @return 包含巡堂登记列表的结果对象
     */
    Result<PageInfo<PatrolRegistrationResModel>> listPatrolRegistrations(PatrolRegistrationQueryReqModel reqModel);

    /**
     * 添加新的巡堂登记记录
     *
     * @param entity 巡堂登记实体对象
     * @param schoolId 学校ID
     * @return 操作结果对象
     */
    Result<String> addPatrolRegistration(List<PatrolRegistrationAddReqModel> entity, Long schoolId);

    /**
     * 更新巡堂登记记录
     *
     * @param entity 巡堂登记实体对象
     * @return 操作结果对象
     */
    PatrolRegistrationEntity updatePatrolRegistration(PatrolRegistrationUpdateReqModel entity);

    /**
     * 删除指定ID的巡堂登记记录
     *
     * @param id 巡堂登记记录ID
     * @return 操作结果对象
     */
    Result<String> deletePatrolRegistration(Long id);

    ResponseEntity<byte[]> exportPatrolRegistrations(PatrolRegistrationQueryReqModel reqModel) throws UnsupportedEncodingException;

    boolean canRemoveRegistrationId(Long registrationId);

    void updateRegistrationContentById(Long registrationId, String registrationContent);
}