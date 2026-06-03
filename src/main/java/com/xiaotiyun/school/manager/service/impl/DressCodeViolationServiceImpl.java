package com.xiaotiyun.school.manager.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.annotations.DataOperationLog;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.enums.DataBusinessTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.DataOperationTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.enums.SchoolLanguageEnum;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.DressCodeViolationDao;
import com.xiaotiyun.school.manager.helper.UserAuthHelper;
import com.xiaotiyun.school.manager.model.entity.DressCodeViolationEntity;
import com.xiaotiyun.school.manager.model.entity.UserEntity;
import com.xiaotiyun.school.manager.model.excel.DressCodeViolationExportEnModel;
import com.xiaotiyun.school.manager.model.excel.DressCodeViolationExportModel;
import com.xiaotiyun.school.manager.model.excel.DressCodeViolationExportPtModel;
import com.xiaotiyun.school.manager.model.req.DressCodeViolationAddReqModel;
import com.xiaotiyun.school.manager.model.req.DressCodeViolationQueryReqModel;
import com.xiaotiyun.school.manager.model.req.DressCodeViolationUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.DressCodeViolationResModel;
import com.xiaotiyun.school.manager.service.DressCodeViolationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 仪表不符登记服务实现类
 */
@Service
public class DressCodeViolationServiceImpl extends ServiceImpl<DressCodeViolationDao, DressCodeViolationEntity> implements DressCodeViolationService {


    @Autowired
    private UserAuthHelper userAuthHelper;
    /**
     * 根据请求参数查询仪表不符登记列表
     *
     * @param reqModel 请求参数对象
     * @return 包含仪表不符登记列表的结果对象
     */
    @Override
    public Result<PageInfo<DressCodeViolationResModel>> listDressCodeViolations(@Validated DressCodeViolationQueryReqModel reqModel) {

        boolean commonUser = userAuthHelper.getCommonUser(reqModel.getUserId(), reqModel.getSchoolId());
        List<Long> classIds = null;
        if(commonUser)
        {
            classIds = userAuthHelper.getUserClassIds(reqModel.getUserId(), reqModel.getSchoolId());
            if(CollectionUtils.isEmpty(classIds))
            {
                PageInfo<DressCodeViolationResModel> pageInfo = new PageInfo<>();
                pageInfo.setList(new ArrayList<>());
                return Result.success(pageInfo);
            }
            reqModel.setClassIds(classIds);
        }
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<DressCodeViolationResModel> list = this.getBaseMapper().page(reqModel);

        return Result.success(new PageInfo<>(list));
    }

    /**
     * 添加新的仪表不符登记记录
     *
     * @param addEntity 仪表不符登记实体对象
     * @param schoolId  学校ID
     * @return 操作结果对象
     */
    @Override
    public Result<String> addDressCodeViolation(List<DressCodeViolationAddReqModel> addEntity, Long schoolId) {
        UserEntity userInfo = (UserEntity) StpUtil.getSession().get("userInfo");
        if (userInfo == null) {
            return Result.failed(ResultCode.UNAUTHORIZED);
        }
        saveBatchByReqModel(addEntity, schoolId, userInfo);
        return Result.success();
    }

    @DataOperationLog(opType = DataOperationTypeEnum.CREATE, businessType = DataBusinessTypeEnum.APPEARANCE_VIOLATION)
    private List<DressCodeViolationEntity> saveBatchByReqModel(List<DressCodeViolationAddReqModel> addEntity, Long schoolId, UserEntity userInfo) {
        List<DressCodeViolationEntity> entities = addEntity.stream().map(e -> {
            DressCodeViolationEntity entity = new DressCodeViolationEntity();
            BeanUtils.copyProperties(e, entity);
            entity.setSchoolId(schoolId);
            entity.setDeleted(0L);
            entity.setRegistrantId(userInfo.getId());
            entity.setRegistrant(userInfo.getUsername());
            return entity;
        }).collect(Collectors.toList());

        saveBatch(entities);
        return entities;
    }

