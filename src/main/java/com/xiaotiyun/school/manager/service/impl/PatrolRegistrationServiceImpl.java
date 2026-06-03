package com.xiaotiyun.school.manager.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.annotations.DataOperationLog;
import com.xiaotiyun.school.manager.basic.enums.DataBusinessTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.DataOperationTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.enums.SchoolLanguageEnum;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.PatrolRegistrationDao;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.helper.UserAuthHelper;
import com.xiaotiyun.school.manager.model.entity.PatrolRegistrationEntity;
import com.xiaotiyun.school.manager.model.entity.UserEntity;
import com.xiaotiyun.school.manager.model.excel.*;
import com.xiaotiyun.school.manager.model.req.PatrolRegistrationAddReqModel;
import com.xiaotiyun.school.manager.model.req.PatrolRegistrationQueryReqModel;
import com.xiaotiyun.school.manager.model.req.PatrolRegistrationUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.PatrolRegistrationResModel;
import com.xiaotiyun.school.manager.service.PatrolRegistrationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 巡堂登记服务实现类
 */
@Service
public class PatrolRegistrationServiceImpl extends ServiceImpl<PatrolRegistrationDao, PatrolRegistrationEntity> implements PatrolRegistrationService {



    @Autowired
    private UserAuthHelper userAuthHelper;
    /**
     * 根据请求参数查询巡堂登记列表
     *
     * @param reqModel 请求参数对象
     * @return 包含巡堂登记列表的结果对象
     */
    @Override
    public Result<PageInfo<PatrolRegistrationResModel>> listPatrolRegistrations(PatrolRegistrationQueryReqModel reqModel) {
        boolean commonUser = userAuthHelper.getCommonUser(reqModel.getUserId(), reqModel.getSchoolId());
        List<Long> classIds = null;
        if(commonUser)
        {
            classIds = userAuthHelper.getUserClassIds(reqModel.getUserId(), reqModel.getSchoolId());
            if(CollectionUtils.isEmpty(classIds))
            {
                PageInfo<PatrolRegistrationResModel> pageInfo = new PageInfo<>();
                pageInfo.setList(new ArrayList<>());
                return Result.success(pageInfo);
            }
            reqModel.setClassIds(classIds);
        }
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<PatrolRegistrationResModel> list = this.getBaseMapper().page(reqModel);

        return Result.success(new PageInfo<>(list));
    }

    /**
     * 添加新的巡堂登记记录
     *
     * @param addEntity 巡堂登记实体对象
     * @return 操作结果对象
     */
    @Override
    public Result<String> addPatrolRegistration(List<PatrolRegistrationAddReqModel> addEntity, Long schoolId) {
        UserEntity userInfo = (UserEntity) StpUtil.getSession().get("userInfo");
        if (userInfo == null) {
            return Result.failed(ResultCode.UNAUTHORIZED);
        }
        saveBatchByReqModel(addEntity, schoolId, userInfo);
        return Result.success();
    }

    @DataOperationLog(opType = DataOperationTypeEnum.CREATE, businessType = DataBusinessTypeEnum.PATROL_RECORD)
    private List<PatrolRegistrationEntity> saveBatchByReqModel(List<PatrolRegistrationAddReqModel> addEntity, Long schoolId, UserEntity userInfo) {
        List<PatrolRegistrationEntity> entities = addEntity.stream().map(e -> {
            PatrolRegistrationEntity entity = new PatrolRegistrationEntity();
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
     * 更新巡堂登记记录
     *
     * @param updateEntity 巡堂登记实体对象
     * @return 操作结果对象
     */
    @Override
    @DataOperationLog(opType = DataOperationTypeEnum.UPDATE, businessType = DataBusinessTypeEnum.PATROL_RECORD)
    public PatrolRegistrationEntity updatePatrolRegistration(PatrolRegistrationUpdateReqModel updateEntity) {
        PatrolRegistrationEntity entity = new PatrolRegistrationEntity();
        BeanUtils.copyProperties(updateEntity, entity);

        updateById(entity);
        return entity;
    }

    /**
     * 删除指定ID的巡堂登记记录
     *
     * @param id 巡堂登记记录ID
     * @return 操作结果对象
     */
    @Override
    public Result<String> deletePatrolRegistration(Long id) {
        removeById(id);
        return Result.success();
    }

    @Override
    public ResponseEntity<byte[]> exportPatrolRegistrations(PatrolRegistrationQueryReqModel reqModel) throws UnsupportedEncodingException {
        List<PatrolRegistrationResModel> resModels = this.getBaseMapper().page(reqModel);
        // 设置导出相应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        // 根据语言，导出不同的语言文档
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode())) {
            List<PatrolRegistrationExportEnModel> exportEnModels = resModels.stream()
                    .map(resModel -> {
                        PatrolRegistrationExportEnModel exportModel = new PatrolRegistrationExportEnModel();
                        BeanUtils.copyProperties(resModel,exportModel);
                        exportModel.setClassName(resModel.getClassGroupName() + resModel.getClassName());
                        return exportModel;
                    })
                    .collect(Collectors.toList());
            EasyExcel.write(outputStream, PatrolRegistrationExportEnModel.class)
                    .sheet("Patrol record")
                    .doWrite(exportEnModels);
            headers.setContentDispositionFormData("attachment", "Patrol record_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx");
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } else if (currentLanguage.equals(SchoolLanguageEnum.ZH_MO.getCode())) {
            List<PatrolRegistrationExportModel> exportEnModels = resModels.stream()
                    .map(resModel -> {
                        PatrolRegistrationExportModel exportModel = new PatrolRegistrationExportModel();
                        BeanUtils.copyProperties(resModel,exportModel);
                        exportModel.setClassName(resModel.getClassGroupName() + resModel.getClassName());
                        return exportModel;
                    })
                    .collect(Collectors.toList());
            EasyExcel.write(outputStream, PatrolRegistrationExportModel.class)
                    .sheet("巡堂記錄")
                    .doWrite(exportEnModels);
            String encodedFileName = URLEncoder.encode("巡堂記錄_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx", "UTF-8");
            headers.setContentDispositionFormData("attachment", encodedFileName);
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } else if (currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode())) {
            List<PatrolRegistrationExportPtModel> exportEnModels = resModels.stream()
                    .map(resModel -> {
                        PatrolRegistrationExportPtModel exportModel = new PatrolRegistrationExportPtModel();
                        BeanUtils.copyProperties(resModel,exportModel);
                        exportModel.setClassName(resModel.getClassGroupName() + resModel.getClassName());
                        return exportModel;
                    })
                    .collect(Collectors.toList());
            EasyExcel.write(outputStream, PatrolRegistrationExportPtModel.class)
                    .sheet("Recordes de patrulha")
                    .doWrite(exportEnModels);
            headers.setContentDispositionFormData("attachment", "Recordes de patrulha_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx");
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        }
        return null;
    }

    @Override
    public boolean canRemoveRegistrationId(Long registrationId){
        if(registrationId == null || registrationId == 0L){
            return false;
        }
        return count(Wrappers.<PatrolRegistrationEntity>lambdaQuery().eq(PatrolRegistrationEntity::getRegistrationId,registrationId)) == 0;
    }

    @Override
    public void updateRegistrationContentById(Long registrationId, String registrationContent) {
        if(registrationId == null || registrationId == 0L){
            return ;
        }
        PatrolRegistrationEntity entity = new PatrolRegistrationEntity();
        entity.setRegistrationContent(registrationContent);
        this.update(entity, Wrappers.<PatrolRegistrationEntity>lambdaQuery().eq(PatrolRegistrationEntity::getRegistrationId,registrationId));
    }
}