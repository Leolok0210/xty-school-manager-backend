package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.entity.DressCodeViolationEntity;
import com.xiaotiyun.school.manager.model.req.DressCodeViolationAddReqModel;
import com.xiaotiyun.school.manager.model.req.DressCodeViolationQueryReqModel;
import com.xiaotiyun.school.manager.model.req.DressCodeViolationUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.DressCodeViolationResModel;
import org.springframework.http.ResponseEntity;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 仪表不符登记服务接口
 */
public interface DressCodeViolationService extends IService<DressCodeViolationEntity> {
    /**
     * 根据请求参数查询仪表不符登记列表
     *
     * @param reqModel 请求参数对象
     * @return 包含仪表不符登记列表的结果对象
     */
    Result<PageInfo<DressCodeViolationResModel>> listDressCodeViolations(DressCodeViolationQueryReqModel reqModel);

    /**
     * 添加新的仪表不符登记记录
     *
     * @param entity 仪表不符登记实体对象
     * @param schoolId 学校ID
     * @return 操作结果对象
     */
    Result<String> addDressCodeViolation(List<DressCodeViolationAddReqModel> entity, Long schoolId);

    /**
     * 更新仪表不符登记记录
     *
     * @param entity 仪表不符登记实体对象
     * @return 操作结果对象
     */
    DressCodeViolationEntity updateDressCodeViolation(DressCodeViolationUpdateReqModel entity);

    /**
     * 删除指定ID的仪表不符登记记录
     *
     * @param id 仪表不符登记记录ID
     * @return 操作结果对象
     */
    Result<String> deleteDressCodeViolation(Long id);

    ResponseEntity<byte[]> exportDressCodeViolations(DressCodeViolationQueryReqModel reqModel) throws UnsupportedEncodingException;

    boolean canRemoveRemarkId(Long remakeId);

    void updateRemarkById(Long remarkId, String remark);
}