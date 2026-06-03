package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.enums.FileTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.ImportTaskTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.SchoolLanguageEnum;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.ImportRecordMapper;
import com.xiaotiyun.school.manager.handler.ExportFileHandler;
import com.xiaotiyun.school.manager.model.dto.ImportRecordSaveDTO;
import com.xiaotiyun.school.manager.model.entity.ImportRecordEntity;
import com.xiaotiyun.school.manager.model.entity.ImportTaskEntity;
import com.xiaotiyun.school.manager.model.excel.*;
import com.xiaotiyun.school.manager.model.req.ImportRecordPageReqModel;
import com.xiaotiyun.school.manager.model.res.ImportRecordResModel;
import com.xiaotiyun.school.manager.service.ImportRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ImportRecordServiceImpl extends ServiceImpl<ImportRecordMapper, ImportRecordEntity> implements ImportRecordService {
    @Resource
    private ExportFileHandler exportFileHandler;


    @Resource
    private LanguageUtil languageUtil;

    @Override
    public PageInfo<ImportRecordResModel> page(ImportRecordPageReqModel reqModel) {
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());

        // 1. 构建查询条件
        LambdaQueryWrapper<ImportRecordEntity> wrapper = new LambdaQueryWrapper<ImportRecordEntity>()
                .eq(ImportRecordEntity::getTaskId, reqModel.getTaskId())
                .eq(ImportRecordEntity::getDeleted, 0L);

        // 2. 查询数据
        List<ImportRecordEntity> list = list(wrapper);
        PageInfo<ImportRecordEntity> pageInfo = new PageInfo<>(list);
        // 3. 转换返回结果
        List<ImportRecordResModel> resList = list.stream().map((ImportRecordEntity entity) -> {
            ImportRecordResModel resModel = new ImportRecordResModel();
            BeanUtils.copyProperties(entity, resModel);
            return resModel;
        }).collect(Collectors.toList());
        PageInfo<ImportRecordResModel> result = new PageInfo<>(resList);
        result.setTotal(pageInfo.getTotal());
        result.setPages(pageInfo.getPages());
        return result;
    }

    @Override
    public void save(List<ImportRecordSaveDTO> dtoList) {
        if (CollectionUtils.isNotEmpty(dtoList)) {
            List<ImportRecordEntity> entityList = new ArrayList<>();
            dtoList.forEach((ImportRecordSaveDTO dto) -> {
                ImportRecordEntity entity = BeanConvertUtil.convert(dto, ImportRecordEntity.class);
                entityList.add(entity);
            });
            if (CollectionUtils.isNotEmpty(entityList)) {
                this.saveBatch(entityList);
            }
        }
    }

    @Override
    public String recordExport(Long schoolId, ImportTaskEntity importTask, ImportRecordPageReqModel reqModel) {
        // 1. 构建查询条件
        LambdaQueryWrapper<ImportRecordEntity> wrapper = new LambdaQueryWrapper<ImportRecordEntity>()
                .eq(ImportRecordEntity::getTaskId, reqModel.getTaskId())
                .eq(ImportRecordEntity::getDeleted, 0L);
        // 2. 查询数据
        List<ImportRecordEntity> list = list(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            String fileName = "失败原因导出.xlsx";
            String currentLanguage = LanguageUtil.getCurrentLanguage();
            
            if (importTask.getType() == ImportTaskTypeEnum.STUDENT_IMAGE.getCode()) {
                if (currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode())) {
                    fileName = "imageImportFailureReasons.xlsx";
                    List<ImageRecordExportEnModel> exportEnModels = list.stream()
                            .map(item -> {
                                ImageRecordExportEnModel resModel = new ImageRecordExportEnModel();
                                BeanUtils.copyProperties(item, resModel);
                                return resModel;
                            }).collect(Collectors.toList());
                    return exportFileHandler.doExportExcel(exportEnModels, fileName, ImageRecordExportEnModel.class, FileTypeEnum.EXPORT, schoolId);
                } else if (currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode())) {
                    fileName = "motivosDeErroDeImportacaoDeImagem.xlsx";
                    List<ImageRecordExportPtModel> exportPtModels = list.stream()
                            .map(item -> {
                                ImageRecordExportPtModel resModel = new ImageRecordExportPtModel();
                                BeanUtils.copyProperties(item, resModel);
                                return resModel;
                            }).collect(Collectors.toList());
                    return exportFileHandler.doExportExcel(exportPtModels, fileName, ImageRecordExportPtModel.class, FileTypeEnum.EXPORT, schoolId);
                } else {
                    return exportFileHandler.doExportExcel(handleImageExportData(list), fileName, ImageRecordExportModel.class, FileTypeEnum.EXPORT, schoolId);
                }
            } else {
                if (currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode())) {
                    fileName = "importFailureReasons.xlsx";
                    List<RecordExportEnModel> exportEnModels = list.stream()
                            .map(item -> {
                                RecordExportEnModel resModel = new RecordExportEnModel();
                                BeanUtils.copyProperties(item, resModel);
                                return resModel;
                            }).collect(Collectors.toList());
                    return exportFileHandler.doExportExcel(exportEnModels, fileName, RecordExportEnModel.class, FileTypeEnum.EXPORT, schoolId);
                } else if (currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode())) {
                    fileName = "motivosDeErroDeImportacao.xlsx";
                    List<RecordExportPtModel> exportPtModels = list.stream()
                            .map(item -> {
                                RecordExportPtModel resModel = new RecordExportPtModel();
                                BeanUtils.copyProperties(item, resModel);
                                return resModel;
                            }).collect(Collectors.toList());
                    return exportFileHandler.doExportExcel(exportPtModels, fileName, RecordExportPtModel.class, FileTypeEnum.EXPORT, schoolId);
                } else {
                    return exportFileHandler.doExportExcel(handleExportData(list), fileName, RecordExportModel.class, FileTypeEnum.EXPORT, schoolId);
                }
            }
        }
        return null;
    }

    private List<ImageRecordExportModel> handleImageExportData(List<ImportRecordEntity> exportDTOS) {
        List<ImageRecordExportModel> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(exportDTOS)) {
            exportDTOS.forEach(studentExportDTO -> {
                ImageRecordExportModel studentExportModel = new ImageRecordExportModel();
                BeanUtils.copyProperties(studentExportDTO, studentExportModel);
                result.add(studentExportModel);
            });
        }
        return result;
    }

    private List<RecordExportModel> handleExportData(List<ImportRecordEntity> exportDTOS) {
        List<RecordExportModel> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(exportDTOS)) {
            exportDTOS.forEach(studentExportDTO -> {
                RecordExportModel studentExportModel = new RecordExportModel();
                BeanUtils.copyProperties(studentExportDTO, studentExportModel);
                result.add(studentExportModel);
            });
        }
        return result;
    }
}