    /**
     * 更新仪表不符登记记录
     *
     * @param updateEntity 仪表不符登记实体对象
     * @return 操作结果对象
     */
    @Override
    @DataOperationLog(opType = DataOperationTypeEnum.UPDATE, businessType = DataBusinessTypeEnum.APPEARANCE_VIOLATION)
    public DressCodeViolationEntity updateDressCodeViolation(DressCodeViolationUpdateReqModel updateEntity) {
        DressCodeViolationEntity entity = new DressCodeViolationEntity();
        BeanUtils.copyProperties(updateEntity, entity);

        updateById(entity);
        return entity;
    }

    /**
     * 删除指定ID的仪表不符登记记录
     *
     * @param id 仪表不符登记记录ID
     * @return 操作结果对象
     */
    @Override
    public Result<String> deleteDressCodeViolation(Long id) {
        removeById(id);
        return Result.success();
    }

    @Override
    public ResponseEntity<byte[]> exportDressCodeViolations(DressCodeViolationQueryReqModel reqModel) throws UnsupportedEncodingException {
        List<DressCodeViolationResModel> resModels = this.getBaseMapper().page(reqModel);
        // 设置导出相应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        // 根据语言，导出不同的语言文档
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode())) {
            List<DressCodeViolationExportEnModel> exportEnModels = resModels.stream()
                    .map(resModel -> {
                        DressCodeViolationExportEnModel exportModel = new DressCodeViolationExportEnModel();
                        BeanUtils.copyProperties(resModel,exportModel);
                        exportModel.setClassName(resModel.getClassGroupName() + resModel.getClassName());
                        return exportModel;
                    })
                    .collect(Collectors.toList());
            EasyExcel.write(outputStream, DressCodeViolationExportEnModel.class)
                    .sheet("Registration of instrument discrepancy")
                    .doWrite(exportEnModels);
            headers.setContentDispositionFormData("attachment", "Registration of instrument discrepancy_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx");
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } else if (currentLanguage.equals(SchoolLanguageEnum.ZH_MO.getCode())) {
            List<DressCodeViolationExportModel> exportEnModels = resModels.stream()
                    .map(resModel -> {
                        DressCodeViolationExportModel exportModel = new DressCodeViolationExportModel();
                        BeanUtils.copyProperties(resModel,exportModel);
                        exportModel.setClassName(resModel.getClassGroupName() + resModel.getClassName());
                        return exportModel;
                    })
                    .collect(Collectors.toList());
            EasyExcel.write(outputStream, DressCodeViolationExportModel.class)
                    .sheet("儀表不符登記")
                    .doWrite(exportEnModels);
            String encodedFileName = URLEncoder.encode("儀表不符登記_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx", "UTF-8");
            headers.setContentDispositionFormData("attachment", encodedFileName);
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } else if (currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode())) {
            List<DressCodeViolationExportPtModel> exportEnModels = resModels.stream()
                    .map(resModel -> {
                        DressCodeViolationExportPtModel exportModel = new DressCodeViolationExportPtModel();
                        BeanUtils.copyProperties(resModel,exportModel);
                        exportModel.setClassName(resModel.getClassGroupName() + resModel.getClassName());
                        return exportModel;
                    })
                    .collect(Collectors.toList());
            EasyExcel.write(outputStream, DressCodeViolationExportPtModel.class)
                    .sheet("Registro de discrepâncias de medidores")
                    .doWrite(exportEnModels);
            headers.setContentDispositionFormData("attachment", "Registro de discrepâncias de medidores_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx");
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        }
        return null;
    }

    @Override
    public boolean canRemoveRemarkId(Long remarkId){
        if(remarkId == null || remarkId == 0L){
            return false;
        }
        return count(Wrappers.<DressCodeViolationEntity>lambdaQuery().eq(DressCodeViolationEntity::getRemarkId,remarkId)) == 0;
    }

    @Override
    public void updateRemarkById(Long remarkId, String remark) {
        if(remarkId == null || remarkId == 0L){
            return ;
        }
        DressCodeViolationEntity entity = new DressCodeViolationEntity();
        entity.setRemark(remark);
        this.update(entity, Wrappers.<DressCodeViolationEntity>lambdaQuery().eq(DressCodeViolationEntity::getRemarkId,remarkId));
    }
}