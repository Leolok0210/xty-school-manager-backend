package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.entity.BigLittleRestEntity;
import com.xiaotiyun.school.manager.model.req.BigLittleRestAddReqModel;
import com.xiaotiyun.school.manager.model.req.BigLittleRestQueryReqModel;
import com.xiaotiyun.school.manager.model.req.BigLittleRestUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.BigLittleRestResModel;
import org.springframework.http.ResponseEntity;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 大息小息表現登記服务接口
 */
public interface BigLittleRestService extends IService<BigLittleRestEntity> {
    /**
     * 根据请求参数查询大息小息表現登記列表
     *
     * @param reqModel 请求参数对象
     * @return 包含大息小息表現登記列表的结果对象
     */
    Result<PageInfo<BigLittleRestResModel>> listBigLittleRests(BigLittleRestQueryReqModel reqModel);

    /**
     * 添加新的大息小息表現登記记录
     *
     * @param entity 大息小息表現登記实体对象
     * @param schoolId 学校ID
     * @return 操作结果对象
     */
    Result<String> addBigLittleRest(List<BigLittleRestAddReqModel> entity, Long schoolId);

    /**
     * 更新大息小息表現登記记录
     *
     * @param entity 大息小息表現登記实体对象
     * @return 操作结果对象
     */
    BigLittleRestEntity updateBigLittleRest(BigLittleRestUpdateReqModel entity);

    /**
     * 删除指定ID的大息小息表現登記记录
     *
     * @param id 大息小息表現登記记录ID
     * @return 操作结果对象
     */
    Result<String> deleteBigLittleRest(Long id);

    ResponseEntity<byte[]> exportBigLittleRests(BigLittleRestQueryReqModel reqModel) throws UnsupportedEncodingException;

    boolean canRemoveRegistrationId(Long registrationId);

    void updateRegistrationContentById(Long registrationId, String registrationContent);
}