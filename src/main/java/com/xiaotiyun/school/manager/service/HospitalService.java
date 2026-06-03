package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.HospitalEntity;
import com.xiaotiyun.school.manager.model.req.HospitalAddReqModel;
import com.xiaotiyun.school.manager.model.req.HospitalQueryReqModel;
import com.xiaotiyun.school.manager.model.res.HospitalResModel;

public interface HospitalService extends IService<HospitalEntity> {
    /**
     * 新增医院
     */
    void addHospital(HospitalAddReqModel reqModel, Long schoolId);

    /**
     * 修改医院
     */
    void updateHospital(Long id, HospitalAddReqModel reqModel, Long schoolId);

    /**
     * 删除医院
     */
    void deleteHospital(Long id, Long schoolId);

    /**
     * 查看医院详情
     */
    HospitalResModel getHospitalDetail(Long id, Long schoolId);

    /**
     * 查询医院列表
     */
    PageInfo<HospitalResModel> getHospitalList(HospitalQueryReqModel reqModel, Long schoolId);
} 