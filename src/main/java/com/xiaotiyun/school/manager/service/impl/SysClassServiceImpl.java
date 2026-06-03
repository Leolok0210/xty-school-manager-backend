package com.xiaotiyun.school.manager.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.*;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.ClassUtils;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.basic.util.LanguageUtils;
import com.xiaotiyun.school.manager.basic.util.SemesterUtils;
import com.xiaotiyun.school.manager.dao.GradeGroupMapper;
import com.xiaotiyun.school.manager.dao.SysClassDao;
import com.xiaotiyun.school.manager.dao.UserDao;
import com.xiaotiyun.school.manager.dao.UserSchoolRelDao;
import com.xiaotiyun.school.manager.handler.ExportFileHandler;
import com.xiaotiyun.school.manager.helper.UserAuthHelper;
import com.xiaotiyun.school.manager.helper.WxHelper;
import com.xiaotiyun.school.manager.listener.SysClassImportEnUsListener;
import com.xiaotiyun.school.manager.listener.SysClassImportListener;
import com.xiaotiyun.school.manager.listener.SysClassImportPtPtListener;
import com.xiaotiyun.school.manager.model.dto.ImportRecordSaveDTO;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.excel.*;
import com.xiaotiyun.school.manager.model.req.SysClassQueryReqModel;
import com.xiaotiyun.school.manager.model.res.*;
import com.xiaotiyun.school.manager.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SysClassServiceImpl extends ServiceImpl<SysClassDao, SysClass> implements SysClassService {

    @Autowired
    private SysClassDao sysClassDao;

    @Resource
    private SchoolMajorService schoolMajorService;

    @Resource
    private UserSchoolRelDao userSchoolRelDao;

    @Resource
    private GradeGroupMapper gradeGroupMapper;

    @Resource
    private StudentService studentService;

    @Resource
    private SysClassUpgradeRelService sysClassUpgradeRelService;

    @Resource
    private UserClassRelService userClassRelService;

    @Resource
    private SystemSettingService systemSettingService;

    @Resource
    private ImportTaskService importTaskService;
    @Resource
    private ImportRecordService importRecordService;

    @Resource(name = "importExecutor")
    private ThreadPoolTaskExecutor importExecutor;

    @Autowired
    private ExportFileHandler exportFileHandler;

    @Resource
    private LanguageUtil languageUtil;

    @Resource
    private UserDao userDao;


    @Autowired
    private UserAuthHelper userAuthHelper;

    @Autowired
    private ClassUtils classUtils;



    @Resource
    private WxHelper wxHelper;

    @Override
    public Result<Boolean> createSysClasses(List<SysClass> sysClasses, long schoolId) {
        List<Long> classIds = new ArrayList<>();
        for (SysClass sysClass : sysClasses)
        {
            //暂时不加事务
            Integer maxNumber = getMaxClassSerialNumberByGradeGroupAndSid(sysClass.getGradeGroup(), sysClass.getSid(), schoolId);
            sysClass.setSchoolId(schoolId);
            sysClass.setClassSerialNumber(maxNumber == null ? 1 : maxNumber+1);
            if(org.springframework.util.StringUtils.isEmpty(sysClass.getClassName()))
            {
                //最终展示的班级名称= 级组名称+班级序号：比如小六1班
                sysClass.setClassName(sysClass.getClassSerialNumber() +"班");
            }
            List<SysClass> oldClass = getSysClassBySchoolIdAndClassNameAndSidAndGradeGroupId(schoolId, sysClass.getClassName(), sysClass.getSid(), sysClass.getGradeGroup());
            if(oldClass != null && !oldClass.isEmpty())
            {
                throw new BusinessMessageException(String.format(languageUtil.getMessage(LanguageConstants.CLASS_NAME_EXISTS), sysClass.getClassName()));
            }
            sysClass.setCreateTime(LocalDateTime.now());
            sysClass.setUpdateTime(LocalDateTime.now());
            sysClass.setDeleted(0L);
            // 查询级组信息
            GradeGroup gradeGroup = gradeGroupMapper.selectById(sysClass.getGradeGroup());
            if (gradeGroup == null) {
                return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.PARAM_ERROR));
            }

            // 计算班级编号学部的K,P,S编号代码，还需要根据system_setting表里的departments里的type值来判断，如果有设置type，则显示type，没设置type,才根据枚举显示

            // 计算班级编号
            String classNumber = classUtils.getClassNumber(schoolId, gradeGroup.getGrade(), sysClass.getClassSerialNumber(), sysClass.getDepartment());
            sysClass.setClassNumber(classNumber);
            // 根据级组信息，确定是否需要填 专业名称 或 文理科
            if (gradeGroup.getProfessionalSubject() == 1 && sysClass.getProfessionalId() == null) {
                return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.PARAM_ERROR));
            }
            if (gradeGroup.getProfessionalSubject() > 1 &&
                    gradeGroup.getArtsScienceType() == 1 && sysClass.getArtsScience() == null) {
                return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.PARAM_ERROR));
            }
            // 初始化文理科
            if (sysClass.getArtsScience() == null) {
                sysClass.setArtsScience(0);
            }
            save(sysClass);
            classIds.add(sysClass.getId());
        }
        wxHelper.crateOrUpdateParents(schoolId, classIds, null, EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_CLASS);
        return Result.success();
    }

    @Override
    public void updateSysClass(SysClass sysClass) {
        sysClass.setUpdateTime(LocalDateTime.now());
        updateById(sysClass);
    }

    @Override
    public void deleteSysClass(Long id) {
        //逻辑删除
        removeById(id);
        // 删除用户班级权限
        userClassRelService.remove(Wrappers.<UserClassRelEntity>lambdaQuery()
                .eq(UserClassRelEntity::getType, 3)
                .eq(UserClassRelEntity::getRelId, id));
    }

    @Override
    public SysClass getSysClassById(Long id) {
        return this.baseMapper.selectById(id);
    }

    @Override
    public PageInfo<SysClassDetailResModel> getSysClassList(SysClassQueryReqModel reqModel) {
        //根据name查询专业
        SchoolMajor schoolMajor = null;
        if (StringUtils.isNotBlank(reqModel.getProfessional())) {
            List<SchoolMajor> schoolMajorByName = schoolMajorService.getSchoolMajorByName(reqModel.getProfessional(), reqModel.getSchoolId());
            if(!CollectionUtils.isEmpty(schoolMajorByName))
            {
                schoolMajor = schoolMajorByName.get(0);
            }else {
                PageInfo<SysClassDetailResModel> pageInfo = new PageInfo<>();
                pageInfo.setList(new ArrayList<>());
                return pageInfo;
            }
        }
        boolean commonUser = userAuthHelper.getCommonUser(reqModel.getUserId(), reqModel.getSchoolId());
        List<Long> classIds = null;
        if(commonUser)
        {
            classIds = userAuthHelper.getUserClassIds(reqModel.getUserId(), reqModel.getSchoolId());
            if(CollectionUtils.isEmpty(classIds))
            {
                PageInfo<SysClassDetailResModel> pageInfo = new PageInfo<>();
                pageInfo.setList(new ArrayList<>());
                return pageInfo;
            }
        }
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        LambdaQueryWrapper<SysClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(reqModel.getClassId() != null && reqModel.getClassId() > 0, SysClass::getId, reqModel.getClassId())
                .eq(SysClass::getDeleted, 0)
                .eq(SysClass::getSchoolId, reqModel.getSchoolId())
                .eq(StringUtils.isNotBlank(reqModel.getSid()), SysClass::getSid, reqModel.getSid())
                .eq(StringUtils.isNotBlank(reqModel.getClassNumber()), SysClass::getClassNumber, reqModel.getClassNumber())
                .eq(reqModel.getGradeGroup() != null && reqModel.getGradeGroup() > 0, SysClass::getGradeGroup, reqModel.getGradeGroup())
                .eq(reqModel.getClassSerialNumber() != null && reqModel.getClassSerialNumber() > 0, SysClass::getClassSerialNumber, reqModel.getClassSerialNumber())
                .eq(reqModel.getArtsScience() != null, SysClass::getArtsScience, reqModel.getArtsScience())
                .in(classIds != null && !classIds.isEmpty(), SysClass::getId, classIds)
                .eq(reqModel.getProfessionalVersion() != null, SysClass::getProfessionalVersion, reqModel.getProfessionalVersion());
        if(schoolMajor != null)
        {
            wrapper.eq(SysClass::getProfessionalId, schoolMajor.getId());
        }
        wrapper.orderByDesc(SysClass::getCreateTime);
        List<SysClass> sysClasses = this.baseMapper.selectList(wrapper);
        PageInfo<SysClass> pageInfo = PageInfo.of(sysClasses);
        if (ObjectUtils.isEmpty(sysClasses)) {
            PageInfo<SysClassDetailResModel> sysClassDetailResModelPageInfo = new PageInfo<>();
            sysClassDetailResModelPageInfo.setTotal(pageInfo.getTotal());
            sysClassDetailResModelPageInfo.setPages(pageInfo.getPages());
            sysClassDetailResModelPageInfo.setList(new ArrayList<>());
            return sysClassDetailResModelPageInfo;
        }
        // 获取所有老师信息
        List<Long> relIds = sysClasses.stream().map(SysClass::getHeadTeacher).collect(Collectors.toList());
        Map<Long, UserSchoolRelEntity> userSchoolRelEntityMap = userSchoolRelDao.selectList(Wrappers.<UserSchoolRelEntity>lambdaQuery().
                in(UserSchoolRelEntity::getId, relIds))
                .stream().collect(Collectors.toMap(UserSchoolRelEntity::getId, a -> a));
        List<SysClassDetailResModel> sysClassDetailResModels = pageInfo.getList().stream()
                .map(item -> {
                    SysClassDetailResModel resModel = new SysClassDetailResModel();
                    BeanUtils.copyProperties(item, resModel);
                    if(resModel.getProfessionalId() != null){
                        SchoolMajor schoolMajorById = schoolMajorService.getSchoolMajorById(resModel.getProfessionalId());
                        resModel.setProfessionalName(schoolMajorById == null ? "" : schoolMajorById.getMajorName());
                    }
                    if(userSchoolRelEntityMap.containsKey(resModel.getHeadTeacher())) {
                        resModel.setHeadTeacherName(userSchoolRelEntityMap.get(resModel.getHeadTeacher()).getUsername());
                    }
                    GradeGroup gradeGroup = gradeGroupMapper.selectById(resModel.getGradeGroup());
                    resModel.setGradeGroupName(gradeGroup == null ? "" : gradeGroup.getGradeGroupName());
                    resModel.setProfessionalSubject(gradeGroup == null ? 0 : gradeGroup.getProfessionalSubject());
                    if (gradeGroup != null && gradeGroup.getProfessionalSubject() > 1) {
                        if (gradeGroup.getArtsScienceType() == 1) {
                            resModel.setArtsScience(item.getArtsScience());
                        } else if (gradeGroup.getArtsScienceType() == 2) {
                            resModel.setArtsScience(0);
                        }
                    } else {
                        resModel.setArtsScience(0);
                    }
//                    //年级编号 + 年级 +班级序号
//                    //年级编号 取“系统设置”中每个学部的编号代码（K、P、S、F）
//                    //年级：对应的一年级、二年级
//                    //班级序号： A、B、C、D（对应1、2、3、4）
//                    String classNumber = ClassUtils.getClassNumber(gradeGroup == null ? "" : gradeGroup.getGrade(), resModel.getClassSerialNumber(), resModel.getDepartment());
//                    resModel.setClassNumber(classNumber);
                    return resModel;
                }).collect(Collectors.toList());
        PageInfo<SysClassDetailResModel> sysClassDetailResModelPageInfo = new PageInfo<>(sysClassDetailResModels);
        sysClassDetailResModelPageInfo.setTotal(pageInfo.getTotal());
        sysClassDetailResModelPageInfo.setPages(pageInfo.getPages());
        sysClassDetailResModelPageInfo.setList(sysClassDetailResModels);
        return sysClassDetailResModelPageInfo;
    }

    @Override
    public PageInfo<SysClassDetailResModel> getSysClassListByStudent(SysClassQueryReqModel reqModel) {
        //根据name查询专业
        SchoolMajor schoolMajor = null;
        if (StringUtils.isNotBlank(reqModel.getProfessional())) {
            List<SchoolMajor> schoolMajorByName = schoolMajorService.getSchoolMajorByName(reqModel.getProfessional(), reqModel.getSchoolId());
            if(!CollectionUtils.isEmpty(schoolMajorByName))
            {
                schoolMajor = schoolMajorByName.get(0);
            }else {
                PageInfo<SysClassDetailResModel> pageInfo = new PageInfo<>();
                pageInfo.setList(new ArrayList<>());
                return pageInfo;
            }
        }
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        LambdaQueryWrapper<SysClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(reqModel.getClassId() != null && reqModel.getClassId() > 0, SysClass::getId, reqModel.getClassId())
                .eq(SysClass::getDeleted, 0)
                .eq(SysClass::getSchoolId, reqModel.getSchoolId())
                .eq(StringUtils.isNotBlank(reqModel.getSid()), SysClass::getSid, reqModel.getSid())
                .eq(StringUtils.isNotBlank(reqModel.getClassNumber()), SysClass::getClassNumber, reqModel.getClassNumber())
                .eq(reqModel.getGradeGroup() != null && reqModel.getGradeGroup() > 0, SysClass::getGradeGroup, reqModel.getGradeGroup())
                .eq(reqModel.getClassSerialNumber() != null && reqModel.getClassSerialNumber() > 0, SysClass::getClassSerialNumber, reqModel.getClassSerialNumber())
                .eq(reqModel.getArtsScience() != null, SysClass::getArtsScience, reqModel.getArtsScience())
                .eq(reqModel.getProfessionalVersion() != null, SysClass::getProfessionalVersion, reqModel.getProfessionalVersion());
        if(schoolMajor != null)
        {
            wrapper.eq(SysClass::getProfessionalId, schoolMajor.getId());
        }
        wrapper.orderByDesc(SysClass::getCreateTime);
        List<SysClass> sysClasses = this.baseMapper.selectList(wrapper);
        PageInfo<SysClass> pageInfo = PageInfo.of(sysClasses);
        if (ObjectUtils.isEmpty(sysClasses)) {
            PageInfo<SysClassDetailResModel> sysClassDetailResModelPageInfo = new PageInfo<>();
            sysClassDetailResModelPageInfo.setTotal(pageInfo.getTotal());
            sysClassDetailResModelPageInfo.setPages(pageInfo.getPages());
            sysClassDetailResModelPageInfo.setList(new ArrayList<>());
            return sysClassDetailResModelPageInfo;
        }
        // 获取所有老师信息
        List<Long> relIds = sysClasses.stream().map(SysClass::getHeadTeacher).collect(Collectors.toList());
        Map<Long, UserSchoolRelEntity> userSchoolRelEntityMap = userSchoolRelDao.selectList(Wrappers.<UserSchoolRelEntity>lambdaQuery().
                        in(UserSchoolRelEntity::getId, relIds))
                .stream().collect(Collectors.toMap(UserSchoolRelEntity::getId, a -> a));
        List<SysClassDetailResModel> sysClassDetailResModels = pageInfo.getList().stream()
                .map(item -> {
                    SysClassDetailResModel resModel = new SysClassDetailResModel();
                    BeanUtils.copyProperties(item, resModel);
                    if(resModel.getProfessionalId() != null){
                        SchoolMajor schoolMajorById = schoolMajorService.getSchoolMajorById(resModel.getProfessionalId());
                        resModel.setProfessionalName(schoolMajorById == null ? "" : schoolMajorById.getMajorName());
                    }
                    if(userSchoolRelEntityMap.containsKey(resModel.getHeadTeacher())) {
                        resModel.setHeadTeacherName(userSchoolRelEntityMap.get(resModel.getHeadTeacher()).getUsername());
                    }
                    GradeGroup gradeGroup = gradeGroupMapper.selectById(resModel.getGradeGroup());
                    resModel.setGradeGroupName(gradeGroup == null ? "" : gradeGroup.getGradeGroupName());
                    resModel.setProfessionalSubject(gradeGroup == null ? 0 : gradeGroup.getProfessionalSubject());
                    return resModel;
                }).collect(Collectors.toList());
        PageInfo<SysClassDetailResModel> sysClassDetailResModelPageInfo = new PageInfo<>(sysClassDetailResModels);
        sysClassDetailResModelPageInfo.setTotal(pageInfo.getTotal());
        sysClassDetailResModelPageInfo.setPages(pageInfo.getPages());
        sysClassDetailResModelPageInfo.setList(sysClassDetailResModels);
        return sysClassDetailResModelPageInfo;
    }

    @Override
    public List<SysClassListResModel> getSysClassListBySchoolId(Long schoolId) {
        return sysClassDao.selectSysClassListBySchoolId(schoolId);
    }

    // 新增方法：根据学校ID和sid查询班级列表
    @Override
    public List<SysClassListResModel> getSysClassListBySchoolIdAndSid(Long schoolId, String sid,Integer department) {
        return sysClassDao.selectSysClassList(schoolId, sid,department);
    }

    @Override
    public List<SysClassListResModel> getSysClassListBySchoolIdAndSidAndGradeGroupId(Long schoolId, Long gradeGroupId, String sid) {
        return sysClassDao.selectSysClassListBySchoolIdAndSidAndGradeGroupId(schoolId, gradeGroupId,sid);
    }

    private List<SysClass> getListBySchoolIdAndSid(long school, String sid)
    {
        LambdaQueryWrapper<SysClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysClass::getSchoolId, school)
                .eq(SysClass::getDeleted, 0)
                .eq(SysClass::getUpgrade, 0)
                .eq(SysClass::getSid, sid);
        return this.baseMapper.selectList(wrapper);
    }

    @Override
    public List<String> promoteClasses(long schoolId) {
        List<String> error = new ArrayList<>();
        //上个学年
        String lastSemesterName = SemesterUtils.getPreviousSemesterName(LocalDate.now());
        //查询这个学年的班级
        List<SysClass> list = getListBySchoolIdAndSid(schoolId, lastSemesterName);
        if(CollectionUtils.isEmpty(list))
        {
            return error;
        }
        //级组
        List<Long> groupId = list.stream().map(SysClass::getGradeGroup).collect(Collectors.toList());
        List<GradeGroup> gradeGroups = gradeGroupMapper.selectBatchIds(groupId);
        //转map
        Map<Long, GradeGroup> gradeGroupMap = gradeGroups.stream().collect(Collectors.toMap(GradeGroup::getId, item -> item));
        String currentSemesterName = SemesterUtils.getCurrentSemesterName(LocalDate.now());
//        List<SysClass> currentList = getListBySchoolIdAndSid(schoolId, currentSemesterName);
//        Map<String, SysClass> map = currentList.stream().collect(Collectors.toMap(SysClass::getClassName, item -> item));
        for (SysClass sysClassListResModel : list)
        {
            GradeGroup gradeGroup = gradeGroupMap.get(sysClassListResModel.getGradeGroup());
            GradeEnum gradeEnum = GradeEnum.getByDesc(gradeGroup.getGrade());
            GradeEnum gradeEnumNew = GradeEnum.upgrade(gradeEnum.getCode(),sysClassListResModel.getDepartment());
            if(gradeEnumNew == null)
            {
                error.add(DepartmentEnum.getByCode(sysClassListResModel.getDepartment()).getDesc() + "  "+sysClassListResModel.getClassName() + "升级失败");
                continue;
            }
            String className = gradeEnumNew.getDesc() +sysClassListResModel.getClassName();
            //查询升级后的级组
            List<GradeGroup> gradeGroupList = gradeGroupMapper.selectList(new LambdaQueryWrapper<GradeGroup>().eq(GradeGroup::getSchoolId, schoolId)
                    .eq(GradeGroup::getGrade, gradeEnumNew.getDesc()));
            if(CollectionUtils.isEmpty(gradeGroupList))
            {
                error.add(DepartmentEnum.getByCode(sysClassListResModel.getDepartment()).getDesc() + "升级后级组不存在 "+DepartmentEnum.getByCode(sysClassListResModel.getDepartment()).getDesc() +
                        "->" + gradeEnumNew.getDesc());
                continue;
            }
            GradeGroup newGradeGroup = gradeGroupList.get(0);
            List<SysClass> oldClass = getSysClassBySchoolIdAndClassNameAndSidAndGradeGroupId(schoolId, sysClassListResModel.getClassName(), currentSemesterName, newGradeGroup.getId());
            if(!CollectionUtils.isEmpty(oldClass))
            {
                error.add( DepartmentEnum.getByCode(sysClassListResModel.getDepartment()).getDesc() + "升级后名称重复 "+sysClassListResModel.getClassName() +
                        "->" + className);
                continue;
            }
            //升班
            //保存老的学生信息
            List<StudentListResModel> studentListResModels = studentService.listByClassId(sysClassListResModel.getId());
            List<SysClassUpgradeRel> sysClassUpgradeRels = new ArrayList<>();
            for (StudentListResModel studentListResModel : studentListResModels)
            {
                SysClassUpgradeRel sysClassUpgradeRel = new SysClassUpgradeRel();
                sysClassUpgradeRel.setClassId(sysClassListResModel.getId());
                sysClassUpgradeRel.setStudentId(studentListResModel.getId());
                sysClassUpgradeRel.setSeatNo(studentListResModel.getSeatNo());
                sysClassUpgradeRel.setCreateTime(LocalDateTime.now());
                sysClassUpgradeRel.setUpdateTime(LocalDateTime.now());
                sysClassUpgradeRels.add(sysClassUpgradeRel);
            }
            sysClassUpgradeRelService.saveBatch(sysClassUpgradeRels);
            sysClassListResModel.setUpgrade(1);
            updateSysClass(sysClassListResModel);
            //升级班级
            SysClass sysClass = new SysClass();
            BeanUtils.copyProperties(sysClassListResModel, sysClass);
            sysClass.setSid(currentSemesterName);
            sysClass.setGradeGroup(newGradeGroup.getId());
            sysClass.setId(null);
            sysClass.setUpgrade(0);
            sysClass.setClassNumber(classUtils.getClassNumber(schoolId, gradeEnumNew.getDesc(), sysClass.getClassSerialNumber(), gradeEnumNew.getDepartment()));
            sysClass.setDepartment(gradeEnumNew.getDepartment());
            save(sysClass);
            //修改学生班级
            if(!CollectionUtils.isEmpty(studentListResModels))
            {
                studentService.updateClassId(studentListResModels.stream().map(StudentListResModel::getId).collect(Collectors.toList()), sysClass.getId());
            }
        }
        return error;
    }

    @Override
    public void graduateStudentsInClass(Long classId) {
        studentService.updateStudentStatusByClassId(classId);
    }

    @Override
    public List<SysClassListResModel> getSysClassListByGradeGroupNames(List<String> gradeGroupNames, String sid, long schoolId) {
        return sysClassDao.selectSysClassListByGradeGroupNames(gradeGroupNames,sid,schoolId);
    }

    @Override
    public Long importClass(MultipartFile file, Long schoolId,String sid) {
        if (schoolId == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.NO_SCHOOL_FILE_CONTENT_EMPTY));
        }
        //获取学校语言设置信息
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        if (StringUtils.isEmpty(currentLanguage)) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.GET_SCHOOL_LANGUAGE_SETTING_ERROR_LANGUAGE_CONFIG_ERROR));
        }
        SchoolLanguageEnum languageEnum = SchoolLanguageEnum.getDefValue(currentLanguage);
        if (languageEnum == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.GET_SCHOOL_LANGUAGE_SETTING_ERROR_LANGUAGE_CONFIG_ERROR));
        }
        // 读取Excel文件
        List<SysClassImportModel> list = readExcelData(file, languageEnum);
        // 创建导入任务
        ImportTaskEntity task = new ImportTaskEntity();
        task.setSchoolId(schoolId);
        task.setFileName(file.getOriginalFilename());
        task.setType(ImportTaskTypeEnum.CLASS_INFO.getCode());
        task.setTotalCount(0);
        task.setSuccessCount(0);
        task.setFailCount(0);
        importTaskService.save(task);
        CompletableFuture.runAsync(() -> {
            languageUtil.setLanguage(languageEnum.getCode());
            log.info("当前使用的语言是:{}", LanguageUtil.getCurrentLanguage());
            handleClassImport(languageEnum,task, list, schoolId,sid);
            LanguageUtil.clearLanguage();
        }, importExecutor).whenComplete((res, ex) -> {
            if (ex != null) {
                log.error("导入班级信息任务执行结束taskId=【{}】异常={}",task.getId(),ex);
            } else {
                log.info("导入班级信息完成，任务ID={}",task.getId());
            }
            task.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
            importTaskService.updateById(task);
        });
        return task.getId();
    }

    @Override
    public Integer getMaxClassSerialNumberByGradeGroupAndSid(Long gradeGroupId, String sid, long schoolId) {
        return sysClassDao.getMaxClassSerialNumberByGradeGroupAndSid(gradeGroupId, sid,schoolId);
    }

    @Override
    public Boolean checkGradeGroupCanUpdate(Long gradeGroupId) {
        // 检查级组下是否有班级
        LambdaQueryWrapper<SysClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysClass::getGradeGroup, gradeGroupId);
        Long count = this.baseMapper.selectCount(wrapper);
        return count == null || count == 0L;
    }

    private void handleClassImport(SchoolLanguageEnum languageEnum,ImportTaskEntity task, List<SysClassImportModel> list
            , Long schoolId,String sid) {
        task.setTotalCount(list.size());
        task.setStatus(ImportTaskStatusEnum.IN_PROCESS.getCode());
        importTaskService.updateById(task);
        List<ImportRecordSaveDTO> failureReason = new ArrayList<>();
        int successCount = 0;
        try {
            //查询专业
            Map<String,SchoolMajor> schoolMajorMap = schoolMajorService.getSchoolMajorMapBySchoolId(schoolId);
            Map<String,List<String>> classNumberMap = new HashMap<>();
            // 获取验证map
            for (SysClassImportModel sysClassImportModel : list) {
                // 处理导入的数据
                // 数据校验
                if (!validateClass(languageEnum,sysClassImportModel, failureReason, task.getId(),schoolId,
                        sid,schoolMajorMap,classNumberMap)) {
                    continue;
                }
                SysClass sysClass = new SysClass();
                //查询级组是否存在
                LambdaQueryWrapper<GradeGroup> groupLambdaQueryWrapper = new LambdaQueryWrapper<>();
                groupLambdaQueryWrapper.eq(GradeGroup::getGrade, sysClassImportModel.getGradeGroup())
                        .eq(GradeGroup::getSchoolId, schoolId);
                GradeGroup gradeGroup = gradeGroupMapper.selectOne(groupLambdaQueryWrapper);
                if(gradeGroup == null)
                {
                    ImportRecordSaveDTO failureReasonDTO = new ImportRecordSaveDTO();
                    failureReasonDTO.setTaskId(task.getId());
                    failureReasonDTO.setIncorrectLineno(String.valueOf(sysClassImportModel.getRowIndex()));
                    failureReasonDTO.setIncorrectReason(String.format(languageUtil.getMessage(LanguageConstants.GRADE_GROUP_NOT_EXISTS),sysClassImportModel.getGradeGroup()));
                    failureReason.add(failureReasonDTO);
                    continue;
                }
                // 根据级组校验必填项, 专业名称或文理科
                if (gradeGroup.getProfessionalSubject() == 1 &&
                        (sysClassImportModel.getProfessional() == null || sysClassImportModel.getProfessional().isEmpty())) {
                    ImportRecordSaveDTO failureReasonDTO = new ImportRecordSaveDTO();
                    failureReasonDTO.setTaskId(task.getId());
                    failureReasonDTO.setIncorrectLineno(String.valueOf(sysClassImportModel.getRowIndex()));
                    failureReasonDTO.setIncorrectReason(languageUtil.getMessage(LanguageConstants.PROFESSIONAL_NAME_EMPTY));
                    failureReason.add(failureReasonDTO);
                    continue;
                } else if (gradeGroup.getProfessionalSubject() != 1 &&
                        sysClassImportModel.getProfessional() != null && !sysClassImportModel.getProfessional().isEmpty()){
                    ImportRecordSaveDTO failureReasonDTO = new ImportRecordSaveDTO();
                    failureReasonDTO.setTaskId(task.getId());
                    failureReasonDTO.setIncorrectLineno(String.valueOf(sysClassImportModel.getRowIndex()));
                    failureReasonDTO.setIncorrectReason(languageUtil.getMessage(LanguageConstants.CLASS_NO_PROFESSIONAL_NAME));
                    failureReason.add(failureReasonDTO);
                    continue;
                }
                if (gradeGroup.getProfessionalSubject() == 2 && sysClassImportModel.getArtsScience() == null) {
                    ImportRecordSaveDTO failureReasonDTO = new ImportRecordSaveDTO();
                    failureReasonDTO.setTaskId(task.getId());
                    failureReasonDTO.setIncorrectLineno(String.valueOf(sysClassImportModel.getRowIndex()));
                    failureReasonDTO.setIncorrectReason(languageUtil.getMessage(LanguageConstants.CLASS_ART_SCIENCE_REQUIRED));
                    failureReason.add(failureReasonDTO);
                    continue;
                } else if (gradeGroup.getProfessionalSubject() == 3 && sysClassImportModel.getArtsScience() == null){
                    ImportRecordSaveDTO failureReasonDTO = new ImportRecordSaveDTO();
                    failureReasonDTO.setTaskId(task.getId());
                    failureReasonDTO.setIncorrectLineno(String.valueOf(sysClassImportModel.getRowIndex()));
                    failureReasonDTO.setIncorrectReason(languageUtil.getMessage(LanguageConstants.CLASS_ART_SCIENCE_REQUIRED));
                    failureReason.add(failureReasonDTO);
                    continue;
                } else if (!(gradeGroup.getProfessionalSubject() == 2 || gradeGroup.getProfessionalSubject() == 3) && sysClassImportModel.getArtsScience() != null){
                    ImportRecordSaveDTO failureReasonDTO = new ImportRecordSaveDTO();
                    failureReasonDTO.setTaskId(task.getId());
                    failureReasonDTO.setIncorrectLineno(String.valueOf(sysClassImportModel.getRowIndex()));
                    failureReasonDTO.setIncorrectReason(languageUtil.getMessage(LanguageConstants.CLASS_NO_ART_SCIENCE));
                    failureReason.add(failureReasonDTO);
                    continue;
                }
                sysClass.setClassSerialNumber(Integer.parseInt(sysClassImportModel.getClassSerialNumber()));
                if(StringUtils.isEmpty(sysClassImportModel.getClassName()))
                {
                    //最终展示的班级名称= 级组名称+班级序号：比如小六1班
                    sysClass.setClassName(sysClass.getClassSerialNumber() + "班");
                }else {
                    sysClass.setClassName(sysClassImportModel.getClassName());
                }
//                sysClass.setProfessionalVersion(LanguageUtils.isYes(languageEnum, sysClassImportModel.getProfessionalVersion()) ? 1 : 0);
                // 补充专业id
                if(gradeGroup.getProfessionalSubject() == 1 &&
                        schoolMajorMap.containsKey(sysClassImportModel.getProfessional())) {
                    SchoolMajor schoolMajor = schoolMajorMap.get(sysClassImportModel.getProfessional());
                    sysClass.setProfessionalId(schoolMajor.getId());
                }
                // 补充文理科
                if(StringUtils.isBlank(sysClassImportModel.getArtsScience())) {
                    sysClass.setArtsScience(0);
                }else if(gradeGroup.getArtsScienceType() == 1){
                    if (LanguageUtils.getArtsScience(languageEnum, sysClassImportModel.getArtsScience()))
                    {
                        sysClass.setArtsScience(1);
                    }
                    else if (LanguageUtils.getProfessional(languageEnum, sysClassImportModel.getArtsScience()))
                    {
                        sysClass.setArtsScience(2);
                    }else if (LanguageUtils.getCommerce(languageEnum, sysClassImportModel.getArtsScience()))
                    {
                        sysClass.setArtsScience(3);
                    }
                }
                sysClass.setGradeGroup(gradeGroup.getId());
                // 根据用户编号，查询用户信息
                if(StringUtils.isNotBlank(sysClassImportModel.getHeadTeacher())) {
                    LambdaQueryWrapper<UserSchoolRelEntity> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(UserSchoolRelEntity::getUserNumber, sysClassImportModel.getHeadTeacher());
                    UserSchoolRelEntity userSchoolRelEntity = userSchoolRelDao.selectOne(queryWrapper);
                    if (userSchoolRelEntity != null) {
                        sysClass.setHeadTeacher(userSchoolRelEntity.getId());
                    } else {
                        ImportRecordSaveDTO failureReasonDTO = new ImportRecordSaveDTO();
                        failureReasonDTO.setTaskId(task.getId());
                        failureReasonDTO.setIncorrectLineno(String.valueOf(sysClassImportModel.getRowIndex()));
                        failureReasonDTO.setIncorrectReason(languageUtil.getMessage(LanguageConstants.USER_NOT_FOUND));
                        failureReason.add(failureReasonDTO);
                        continue;
                    }
                }
                sysClass.setSid(sid);
                sysClass.setDepartment(gradeGroup.getDepartment().intValue());
                sysClass.setSchoolId(schoolId);
                sysClass.setDeleted(0L);
                sysClass.setUpgrade(0);
                sysClass.setCreateTime(LocalDateTime.now());
                sysClass.setUpdateTime(LocalDateTime.now());
                createSysClasses(Lists.newArrayList(sysClass),schoolId);
                successCount++;
            }
//        saveBatch(sysClasses);
            // 保存错误信息
            if (!CollectionUtils.isEmpty(failureReason)) {
                importRecordService.save(failureReason);
            }
        }finally {
            task.setStatus(ImportTaskStatusEnum.HANDLED.getCode());
            task.setFailCount(list.size() - successCount);
            task.setSuccessCount(successCount);
            importTaskService.updateById(task);
        }

    }

