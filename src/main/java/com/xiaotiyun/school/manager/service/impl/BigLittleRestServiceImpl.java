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
import com.xiaotiyun.school.manager.dao.BigLittleRestDao;
import com.xiaotiyun.school.manager.helper.UserAuthHelper;
import com.xiaotiyun.school.manager.model.entity.BigLittleRestEntity;
import com.xiaotiyun.school.manager.model.entity.UserEntity;
import com.xiaotiyun.school.manager.model.excel.*;
import com.xiaotiyun.school.manager.model.req.BigLittleRestAddReqModel;
import com.xiaotiyun.school.manager.model.req.BigLittleRestQueryReqModel;
import com.xiaotiyun.school.manager.model.req.BigLittleRestUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.BigLittleRestResModel;
import com.xiaotiyun.school.manager.service.BigLittleRestService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 大息小息表現登記服务实现类
 */
@Service
public class BigLittleRestServiceImpl extends ServiceImpl<BigLittleRestDao, BigLittleRestEntity> implements BigLittleRestService {


    @Resource
    private UserAuthHelper userAuthHelper;
    /**
     * 根据请求参数查询大息小息表現登記列表
     *
     * @param reqModel 请求参数对象
     * @return 包含大息小息表現登記列表的结果对象
     */
    @Override
    public Result<PageInfo<BigLittleRestResModel>> listBigLittleRests(BigLittleRestQueryReqModel reqModel) {
        boolean commonUser = userAuthHelper.getCommonUser(reqModel.getUserId(), reqModel.getSchoolId());
        List<Long> classIds = null;
        if(commonUser)
        {
            classIds = userAuthHelper.getUserClassIds(reqModel.getUserId(), reqModel.getSchoolId());
            if(CollectionUtils.isEmpty(classIds))
            {
                return Result.success(PageInfo.emptyPageInfo());
            }
            reqModel.setClassIds(classIds);
        }
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<BigLittleRestResModel> list = this.getBaseMapper().page(reqModel);

        return Result.success(new PageInfo<>(list));
    }

    /**
     * 添加新的大息小息表現登記记录
     *
     * @param addEntity 大息小息表現登記实体对象
     * @return 操作结果对象
     */
    @Override
    public Result<String> addBigLittleRest(List<BigLittleRestAddReqModel> addEntity, Long schoolId) {
        UserEntity userInfo = (UserEntity) StpUtil.getSession().get("userInfo");
        if (userInfo == null) {
            return Result.failed(ResultCode.UNAUTHORIZED);
        }
        saveBatchByReqModel(addEntity, schoolId, userInfo);
        return Result.success();
    }

    @DataOperationLog(opType = DataOperationTypeEnum.CREATE, businessType = DataBusinessTypeEnum.BREAK_BEHAVIOR)
    private List<BigLittleRestEntity> saveBatchByReqModel(List<BigLittleRestAddReqModel> addEntity, Long schoolId, UserEntity userInfo) {
        List<BigLittleRestEntity> entities = addEntity.stream().map(e -> {
            BigLittleRestEntity entity = new BigLittleRestEntity();
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
     * 更新大息小息表現登記记录
     *
     * @param updateEntity 大息小息表現登記实体对象
     * @return 操作结果对象
     */
    @Override
    @DataOperationLog(opType = DataOperationTypeEnum.UPDATE, businessType = DataBusinessTypeEnum.BREAK_BEHAVIOR)
    public BigLittleRestEntity updateBigLittleRest(BigLittleRestUpdateReqModel updateEntity) {
        BigLittleRestEntity entity = new BigLittleRestEntity();
        BeanUtils.copyProperties(updateEntity, entity);

        updateById(entity);
        return entity;
    }

    /**
     * 删除指定ID的大息小息表現登記记录
     *
     * @param id 大息小息表現登記记录ID
     * @return 操作结果对象
     */
    @Override
    public Result<String> deleteBigLittleRest(Long id) {
        removeById(id);
        return Result.success();
    }

    @Override
    public ResponseEntity<byte[]> exportBigLittleRests(BigLittleRestQueryReqModel reqModel) throws UnsupportedEncodingException {
        List<BigLittleRestResModel> resModels = this.getBaseMapper().page(reqModel);
        // 设置导出相应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        // 根据语言，导出不同的语言文档
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode())) {
            List<BigLittleRestExportEnModel> exportEnModels = resModels.stream()
                    .map(resModel -> {
                        BigLittleRestExportEnModel exportModel = new BigLittleRestExportEnModel();
                        BeanUtils.copyProperties(resModel,exportModel);
                        exportModel.setClassName(resModel.getClassGroupName() + resModel.getClassName());
                        return exportModel;
                    })
                    .collect(Collectors.toList());
            EasyExcel.write(outputStream, BigLittleRestExportEnModel.class)
                    .sheet("Big/small rest performance register")
                    .doWrite(exportEnModels);
            headers.setContentDispositionFormData("attachment", "Big/small rest performance register_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx");
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } else if (currentLanguage.equals(SchoolLanguageEnum.ZH_MO.getCode())) {
            List<BigLittleRestExportModel> exportEnModels = resModels.stream()
                    .map(resModel -> {
                        BigLittleRestExportModel exportModel = new BigLittleRestExportModel();
                        BeanUtils.copyProperties(resModel,exportModel);
                        exportModel.setClassName(resModel.getClassGroupName() + resModel.getClassName());
                        return exportModel;
                    })
                    .collect(Collectors.toList());
            EasyExcel.write(outputStream, BigLittleRestExportModel.class)
                    .sheet("大小息表現登記")
                    .doWrite(exportEnModels);
            String encodedFileName = URLEncoder.encode("大小息表現登記_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx", "UTF-8");
            headers.setContentDispositionFormData("attachment", encodedFileName);
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } else if (currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode())) {
            List<BigLittleRestExportPtModel> exportEnModels = resModels.stream()
                    .map(resModel -> {
                        BigLittleRestExportPtModel exportModel = new BigLittleRestExportPtModel();
                        BeanUtils.copyProperties(resModel,exportModel);
                        exportModel.setClassName(resModel.getClassGroupName() + resModel.getClassName());
                        return exportModel;
                    })
                    .collect(Collectors.toList());
            EasyExcel.write(outputStream, BigLittleRestExportPtModel.class)
                    .sheet("Registro de desempenho de juros de tamanho")
                    .doWrite(exportEnModels);
            headers.setContentDispositionFormData("attachment", "Registro de desempenho de juros de tamanho_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx");
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        }
        return null;
    }

    @Override
    public boolean canRemoveRegistrationId(Long registrationId){
        if(registrationId == null || registrationId == 0L){
            return false;
        }
        return count(Wrappers.<BigLittleRestEntity>lambdaQuery().eq(BigLittleRestEntity::getRegistrationId,registrationId)) == 0;
    }

    @Override
    public void updateRegistrationContentById(Long registrationId, String registrationContent) {
        if(registrationId == null || registrationId == 0L){
            return ;
        }
        BigLittleRestEntity entity = new BigLittleRestEntity();
        entity.setRegistrationContent(registrationContent);
        this.update(entity, Wrappers.<BigLittleRestEntity>lambdaQuery().eq(BigLittleRestEntity::getRegistrationId,registrationId));
    }
}