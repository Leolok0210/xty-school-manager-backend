package com.xiaotiyun.school.manager.service.impl;

import cn.hutool.core.io.FileUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.*;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.*;
import com.xiaotiyun.school.manager.config.FileConfig;
import com.xiaotiyun.school.manager.dao.StudentMapper;
import com.xiaotiyun.school.manager.dao.SysClassDao;
import com.xiaotiyun.school.manager.handler.ExportFileHandler;
import com.xiaotiyun.school.manager.helper.UserAuthHelper;
import com.xiaotiyun.school.manager.listener.StudentImportEnUsListener;
import com.xiaotiyun.school.manager.listener.StudentImportPtPtListener;
import com.xiaotiyun.school.manager.listener.StudentImportZhTwListener;
import com.xiaotiyun.school.manager.model.dto.*;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.excel.StudentImportEnUsModel;
import com.xiaotiyun.school.manager.model.excel.StudentImportModel;
import com.xiaotiyun.school.manager.model.excel.StudentImportPtPtModel;
import com.xiaotiyun.school.manager.model.excel.StudentImportZhTwModel;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.*;
import com.xiaotiyun.school.manager.service.*;
import com.xiaotiyun.school.manager.support.ReportSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, StudentEntity> implements StudentService {
    @Resource
    private StudentFamilyService studentFamilyService;
    @Resource
    private ImportTaskService importTaskService;
    @Resource
    private ImportRecordService importRecordService;
    @Resource
    private SysClassDao sysClassDao;
    @Resource
    private SchoolService schoolService;

    @Resource
    private SysClassUpgradeRelService sysClassUpgradeRelService;
    @Resource
    private StudentUsuallyTaskService studentUsuallyTaskService;
    @Resource
    private StudentExamTaskService studentExamTaskService;
    @Resource
    private StudentGraduateExamTaskService studentGraduateExamTaskService;
    @Resource
    private SystemSettingService systemSettingService;
    @Resource
    private ExportFileHandler exportFileHandler;
    @Resource
    private FileConfig fileConfig;
    @Resource
    private LanguageUtil languageUtil;
    @Resource
    private GradeGroupService gradeGroupService;
    @Resource
    private ExportHeaderService exportHeaderService;
    @Resource
    ReportSupport reportSupport;
    @Resource
    private StudentEnrollService studentEnrollService;
    @Resource
    private StudentMedicalAttentionService studentMedicalAttentionService;
    @Resource
    private CrossBorderStudentService crossBorderStudentService;
    @Resource
    private StudentDateRecordService studentDateRecordService;
    @Resource
    private HospitalService hospitalService;
    private static List<String> fileSuffix = new ArrayList<>();
    private static Queue<ImportImageTaskDTO> imageQueueList = new LinkedBlockingQueue<>();
    private static final ExecutorService studentImportPool = new ThreadPoolExecutor(10, 15, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100));


    @Resource
    private UserAuthHelper userAuthHelper;


    @Resource
    private SchoolWeixinRelevanceService schoolWeixinRelevanceService;


    @Resource
    private StudentParentService studentParentService;



    @Resource
    private EnterpriseWechatService enterpriseWechatService;


    @Resource
    private EnterpriseWechatRelService enterpriseWechatRelService;



    @PostConstruct
    public void init() {
        fileSuffix.add("jpg");
        fileSuffix.add("jpeg");
        fileSuffix.add("png");
        fileSuffix.add("gif");
    }

    @Override
    public PageInfo<StudentPageResModel> page(StudentPageReqModel reqModel) {
        boolean commonUser = userAuthHelper.getCommonUser(reqModel.getUserId(), reqModel.getSchoolId());
        List<Long> classIds = null;
        if(commonUser)
        {
            classIds = userAuthHelper.getUserClassIds(reqModel.getUserId(), reqModel.getSchoolId());
            if(CollectionUtils.isEmpty(classIds))
            {
                PageInfo<StudentPageResModel> pageInfo = new PageInfo<>();
                pageInfo.setList(new ArrayList<>());
                return pageInfo;
            }
            reqModel.setClassIds(classIds);
        }
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<StudentPageResModel> list = this.getBaseMapper().page(reqModel);
        return new PageInfo<>(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(StudentSaveReqModel reqModel) {
        // 校验学生编号是否重复
        checkStudentNoDuplicate(null, reqModel.getSchoolId(), reqModel.getStudentNo());
        // 校验教青局编号是否重复
        checkEducationNoDuplicate(null, reqModel.getSchoolId(), reqModel.getEducationNo());
        // 校验座位号是否重复
        checkSeatNoDuplicate(null, reqModel.getClassId(), reqModel.getSeatNo());

        StudentEntity entity = BeanConvertUtil.convert(reqModel, StudentEntity.class);
        this.save(entity);
        if (reqModel.getFamilyInfo() != null) {
            //保存家庭信息
            studentFamilyService.save(entity.getId(), reqModel.getFamilyInfo());
        }
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, StudentSaveReqModel reqModel) {
        StudentEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.STUDENT_FAMILY_EXISTS);
        }
        // 校验学生编号是否重复
        checkStudentNoDuplicate(id, reqModel.getSchoolId(), reqModel.getStudentNo());
        // 校验教青局编号是否重复
        checkEducationNoDuplicate(id, reqModel.getSchoolId(), reqModel.getEducationNo());
        // 校验座位号是否重复
        checkSeatNoDuplicate(id, reqModel.getClassId(), reqModel.getSeatNo());
        // 使用BeanUtils替代BeanConvertUtil
        BeanUtils.copyProperties(reqModel, entity);
        this.updateById(entity);
        if (reqModel.getFamilyInfo() != null) {
            //修改家庭信息
            studentFamilyService.update(entity.getId(), reqModel.getFamilyInfo());
        }
    }

    @Override
    public StudentResModel info(Long id) {
        StudentEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.STUDENT_FAMILY_EXISTS);
        }
        StudentResModel reqModel = BeanConvertUtil.convert(entity, StudentResModel.class);
        StudentFamilyResModel familyResModel = studentFamilyService.getByStudentId(id);
        if (familyResModel != null) {
            reqModel.setFamilyInfo(familyResModel);
        }
        SchoolEntity school = schoolService.getById(reqModel.getSchoolId());
        if (school != null) {
            reqModel.setSchoolName(school.getName());
        }
        StudentSchoolConfigResModel schoolConfig = new StudentSchoolConfigResModel();
        reqModel.setSchoolConfig(schoolConfig);
        List<SystemSettingEntity> list = systemSettingService.list(Wrappers.<SystemSettingEntity>lambdaQuery()
                .eq(SystemSettingEntity::getSettingKey, SystemSettingKeyEnum.USUAL_TYPE_REL_SUB.getKey())
                .eq(SystemSettingEntity::getSchoolId, reqModel.getSchoolId()));
        if (!CollectionUtils.isEmpty(list)) {
            reqModel.getSchoolConfig().setIsUsuallyTypeRelSubject(Integer.parseInt(list.get(0).getSettingValue()));
        } else {
            reqModel.getSchoolConfig().setIsUsuallyTypeRelSubject(0);
        }
        SysClass sysClass = sysClassDao.selectById(reqModel.getClassId());
        if (sysClass != null) {
            reqModel.setGroupId(sysClass.getGradeGroup());
            GradeGroup gradeGroup = gradeGroupService.getById(sysClass.getGradeGroup());
            if (gradeGroup != null) {
                reqModel.setGroupName(gradeGroup.getGradeGroupName());
            }
            reqModel.setClassName(sysClass.getClassName());
        }
        return reqModel;
    }

    @Override
    public StudentResModel getStudentById(Long id) {
        StudentEntity entity = this.getById(id);
        if (entity == null) {
            return null;
        }
        return BeanConvertUtil.convert(entity, StudentResModel.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        StudentEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.STUDENT_FAMILY_EXISTS);
        }

        this.removeById(id);
        StudentFamilyResModel studentFamilyInfo = studentFamilyService.getByStudentId(id);
        if (studentFamilyInfo != null) {
            studentFamilyService.removeById(studentFamilyInfo.getId());
        }
    }

    @Override
    public Long importStudent(String schoolYear, Long schoolId, MultipartFile file) {
        if (schoolId == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.NO_SCHOOL_FILE_CONTENT_EMPTY));
        }
        // 获取学校语言设置信息
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        if (StringUtils.isEmpty(currentLanguage)) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.GET_SCHOOL_LANGUAGE_SETTING_ERROR_LANGUAGE_CONFIG_ERROR));
        }
        SchoolLanguageEnum languageEnum = SchoolLanguageEnum.getDefValue(currentLanguage);
        if (languageEnum == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.GET_SCHOOL_LANGUAGE_SETTING_ERROR_LANGUAGE_CONFIG_ERROR));
        }
        // 读取Excel文件
        List<StudentImportModel> list = readExcelData(file, languageEnum);
        if (CollectionUtils.isNotEmpty(list)) {
            // 创建导入任务
            ImportTaskEntity task = new ImportTaskEntity();
            task.setSchoolId(schoolId);
            task.setFileName(file.getOriginalFilename());
            task.setType(ImportTaskTypeEnum.STUDENT_INFO.getCode());
            task.setTotalCount(0);
            task.setSuccessCount(0);
            task.setFailCount(0);
            importTaskService.save(task);
            log.info("当前使用的语言是:{}", LanguageUtil.getCurrentLanguage());
            CompletableFuture.runAsync(() -> {
                languageUtil.setLanguage(languageEnum.getCode());
                log.info("当前使用的语言是:{}", LanguageUtil.getCurrentLanguage());
                handleStudentImport(task, list, schoolId, schoolYear, languageEnum);
                LanguageUtil.clearLanguage();
            }, studentImportPool).whenComplete((res, ex) -> {
                if (ex != null) {
                    log.error("导入学生信息任务执行结束taskId=【{}】异常={}",task.getId(),ex);
                } else {
                    log.info("导入学生信息完成，任务ID={}",task.getId());
                }
                task.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
                importTaskService.updateById(task);
            });
            log.info("当前使用的语言是:{}", LanguageUtil.getCurrentLanguage());
            return task.getId();
        }
        return null;
    }

    private void handleStudentImport(ImportTaskEntity task, List<StudentImportModel> list, Long schoolId, String schoolYear, SchoolLanguageEnum schoolLanguageEnum) {
        if (CollectionUtils.isNotEmpty(list)) {
            task.setTotalCount(list.size());
            task.setStatus(ImportTaskStatusEnum.IN_PROCESS.getCode());
            importTaskService.updateById(task);
            log.info("开始处理数据导入...");
            Iterator<StudentImportModel> iterator = list.iterator();
            //每500个处理一次
            List<StudentImportModel> batchExcelLine = new ArrayList<>(500);
            Map<String, List<StudentImportModel>> studentNoMap = new HashMap<>();// 校验本次表格中的重复
            Map<String, List<StudentEntity>> oldStudentNoMap = new HashMap<>();// 校验本学校中的重复
            Map<String, List<StudentImportModel>> educationNoMap = new HashMap<>();// 校验本次表格中的重复
            Map<String, List<StudentEntity>> oldEducationNoMap = new HashMap<>();// 校验本学校中的重复
            Map<String, List<StudentImportModel>> seatNoMap = new HashMap<>();
            boolean existWx = schoolWeixinRelevanceService.exist(schoolId);
            if (CollectionUtils.isNotEmpty(list)) {
                studentNoMap = list.stream().filter(studentImportModel -> StringUtils.isNotBlank(studentImportModel.getStudentNo())).collect(Collectors.groupingBy(StudentImportModel::getStudentNo));
                seatNoMap = list.stream().filter(studentImportModel -> StringUtils.isNotBlank(studentImportModel.getSeatNo())).collect(Collectors.groupingBy(
                        studentImportModel -> studentImportModel.getGradeGroup() + "_" + studentImportModel.getClassName() + "_" + studentImportModel.getSeatNo()));
                educationNoMap = list.stream().filter(studentImportModel -> StringUtils.isNotBlank(studentImportModel.getEducationNo())).collect(Collectors.groupingBy(StudentImportModel::getEducationNo));
            }
            // 查询学校的所有学生证编号
            List<StudentEntity> studentEntities = this.list(Wrappers.<StudentEntity>lambdaQuery()
                    .eq(StudentEntity::getSchoolId, schoolId));
            if (CollectionUtils.isNotEmpty(studentEntities)) {
                oldStudentNoMap = studentEntities.stream().filter(studentEntity -> StringUtils.isNotBlank(studentEntity.getStudentNo())).collect(Collectors.groupingBy(StudentEntity::getStudentNo));
                oldEducationNoMap = studentEntities.stream().filter(studentEntity -> StringUtils.isNotBlank(studentEntity.getEducationNo())).collect(Collectors.groupingBy(StudentEntity::getEducationNo));
            }
            int correctCount = 0;
            List<ImportRecordSaveDTO> importRecordSaveDTOS = new ArrayList<>();
            while (iterator.hasNext()) {
                StudentImportModel studentImportModel = iterator.next();
                batchExcelLine.add(studentImportModel);
                if (batchExcelLine.size() >= 500) {
                    //处理数据 插入数据库
                    correctCount += processBatchExcelLine(importRecordSaveDTOS, batchExcelLine, schoolId, schoolYear,
                            studentNoMap, oldStudentNoMap, educationNoMap, oldEducationNoMap, seatNoMap, schoolLanguageEnum,existWx);
                    batchExcelLine.clear();
                }
            }
            if (!batchExcelLine.isEmpty()) {
                correctCount += processBatchExcelLine(importRecordSaveDTOS, batchExcelLine, schoolId, schoolYear,
                        studentNoMap, oldStudentNoMap, educationNoMap, oldEducationNoMap, seatNoMap, schoolLanguageEnum,existWx);
                batchExcelLine.clear();
            }
            //当前处理进度写入数据库
            task.setSuccessCount(correctCount);
            task.setFailCount(list.size() - correctCount);
            task.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
            importTaskService.updateById(task);
            //错误信息写入数据库
            if (CollectionUtils.isNotEmpty(importRecordSaveDTOS)) {
                List<ImportRecordEntity> entityList = importRecordSaveDTOS.stream().map(dto -> {
                    ImportRecordEntity importRecordEntity = new ImportRecordEntity();
                    BeanUtils.copyProperties(dto, importRecordEntity);
                    importRecordEntity.setTaskId(task.getId());
                    return importRecordEntity;
                }).collect(Collectors.toList());
                importRecordService.saveBatch(entityList);
            }
        } else {
            task.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
            importTaskService.updateById(task);
        }
    }

    public int processBatchExcelLine(List<ImportRecordSaveDTO> importErrorDTOS, List<StudentImportModel> list, Long schoolId,
                                     String schoolYear, Map<String, List<StudentImportModel>> studentNoMap, Map<String, List<StudentEntity>> oldStudentNoMap,
                                     Map<String, List<StudentImportModel>> educationNoMap, Map<String, List<StudentEntity>> oldEducationNoMap,
                                     Map<String, List<StudentImportModel>> seatNoMap, SchoolLanguageEnum schoolLanguageEnum, boolean existWx) {
        if (CollectionUtils.isNotEmpty(list)) {
            int correctCount = list.size();//正确处理的条数
            //待插入的学生信息
            List<StudentImportDTO> studentSaveOrUpdateList = new ArrayList<>();
            List<SysClassListResModel> classList = sysClassDao.selectSysClassListBySchoolIdAndSid(schoolId, schoolYear);
            Map<String, SysClassListResModel> classMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(classList)) {
                classMap = classList.stream().collect(Collectors.toMap(sysClassListResModel -> sysClassListResModel.getGrade() + "_" + sysClassListResModel.getClassName(), sysClassListResModel -> sysClassListResModel));
            }
            Map<String, StudentEntity> studentNumberMap = new HashMap<>();
            Map<String, StudentEntity> studentClassCampusidMap = new HashMap<>();

            List<String> studentNumbers = list.stream().map(StudentImportModel::getStudentNo).filter(StringUtils::isNotBlank).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(studentNumbers)) {
                QueryWrapper<StudentEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().in(StudentEntity::getStudentNo, studentNumbers)
                        .eq(StudentEntity::getSchoolId, schoolId)
                        .eq(StudentEntity::getDeleted, 0);
                List<StudentEntity> byStudentNumbers = this.list(queryWrapper);
                if (CollectionUtils.isNotEmpty(byStudentNumbers)) {
                    studentNumberMap = byStudentNumbers.stream().collect(Collectors.toMap(StudentEntity::getStudentNo, student -> student, (key1, key2) -> key1));
                }
            }
            List<Long> classIds = new ArrayList<>();
            for (StudentImportModel studentImportModel : list) {
                SysClassListResModel classListResModel = classMap.get(studentImportModel.getGradeGroup() + "_" + studentImportModel.getClassName());
                if (classListResModel != null) {
                    classIds.add(classListResModel.getClassId());
                }
            }
            List<Integer> classCampusidList = list.stream().filter(studentImportModel -> StringUtils.isNotBlank(studentImportModel.getSeatNo()) &&
                    NumberUtils.isDigits(studentImportModel.getSeatNo()))
                    .map(StudentImportModel::getSeatNo).map(Integer::parseInt).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(classIds) && CollectionUtils.isNotEmpty(classCampusidList)) {
                QueryWrapper<StudentEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().in(StudentEntity::getClassId, classIds)
                        .in(StudentEntity::getSeatNo, classCampusidList)
                        .eq(StudentEntity::getDeleted, 0);
                List<StudentEntity> byClassIdStudents = this.list(queryWrapper);
                if (CollectionUtils.isNotEmpty(byClassIdStudents)) {
                    studentClassCampusidMap = byClassIdStudents.stream().collect(Collectors.toMap(student -> student.getClassId() + "_" + student.getSeatNo(), student -> student));
                }
            }
            //遍历要插入的每一行
            for (StudentImportModel bo : list) {
                List<String> studentErrorList = new ArrayList<>();
                if (!check(bo, studentErrorList, classMap, studentClassCampusidMap, studentNoMap, oldStudentNoMap,
                        educationNoMap, oldEducationNoMap, seatNoMap, schoolLanguageEnum, existWx)) {
                    //不合法
                    correctCount--;
                    if (CollectionUtils.isNotEmpty(studentErrorList)) {
                        ImportRecordSaveDTO errorDTO = new ImportRecordSaveDTO();
                        errorDTO.setIncorrectLineno(String.valueOf(bo.getExcelLineNo()));
                        errorDTO.setIncorrectReason(StringUtils.join(studentErrorList, "；"));
                        importErrorDTOS.add(errorDTO);
                    }
                    continue;
                }
                StudentImportDTO student = studentImportConvert(schoolId, bo, classMap, studentNumberMap, schoolLanguageEnum);
                studentSaveOrUpdateList.add(student);
            }
            if (CollectionUtils.isNotEmpty(studentSaveOrUpdateList)) {
                log.info("导入数据创建/更新学生信息开始");
                List<StudentEntity> students = new ArrayList<>();
                studentSaveOrUpdateList.forEach(studentImportDTO -> {
                    StudentEntity student = new StudentEntity();
                    BeanUtils.copyProperties(studentImportDTO, student);
                    student.setArtsScience(studentImportDTO.getStudentType());
                    students.add(student);
                });
                this.saveOrUpdateBatch(students);
                if (CollectionUtils.isNotEmpty(students)) {
                    Map<String, StudentEntity> studentEntityMap = students.stream().collect(Collectors.toMap(StudentEntity::getStudentNo, student -> student, (key1, key2) -> key1));
                    //获取家庭信息
                    List<Long> studentIds = students.stream().map(StudentEntity::getId).collect(Collectors.toList());
                    Map<Long, StudentFamilyEntity> familyEntityMap = new HashMap<>();
                    if (CollectionUtils.isNotEmpty(studentIds)) {
                        QueryWrapper<StudentFamilyEntity> queryWrapper = new QueryWrapper<>();
                        queryWrapper.lambda().in(StudentFamilyEntity::getStudentId, studentIds)
                                .eq(StudentFamilyEntity::getDeleted, 0);
                        List<StudentFamilyEntity> familyEntities = studentFamilyService.list(queryWrapper);
                        if (CollectionUtils.isNotEmpty(familyEntities)) {
                            familyEntityMap = familyEntities.stream().collect(Collectors.toMap(StudentFamilyEntity::getStudentId, family -> family));
                        }
                    }
                    //保存家庭信息
                    List<StudentFamilyEntity> studentFamilySaveOrUpdateList = new ArrayList<>();
                    for (StudentImportDTO studentImportDTO : studentSaveOrUpdateList) {
                        StudentEntity studentEntity = studentEntityMap.get(studentImportDTO.getStudentNo());
                        if (studentEntity != null) {
                            StudentFamilyImportDTO familyImportDTO = studentImportDTO.getFamilyImportDTO();
                            if (familyImportDTO != null) {
                                StudentFamilyEntity familyEntity = new StudentFamilyEntity();
                                StudentFamilyEntity studentFamily = familyEntityMap.get(studentEntity.getId());
                                if (studentFamily != null) {
                                    familyEntity.setId(studentFamily.getId());
                                }
                                BeanUtils.copyProperties(familyImportDTO, familyEntity);
                                familyEntity.setStudentId(studentEntity.getId());
                                studentFamilySaveOrUpdateList.add(familyEntity);
                            }
                        }
                    }
                    if (CollectionUtils.isNotEmpty(studentFamilySaveOrUpdateList)) {
                        studentFamilyService.saveOrUpdateBatch(studentFamilySaveOrUpdateList);
                    }
                    //同步企业微信学生的信息
                    if (existWx)
                    {
                        try {
                            Map<Long, EnterpriseWechatRelEntity> relEntityMap = enterpriseWechatRelService.list(schoolId,
                                    EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_STUDENT.getCode(), studentIds, schoolYear);
                            List<SynWxChatStatusUpdateDTO> relUpdateList = new ArrayList<>();
                            List<SynWxChatStatusUpdateDTO> relSaveList = new ArrayList<>();
                            List<EnterpriseWechatRelEntity> relSaveListEntity = new ArrayList<>();
                            for (StudentImportDTO studentImportDTO : studentSaveOrUpdateList) {
                                StudentEntity studentEntity = studentEntityMap.get(studentImportDTO.getStudentNo());
                                if (studentEntity != null) {
                                    EnterpriseWechatRelEntity relEntity = relEntityMap.get(studentEntity.getId());
                                    EnterpriseWechatRelEntity relEntityEntity = new EnterpriseWechatRelEntity();
                                    if (relEntity != null)
                                    {
                                        BeanUtils.copyProperties(relEntity, relEntityEntity);
                                        if (StringUtils.isNotBlank(studentImportDTO.getStudentWeChat())){
                                            relEntityEntity.setWxId(studentImportDTO.getStudentWeChat());
                                            relSaveListEntity.add(relEntityEntity);
                                        }
                                    }else {
                                        relEntityEntity.setType(EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_STUDENT.getCode());
                                        relEntityEntity.setRelId(studentEntity.getId());
                                        relEntityEntity.setSchoolId(schoolId);
                                        relEntityEntity.setWxId(studentEntity.getId().toString());
                                        relEntityEntity.setSchoolYear(schoolYear);
                                        relSaveListEntity.add(relEntityEntity);
                                    }
                                    if (StringUtils.isNotBlank(studentImportDTO.getStudentWeChat())) {
                                        SynWxChatStatusUpdateDTO relUpdate = new SynWxChatStatusUpdateDTO();
                                        relUpdate.setRelId(studentEntity.getId());
                                        relUpdate.setThirdId(studentImportDTO.getStudentWeChat());
                                        relUpdate.setPhone(studentImportDTO.getStudentPhone());
                                        relUpdateList.add(relUpdate);
                                    } else {
                                        SynWxChatStatusUpdateDTO relSave = new SynWxChatStatusUpdateDTO();
                                        relSave.setRelId(studentEntity.getId());
                                        relSave.setThirdId(studentEntity.getId().toString());
                                        relSave.setPhone(studentImportDTO.getStudentPhone());
                                        relSaveList.add(relSave);
                                    }
                                }
                            }
                            enterpriseWechatService.createOrUpdateStudents(schoolId, relSaveList, WechatBusinessTypeEnum.CREATE, schoolYear);
                            enterpriseWechatService.createOrUpdateStudents(schoolId, relUpdateList, WechatBusinessTypeEnum.UPDATE, schoolYear);
                            //绑定进表
                            if (CollectionUtils.isNotEmpty(relSaveListEntity)) {
                                enterpriseWechatRelService.saveOrUpdateBatch(relSaveListEntity);
                            }
                        }catch (Exception e)
                        {
                            log.error("同步学生信息到企业微信失败",e);
                        }
                    }
                    //保存学生家长信息
                    Map<Long, List<StudentParentEntity>> parentEntityMap = new HashMap<>();
                    if (CollectionUtils.isNotEmpty(studentIds)) {
                        QueryWrapper<StudentParentEntity> queryWrapper = new QueryWrapper<>();
                        queryWrapper.lambda().in(StudentParentEntity::getStudentId, studentIds)
                                .eq(StudentParentEntity::getStudentId, schoolId)
                                .eq(StudentParentEntity::getDeleted, 0);
                        List<StudentParentEntity> parentEntities = studentParentService.list(queryWrapper);
                        if (CollectionUtils.isNotEmpty(parentEntities)) {
                            parentEntityMap = parentEntities.stream().collect(Collectors.groupingBy(StudentParentEntity::getStudentId));
                        }
                    }

                    List<StudentParentAddReqModel> studentParentSaveOrUpdateList = new ArrayList<>();
                    for (StudentImportDTO studentImportDTO : studentSaveOrUpdateList) {
                        StudentEntity studentEntity = studentEntityMap.get(studentImportDTO.getStudentNo());
                        if (studentEntity != null) {
                            List<StudentParentImportDTO> parentImportDTOS = studentImportDTO.getParentImportDTOList();
                            if (CollectionUtils.isNotEmpty(parentImportDTOS)) {
                                List<StudentParentEntity> parentEntities = parentEntityMap.get(studentEntity.getId());
                                for (StudentParentImportDTO parentImportDTO : parentImportDTOS)
                                {
                                    getParentId(parentEntities,parentImportDTO);
                                    StudentParentAddReqModel parentEntity = new StudentParentAddReqModel();
                                    BeanUtils.copyProperties(parentImportDTO, parentEntity);
                                    parentEntity.setStudentId(studentEntity.getId());
                                    parentEntity.setSchoolId(schoolId);
                                    studentParentSaveOrUpdateList.add(parentEntity);
                                }
                            }
                        }
                    }
                    if (CollectionUtils.isNotEmpty(studentParentSaveOrUpdateList)) {
                        studentParentService.addOrUpdate(studentParentSaveOrUpdateList,schoolId,schoolYear);
                    }
                }
            }
            return correctCount;
        }
        return 0;
    }


    private void getParentId(List<StudentParentEntity> parentEntities,StudentParentImportDTO parentImportDTO)
    {
        if (CollectionUtils.isEmpty(parentEntities))
        {
            return;
        }
        for (StudentParentEntity parentEntity : parentEntities) {
            if (parentEntity.getParentType().equals(parentImportDTO.getParentType())
                    && parentEntity.getParentRelation().equals(parentImportDTO.getParentRelation())) {
                parentImportDTO.setId(parentEntity.getId());
            }
        }
    }

    private StudentImportDTO studentImportConvert(Long schoolId, StudentImportModel bo, Map<String, SysClassListResModel> classMap,
                                                  Map<String, StudentEntity> studentNumberMap, SchoolLanguageEnum schoolLanguageEnum) {
        StudentImportDTO student = new StudentImportDTO();
        StudentEntity studentEntity = studentNumberMap.get(bo.getStudentNo());
        if (studentEntity != null) {
            student.setId(studentEntity.getId());
        }
        student.setSchoolId(schoolId);
        student.setChineseName(bo.getChineseName());
        student.setEnglishName(bo.getEnglishName());
        student.setGender(LanguageUtils.isMale(schoolLanguageEnum, bo.getGender()) ? 1 : 2);
        student.setStudentNo(bo.getStudentNo());
        if (StringUtils.isNotBlank(bo.getSeatNo())) {
            student.setSeatNo(Integer.valueOf(bo.getSeatNo()));
        }
        SysClassListResModel classInfo = classMap.get(bo.getGradeGroup() + "_" + bo.getClassName());
        if (classInfo != null) {
            student.setClassId(classInfo.getClassId());
        }
        student.setIdType(StudentImportIdTypeEnum.getCode(bo.getIdType(), schoolLanguageEnum));
        student.setIdNo(bo.getIdNo());
        student.setEducationNo(bo.getEducationNo());
        student.setBirthDate(DateUtils.convertImportDate(bo.getBirthDate()));
        student.setBirthPlace(BirthPlaceEnum.getCode(bo.getBirthPlace(), schoolLanguageEnum));
        // 测试提测说是文件导入是什么就显示什么
        student.setIdIssuePlace(bo.getIdIssuePlace());
        student.setIdIssueDate(DateUtils.convertImportDate(bo.getIdIssueDate()));
        student.setIdValidDate(DateUtils.convertImportDate(bo.getIdValidDate()));
        student.setReEntryPermitNo(bo.getReEntryPermitNo());
        student.setStayType(StayTypeEnum.getCode(bo.getStayType(), schoolLanguageEnum));
        student.setStayIssueDate(DateUtils.convertImportDate(bo.getStayIssueDate()));
        student.setStayValidDate(DateUtils.convertImportDate(bo.getStayValidDate()));
        student.setNationality(NationalityEnum.getCode(bo.getNationality(), schoolLanguageEnum));
        student.setNativePlace(bo.getNativePlace());
        student.setPermanentPhone(bo.getPermanentPhone());
        student.setMobilePhone(bo.getMobilePhone());
        student.setPermanentAddressAreaId(AddressAreaEnum.getAreaCode(bo.getPermanentAddressAreaId(), schoolLanguageEnum));
        student.setPermanentAddress(bo.getPermanentAddress());
        student.setNightAddressAreaId(AddressAreaEnum.getAreaCode(bo.getNightAddressAreaId(), schoolLanguageEnum));
        student.setNightAddress(bo.getNightAddress());
        student.setStatus(StudentStatusEnum.AT_SCHOOL.getCode());
        student.setStudentType(StudentTypeEnum.getCodeByLanguage(schoolLanguageEnum.getCode(),bo.getStudentType()));
        StudentFamilyImportDTO familyImportDTO = new StudentFamilyImportDTO();
        familyImportDTO.setGuardianName(bo.getGuardianName());
        familyImportDTO.setGuardianPhone(bo.getGuardianPhone());
        familyImportDTO.setGuardianMobile(bo.getGuardianMobile());
        familyImportDTO.setGuardianOccupation(bo.getGuardianOccupation());
        familyImportDTO.setGuardianRelation(RelationTypeEnum.getCode(bo.getGuardianRelation(), schoolLanguageEnum));
        familyImportDTO.setGuardianAddressAreaId(AddressAreaEnum.getAreaCode(bo.getGuardianAddressAreaId(), schoolLanguageEnum));
        familyImportDTO.setGuardianAddress(bo.getGuardianAddress());
        familyImportDTO.setLiveWithGuardian(YesNoEnum.getCode(bo.getLiveWithGuardian(), schoolLanguageEnum));
        familyImportDTO.setEmergencyContact(bo.getEmergencyContact());
        familyImportDTO.setEmergencyRelation(RelationTypeEnum.getCode(bo.getEmergencyRelation(), schoolLanguageEnum));
        familyImportDTO.setEmergencyPhone(bo.getEmergencyPhone());
        familyImportDTO.setEmergencyAddressAreaId(AddressAreaEnum.getAreaCode(bo.getEmergencyAddressAreaId(), schoolLanguageEnum));
        familyImportDTO.setEmergencyAddress(bo.getEmergencyAddress());
        student.setFamilyImportDTO(familyImportDTO);


        student.setStudentPhone(bo.getStudentPhone());
        student.setStudentWeChat(bo.getStudentWeChat());
        //插入家庭信息
        List<StudentParentImportDTO> parentImportDTOList = new ArrayList<>();

        if (StringUtils.isNotBlank(bo.getParentName())) {
            StudentParentImportDTO parentImportDTO = new StudentParentImportDTO();
            parentImportDTO.setParentName(bo.getParentName());
            parentImportDTO.setParentPhone(bo.getParentPhoneOne());
            ParentTypeEnum name = ParentTypeEnum.getName(bo.getParentRelationOne(), schoolLanguageEnum);
            parentImportDTO.setParentType(name == null ? null : name.getCode().toString());
            parentImportDTO.setParentRelation(bo.getParentRelationOne());
            parentImportDTO.setJob(bo.getParentOccupation());
            parentImportDTOList.add(parentImportDTO);
        }

        if (StringUtils.isNotBlank(bo.getParentNameTwo())) {
            StudentParentImportDTO parentImport2DTO = new StudentParentImportDTO();
            parentImport2DTO.setParentName(bo.getParentNameTwo());
            parentImport2DTO.setParentPhone(bo.getParentPhoneTwo());
            ParentTypeEnum name2 = ParentTypeEnum.getName(bo.getParentRelationTwo(), schoolLanguageEnum);
            parentImport2DTO.setParentType(name2 == null ? null : name2.getCode().toString());
            parentImport2DTO.setParentRelation(bo.getParentRelationTwo());
            parentImport2DTO.setJob(bo.getParentOccupationTwo());
            parentImportDTOList.add(parentImport2DTO);
        }

        if (StringUtils.isNotBlank(bo.getParentNameThree())) {
            StudentParentImportDTO parentImport3DTO = new StudentParentImportDTO();
            parentImport3DTO.setParentName(bo.getParentNameThree());
            parentImport3DTO.setParentPhone(bo.getParentPhoneThree());
            ParentTypeEnum name3 = ParentTypeEnum.getName(bo.getParentRelationThree(), schoolLanguageEnum);
            parentImport3DTO.setParentType(name3 == null ? null : name3.getCode().toString());
            parentImport3DTO.setParentRelation(bo.getParentRelationThree());
            parentImportDTOList.add(parentImport3DTO);
        }
        if (StringUtils.isNotBlank(bo.getParentNameFour())) {
            StudentParentImportDTO parentImport4DTO = new StudentParentImportDTO();
            parentImport4DTO.setParentName(bo.getParentNameFour());
            parentImport4DTO.setParentPhone(bo.getParentPhoneFour());
            ParentTypeEnum name4 = ParentTypeEnum.getName(bo.getParentRelationFour(), schoolLanguageEnum);
            parentImport4DTO.setParentType(name4 == null ? null : name4.getCode().toString());
            parentImport4DTO.setParentRelation(bo.getParentRelationFour());
            parentImportDTOList.add(parentImport4DTO);
        }


        //监护人信息
        StudentParentImportDTO guardianImportDTO = new StudentParentImportDTO();
        guardianImportDTO.setParentName(bo.getGuardianName());
        guardianImportDTO.setParentPhone(bo.getGuardianPhone());
        guardianImportDTO.setParentType("3");
        guardianImportDTO.setParentRelation(bo.getGuardianRelation());
        guardianImportDTO.setJob(bo.getGuardianOccupation());
        guardianImportDTO.setGuardianMobile(bo.getGuardianMobile());
        guardianImportDTO.setAddressAreaId(AddressAreaEnum.getAreaCode(bo.getGuardianAddressAreaId(), schoolLanguageEnum));
        guardianImportDTO.setGuardianAddress(bo.getGuardianAddress());
        Integer code = YesNoEnum.getCode(bo.getLiveWithGuardian(), schoolLanguageEnum);
        guardianImportDTO.setWithGuardian(code != null && code == 1);
        parentImportDTOList.add(guardianImportDTO);

        student.setParentImportDTOList(parentImportDTOList);


        return student;
    }




    private boolean check(StudentImportModel bo, List<String> studentErrorList, Map<String, SysClassListResModel> classMap,
                          Map<String, StudentEntity> studentClassCampusidMap, Map<String, List<StudentImportModel>> studentNoMap, Map<String, List<StudentEntity>> oldStudentNoMap,
                          Map<String, List<StudentImportModel>> educationNoMap, Map<String, List<StudentEntity>> oldEducationNoMap,
                          Map<String, List<StudentImportModel>> seatNoMap, SchoolLanguageEnum schoolLanguageEnum,boolean existWx) {
        //一项一项检查
        //中文姓名检查
        if (!StringUtils.isNotBlank(bo.getChineseName())) {
            studentErrorList.add(languageUtil.getMessage(LanguageConstants.CHINESE_NAME_REQUIRED));
        }
//        else {
//            if (!checkChineseName(bo.getChineseName())) {
//                studentErrorList.add(String.format(languageUtil.getMessage(LanguageConstants.NAME_FORMAT_ERROR), bo.getChineseName()));
//            }
//        }
        //学生编号检查
        if (!StringUtils.isNotBlank(bo.getStudentNo())) {
            studentErrorList.add(languageUtil.getMessage(LanguageConstants.STUDENT_NO_REQUIRED));
        } else {
            if (CollectionUtils.isNotEmpty(studentNoMap.get(bo.getStudentNo())) && studentNoMap.get(bo.getStudentNo()).size() > 1) {
                studentErrorList.add(String.format(languageUtil.getMessage(LanguageConstants.STUDENT_NO_EXISTS), bo.getStudentNo()));
            }
            if (CollectionUtils.isNotEmpty(oldStudentNoMap.get(bo.getStudentNo())) && !oldStudentNoMap.get(bo.getStudentNo()).isEmpty()
                    && !oldStudentNoMap.get(bo.getStudentNo()).get(0).getEducationNo().equals(bo.getEducationNo())) {
                studentErrorList.add(String.format(languageUtil.getMessage(LanguageConstants.STUDENT_NO_EXISTS_IN_SYSTEM), bo.getStudentNo()));
            }
        }
        // 学生证编号检查
        if (!StringUtils.isNotBlank(bo.getEducationNo())) {
            studentErrorList.add(languageUtil.getMessage(LanguageConstants.EDUCATION_NO_REQUIRED));
        } else {
            if (CollectionUtils.isNotEmpty(educationNoMap.get(bo.getEducationNo())) && educationNoMap.get(bo.getEducationNo()).size() > 1) {
                studentErrorList.add(String.format(languageUtil.getMessage(LanguageConstants.EDUCATION_NO_EXISTS_EXCEL), bo.getEducationNo()));
            }
            if (CollectionUtils.isNotEmpty(oldEducationNoMap.get(bo.getEducationNo())) && !oldEducationNoMap.get(bo.getEducationNo()).isEmpty()
                    && !oldEducationNoMap.get(bo.getEducationNo()).get(0).getStudentNo().equals(bo.getStudentNo())) {
                studentErrorList.add(String.format(languageUtil.getMessage(LanguageConstants.EDUCATION_NO_EXISTS_IN_SYSTEM), bo.getEducationNo()));
            }
        }
        //座位号检查
        if (StringUtils.isNotBlank(bo.getSeatNo())) {
            if (!NumberUtils.isDigits(bo.getSeatNo())) {
                studentErrorList.add(String.format(languageUtil.getMessage(LanguageConstants.SEAT_NO_FORMAT_ERROR), bo.getSeatNo()));
            } else {
                if (CollectionUtils.isNotEmpty(seatNoMap.get(bo.getGradeGroup() + "_" + bo.getClassName() + "_" + bo.getSeatNo())) &&
                        seatNoMap.get(bo.getGradeGroup() + "_" + bo.getClassName() + "_" + bo.getSeatNo()).size() > 1) {
                    studentErrorList.add(String.format(languageUtil.getMessage(LanguageConstants.SEAT_NO_EXISTS), bo.getSeatNo()));
                }
                if (classMap.get(bo.getGradeGroup() + "_" + bo.getClassName()) != null &&
                        studentClassCampusidMap.get(classMap.get(bo.getGradeGroup() + "_" + bo.getClassName()).getClassId() + "_" + bo.getSeatNo()) != null &&
                        !studentClassCampusidMap.get(classMap.get(bo.getGradeGroup() + "_" + bo.getClassName()).getClassId() + "_" + bo.getSeatNo()).getStudentNo().equals(bo.getStudentNo())) {
                    studentErrorList.add(String.format(languageUtil.getMessage(LanguageConstants.SEAT_NO_EXISTS_IN_CLASS), bo.getSeatNo()));
                }
            }
        }
        //级组班级检查
        if (StringUtils.isNotBlank(bo.getGradeGroup()) && StringUtils.isNotBlank(bo.getClassName())) {
            if (classMap.get(bo.getGradeGroup() + "_" + bo.getClassName()) == null) {
                studentErrorList.add(String.format(languageUtil.getMessage(LanguageConstants.CLASS_NAME_NOT_EXISTS), bo.getClassName()));
            }
        } else {
            if (!StringUtils.isNotBlank(bo.getGradeGroup())) {
                studentErrorList.add(languageUtil.getMessage(LanguageConstants.GRADE_GROUP_REQUIRED));
            }
            if (!StringUtils.isNotBlank(bo.getClassName())) {
                studentErrorList.add(languageUtil.getMessage(LanguageConstants.CLASS_REQUIRED));
            }
        }
        //生日检查
        if (StringUtils.isNotBlank(bo.getBirthDate())) {
            try {
                if (StringUtils.isNumeric(bo.getBirthDate())) {
                    //execl日期格式解析为全数字，如：43444
                    DateUtil.getJavaDate(Double.parseDouble(bo.getBirthDate()));
                } else if (bo.getBirthDate().contains("/")) {
                    //字符串格式日期，如2024/12/18
                    DateUtils.formatStringToDate(bo.getBirthDate(), "yyyy/MM/dd");
                } else {
                    //字符串格式日期，如2024-12-18
                    DateUtils.formatStringToDate(bo.getBirthDate(), "yyyy-MM-dd");
                }
            } catch (Exception e) {
                studentErrorList.add(String.format(languageUtil.getMessage(LanguageConstants.BIRTH_DATE_FORMAT_ERROR), bo.getBirthDate()));
            }
        }
        //性别检查
        if (StringUtils.isNotBlank(bo.getGender()) && GenderEnum.getCode(bo.getGender(), SchoolLanguageEnum.getDefValue(LanguageUtil.getCurrentLanguage())) == null) {
            studentErrorList.add(String.format(languageUtil.getMessage(LanguageConstants.GENDER_FORMAT_ERROR), bo.getGender()));
        }
        //出生地检查
        if (StringUtils.isNotBlank(bo.getBirthPlace()) && BirthPlaceEnum.getCode(bo.getBirthPlace(), SchoolLanguageEnum.getDefValue(LanguageUtil.getCurrentLanguage())) == null) {
            studentErrorList.add(languageUtil.getMessage(LanguageConstants.BIRTH_PLACE_FORMAT_ERROR));
        }
        //證件發出地點检查
        if (StringUtils.isNotBlank(bo.getIdIssuePlace()) && BirthPlaceEnum.getCode(bo.getIdIssuePlace(), SchoolLanguageEnum.getDefValue(LanguageUtil.getCurrentLanguage())) == null) {
            studentErrorList.add(languageUtil.getMessage(LanguageConstants.ID_ISSUE_PLACE_FORMAT_ERROR));
        }
        //證件發出日期检查
        if (StringUtils.isNotBlank(bo.getIdIssueDate())) {
            try {
                if (StringUtils.isNumeric(bo.getIdIssueDate())) {
                    //execl日期格式解析为全数字，如：43444
                    DateUtil.getJavaDate(Double.parseDouble(bo.getIdIssueDate()));
                } else if (bo.getIdIssueDate().contains("/")) {
                    //字符串格式日期，如2024/12/18
                    DateUtils.formatStringToDate(bo.getIdIssueDate(), "yyyy/MM/dd");
                } else {
                    //字符串格式日期，如2024-12-18
                    DateUtils.formatStringToDate(bo.getIdIssueDate(), "yyyy-MM-dd");
                }
            } catch (Exception e) {
                studentErrorList.add(languageUtil.getMessage(LanguageConstants.ID_ISSUE_DATE_FORMAT_ERROR));
            }
        }
        //證件有效日期检查
        if (StringUtils.isNotBlank(bo.getIdValidDate())) {
            try {
                if (StringUtils.isNumeric(bo.getIdValidDate())) {
                    //execl日期格式解析为全数字，如：43444
                    DateUtil.getJavaDate(Double.parseDouble(bo.getIdValidDate()));
                } else if (bo.getIdValidDate().contains("/")) {
                    //字符串格式日期，如2024/12/18
                    DateUtils.formatStringToDate(bo.getIdValidDate(), "yyyy/MM/dd");
                } else {
                    //字符串格式日期，如2024-12-18
                    DateUtils.formatStringToDate(bo.getIdValidDate(), "yyyy-MM-dd");
                }
            } catch (Exception e) {
                studentErrorList.add(languageUtil.getMessage(LanguageConstants.ID_VALID_DATE_FORMAT_ERROR));
            }
        }
        //回乡证编号检查
        if (StringUtils.isNotBlank(bo.getReEntryPermitNo()) && bo.getReEntryPermitNo().length() > 11) {
            studentErrorList.add(languageUtil.getMessage(LanguageConstants.RE_ENTRY_PERMIT_NO_LENGTH_ERROR));
        }
        //逗留許可類型检查
        if (StringUtils.isNotBlank(bo.getStayType()) && StayTypeEnum.getCode(bo.getStayType(), SchoolLanguageEnum.getDefValue(LanguageUtil.getCurrentLanguage())) == null) {
            studentErrorList.add(languageUtil.getMessage(LanguageConstants.STAY_TYPE_FORMAT_ERROR));
        }
        //逗留許可發出日期检查
        if (StringUtils.isNotBlank(bo.getStayIssueDate())) {
            try {
                if (StringUtils.isNumeric(bo.getStayIssueDate())) {
                    //execl日期格式解析为全数字，如：43444
                    DateUtil.getJavaDate(Double.parseDouble(bo.getStayIssueDate()));
                } else if (bo.getStayIssueDate().contains("/")) {
                    //字符串格式日期，如2024/12/18
                    DateUtils.formatStringToDate(bo.getStayIssueDate(), "yyyy/MM/dd");
                } else {
                    //字符串格式日期，如2024-12-18
                    DateUtils.formatStringToDate(bo.getStayIssueDate(), "yyyy-MM-dd");
                }
            } catch (Exception e) {
                studentErrorList.add(languageUtil.getMessage(LanguageConstants.STAY_ISSUE_DATE_FORMAT_ERROR));
            }
        }
        //逗留許可有效日期检查
        if (StringUtils.isNotBlank(bo.getStayValidDate())) {
            try {
                if (StringUtils.isNumeric(bo.getStayValidDate())) {
                    //execl日期格式解析为全数字，如：43444
                    DateUtil.getJavaDate(Double.parseDouble(bo.getStayValidDate()));
                } else if (bo.getStayValidDate().contains("/")) {
                    //字符串格式日期，如2024/12/18
                    DateUtils.formatStringToDate(bo.getStayValidDate(), "yyyy/MM/dd");
                } else {
                    //字符串格式日期，如2024-12-18
                    DateUtils.formatStringToDate(bo.getStayValidDate(), "yyyy-MM-dd");
                }
            } catch (Exception e) {
                studentErrorList.add(languageUtil.getMessage(LanguageConstants.STAY_VALID_DATE_FORMAT_ERROR));
            }
        }
        //国籍检查
        if (StringUtils.isNotBlank(bo.getNationality()) && NationalityEnum.getCode(bo.getNationality(), SchoolLanguageEnum.getDefValue(LanguageUtil.getCurrentLanguage())) == null) {
            studentErrorList.add(languageUtil.getMessage(LanguageConstants.NATIONALITY_FORMAT_ERROR));
        }
        //常用住址-地區检查
        if (StringUtils.isNotBlank(bo.getPermanentAddressAreaId()) && AddressAreaEnum.getCode(bo.getPermanentAddressAreaId(), SchoolLanguageEnum.getDefValue(LanguageUtil.getCurrentLanguage())) == null) {
            studentErrorList.add(languageUtil.getMessage(LanguageConstants.PERMANENT_ADDRESS_FORMAT_ERROR));
        }
        //夜間留宿住址-地區检查
        if (StringUtils.isNotBlank(bo.getNightAddressAreaId()) && AddressAreaEnum.getCode(bo.getNightAddressAreaId(), SchoolLanguageEnum.getDefValue(LanguageUtil.getCurrentLanguage())) == null) {
            studentErrorList.add(languageUtil.getMessage(LanguageConstants.NIGHT_ADDRESS_FORMAT_ERROR));
        }
        //監護人和学生关系检查
        if (StringUtils.isNotBlank(bo.getGuardianRelation()) && RelationTypeEnum.getCode(bo.getGuardianRelation(), SchoolLanguageEnum.getDefValue(LanguageUtil.getCurrentLanguage())) == null) {
            studentErrorList.add(languageUtil.getMessage(LanguageConstants.GUARDIAN_RELATION_FORMAT_ERROR));
        }
        //監護人住址-地區检查
        if (StringUtils.isNotBlank(bo.getGuardianAddressAreaId()) && AddressAreaEnum.getCode(bo.getGuardianAddressAreaId(), SchoolLanguageEnum.getDefValue(LanguageUtil.getCurrentLanguage())) == null) {
            studentErrorList.add(languageUtil.getMessage(LanguageConstants.GUARDIAN_ADDRESS_FORMAT_ERROR));
        }
        //與監護人同住检查
        if (StringUtils.isNotBlank(bo.getLiveWithGuardian()) && YesNoEnum.getCode(bo.getLiveWithGuardian(), SchoolLanguageEnum.getDefValue(LanguageUtil.getCurrentLanguage())) == null) {
            studentErrorList.add(languageUtil.getMessage(LanguageConstants.LIVE_WITH_GUARDIAN_FORMAT_ERROR));
        }
        //緊急聯絡人與學生關係检查
        if (StringUtils.isNotBlank(bo.getEmergencyRelation()) && RelationTypeEnum.getCode(bo.getEmergencyRelation(), SchoolLanguageEnum.getDefValue(LanguageUtil.getCurrentLanguage())) == null) {
            studentErrorList.add(languageUtil.getMessage(LanguageConstants.EMERGENCY_RELATION_FORMAT_ERROR));
        }
        //紧急联系人电话检查
        if (!StringUtils.isNotBlank(bo.getEmergencyPhone())) {
            studentErrorList.add(languageUtil.getMessage(LanguageConstants.EMERGENCY_CONTACT_PHONE_REQUIRED));
        }
        //緊急聯絡人住址-地區检查
        if (StringUtils.isNotBlank(bo.getEmergencyAddressAreaId()) && AddressAreaEnum.getCode(bo.getEmergencyAddressAreaId(), SchoolLanguageEnum.getDefValue(LanguageUtil.getCurrentLanguage())) == null) {
            studentErrorList.add(languageUtil.getMessage(LanguageConstants.EMERGENCY_ADDRESS_FORMAT_ERROR));
        }
        //緊急聯絡人姓名（必填）必填
        if (StringUtils.isBlank(bo.getEmergencyContact())) {
            studentErrorList.add(languageUtil.getMessage(LanguageConstants.EMERGENCY_CONTACT_REQUIRED));
        }
        // 学生类型检查
        if (StringUtils.isNotBlank(bo.getStudentType()) && StudentTypeEnum.getCodeByLanguage(schoolLanguageEnum.getCode(), bo.getStudentType()) == -1) {
            studentErrorList.add(languageUtil.getMessage(LanguageConstants.SELECT_VALID_VALUE));
        }
        //企业微信账号检查
        if (existWx){
            if (StringUtils.isBlank(bo.getStudentWeChat())) {
                studentErrorList.add(languageUtil.getMessage(LanguageConstants.STUDENT_WECHAT_REQUIRED));
            }
            if (StringUtils.isBlank(bo.getParentPhoneOne()) || StringUtils.isBlank(bo.getParentRelationOne()))
            {
                studentErrorList.add(languageUtil.getMessage(LanguageConstants.STUDENT_WECHAT_PARENT_PHONE_REQUIRED));
            }
            //校验家长手机号是否重复
            Map<String, String> parentPhoneMap = new HashMap<>();
            if (StringUtils.isNotBlank(bo.getParentPhoneOne()))
            {
                parentPhoneMap.put(bo.getParentPhoneOne(), bo.getParentRelationOne());
            }
            if (StringUtils.isNotBlank(bo.getParentPhoneTwo()))
            {
                if (!parentPhoneMap.containsKey(bo.getParentPhoneTwo())) {
                    parentPhoneMap.put(bo.getParentPhoneTwo(), bo.getParentRelationTwo());
                }else {
                    studentErrorList.add(languageUtil.getMessage(LanguageConstants.STUDENT_WECHAT_PARENT_PHONE_REPEAT));
                }
            }
            if (StringUtils.isNotBlank(bo.getParentPhoneThree()))
            {
                if (!parentPhoneMap.containsKey(bo.getParentPhoneThree())) {
                    parentPhoneMap.put(bo.getParentPhoneThree(), bo.getParentRelationThree());
                }else {
                    studentErrorList.add(languageUtil.getMessage(LanguageConstants.STUDENT_WECHAT_PARENT_PHONE_REPEAT));
                }
            }
            if (StringUtils.isNotBlank(bo.getParentPhoneFour()))
            {
                if (!parentPhoneMap.containsKey(bo.getParentPhoneFour())) {
                    parentPhoneMap.put(bo.getParentPhoneFour(), bo.getParentRelationFour());
                }else {
                    studentErrorList.add(languageUtil.getMessage(LanguageConstants.STUDENT_WECHAT_PARENT_PHONE_REPEAT));
                }
            }
        }

        return !CollectionUtils.isNotEmpty(studentErrorList);
    }

    private boolean checkChineseName(String name) {
        // 定义一个正则表达式，表示中文字符、数字和字母的范围
        String regex = "[\\u4e00-\\u9fa5a-zA-Z0-9]+$";
        // 使用正则表达式检查字符串是否完全由中文字符、数字或字母组成
        return name.matches(regex);
    }

    private List<StudentImportModel> readExcelData(MultipartFile file, SchoolLanguageEnum schoolLanguageEnum) {
        List<StudentImportModel> result = new ArrayList<>();
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            switch (schoolLanguageEnum) {
                case ZH_MO:
                    StudentImportZhTwListener importZhTwListener = new StudentImportZhTwListener();
                    EasyExcel.read(inputStream, StudentImportZhTwModel.class, importZhTwListener).sheet().headRowNumber(2).doReadSync();
                    List<StudentImportZhTwModel> importZhTwModels = importZhTwListener.getDataList();
                    result = importZhTwModels.stream().map(item -> {
                        StudentImportModel model = new StudentImportModel();
                        BeanUtils.copyProperties(item, model);
                        return model;
                    }).collect(Collectors.toList());
                    break;
                case EN_US:
                    StudentImportEnUsListener importEnUsListener = new StudentImportEnUsListener();
                    EasyExcel.read(inputStream, StudentImportEnUsModel.class, importEnUsListener).sheet().headRowNumber(2).doReadSync();
                    List<StudentImportEnUsModel> importEnUsModels = importEnUsListener.getDataList();
                    result = importEnUsModels.stream().map(item -> {
                        StudentImportModel model = new StudentImportModel();
                        BeanUtils.copyProperties(item, model);
                        return model;
                    }).collect(Collectors.toList());
                    break;
                case PT_PT:
                    StudentImportPtPtListener importPtPtListener = new StudentImportPtPtListener();
                    EasyExcel.read(inputStream, StudentImportPtPtModel.class, importPtPtListener).sheet().headRowNumber(2).doReadSync();
                    List<StudentImportPtPtModel> importPtPtModels = importPtPtListener.getDataList();
                    result = importPtPtModels.stream().map(item -> {
                        StudentImportModel model = new StudentImportModel();
                        BeanUtils.copyProperties(item, model);
                        return model;
                    }).collect(Collectors.toList());
                    break;
                default:
            }
        } catch (IOException e) {
            log.error("Excel文件读取失败", e);
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.FILE_READ_ERROR));
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    @Override
    public String exportStudent(Long userId, StudentPageExportReqModel reqModel) {
        QueryWrapper<ExportHeaderEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ExportHeaderEntity::getSchoolId, reqModel.getSchoolId())
                .eq(ExportHeaderEntity::getUserId, userId)
                .eq(ExportHeaderEntity::getType, ExportHeaderTypeEnum.STUDENT_INFO.getCode());
        ExportHeaderEntity exportHeader = exportHeaderService.getOne(wrapper);
        if (exportHeader == null) {
            exportHeader = new ExportHeaderEntity();
            exportHeader.setSchoolId(reqModel.getSchoolId());
            exportHeader.setUserId(userId);
            exportHeader.setType(ExportHeaderTypeEnum.STUDENT_INFO.getCode());
        }
        exportHeader.setHeader(reqModel.getHeaderStr());
        exportHeaderService.saveOrUpdate(exportHeader);
        List<StudentPageResModel> exportDTOS = this.getBaseMapper().page(reqModel);
        if (CollectionUtils.isNotEmpty(exportDTOS)) {
            List<ExportHeaderModuleDTO> exportHeaderModuleDTOS = JSON.parseArray(reqModel.getHeaderStr(), ExportHeaderModuleDTO.class);
            if (CollectionUtils.isNotEmpty(exportHeaderModuleDTOS)) {
                String fileName = "学生信息导出.xlsx";
                List<List<String>> headers = new ArrayList<>();
                exportHeaderModuleDTOS.forEach(exportHeaderModuleDTO -> {
                    if (CollectionUtils.isNotEmpty(exportHeaderModuleDTO.getHeaders())) {
                        exportHeaderModuleDTO.getHeaders().forEach(exportHeaderDTO -> {
                            headers.add(Collections.singletonList(exportHeaderDTO.getName()));
                        });
                    }
                });
                List<List<String>> data = handleExportData(exportDTOS, exportHeaderModuleDTOS);
                return exportFileHandler.doExportExcelCommon(data, fileName, headers, FileTypeEnum.EXPORT, reqModel.getSchoolId());
            }
        }
        return null;
    }

    private List<List<String>> handleExportData(List<StudentPageResModel> exportDTOS, List<ExportHeaderModuleDTO> exportHeaderModuleDTOS) {
        List<Long> studentIds = exportDTOS.stream().map(StudentPageResModel::getId).collect(Collectors.toList());
        //获取学生基础信息
        QueryWrapper<StudentEntity> studentWrapper = new QueryWrapper<>();
        studentWrapper.lambda().in(StudentEntity::getId, studentIds);
        List<StudentEntity> studentEntities = this.list(studentWrapper);
        Map<Long, StudentEntity> studentMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(studentEntities)) {
            studentMap = studentEntities.stream().collect(Collectors.toMap(StudentEntity::getId, studentEntity -> studentEntity));
        }
        //获取学生家庭信息
        QueryWrapper<StudentFamilyEntity> studentFamilyWrapper = new QueryWrapper<>();
        studentFamilyWrapper.lambda().in(StudentFamilyEntity::getStudentId, studentIds);
        List<StudentFamilyEntity> studentFamilyEntities = studentFamilyService.list(studentFamilyWrapper);
        Map<Long, StudentFamilyEntity> studentFamilyMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(studentFamilyEntities)) {
            studentFamilyMap = studentFamilyEntities.stream().collect(Collectors.toMap(StudentFamilyEntity::getStudentId, studentFamilyEntity -> studentFamilyEntity));
        }
        //获取学生入学记录信息
        QueryWrapper<StudentEnrollEntity> studentEnrollWrapper = new QueryWrapper<>();
        studentEnrollWrapper.lambda().in(StudentEnrollEntity::getStudentId, studentIds);
        List<StudentEnrollEntity> studentEnrollEntities = studentEnrollService.list(studentEnrollWrapper);
        Map<Long, StudentEnrollEntity> studentEnrollMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(studentEnrollEntities)) {
            studentEnrollMap = studentEnrollEntities.stream().collect(Collectors.toMap(StudentEnrollEntity::getStudentId, studentEnrollEntity -> studentEnrollEntity));
        }
        //获取学生医护注意事项信息
        QueryWrapper<StudentMedicalAttentionEntity> studentMedicalAttentionWrapper = new QueryWrapper<>();
        studentMedicalAttentionWrapper.lambda().in(StudentMedicalAttentionEntity::getStudentId, studentIds);
        List<StudentMedicalAttentionEntity> studentMedicalAttentionEntities = studentMedicalAttentionService.list(studentMedicalAttentionWrapper);
        Map<Long, StudentMedicalAttentionEntity> studentMedicalAttentionMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(studentMedicalAttentionEntities)) {
            studentMedicalAttentionMap = studentMedicalAttentionEntities.stream().collect(Collectors.toMap(StudentMedicalAttentionEntity::getStudentId, studentMedicalAttentionEntity -> studentMedicalAttentionEntity));
        }
        //获取学生跨境信息
        QueryWrapper<CrossBorderStudentEntity> crossBorderStudentWrapper = new QueryWrapper<>();
        crossBorderStudentWrapper.lambda().in(CrossBorderStudentEntity::getStudentId, studentIds);
        List<CrossBorderStudentEntity> crossBorderStudentEntities = crossBorderStudentService.list(crossBorderStudentWrapper);
        Map<Long, CrossBorderStudentEntity> crossBorderStudentMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(crossBorderStudentEntities)) {
            crossBorderStudentMap = crossBorderStudentEntities.stream().collect(Collectors.toMap(CrossBorderStudentEntity::getStudentId, crossBorderStudentEntity -> crossBorderStudentEntity));
        }
        //获取学生日期记录信息
        QueryWrapper<StudentDateRecordEntity> studentDateRecordWrapper = new QueryWrapper<>();
        studentDateRecordWrapper.lambda().in(StudentDateRecordEntity::getStudentId, studentIds);
        List<StudentDateRecordEntity> studentDateRecordEntities = studentDateRecordService.list(studentDateRecordWrapper);
        Map<Long, StudentDateRecordEntity> studentDateRecordMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(studentDateRecordEntities)) {
            studentDateRecordMap = studentDateRecordEntities.stream().collect(Collectors.toMap(StudentDateRecordEntity::getStudentId, studentDateRecordEntity -> studentDateRecordEntity));
        }
        //获取医院信息
        List<HospitalEntity> hospitalEntities = hospitalService.list();
        Map<Long, HospitalEntity> hospitalMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(hospitalEntities)) {
            hospitalMap = hospitalEntities.stream().collect(Collectors.toMap(HospitalEntity::getId, hospitalEntity -> hospitalEntity));
        }
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        SchoolLanguageEnum schoolLanguageEnum = SchoolLanguageEnum.getDefValue(currentLanguage);
        List<List<String>> result = new ArrayList<>();
        // 遍历每个导出数据对象
        for (StudentPageResModel exportDTO : exportDTOS) {
            // 遍历每个表头字段
            List<String> rowData = new ArrayList<>();
            for (ExportHeaderModuleDTO exportHeaderModuleDTO : exportHeaderModuleDTOS) {
                switch (exportHeaderModuleDTO.getModule()) {
                    case "basic":
                        //基础数据
                        StudentEntity studentEntity = studentMap.get(exportDTO.getId());
                        for (ExportHeaderDTO headerDTO : exportHeaderModuleDTO.getHeaders()) {
                            try {
                                String rowValue = "";
                                String fieldName = headerDTO.getField();
                                if (studentEntity != null) {
                                    switch (fieldName) {
                                        case "gender":
                                            //性别需要转换
                                            rowValue = GenderEnum.getValue(studentEntity.getGender(), schoolLanguageEnum);
                                            break;
                                        case "status":
                                            //状态需要转换
                                            rowValue = StudentStatusEnum.getValue(studentEntity.getStatus(), schoolLanguageEnum);
                                            break;
                                        case "displayName":
                                            //成績展示姓名需要转换
                                            rowValue = StudentDisplayNameEnum.getValue(studentEntity.getDisplayNameType(), schoolLanguageEnum);
                                            break;
                                        case "className":
                                            //班级需要转换
                                            rowValue = exportDTO.getGradeName() + exportDTO.getClassName();
                                            break;
                                        case "birthPlace":
                                            //出生地需要转换
                                            if (StringUtils.isNotBlank(studentEntity.getBirthPlace())) {
                                                rowValue = BirthPlaceEnum.getValue(studentEntity.getBirthPlace(), schoolLanguageEnum);
                                            }
                                            break;
                                        case "nationality":
                                            //国籍需要转换
                                            if (StringUtils.isNotBlank(studentEntity.getNationality())) {
                                                rowValue = NationalityEnum.getValue(studentEntity.getNationality(), schoolLanguageEnum);
                                            }
                                            break;
                                        case "idType":
                                            //证件类型需要转换
                                            if (studentEntity.getIdType() != null) {
                                                rowValue = StudentImportIdTypeEnum.getValue(studentEntity.getIdType(), schoolLanguageEnum);
                                            }
                                            break;
                                        case "stayType":
                                            //逗留许可证类型需要转换
                                            if (studentEntity.getStayType() != null) {
                                                rowValue = StayTypeEnum.getValue(studentEntity.getStayType(), schoolLanguageEnum);
                                            }
                                            break;
                                        case "permanentAddress":
                                            //常住地址区域需要转换
                                            if (StringUtils.isNotBlank(studentEntity.getPermanentAddressAreaId())) {
                                                rowValue = AddressAreaEnum.getValue(studentEntity.getPermanentAddressAreaId(), schoolLanguageEnum);
                                            }
                                            if (StringUtils.isNotBlank(studentEntity.getPermanentAddress())) {
                                                rowValue += studentEntity.getPermanentAddress();
                                            }
                                            break;
                                        case "nightAddress":
                                            //夜间留宿地址区域需要转换
                                            if (StringUtils.isNotBlank(studentEntity.getNightAddressAreaId())) {
                                                rowValue = AddressAreaEnum.getValue(studentEntity.getNightAddressAreaId(), schoolLanguageEnum);
                                            }
                                            if (StringUtils.isNotBlank(studentEntity.getNightAddress())) {
                                                rowValue += studentEntity.getNightAddress();
                                            }
                                            break;
                                        case "vaccineStatus":
                                            //疫苗接種證明書需要转换
                                            if (studentEntity.getVaccineStatus() != null) {
                                                rowValue = StudentVaccineStatusEnum.getValue(studentEntity.getVaccineStatus(), schoolLanguageEnum);
                                            }
                                            break;
                                        case "emergencyHospital":
                                            //如過意外送往之醫院需要转换
                                            if (studentEntity.getEmergencyHospital() != null && hospitalMap.get(studentEntity.getEmergencyHospital()) != null) {
                                                rowValue = hospitalMap.get(studentEntity.getEmergencyHospital()).getName();
                                            }
                                            break;
                                        case "artsScience":
                                            //文理科需要转换
                                            if (studentEntity.getArtsScience()!= null) {
                                                if (studentEntity.getArtsScience() == 0){
                                                    rowValue = "";
                                                } else {
                                                    rowValue = LanguageUtils.getArtsScienceAndCommerce(schoolLanguageEnum, studentEntity.getArtsScience());
                                                }
                                            }
                                            break;
                                        default:
                                            // 使用反射获取字段值
                                            Field field = StudentEntity.class.getDeclaredField(fieldName);
                                            field.setAccessible(true);
                                            Object value = field.get(studentEntity);
                                            rowValue = value != null ? value.toString() : "";
                                            break;
                                    }
                                }
                                rowData.add(rowValue);
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                rowData.add("");
                            }
                        }
                        break;
                    case "family":
                        //家庭情况
                        StudentFamilyEntity studentFamily = studentFamilyMap.get(exportDTO.getId());
                        for (ExportHeaderDTO headerDTO : exportHeaderModuleDTO.getHeaders()) {
                            try {
                                String rowValue = "";
                                String fieldName = headerDTO.getField();
                                if (studentFamily != null) {
                                    switch (fieldName) {
                                        case "fatherSms":
                                            if (studentFamily.getFatherSms() != null) {
                                                rowValue = YesNoEnum.getValue(studentFamily.getFatherSms(), schoolLanguageEnum);
                                            }
                                            break;
                                        case "motherSms":
                                            if (studentFamily.getMotherSms() != null) {
                                                rowValue = YesNoEnum.getValue(studentFamily.getMotherSms(), schoolLanguageEnum);
                                            }
                                            break;
                                        case "guardianSms":
                                            if (studentFamily.getGuardianSms() != null) {
                                                rowValue = YesNoEnum.getValue(studentFamily.getGuardianSms(), schoolLanguageEnum);
                                            }
                                            break;
                                        case "guardianRelation":
                                            if (studentFamily.getGuardianRelation() != null) {
                                                rowValue = RelationTypeEnum.getValue(studentFamily.getGuardianRelation(), schoolLanguageEnum);
                                            }
                                            break;
                                        case "liveWithGuardian":
                                            if (studentFamily.getLiveWithGuardian() != null) {
                                                rowValue = YesNoEnum.getValue(studentFamily.getLiveWithGuardian(), schoolLanguageEnum);
                                            }
                                            break;
                                        case "guardianAddress":
                                            if (StringUtils.isNotBlank(studentFamily.getGuardianAddressAreaId())) {
                                                rowValue = AddressAreaEnum.getValue(studentFamily.getGuardianAddressAreaId(), schoolLanguageEnum);
                                            }
                                            if (StringUtils.isNotBlank(studentFamily.getGuardianAddress())) {
                                                rowValue += studentFamily.getGuardianAddress();
                                            }
                                            break;
                                        case "emergencyRelation":
                                            if (studentFamily.getEmergencyRelation() != null) {
                                                rowValue = RelationTypeEnum.getValue(studentFamily.getEmergencyRelation(), schoolLanguageEnum);
                                            }
                                            break;
                                        case "emergencyAddress":
                                            if (StringUtils.isNotBlank(studentFamily.getEmergencyAddressAreaId())) {
                                                rowValue = AddressAreaEnum.getValue(studentFamily.getEmergencyAddressAreaId(), schoolLanguageEnum);
                                            }
                                            if (StringUtils.isNotBlank(studentFamily.getEmergencyAddress())) {
                                                rowValue += studentFamily.getEmergencyAddress();
                                            }
                                            break;
                                        case "secondEmergencyRelation":
                                            if (studentFamily.getSecondEmergencyRelation() != null) {
                                                rowValue = RelationTypeEnum.getValue(studentFamily.getSecondEmergencyRelation(), schoolLanguageEnum);
                                            }
                                            break;
                                        case "secondEmergencyAddress":
                                            if (StringUtils.isNotBlank(studentFamily.getSecondEmergencyAddressAreaId())) {
                                                rowValue = AddressAreaEnum.getValue(studentFamily.getSecondEmergencyAddressAreaId(), schoolLanguageEnum);
                                            }
                                            if (StringUtils.isNotBlank(studentFamily.getSecondEmergencyAddress())) {
                                                rowValue += studentFamily.getSecondEmergencyAddress();
                                            }
                                            break;
                                        default:
                                            // 使用反射获取字段值
                                            Field field = StudentFamilyEntity.class.getDeclaredField(fieldName);
                                            field.setAccessible(true);
                                            Object value = field.get(studentFamily);
                                            rowValue = value != null ? value.toString() : "";
                                            break;
                                    }
                                }
                                rowData.add(rowValue);
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                rowData.add("");
                            }
                        }
                        break;
                    case "enrollment":
                        //入学记录
                        StudentEnrollEntity studentEnroll = studentEnrollMap.get(exportDTO.getId());
                        for (ExportHeaderDTO headerDTO : exportHeaderModuleDTO.getHeaders()) {
                            try {
                                String rowValue = "";
                                String fieldName = headerDTO.getField();
                                if (studentEnroll != null) {
                                    // 使用反射获取字段值
                                    Field field = StudentEnrollEntity.class.getDeclaredField(fieldName);
                                    field.setAccessible(true);
                                    Object value = field.get(studentEnroll);
                                    rowValue = value != null ? value.toString() : "";
                                }
                                rowData.add(rowValue);
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                rowData.add("");
                            }
                        }
                        break;
                    case "medicalNotice":
                        //医护注意事项
                        StudentMedicalAttentionEntity studentMedicalAttention = studentMedicalAttentionMap.get(exportDTO.getId());
                        for (ExportHeaderDTO headerDTO : exportHeaderModuleDTO.getHeaders()) {
                            try {
                                String rowValue = "";
                                String fieldName = headerDTO.getField();
                                if (studentMedicalAttention != null) {
                                    // 使用反射获取字段值
                                    Field field = StudentMedicalAttentionEntity.class.getDeclaredField(fieldName);
                                    field.setAccessible(true);
                                    Object value = field.get(studentMedicalAttention);
                                    rowValue = value != null ? value.toString() : "";
                                }
                                rowData.add(rowValue);
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                rowData.add("");
                            }
                        }
                        break;
                    case "crossBorder":
                        //跨境学生
                        CrossBorderStudentEntity crossBorderStudent = crossBorderStudentMap.get(exportDTO.getId());
                        for (ExportHeaderDTO headerDTO : exportHeaderModuleDTO.getHeaders()) {
                            try {
                                String rowValue = "";
                                String fieldName = headerDTO.getField();
                                if (crossBorderStudent != null) {
                                    switch (fieldName) {
                                        case "isCrossBorder":
                                            rowValue = YesNoEnum.getValue(1, schoolLanguageEnum);
                                            break;
                                        case "mainlandCertificateType":
                                            if (crossBorderStudent.getMainlandCertificateType() != null) {
                                                if (crossBorderStudent.getMainlandCertificateType() == MainlandCertificateTypeEnum.OTHER.getCode()) {
                                                    if (StringUtils.isNotBlank(crossBorderStudent.getMainlandCertificateTypeOther())) {
                                                        rowValue = crossBorderStudent.getMainlandCertificateTypeOther();
                                                    }
                                                } else {
                                                    rowValue = MainlandCertificateTypeEnum.getValue(crossBorderStudent.getMainlandCertificateType(), schoolLanguageEnum);
                                                }
                                            }
                                            break;
                                        case "companionGender":
                                            if (crossBorderStudent.getCompanionGender() != null) {
                                                rowValue = GenderEnum.getValue(crossBorderStudent.getCompanionGender(), schoolLanguageEnum);
                                            }
                                            break;
                                        case "macauCertificateType":
                                            if (crossBorderStudent.getMacauCertificateType() != null) {
                                                if (crossBorderStudent.getMacauCertificateType() == MacauCertificateTypeEnum.OTHER.getCode()) {
                                                    if (StringUtils.isNotBlank(crossBorderStudent.getMacauCertificateTypeOther())) {
                                                        rowValue = crossBorderStudent.getMacauCertificateTypeOther();
                                                    }
                                                } else {
                                                    rowValue = MacauCertificateTypeEnum.getValue(crossBorderStudent.getMacauCertificateType(), schoolLanguageEnum);
                                                }
                                            }
                                            break;
                                        case "hkMacauPassVisaType":
                                            if (crossBorderStudent.getHkMacauPassVisaType() != null) {
                                                rowValue = MacauCertificateTypeEnum.getValue(crossBorderStudent.getHkMacauPassVisaType(), schoolLanguageEnum);
                                            }
                                            break;
                                        case "mainlandEntryCertificateType":
                                            if (crossBorderStudent.getMainlandEntryCertificateType() != null) {
                                                if (crossBorderStudent.getMainlandEntryCertificateType() == MainlandCertificateTypeEnum.OTHER.getCode()) {
                                                    if (StringUtils.isNotBlank(crossBorderStudent.getMainlandEntryCertificateTypeOther())) {
                                                        rowValue = crossBorderStudent.getMainlandEntryCertificateTypeOther();
                                                    }
                                                } else {
                                                    rowValue = MainlandCertificateTypeEnum.getValue(crossBorderStudent.getMainlandEntryCertificateType(), schoolLanguageEnum);
                                                }
                                            }
                                            break;
                                        default:
                                            // 使用反射获取字段值
                                            Field field = CrossBorderStudentEntity.class.getDeclaredField(fieldName);
                                            field.setAccessible(true);
                                            Object value = field.get(crossBorderStudent);
                                            rowValue = value != null ? value.toString() : "";
                                            break;
                                    }
                                } else {
                                    if (fieldName.equals("isCrossBorder")) {
                                        rowValue = YesNoEnum.getValue(0, schoolLanguageEnum);
                                    }
                                }
                                rowData.add(rowValue);
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                rowData.add("");
                            }
                        }
                        break;
                    case "dateRecord":
                        //日期记录
                        StudentDateRecordEntity studentDateRecord = studentDateRecordMap.get(exportDTO.getId());
                        for (ExportHeaderDTO headerDTO : exportHeaderModuleDTO.getHeaders()) {
                            try {
                                String rowValue = "";
                                String fieldName = headerDTO.getField();
                                if (studentDateRecord != null) {
                                    // 使用反射获取字段值
                                    Field field = StudentDateRecordEntity.class.getDeclaredField(fieldName);
                                    field.setAccessible(true);
                                    Object value = field.get(studentDateRecord);
                                    rowValue = value != null ? value.toString() : "";
                                }
                                rowData.add(rowValue);
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                rowData.add("");
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
            result.add(rowData);
        }
        return result;
    }

    @Override
    public String exportStudentHeader(Long schoolId, Long userId) {
        QueryWrapper<ExportHeaderEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ExportHeaderEntity::getSchoolId, schoolId)
                .eq(ExportHeaderEntity::getUserId, userId)
                .eq(ExportHeaderEntity::getType, ExportHeaderTypeEnum.STUDENT_INFO.getCode());
        ExportHeaderEntity exportHeader = exportHeaderService.getOne(wrapper);
        if (exportHeader != null) {
            return exportHeader.getHeader();
        }
        return null;
    }

    @Override
    public List<StudentListResModel> listByClassId(Long classId) {
        //
        SysClass sysClass = sysClassDao.selectById(classId);
        if(sysClass == null)
        {
            return new ArrayList<>();
        }
        if (sysClass.getUpgrade() == 1) {
            List<SysClassUpgradeRel> upgradeRelByClassId = sysClassUpgradeRelService.getSysClassUpgradeRelByClassId(classId);
            if (CollectionUtils.isEmpty(upgradeRelByClassId)) {
                return new ArrayList<>();
            }
            //转map
            Map<Long, SysClassUpgradeRel> map = upgradeRelByClassId.stream().collect(Collectors.toMap(SysClassUpgradeRel::getStudentId, Function.identity()));
            List<Long> ids = upgradeRelByClassId.stream().map(SysClassUpgradeRel::getStudentId).collect(Collectors.toList());
            List<StudentEntity> list = this.list(new LambdaQueryWrapper<StudentEntity>()
                    .in(StudentEntity::getId, ids)
                    .eq(StudentEntity::getDeleted, 0));
            if (!CollectionUtils.isEmpty(list)) {
                List<StudentListResModel> result = list.stream().map(item -> {
                    StudentListResModel resModel = new StudentListResModel();
                    resModel.setId(item.getId());
                    resModel.setChineseName(item.getChineseName());
                    resModel.setEnglishName(item.getEnglishName());
                    resModel.setStudentNo(item.getStudentNo());
                    SysClassUpgradeRel sysClassUpgradeRel = map.get(item.getId());
                    if (sysClassUpgradeRel != null) {
                        resModel.setSeatNo(sysClassUpgradeRel.getSeatNo());
                    }
                    return resModel;
                }).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(result)) {
                    return new ArrayList<>();
                }
                result.sort(Comparator.nullsLast(
                        Comparator.comparing(
                                StudentListResModel::getSeatNo,
                                Comparator.nullsLast(Comparator.naturalOrder())
                        )
                ));
                return result;
            }
            return new ArrayList<>();
        } else {
            return baseMapper.listByClassId(classId);
        }
    }

    @Override
    public StudentResModel getStudentIdByNameAndSchoolId(String studentName, Long schoolId) {
        StudentEntity entity = this.getOne(new LambdaQueryWrapper<StudentEntity>()
                .eq(StudentEntity::getSchoolId, schoolId)
                .eq(StudentEntity::getChineseName, studentName)
                .eq(StudentEntity::getDeleted, 0));
        if (entity == null) {
            return null;
        }
        StudentResModel studentResModel = BeanConvertUtil.convert(entity, StudentResModel.class);
        return studentResModel;
    }

    @Override
    public void uploadImage(MultipartFile file, Long schoolId, Long studentId) {
        String fileName = file.getOriginalFilename();
        String suffix = FileUtil.getSuffix(fileName).toLowerCase();
        if ("jpg".equalsIgnoreCase(suffix)) {
            long fileSize = file.getSize();
            if (fileSize < fileConfig.getMinFileSize() || fileSize > fileConfig.getMaxFileSize()) {
                throw new BusinessException(LanguageConstants.IMAGE_SIZE_ERROR);
            } else {
                StudentEntity studentEntity = this.getById(studentId);
                if (studentEntity != null) {
                    try {
                        //照片保存位置
                        String saveFilePath = File.separator + studentEntity.getSchoolId() + File.separator + FileTypeEnum.STUDENT_IMAGE.getTypePath() + File.separator;
                        String imageUrl = saveStudentImage(saveFilePath, studentEntity.getStudentNo(), file.getInputStream(), suffix);
                        if (StringUtils.isNotBlank(imageUrl)) {
                            //更新学生照片
                            studentEntity.setImgUrl(imageUrl);
                            this.updateById(studentEntity);
                        }
                    } catch (IOException e) {
                        throw new BusinessException(LanguageConstants.IMAGE_SAVE_ERROR);
                    }
                }
            }
        } else {
            throw new BusinessException(LanguageConstants.IMAGE_FORMAT_ERROR);
        }
    }

    @Override
    public Long batchImageUpload(StudentImageBatchUploadReqModel resModel) {
        ImportTaskEntity importTask = new ImportTaskEntity();
        importTask.setSchoolId(resModel.getSchoolId());
        importTask.setFileName(resModel.getFileName());
        importTask.setType(ImportTaskTypeEnum.STUDENT_IMAGE.getCode());
        importTask.setFileUrl(fileConfig.getFileRootPath() + resModel.getFileUrl());
        importTask.setTotalCount(0);
        importTask.setFailCount(0);
        importTask.setSuccessCount(0);
        importTaskService.save(importTask);
        ImportImageTaskDTO importImageTaskDTO = new ImportImageTaskDTO();
        importImageTaskDTO.setTaskId(importTask.getId());
        importImageTaskDTO.setLanguageEnum(SchoolLanguageEnum.getDefValue(LanguageUtil.getCurrentLanguage()));
        imageQueueList.offer(importImageTaskDTO);
        return importTask.getId();
    }

    @Override
    public void queryUntreatedStudentImportTask() {
        LambdaQueryWrapper<ImportTaskEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ImportTaskEntity::getStatus, ImportTaskStatusEnum.UNTREATED.getCode())
                .eq(ImportTaskEntity::getType, ImportTaskTypeEnum.STUDENT_IMAGE.getCode())
                .eq(ImportTaskEntity::getDeleted, 0);
        List<ImportTaskEntity> taskEntities = importTaskService.list(wrapper);
        if (CollectionUtils.isNotEmpty(taskEntities)) {
            taskEntities.forEach(entity -> {
                ImportImageTaskDTO importImageTaskDTO = new ImportImageTaskDTO();
                importImageTaskDTO.setTaskId(entity.getId());
                importImageTaskDTO.setLanguageEnum(SchoolLanguageEnum.getDefValue(LanguageUtil.getCurrentLanguage()));
                imageQueueList.offer(importImageTaskDTO);
            });
        }
    }

    @Override
    public void handleStudentImportBatchUpload() {
        while (true) {
            ImportImageTaskDTO importImageTaskDTO = imageQueueList.poll();
            if (importImageTaskDTO != null) {
                ImportTaskEntity importTask = importTaskService.getById(importImageTaskDTO.getTaskId());
                String fileUrl = importTask.getFileUrl();
                importTask.setStatus(ImportTaskStatusEnum.IN_PROCESS.getCode());
                importTaskService.updateById(importTask);
                if (StringUtils.isNotBlank(fileUrl)) {
                    //查看文件是否存在
                    File faceFile = new File(fileUrl);
                    if (faceFile.exists()) {
                        log.info("==========开始处理文件【" + fileUrl + "】=========");
                        try {
                            //获取学生信息
                            Map<String, List<StudentEntity>> studentMap = new HashMap<>();
                            LambdaQueryWrapper<StudentEntity> wrapper = new LambdaQueryWrapper<>();
                            wrapper.eq(StudentEntity::getSchoolId, importTask.getSchoolId())
                                    .eq(StudentEntity::getDeleted, 0);
                            List<StudentEntity> studentEntities = this.list(wrapper);
                            if (CollectionUtils.isNotEmpty(studentEntities)) {
                                studentMap = studentEntities.stream().collect(Collectors.groupingBy(studentEntity -> {
                                    String studentNo = studentEntity.getEducationNo();
                                    if (StringUtils.isNotBlank(studentNo)) {
                                        return studentNo.length() >= 7 ? studentNo.substring(0, 7) : studentNo;
                                    }
                                    return null;
                                }));
                            }
                            //开始处理文件
                            ZipFile zf = new ZipFile(faceFile, Charset.forName("GBK"));
                            Enumeration<? extends ZipEntry> entries = zf.entries();
                            int failCount = 0;
                            int totalCount = 0;
                            while (entries.hasMoreElements()) {
                                ZipEntry entry = entries.nextElement();
                                if (!entry.isDirectory()) {
                                    //只处理文件
                                    String fileName = entry.getName().substring(entry.getName().lastIndexOf("/") + 1);
                                    String suffix = FileUtil.getSuffix(fileName).toLowerCase();
                                    if (fileSuffix.contains(suffix) && !fileName.startsWith("._")) {
                                        //过滤苹果电脑压缩产生的隐藏文件（“._”开头）和不是图片的文件
                                        //只处理图片文件
                                        failCount += checkAndUploadFile(importTask, zf.getInputStream(entry), studentMap, fileName, entry.getSize(), importImageTaskDTO.getLanguageEnum());
                                        totalCount++;
                                    }
                                }
                            }
                            zf.close();
                            //更新任务信息
                            importTask.setTotalCount(totalCount);
                            importTask.setFailCount(failCount);
                            importTask.setSuccessCount(totalCount - failCount);
                        } catch (Exception e) {
                            log.info("==========文件【" + fileUrl + "】解压失败=========");
                            e.printStackTrace();
                        } finally {
                            //处理完成删除临时文件
                            FileUtil.del(fileUrl);
                        }
                    } else {
                        log.info("==========文件【" + fileUrl + "】不存在=========");
                    }
                }
                importTask.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
                importTaskService.updateById(importTask);
                log.info("==========文件【" + fileUrl + "】处理完成=========");
            } else {
                //无任务时休眠5秒
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public List<StudentEntity> getStudentListByClassId(Long classId) {
        LambdaQueryWrapper<StudentEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentEntity::getClassId, classId)
                .eq(StudentEntity::getDeleted, 0);
        return this.list(wrapper);
    }

    @Override
    public void updateStudentByHealthDeclare(StudentHealthDeclareAddReqModel reqModel) {
        StudentEntity studentEntity = this.getById(reqModel.getStudentId());
        studentEntity.setIntention(reqModel.getIntention());
        if (reqModel.getIntention() != 1) {
            studentEntity.setProveImgUrl(reqModel.getProveImgUrl());
        } else {
            studentEntity.setProveImgUrl(null);
        }
        if (reqModel.getEmergencyHospital() != null && reqModel.getEmergencyHospital() > 0L) {
            studentEntity.setEmergencyHospital(reqModel.getEmergencyHospital());
        }
        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(reqModel.getHospitalCardNo())) {
            studentEntity.setEmergencyHospitalCardId(reqModel.getHospitalCardNo());
        }
        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(reqModel.getGoldCardNo())) {
            studentEntity.setGoldCardNo(reqModel.getGoldCardNo());
        }
        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(reqModel.getIdNo())) {
            studentEntity.setIdCardNo(reqModel.getIdNo());
        }
        this.updateById(studentEntity);
    }


    private int checkAndUploadFile(ImportTaskEntity importTask, InputStream inputStream, Map<String, List<StudentEntity>> studentMap, String fileName, long fileSize, SchoolLanguageEnum languageEnum) {
        try {
            languageUtil.setLanguage(languageEnum.getCode());
            ImportRecordEntity importRecord = new ImportRecordEntity();
            importRecord.setTaskId(importTask.getId());
            importRecord.setIncorrectLineno(fileName);
            String suffix = FileUtil.getSuffix(fileName);
            String studentNo = fileName.replace("." + suffix, "");
            if ("jpg".equalsIgnoreCase(suffix)) {
                if (fileSize < fileConfig.getMinFileSize() || fileSize > fileConfig.getMaxFileSize()) {
                    importRecord.setIncorrectReason(languageUtil.getMessage(LanguageConstants.IMAGE_SIZE_ERROR));
                } else {
                    List<StudentEntity> studentEntities = studentMap.get(studentNo);
                    if (CollectionUtils.isNotEmpty(studentEntities)) {
                        if (studentEntities.size() > 1) {
                            importRecord.setIncorrectReason(languageUtil.getMessage(LanguageConstants.IMAGE_IMPORT_STUDENT_NO_REPEAT_ERROR));
                        } else {
                            StudentEntity studentEntity = studentEntities.get(0);
                            //照片保存位置
                            String saveFilePath = File.separator + importTask.getSchoolId() + File.separator + FileTypeEnum.STUDENT_IMAGE.getTypePath() + File.separator;
                            String imageUrl = saveStudentImage(saveFilePath, studentEntity.getStudentNo(), inputStream, suffix);
                            if (StringUtils.isNotBlank(imageUrl)) {
                                //更新学生照片
                                studentEntity.setImgUrl(imageUrl);
                                this.updateById(studentEntity);
                            }
                        }
                    } else {
                        importRecord.setIncorrectReason(String.format(languageUtil.getMessage(LanguageConstants.STUDENT_NO_EXISTS), studentNo));
                    }
                }
            } else {
                importRecord.setIncorrectReason(languageUtil.getMessage(LanguageConstants.IMAGE_FORMAT_ERROR));
            }
            if (StringUtils.isNotBlank(importRecord.getIncorrectReason())) {
                importRecordService.save(importRecord);
                //更新失败数量
                return 1;
            }
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("处理文件：{}失败", fileName);
        }
        return 0;
    }

    private String saveStudentImage(String saveFilePath, String studentNo, InputStream inputStream, String suffix) {
        if (StringUtils.isNotBlank(studentNo)) {
            String imagePath = fileConfig.getFileRootPath() + saveFilePath + studentNo + "." + suffix;
            FileUtil.writeFromStream(inputStream, imagePath);
            return imagePath.replace(fileConfig.getFileRootPath(), "");
        }
        return null;
    }

    @Override
    public List<StudentScorePageResModel> score(StudentScoreReqModel resModel) {
        List<StudentScorePageResModel> result = new ArrayList<>();
        switch (resModel.getType()) {
            case 1:
                //平时成绩
                result = studentUsuallyTaskService.studentScore(resModel);
                break;
            case 2:
                //考试成绩
                result = studentExamTaskService.studentScore(resModel);
                break;
            case 3:
                //毕业成绩
                result = studentGraduateExamTaskService.studentScore(resModel);
                break;
            default:
                break;
        }
        if (CollectionUtils.isNotEmpty(result)) {
            for (StudentScorePageResModel studentScorePageResModel : result) {
                studentScorePageResModel.setSchoolYear(resModel.getSchoolYear());
                studentScorePageResModel.setType(resModel.getType());
            }
        }
        return result;
    }

    @Override
    public String downloadImage(Long schoolId, StudentImageDownloadReqModel resModel) {
        SysClass sysClass = sysClassDao.selectById(resModel.getClassId());
        if (sysClass != null) {
            //查询学生信息
            List<StudentEntity> students = new ArrayList<>();
            if (sysClass.getUpgrade() == 1) {
                //已升级班级学生信息去sys_class_upgrade_rel表查询
                QueryWrapper<SysClassUpgradeRel> wrapper = new QueryWrapper<>();
                wrapper.lambda().eq(SysClassUpgradeRel::getClassId, resModel.getClassId());
                List<SysClassUpgradeRel> sysClassUpgradeRelEntities = sysClassUpgradeRelService.list(wrapper);
                if (CollectionUtils.isNotEmpty(sysClassUpgradeRelEntities)) {
                    List<Long> studentIds = sysClassUpgradeRelEntities.stream().map(SysClassUpgradeRel::getStudentId).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(studentIds)) {
                        QueryWrapper<StudentEntity> queryWrapper = new QueryWrapper<>();
                        queryWrapper.lambda().eq(StudentEntity::getSchoolId, schoolId)
                                .in(StudentEntity::getId, studentIds);
                        students = this.list(queryWrapper);
                        if (CollectionUtils.isNotEmpty(students)) {
                            Map<Long, SysClassUpgradeRel> upgradeRelMap = sysClassUpgradeRelEntities.stream().collect(Collectors.toMap(SysClassUpgradeRel::getStudentId, sysClassUpgradeRelEntity -> sysClassUpgradeRelEntity));
                            for (StudentEntity studentEntity : students) {
                                studentEntity.setSeatNo(upgradeRelMap.get(studentEntity.getId()).getSeatNo());
                            }
                        }
                    }
                }
            } else {
                QueryWrapper<StudentEntity> wrapper = new QueryWrapper<>();
                wrapper.lambda().eq(StudentEntity::getSchoolId, schoolId)
                        .eq(StudentEntity::getClassId, resModel.getClassId());
                students = this.list(wrapper);
            }
            if (CollectionUtils.isNotEmpty(students)) {
                List<StudentEntity> expotStudents = students.stream().filter(studentEntity -> StringUtils.isNotBlank(studentEntity.getImgUrl())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(expotStudents)) {
                    List<StudentImageDownloadDTO> studentPhotoPaths = new ArrayList<>();
                    StudentImageDownloadTypeEnum typeEnum = StudentImageDownloadTypeEnum.toEnum(resModel.getType());
                    if (typeEnum != null) {
                        switch (typeEnum) {
                            case STUDENT_NUMBER:
                                //命名方式【班内号_学生编号】部分学生的 班内号缺失，提示：部分学生的 班内号未填写，请补齐后下载
                                long count = expotStudents.stream().filter(studentEntity -> studentEntity.getSeatNo() == null || studentEntity.getSeatNo() <= 0).count();
                                if (count > 0) {
                                    throw new BusinessException(languageUtil.getMessage(LanguageConstants.STUDENT_SEAT_NO_NOT_EXISTS));
                                }
                                for (StudentEntity studentEntity : expotStudents) {
                                    StudentImageDownloadDTO studentImageDownloadDTO = new StudentImageDownloadDTO();
                                    studentImageDownloadDTO.setImgUrl(studentEntity.getImgUrl());
                                    studentImageDownloadDTO.setImgName(studentEntity.getSeatNo() + "_" + studentEntity.getStudentNo() + ".jpg");
                                    studentPhotoPaths.add(studentImageDownloadDTO);
                                }
                                break;
                            case EDUCATION_BUREAU_NUMBER:
                                //命名方式【班内号_教青局编号】部分学生的 班内号或者教青局未填写，提示：部分学生的 班内号/教青局编号未填写，请补齐后下载
                                count = expotStudents.stream().filter(studentEntity -> studentEntity.getSeatNo() == null || studentEntity.getSeatNo() <= 0
                                        || StringUtils.isBlank(studentEntity.getEducationNo())).count();
                                if (count > 0) {
                                    throw new BusinessException(languageUtil.getMessage(LanguageConstants.STUDENT_SEAT_NO_OR_EDUCATION_NO_NOT_EXISTS));
                                }
                                for (StudentEntity studentEntity : expotStudents) {
                                    StudentImageDownloadDTO studentImageDownloadDTO = new StudentImageDownloadDTO();
                                    studentImageDownloadDTO.setImgUrl(studentEntity.getImgUrl());
                                    studentImageDownloadDTO.setImgName(studentEntity.getSeatNo() + "_" + studentEntity.getEducationNo() + ".jpg");
                                    studentPhotoPaths.add(studentImageDownloadDTO);
                                }
                                break;
                            case CLASS_NAME_STUDENT_NUMBER_CHINESE_NAME:
                                //命名方式【班级名称_班内号_中文姓名】部分学生的 班内号缺失，提示：部分学生的 班内号未填写，请补齐后下载
                                count = expotStudents.stream().filter(studentEntity -> studentEntity.getSeatNo() == null || studentEntity.getSeatNo() <= 0).count();
                                if (count > 0) {
                                    throw new BusinessException(languageUtil.getMessage(LanguageConstants.STUDENT_SEAT_NO_NOT_EXISTS));
                                }
                                //获取班级信息
                                List<Long> classIds = expotStudents.stream().map(StudentEntity::getClassId).collect(Collectors.toList());
                                Map<Long, SysClass> classMap = new HashMap<>();
                                Map<Long, String> gradeNameMap = new HashMap<>();
                                if (CollectionUtils.isNotEmpty(classIds)) {
                                    List<SysClass> sysClasses = sysClassDao.selectBatchIds(classIds);
                                    if (CollectionUtils.isNotEmpty(sysClasses)) {
                                        classMap = sysClasses.stream().collect(Collectors.toMap(SysClass::getId, sysClass1 -> sysClass1));
                                        List<Long> gradeIds = sysClasses.stream().map(SysClass::getGradeGroup).collect(Collectors.toList());
                                        if (CollectionUtils.isNotEmpty(gradeIds)) {
                                            gradeNameMap = gradeGroupService.getNamesByIds(gradeIds);
                                        }
                                    }
                                }
                                for (StudentEntity studentEntity : expotStudents) {
                                    StudentImageDownloadDTO studentImageDownloadDTO = new StudentImageDownloadDTO();
                                    studentImageDownloadDTO.setImgUrl(studentEntity.getImgUrl());
                                    studentImageDownloadDTO.setImgName(gradeNameMap.get(classMap.get(studentEntity.getClassId()).getGradeGroup()) + classMap.get(studentEntity.getClassId()).getClassName() + "_" + studentEntity.getSeatNo() + "_" + studentEntity.getChineseName() + ".jpg");
                                    studentPhotoPaths.add(studentImageDownloadDTO);
                                }
                                break;
                            default:
                                //命名方式【系统内相片名称】
                                for (StudentEntity studentEntity : expotStudents) {
                                    StudentImageDownloadDTO studentImageDownloadDTO = new StudentImageDownloadDTO();
                                    studentImageDownloadDTO.setImgUrl(studentEntity.getImgUrl());
                                    studentImageDownloadDTO.setImgName(studentEntity.getStudentNo() + ".jpg");
                                    studentPhotoPaths.add(studentImageDownloadDTO);
                                }
                                break;
                        }
                        if (CollectionUtils.isNotEmpty(studentPhotoPaths)) {
                            return exportPhotosToZip(studentPhotoPaths, schoolId);
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 导出学生照片为ZIP包
     *
     * @param studentPhotoPaths 学生照片路径列表
     * @param schoolId          学校id
     * @throws IOException 如果导出失败
     */
    private String exportPhotosToZip(List<StudentImageDownloadDTO> studentPhotoPaths, Long schoolId) {
        try {
            String randomId = UUID.randomUUID().toString();
            String fileNameNotExt = "学生相片导出";
            String uploadFileName = MD5Util.md5Encode(fileNameNotExt + randomId) + ".zip";
            String zipFilePath = fileConfig.getFileRootPath() + File.separator + schoolId + File.separator + FileTypeEnum.EXPORT.getTypePath() + File.separator + uploadFileName;
            // 确保路径存在
            File zipFileDir = new File(zipFilePath).getParentFile();
            if (zipFileDir != null && !zipFileDir.exists()) {
                zipFileDir.mkdirs(); // 创建目录
            }
            try (FileOutputStream fos = new FileOutputStream(zipFilePath);
                 ZipOutputStream zipOut = new ZipOutputStream(fos)) {
                for (StudentImageDownloadDTO studentImageDownloadDTO : studentPhotoPaths) {
                    File photoFile = new File(fileConfig.getFileRootPath() + File.separator + studentImageDownloadDTO.getImgUrl());
                    if (photoFile.exists()) {
                        try (FileInputStream fis = new FileInputStream(photoFile)) {
                            // 创建ZIP条目
                            ZipEntry zipEntry = new ZipEntry(studentImageDownloadDTO.getImgName());
                            zipOut.putNextEntry(zipEntry);
                            // 写入文件内容
                            byte[] bytes = new byte[8192]; // 增大缓冲区大小
                            int length;
                            while ((length = fis.read(bytes)) >= 0) {
                                zipOut.write(bytes, 0, length);
                            }
                            // 显式关闭当前条目
                            zipOut.closeEntry();
                        }
                    }
                }
            }
            return zipFilePath.replace(fileConfig.getFileRootPath(), "");
        } catch (Exception e) {
            throw new BusinessException(String.format(languageUtil.getMessage(LanguageConstants.STUDENT_PHOTO_EXPORT_FAIL), e.getMessage()));
        }
    }

    @Override
    public void updateClassId(List<Long> ids, Long classId) {
        this.update(new LambdaUpdateWrapper<StudentEntity>()
                .set(StudentEntity::getClassId, classId)
                .in(StudentEntity::getId, ids));
    }

    @Override
    public void updateStudentStatusByClassId(Long classId) {
        LambdaUpdateWrapper<StudentEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(StudentEntity::getClassId, classId)
                .eq(StudentEntity::getDeleted, 0)
                .eq(StudentEntity::getStatus, 1)
                .set(StudentEntity::getStatus, 2);
        this.update(updateWrapper);
    }

    @Override
    public Result<String> getStudentImagePdf(StudentImagePDFReqModel reqModel) {
        String respStr = reportSupport.getReport(ReportSupport.STUDENT_IMAGE_PDF, JSONObject.parseObject(JSONObject.toJSONString(reqModel)));
        JSONObject respJson = JSON.parseObject(respStr);
        if (respJson.getInteger("error") == 10000) {
            return Result.success(respJson.getString("data"));
        }
        return Result.failed(ResultCode.FAILED.getCode(), respJson.getString("message"));
    }

    /**
     * 校验学生编号是否重复
     */
    private void checkStudentNoDuplicate(Long id, Long schoolId, String studentNo) {
        if (studentNo != null) {
            LambdaQueryWrapper<StudentEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(StudentEntity::getSchoolId, schoolId)
                    .eq(StudentEntity::getStudentNo, studentNo)
                    .eq(StudentEntity::getDeleted, 0);
            if (id != null) {
                wrapper.ne(StudentEntity::getId, id);
            }
            if (this.count(wrapper) > 0) {
                throw new BusinessException(LanguageConstants.STUDENT_NUMBER_EXISTS);
            }
        }
    }

    /**
     * 校验教青局编号是否重复
     */
    private void checkEducationNoDuplicate(Long id, Long schoolId, String educationNo) {
        if (educationNo != null) {
            LambdaQueryWrapper<StudentEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(StudentEntity::getSchoolId, schoolId)
                    .eq(StudentEntity::getEducationNo, educationNo)
                    .eq(StudentEntity::getDeleted, 0);
            if (id != null) {
                wrapper.ne(StudentEntity::getId, id);
            }
            if (this.count(wrapper) > 0) {
                throw new BusinessException(LanguageConstants.EDUCATION_NO_EXISTS);
            }
        }
    }

    /**
     * 校验座位号是否重复
     */
    private void checkSeatNoDuplicate(Long id, Long classId, Integer seatNo) {
        if (classId != null && seatNo != null) {
            LambdaQueryWrapper<StudentEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(StudentEntity::getClassId, classId)
                    .eq(StudentEntity::getSeatNo, seatNo)
                    .eq(StudentEntity::getDeleted, 0);
            if (id != null) {
                wrapper.ne(StudentEntity::getId, id);
            }
            if (this.count(wrapper) > 0) {
                throw new BusinessException(LanguageConstants.CLASS_SEAT_NO_EXISTS);
            }
        }
    }

    @Override
    public Map<String, StudentEntity> getStudentMapByStudentNos(Long schoolId, List<String> studentNos) {
        if (CollectionUtils.isEmpty(studentNos) || schoolId == null) {
            return Collections.emptyMap();
        }
        LambdaQueryWrapper<StudentEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(StudentEntity::getStudentNo, studentNos)
                .eq(StudentEntity::getSchoolId, schoolId)
                .eq(StudentEntity::getDeleted, 0);
        List<StudentEntity> studentList = this.list(wrapper);
        if (CollectionUtils.isEmpty(studentList)) {
            return Collections.emptyMap();
        }
        return studentList.stream().collect(Collectors.toMap(StudentEntity::getStudentNo, student -> student, (key1, key2) -> key1));
    }
}