private boolean validateClass(SchoolLanguageEnum languageEnum, SysClassImportModel sysClassImportModel,
                              List<ImportRecordSaveDTO> failureReasons, Long taskId,Long schoolId,String sid
        ,Map<String,SchoolMajor> schoolMajorMap,Map<String,List<String>> classNumberMap) {
    boolean isValid = true;

    StringBuilder failureReason = new StringBuilder();

    // 班级名称校验
    String className = sysClassImportModel.getClassName();
    if(className != null && !className.isEmpty()) {
        if(sysClassImportModel.getGradeGroup() != null) {
            if (sysClassDao.getClassNameByGradeGroupAndSid(sysClassImportModel.getGradeGroup(), sysClassImportModel.getClassName(), schoolId,sid) > 0) {
                failureReason.append(String.format(languageUtil.getMessage(LanguageConstants.CLASS_NAME_DUPLICATE), className));
                isValid = false;
            }
        }
    }

    // 级组校验
    String gradeGroup = sysClassImportModel.getGradeGroup();
    if (gradeGroup == null || gradeGroup.isEmpty()) {
        failureReason.append(languageUtil.getMessage(LanguageConstants.GRADE_GROUP_EMPTY));
        isValid = false;
    }

    // 班级序号校验
    String classNumber = sysClassImportModel.getClassSerialNumber();
    if (classNumber == null || classNumber.isEmpty()) {
        failureReason.append(languageUtil.getMessage(LanguageConstants.CLASS_SERIAL_NUMBER_EMPTY));
        isValid = false;
    } else if (!classNumber.matches("\\d+")) {
        failureReason.append(languageUtil.getMessage(LanguageConstants.CLASS_SERIAL_NUMBER_FORMAT));
        isValid = false;
    } else {
        if(sysClassImportModel.getGradeGroup() != null) {
            if(sysClassDao.getClassSerialNumberByGradeGroupAndSid(sysClassImportModel.getGradeGroup(), sysClassImportModel.getClassSerialNumber(), schoolId,sid) > 0) {
                failureReason.append(String.format(languageUtil.getMessage(LanguageConstants.CLASS_SERIAL_NUMBER_DUPLICATE), classNumber));
                isValid = false;
            }
        }
        if(classNumberMap.containsKey(gradeGroup) && classNumberMap.get(gradeGroup).contains(classNumber)) {
            failureReason.append(String.format(languageUtil.getMessage(LanguageConstants.CLASS_SERIAL_NUMBER_DUPLICATE), classNumber));
            isValid = false;
        }
    }
    if(classNumber != null && !classNumber.isEmpty()) {
        List<String> strings = classNumberMap.get(gradeGroup);
        if(CollectionUtils.isEmpty(strings)) {
            strings = new ArrayList<>();
        }
        strings.add(classNumber);
        classNumberMap.put(gradeGroup,strings);
    }

    // 是否专业班校验
//    String isProfessionalClass = sysClassImportModel.getProfessionalVersion();
//    if (isProfessionalClass == null || isProfessionalClass.isEmpty()) {
//        failureReason.append(languageUtil.getMessage(LanguageConstants.PROFESSIONAL_VERSION_EMPTY));
//        isValid = false;
//    } else if (!LanguageUtils.isYes(languageEnum, isProfessionalClass) && !LanguageUtils.isNo(languageEnum, isProfessionalClass)) {
//        failureReason.append(String.format(languageUtil.getMessage(LanguageConstants.PROFESSIONAL_VERSION_FORMAT), isProfessionalClass));
//        isValid = false;
//    }

    // 文科/理科校验
    String artsOrScience = sysClassImportModel.getArtsScience();
    if (artsOrScience != null && !artsOrScience.isEmpty() && (!LanguageUtils.getArtsScience(languageEnum, artsOrScience)
            && !LanguageUtils.getProfessional(languageEnum, artsOrScience) && !LanguageUtils.getCommerce(languageEnum, artsOrScience))) {
        failureReason.append(String.format(languageUtil.getMessage(LanguageConstants.ARTS_SCIENCE_FORMAT), artsOrScience));
        isValid = false;
    }

    // 专业名称校验
    String majorName = sysClassImportModel.getProfessional();
    if (StringUtils.isNotBlank(majorName) && !schoolMajorMap.containsKey(majorName)) {
        failureReason.append(String.format(languageUtil.getMessage(LanguageConstants.PROFESSIONAL_NAME_FORMAT), majorName));
        isValid = false;
    }

    // 班主任用户编码
    String headTeacher = sysClassImportModel.getHeadTeacher();
    if (headTeacher == null || headTeacher.isEmpty()) {
        failureReason.append(languageUtil.getMessage(LanguageConstants.HEAD_TEACHER_USER_NO_REQUIRED));
        isValid = false;
    }

    if (!isValid) {
        ImportRecordSaveDTO failureReasonDTO = new ImportRecordSaveDTO();
        failureReasonDTO.setTaskId(taskId);
        failureReasonDTO.setIncorrectLineno(String.valueOf(sysClassImportModel.getRowIndex()));
        failureReasonDTO.setIncorrectReason(failureReason.toString());
        failureReasons.add(failureReasonDTO);
    }

    return isValid;
}


    private List<SysClassImportModel> readExcelData(MultipartFile file, SchoolLanguageEnum schoolLanguageEnum) {
        List<SysClassImportModel> result = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream()) {
            switch (schoolLanguageEnum) {
                case ZH_MO:
                    SysClassImportListener importZhCnListener = new SysClassImportListener();
                    EasyExcel.read(inputStream, SysClassZhImportModel.class, importZhCnListener).sheet().headRowNumber(2).doReadSync();
                    List<SysClassZhImportModel> dataList = importZhCnListener.getDataList();
                    result = dataList.stream().map(item -> {
                        SysClassImportModel model = new SysClassImportModel();
                        BeanUtils.copyProperties(item, model);
                        return model;
                    }).collect(Collectors.toList());
                    break;
                // 其他语言处理逻辑可以在这里添加
                case EN_US:
                    SysClassImportEnUsListener importEnUsListener = new SysClassImportEnUsListener();
                    EasyExcel.read(inputStream, SysClassEnImportModel.class, importEnUsListener).sheet().headRowNumber(2).doReadSync();
                    List<SysClassEnImportModel> importEnUsListenerDataList = importEnUsListener.getDataList();
                    result = importEnUsListenerDataList.stream().map(item -> {
                        SysClassImportModel model = new SysClassImportModel();
                        BeanUtils.copyProperties(item, model);
                        return model;
                    }).collect(Collectors.toList());
                    break;
                case PT_PT:
                    SysClassImportPtPtListener importPtPtListener = new SysClassImportPtPtListener();
                    EasyExcel.read(inputStream, SysClassPtImportModel.class, importPtPtListener).sheet().headRowNumber(2).doReadSync();
                    List<SysClassPtImportModel> importPtPtListenerDataList = importPtPtListener.getDataList();
                    result = importPtPtListenerDataList.stream().map(item -> {
                        SysClassImportModel model = new SysClassImportModel();
                        BeanUtils.copyProperties(item, model);
                        return model;
                    }).collect(Collectors.toList());
                    break;
            }
        } catch (IOException e) {
            log.error("Excel文件读取失败", e);
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.FILE_READ_ERROR));
        }
        return result;
    }

    @Override
    public Map<Long, String> getNamesByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashMap<>();
        }

        // 查询班级信息
        List<SysClass> classes = sysClassDao.selectList(
                new LambdaQueryWrapper<SysClass>()
                        .in(SysClass::getId, ids)
                        .eq(SysClass::getDeleted, 0));

        // 转换为Map
        return classes.stream()
                .collect(Collectors.toMap(
                        SysClass::getId,
                        SysClass::getClassName,
                        (v1, v2) -> v1));  // 如果有重复key,保留第一个值
    }

    @Override
    public List<SysClass> getSysClassBySchoolIdAndClassNameAndSidAndGradeGroupId(Long schoolId, String className, String sid, Long gradeGroupId) {
        LambdaQueryWrapper<SysClass> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysClass::getSchoolId, schoolId)
                .eq(SysClass::getClassName, className)
                .eq(SysClass::getSid, sid)
                .eq(SysClass::getGradeGroup, gradeGroupId)
                .eq(SysClass::getDeleted, 0);
        return sysClassDao.selectList(queryWrapper);
    }

    @Override
    public List<SysClassListResModel> getSysClassListBySchoolIdAndClassName(Long schoolId, String className) {
        log.info("getSysClassListBySchoolIdAndClassName called with schoolId={}, className={}", schoolId, className);
        List<SysClassListResModel> allClasses = sysClassDao.selectSysClassListBySchoolId(schoolId);
        log.info("getSysClassListBySchoolId returned {} classes", allClasses.size());
        if (className == null || className.isEmpty()) {
            return allClasses;
        }
        String searchName = className.toLowerCase();
        // 同時搜索班級名稱和年級名稱，也支援「年級+班級編號」的組合名稱如「中五1班」
        List<SysClassListResModel> filtered = allClasses.stream()
                .filter(c -> {
                    // 直接匹配班級名稱
                    if (c.getClassName() != null && c.getClassName().toLowerCase().contains(searchName)) {
                        return true;
                    }
                    // 直接匹配年級名稱
                    if (c.getGroupName() != null && c.getGroupName().toLowerCase().contains(searchName)) {
                        return true;
                    }
                    // 處理「中五1班」-> 拆分為「年級」+ 「班級編號」
                    // 找到第一個數字的位置作為分界（年級是最後一個中文部分）
                    int splitIndex = -1;
                    for (int i = 1; i < searchName.length(); i++) {
                        char ch = searchName.charAt(i);
                        // 如果是阿拉伯數字，則其前面的中文字符是最後的年級部分
                        if (ch >= '0' && ch <= '9') {
                            splitIndex = i - 1; // 中文结束位置
                            break;
                        }
                    }
                    if (splitIndex > 0 && splitIndex < searchName.length() - 1) {
                        String gradePart = searchName.substring(0, splitIndex + 1);
                        String classPart = searchName.substring(splitIndex + 1);
                        log.info("DEBUG: gradePart={}, classPart={}, groupName={}, className={}",
                            gradePart, classPart, c.getGroupName(), c.getClassName());
                        boolean gradeMatch = c.getGroupName() != null && c.getGroupName().toLowerCase().contains(gradePart);
                        boolean classMatch = c.getClassName() != null && (
                            c.getClassName().toLowerCase().contains(classPart) ||
                            c.getClassName().toLowerCase().contains(classPart + "班") ||
                            c.getClassName().toLowerCase().contains("班" + classPart)
                        );
                        return gradeMatch && classMatch;
                    }
                    return false;
                })
                .collect(Collectors.toList());
        log.info("filtered to {} classes matching '{}'", filtered.size(), searchName);
        return filtered;
    }

    @Override
    public String exportClassList(SysClassQueryReqModel reqModel) {
        //根据name查询专业
        SchoolMajor schoolMajor = null;
        if (StringUtils.isNotBlank(reqModel.getProfessional())) {
            List<SchoolMajor> schoolMajorByName = schoolMajorService.getSchoolMajorByName(reqModel.getProfessional(), reqModel.getSchoolId());
            if(!CollectionUtils.isEmpty(schoolMajorByName))
            {
                schoolMajor = schoolMajorByName.get(0);
            }else {
                String fileName = "班级列表.xlsx";
                return exportFileHandler.doExportExcel(new ArrayList<>(), fileName, SysClassDetaiExportModel.class, FileTypeEnum.EXPORT, reqModel.getSchoolId());
            }
        }
        LambdaQueryWrapper<SysClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(reqModel.getClassId() != null && reqModel.getClassId() > 0, SysClass::getId, reqModel.getClassId())
                .eq(SysClass::getDeleted, 0)
                .eq(SysClass::getSchoolId, reqModel.getSchoolId())
                .eq(StringUtils.isNotBlank(reqModel.getSid()), SysClass::getSid, reqModel.getSid())
                .eq(reqModel.getGradeGroup() != null && reqModel.getGradeGroup() > 0, SysClass::getGradeGroup, reqModel.getGradeGroup())
                .eq(reqModel.getClassSerialNumber() != null && reqModel.getClassSerialNumber() > 0, SysClass::getClassSerialNumber, reqModel.getClassSerialNumber())
                .eq(reqModel.getArtsScience() != null, SysClass::getArtsScience, reqModel.getArtsScience())
                .eq(reqModel.getProfessionalVersion() != null, SysClass::getProfessionalVersion, reqModel.getProfessionalVersion());
        if(schoolMajor != null)
        {
            wrapper.eq(SysClass::getProfessionalId, schoolMajor.getId());
        }
        List<SysClass> sysClasses = this.baseMapper.selectList(wrapper);
        //数据量不多，直接导出
        //转map
        Map<Long, SchoolMajor> sysClassMap;
        List<Long> majorIds = sysClasses.stream().map(SysClass::getProfessionalId).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(majorIds)) {
            List<SchoolMajor> schoolMajors = schoolMajorService.listByIds(majorIds);
            sysClassMap = schoolMajors.stream().collect(Collectors.toMap(SchoolMajor::getId, Function.identity(), (x1, x2) -> x1));
        }else {
            sysClassMap = new HashMap<>();
        }
        // 查询所有用户学校关系
        List<Long> headTeacherIds = sysClasses.stream().map(SysClass::getHeadTeacher).collect(Collectors.toList());
        Map<Long, UserSchoolRelEntity> userSchoolRelMap;
        if(!CollectionUtils.isEmpty(headTeacherIds)) {
            List<UserSchoolRelEntity> userSchoolRelEntities = userSchoolRelDao.selectBatchIds(headTeacherIds);
            userSchoolRelMap = userSchoolRelEntities.stream().collect(Collectors.toMap(UserSchoolRelEntity::getId, Function.identity(), (x1, x2) -> x1));
        }else {
            userSchoolRelMap = new HashMap<>();
        }
        // 查询所有年级组
        List<Long> gradeGroupIds = sysClasses.stream().map(SysClass::getGradeGroup).collect(Collectors.toList());
        Map<Long, GradeGroup> gradeGroupMap;
        if(!CollectionUtils.isEmpty(gradeGroupIds)) {
            List<GradeGroup> gradeGroups = gradeGroupMapper.selectBatchIds(gradeGroupIds);
            gradeGroupMap = gradeGroups.stream().collect(Collectors.toMap(GradeGroup::getId, Function.identity(), (x1, x2) -> x1));
        }else {
            gradeGroupMap = new HashMap<>();
        }
        String currentLanguage = LanguageUtil.getCurrentLanguage();
        SchoolLanguageEnum languageEnum = SchoolLanguageEnum.getDefValue(currentLanguage);
        List<SysClassDetaiExportModel> sysClassDetailResModels = sysClasses.stream()
                .map(item -> {
                    SysClassDetaiExportModel resModel = new SysClassDetaiExportModel();
                    BeanUtils.copyProperties(item, resModel);
                    // 获取专业名称
                    if (resModel.getProfessionalId() != null) {
                        SchoolMajor schoolMajorById = sysClassMap.get(resModel.getProfessionalId());
                        resModel.setProfessionalName(schoolMajorById == null ? "" : schoolMajorById.getMajorName());
                    }

                    // 获取班主任名称
                    UserSchoolRelEntity userSchoolRelEntity = userSchoolRelMap.get(resModel.getHeadTeacher());
                    if (userSchoolRelEntity != null) {
                        resModel.setHeadTeacherName(userSchoolRelEntity.getUsername());
                    }

                    // 获取年级组名称
                    GradeGroup gradeGroup = gradeGroupMap.get(resModel.getGradeGroup());
                    resModel.setGradeGroupName(gradeGroup == null ? "" : gradeGroup.getGradeGroupName());
                    if(resModel.getProfessionalVersion() != null) {
                        resModel.setProfessionalVersionName(LanguageUtils.getYes(languageEnum, resModel.getProfessionalVersion() == 1));
                    }
                    if(resModel.getArtsScience() != null) {
                        resModel.setArtsScienceName(LanguageUtils.getArtsScienceAndCommerce(languageEnum,resModel.getArtsScience()));
                    }
                    //年级编号 + 年级 +班级序号
                    //年级编号 取“系统设置”中每个学部的编号代码（K、P、S、F）
                    //年级：对应的一年级、二年级
                    //班级序号： A、B、C、D（对应1、2、3、4）
//                    String classNumber = ClassUtils.getClassNumber(gradeGroup == null ? "" : gradeGroup.getGrade(), resModel.getClassSerialNumber(), resModel.getDepartment());
//                    resModel.setClassNumber(classNumber);
                    return resModel;
                }).collect(Collectors.toList());
        String fileName = "班级列表.xlsx";

        if (currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode())) {
            fileName = "classList.xlsx";
            List<SysClassDetaiExportEnModel> exportEnModels = sysClassDetailResModels.stream()
                    .map(item -> {
                        SysClassDetaiExportEnModel resModel = new SysClassDetaiExportEnModel();
                        BeanUtils.copyProperties(item, resModel);
                        return resModel;
                    }).collect(Collectors.toList());
            return exportFileHandler.doExportExcel(exportEnModels, fileName, SysClassDetaiExportEnModel.class, FileTypeEnum.EXPORT, reqModel.getSchoolId());
        }else if (currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode())) {
            fileName = "classList.xlsx";
            List<SysClassDetaiExportPtModel> exportPtModels = sysClassDetailResModels.stream()
                    .map(item -> {
                        SysClassDetaiExportPtModel resModel = new SysClassDetaiExportPtModel();
                        BeanUtils.copyProperties(item, resModel);
                        return resModel;
                    }).collect(Collectors.toList());
            return exportFileHandler.doExportExcel(exportPtModels, fileName, SysClassDetaiExportPtModel.class, FileTypeEnum.EXPORT, reqModel.getSchoolId());
        }else {
            return exportFileHandler.doExportExcel(sysClassDetailResModels, fileName, SysClassDetaiExportModel.class, FileTypeEnum.EXPORT, reqModel.getSchoolId());
        }
    }

    @Override
    public List<SysClassListResModel> listClasses(Long schoolId, String schoolYear, Long gradeGroupId,Integer department,Long userId) {
        if (schoolId == null) {
            throw new BusinessException(LanguageConstants.SEMESTER_ID_NOT_EXISTS);
        }
        boolean commonUser = userAuthHelper.getCommonUser(userId, schoolId);
        List<Long> classIds = null;
        if(commonUser)
        {
            classIds = userAuthHelper.getUserClassIds(userId, schoolId);
            if(CollectionUtils.isEmpty(classIds))
            {
                return Collections.emptyList();
            }
        }
        return this.baseMapper.selectClassList(schoolId, schoolYear, gradeGroupId,department,classIds);
    }

    @Override
    public void getUserDetail(LoginResModel resModel, StudentEntity student) {
        SysClass sysClass = this.getById(student.getClassId());
        if (sysClass != null){
            MinigrogramUserDetailResModel detailResModel = new MinigrogramUserDetailResModel();
            detailResModel.setClassId(sysClass.getId());
            detailResModel.setClassName(sysClass.getClassName());
            detailResModel.setClassInnerNo(student.getSeatNo());
            GradeGroup gradeGroup = gradeGroupMapper.selectById(sysClass.getGradeGroup());
            if (gradeGroup != null) {
                detailResModel.setGradeGroupId(gradeGroup.getId());
                detailResModel.setGradeGroupName(gradeGroup.getGradeGroupName());
            }
            resModel.setUser(detailResModel);
        }
    }

    @Override
    public void getUserDetail(MinigrogramUserResModel resModel, StudentEntity student) {
        SysClass sysClass = this.getById(student.getClassId());
        if (sysClass != null){
            MinigrogramUserDetailResModel detailResModel = new MinigrogramUserDetailResModel();
            detailResModel.setClassId(sysClass.getId());
            detailResModel.setClassName(sysClass.getClassName());
            detailResModel.setClassInnerNo(student.getSeatNo());
            GradeGroup gradeGroup = gradeGroupMapper.selectById(sysClass.getGradeGroup());
            if (gradeGroup != null) {
                detailResModel.setGradeGroupId(gradeGroup.getId());
                detailResModel.setGradeGroupName(gradeGroup.getGradeGroupName());
            }
            resModel.setUser(detailResModel);
        }
    }

    @Override
    public void getUserDetail(MinigrogramAuthResModel resModel, StudentEntity student) {
        SysClass sysClass = this.getById(student.getClassId());
        if (sysClass != null){
            MinigrogramUserDetailResModel detailResModel = new MinigrogramUserDetailResModel();
            detailResModel.setClassId(sysClass.getId());
            detailResModel.setClassName(sysClass.getClassName());
            detailResModel.setClassInnerNo(student.getSeatNo());
            GradeGroup gradeGroup = gradeGroupMapper.selectById(sysClass.getGradeGroup());
            if (gradeGroup != null) {
                detailResModel.setGradeGroupId(gradeGroup.getId());
                detailResModel.setGradeGroupName(gradeGroup.getGradeGroupName());
            }
            resModel.setUser(detailResModel);
        }
    }

}