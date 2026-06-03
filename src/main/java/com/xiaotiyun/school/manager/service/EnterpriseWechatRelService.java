package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.basic.enums.EnterpriseWxChatTypeEnum;
import com.xiaotiyun.school.manager.model.entity.EnterpriseWechatRelEntity;
import com.xiaotiyun.school.manager.model.req.EnterpriseWechatRelCheckReqModel;
import com.xiaotiyun.school.manager.model.res.EnterpriseWechatRelResModel;

import java.util.List;
import java.util.Map;

/**
 * 企业微信关联关系表服务接口
 */
public interface EnterpriseWechatRelService extends IService<EnterpriseWechatRelEntity> {


    /**
     * 企业微信存在的情况
     * @param entities
     * @param type
     * @return
     */
    boolean saveBatchExist(List<EnterpriseWechatRelCheckReqModel> entities, EnterpriseWxChatTypeEnum type);


    /**
     * 企业微信不存在的情况
     * @param entities
     * @param type
     * @return
     */
    boolean saveBatchNoExist(List<EnterpriseWechatRelCheckReqModel> entities, EnterpriseWxChatTypeEnum type);

    EnterpriseWechatRelEntity get(Long schoolId, Integer type,Long relId,String schoolYear);

    Map<Long,EnterpriseWechatRelEntity> list(Long schoolId, Integer type, List< Long> relIds,String schoolYear);

    List<EnterpriseWechatRelEntity> listByWxIds(Long schoolId, Integer type, List<String> wxIds,String schoolYear);


    List<EnterpriseWechatRelResModel> list(Long schoolId,Integer type, Long groupId, Long classId,String schoolYear,Integer  department);
}