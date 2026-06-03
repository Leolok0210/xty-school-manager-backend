package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.UserGroupTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.UserTypeEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.util.DateUtils;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.CourseScheduleDao;
import com.xiaotiyun.school.manager.dao.UserSchoolRelDao;
import com.xiaotiyun.school.manager.helper.UserAuthHelper;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.*;
import com.xiaotiyun.school.manager.service.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseScheduleServiceImpl extends ServiceImpl<CourseScheduleDao, CourseScheduleEntity> implements CourseScheduleService {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
    @Resource
    private SubstituteRecordService substituteRecordService;
    @Resource
    private UserSchoolRelDao userSchoolRelDao;
    @Resource
    private SysClassService sysClassService;
    @Resource
    private GradeGroupService gradeGroupService;
    @Resource
    private TeachingSettingService teachingSettingService;
    @Resource
    private ClassroomService classroomService;
    @Resource
    private ClassroomTypeService classroomTypeService;
    @Resource
    private UserGroupService userGroupService;
    @Resource
    private LessonService lessonService;
    @Resource
    private LanguageUtil languageUtil;
    @Resource
    private UserService userService;
    @Resource
    private SemesterService semesterService;

    @Resource
    private SubjectRelService subjectRelService;

    @Resource
    private UserAuthHelper userAuthHelper;

    @Override
    @Transactional
    public void add(Long schoolId, CourseScheduleSaveReqModel reqModel) {
        if (CollectionUtils.isNotEmpty(reqModel.getSubstituteDateList())) {
            List<CourseScheduleEntity> saveOrUpdateList = new ArrayList<>();
            List<String> errorList = new ArrayList<>();
            List<LocalDate> courseScheduleDates = reqModel.getSubstituteDateList().stream().map(CourseScheduleDateSaveReqModel::getCourseScheduleDate).collect(Collectors.toList());
            Map<LocalDate, List<CourseScheduleEntity>> courseMap = new HashMap<>();
            Map<LocalDate, List<SubstituteRecordEntity>> substituteMap = new HashMap<>();
            Map<Long, CourseScheduleEntity> courseScheduleMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(courseScheduleDates)) {
                //获取课表信息
                QueryWrapper<CourseScheduleEntity> wrapper = new QueryWrapper<>();
                wrapper.lambda().eq(CourseScheduleEntity::getSchoolId, schoolId)
                        .in(CourseScheduleEntity::getCourseDate, courseScheduleDates);
                List<CourseScheduleEntity> courses = this.list(wrapper);
                if (CollectionUtils.isNotEmpty(courses)) {
                    courseMap = courses.stream().collect(Collectors.groupingBy(CourseScheduleEntity::getCourseDate));
                }
                //获取代课信息
                QueryWrapper<SubstituteRecordEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(SubstituteRecordEntity::getSchoolId, schoolId)
                        .in(SubstituteRecordEntity::getSubstituteDate, courseScheduleDates);
                List<SubstituteRecordEntity> substitutes = substituteRecordService.list(queryWrapper);
                if (CollectionUtils.isNotEmpty(substitutes)) {
                    substituteMap = substitutes.stream().collect(Collectors.groupingBy(SubstituteRecordEntity::getSubstituteDate));
                    List<Long> courseScheduleIds = substitutes.stream().map(SubstituteRecordEntity::getCourseScheduleId).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(courseScheduleIds)) {
                        List<CourseScheduleEntity> courseScheduleEntities = this.listByIds(courseScheduleIds);
                        if (CollectionUtils.isNotEmpty(courseScheduleEntities)) {
                            courseScheduleMap = courseScheduleEntities.stream().collect(Collectors.toMap(CourseScheduleEntity::getId, courseScheduleEntity -> courseScheduleEntity));
                        }
                    }
                }
            }
            //获取课节信息
            List<Long> lessonIds = reqModel.getSubstituteDateList().stream().map(CourseScheduleDateSaveReqModel::getLessonIds).flatMap(List::stream).collect(Collectors.toList());
            Map<Long, LessonEntity> lessonMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(lessonIds)) {
                List<LessonEntity> lessons = lessonService.listByIds(lessonIds);
                if (CollectionUtils.isNotEmpty(lessons)) {
                    lessonMap = lessons.stream().collect(Collectors.toMap(LessonEntity::getId, lessonEntity -> lessonEntity));
                }
            }
            //获取教室信息
            ClassroomEntity classroom = null;
            if (reqModel.getClassroomId() != null) {
                classroom = classroomService.getById(reqModel.getClassroomId());
            }
            //获取科目信息
            SubjectRelResModel subjectRelResModel = subjectRelService.listByIds(Lists.newArrayList(reqModel.getSubjectId())).get(0);
            SubjectDetailResModel subject = subjectRelResModel.getSubject();
            //获取班级信息
            SysClass sysClass = sysClassService.getById(reqModel.getClassId());
            //获取教师任课信息
            QueryWrapper<TeachingSetting> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(TeachingSetting::getSchoolId, schoolId)
                    .eq(TeachingSetting::getSid, reqModel.getSchoolYear())
                    .eq(TeachingSetting::getClassId, reqModel.getClassId())
                    .eq(TeachingSetting::getSubjectId, reqModel.getSubjectId());
            List<TeachingSetting> teachingSettings = teachingSettingService.list(queryWrapper);
            if (CollectionUtils.isNotEmpty(teachingSettings)) {
                UserSchoolRelEntity user = userSchoolRelDao.selectById(teachingSettings.get(0).getTeacherId());
                if (user != null) {
                    for (CourseScheduleDateSaveReqModel courseScheduleDate : reqModel.getSubstituteDateList()) {
                        //获取当天的课程信息
                        List<CourseScheduleEntity> courses = courseMap.get(courseScheduleDate.getCourseScheduleDate());
                        Map<String, CourseScheduleEntity> scheduleEntityMap = new HashMap<>();
                        if (CollectionUtils.isNotEmpty(courses)) {
                            scheduleEntityMap = courses.stream().collect(Collectors.toMap(courseScheduleEntity -> courseScheduleEntity.getClassId() + "_" + courseScheduleEntity.getLessonId(), courseScheduleEntity -> courseScheduleEntity));
                        }
                        //获取当天的代课信息
                        List<SubstituteRecordEntity> substituteRecords = substituteMap.get(courseScheduleDate.getCourseScheduleDate());
                        if (CollectionUtils.isNotEmpty(courseScheduleDate.getLessonIds())) {
                            for (Long lessonId : courseScheduleDate.getLessonIds()) {
                                LessonEntity lesson = lessonMap.get(lessonId);
                                //课节信息不存在，直接跳过
                                if (lesson != null) {
                                    if (CollectionUtils.isNotEmpty(courses)) {
                                        List<CourseScheduleEntity> classCourses = courses.stream().filter(courseScheduleEntity -> !courseScheduleEntity.getClassId().equals(reqModel.getClassId())).collect(Collectors.toList());
                                        if (CollectionUtils.isNotEmpty(classCourses)) {
                                            //其他班级课表时间是否冲突
                                            //教师课表时间是否冲突
                                            List<CourseScheduleEntity> teacherCourses = courses.stream().filter(courseScheduleEntity -> courseScheduleEntity.getTeacherId().equals(user.getId())).collect(Collectors.toList());
                                            if (CollectionUtils.isNotEmpty(teacherCourses)) {
                                                long count = teacherCourses.stream().filter(courseScheduleEntity -> courseScheduleEntity.getStartTime().isBefore(lesson.getEndTime()) && courseScheduleEntity.getEndTime().isAfter(lesson.getStartTime())).count();
                                                if (count > 0) {
                                                    errorList.add("[" + formatter.format(courseScheduleDate.getCourseScheduleDate()) + "-" + lesson.getName() + "]");
                                                    break;
                                                }
                                            }
                                            //教室是否冲突
                                            long count = classCourses.stream().filter(courseScheduleEntity -> courseScheduleEntity.getClassroomId() != null && courseScheduleEntity.getClassroomId().equals(reqModel.getClassroomId()) && courseScheduleEntity.getStartTime().isBefore(lesson.getEndTime()) && courseScheduleEntity.getEndTime().isAfter(lesson.getStartTime())).count();
                                            if (count > 0) {
                                                errorList.add("[" + formatter.format(courseScheduleDate.getCourseScheduleDate()) + "-" + lesson.getName() + "]");
                                                break;
                                            }
                                        }
                                    }
                                    //教师代课是否冲突
                                    if (CollectionUtils.isNotEmpty(substituteRecords)) {
                                        List<SubstituteRecordEntity> teacherSubstitutes = substituteRecords.stream().filter(substituteRecord -> substituteRecord.getSubstituteTeacherId().equals(user.getId())).collect(Collectors.toList());
                                        if (CollectionUtils.isNotEmpty(teacherSubstitutes)) {
                                            for (SubstituteRecordEntity teacherSubstitute : teacherSubstitutes) {
                                                CourseScheduleEntity courseScheduleEntity = courseScheduleMap.get(teacherSubstitute.getCourseScheduleId());
                                                if (courseScheduleEntity != null && courseScheduleEntity.getStartTime().isBefore(lesson.getEndTime()) && courseScheduleEntity.getEndTime().isAfter(lesson.getStartTime())) {
                                                    errorList.add("[" + formatter.format(courseScheduleDate.getCourseScheduleDate()) + "-" + lesson.getName() + "]");
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    CourseScheduleEntity entity = new CourseScheduleEntity();
                                    CourseScheduleEntity courseScheduleEntity = scheduleEntityMap.get(reqModel.getClassId() + "_" + lessonId);
                                    if (courseScheduleEntity != null) {
                                        entity.setId(courseScheduleEntity.getId());
                                    }
                                    entity.setSchoolId(schoolId);
                                    entity.setPeriodId(reqModel.getPeriodId());
                                    entity.setGradeId(sysClass.getGradeGroup());
                                    entity.setClassId(reqModel.getClassId());
                                    entity.setSubjectId(reqModel.getSubjectId());
                                    entity.setSubjectName(subject.getSubjectName());
                                    entity.setTeacherId(user.getId());
                                    entity.setTeacherName(user.getUsername());
                                    entity.setLessonId(lessonId);
                                    entity.setLessonName(lesson.getName());
                                    entity.setClassroomId(reqModel.getClassroomId());
                                    if (classroom != null) {
                                        entity.setClassroomName(classroom.getName());
                                    }
                                    entity.setCourseDate(courseScheduleDate.getCourseScheduleDate());
                                    entity.setStartTime(lesson.getStartTime());
                                    entity.setEndTime(lesson.getEndTime());
                                    saveOrUpdateList.add(entity);
                                }
                            }
                        }
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(errorList)) {
                //教师或教室存在冲突
                throw new BusinessException(String.format(languageUtil.getMessage(LanguageConstants.COURSE_FORMAT_ERROR), String.join("、", errorList)));
            } else {
                if (CollectionUtils.isNotEmpty(saveOrUpdateList)) {
                    this.saveOrUpdateBatch(saveOrUpdateList);
                }
            }
        }
    }

    @Override
    @Transactional
    public void update(Long id, CourseScheduleUpdateReqModel reqModel) {
        CourseScheduleEntity entity = getById(id);
        if (entity != null) {
            if (!entity.getCourseDate().isAfter(LocalDate.now())) {
                throw new BusinessException(LanguageConstants.COURSE_UPDATE_FAIL);
            }
            if (!entity.getSubjectId().equals(reqModel.getSubjectId())) {
                QueryWrapper<TeachingSetting> wrapper = new QueryWrapper<>();
                wrapper.lambda().eq(TeachingSetting::getSchoolId, entity.getSchoolId())
                        .eq(TeachingSetting::getSubjectId, reqModel.getSubjectId())
                        .eq(TeachingSetting::getClassId, entity.getClassId());
                List<TeachingSetting> teachingSettings = teachingSettingService.list(wrapper);
                if (!CollectionUtils.isNotEmpty(teachingSettings)) {
                    throw new BusinessException(LanguageConstants.SUBJECT_TEACHING_NOT_FOUND);
                }
                SubjectRelResModel subjectRelResModel = subjectRelService.listByIds(Lists.newArrayList(reqModel.getSubjectId())).get(0);
                SubjectDetailResModel subject = subjectRelResModel.getSubject();
                if (subject == null) {
                    throw new BusinessException(LanguageConstants.SUBJECT_NOT_FOUND);
                }
                Long teacherId = teachingSettings.get(0).getTeacherId();
                //修改为其他科目时需要校验教师时间冲突（排除当前记录）
                checkTeacherTimeConflict(entity, teacherId, id);
                entity.setSubjectId(reqModel.getSubjectId());
                entity.setSubjectName(subject.getSubjectName());
                entity.setTeacherId(teacherId);
                UserSchoolRelEntity user = userSchoolRelDao.selectById(teacherId);
                if (user != null) {
                    entity.setTeacherName(user.getUsername());
                }
            }
            if (reqModel.getClassroomId() != null) {
                // 校验教室占用（排除当前记录）
                checkClassroomConflict(entity, reqModel.getClassroomId(), id);
                entity.setClassroomId(reqModel.getClassroomId());
                ClassroomEntity classroom = classroomService.getById(reqModel.getClassroomId());
                if (classroom != null) {
                    entity.setClassroomName(classroom.getName());
                }
            }
            this.updateById(entity);
        }
    }

    @Override
    @Transactional
    public void delete(CourseScheduleDeleteReqModel reqModel) {
        CourseScheduleEntity entity = getById(reqModel.getId());
        if (entity != null) {
            if (!entity.getCourseDate().isAfter(LocalDate.now())) {
                throw new BusinessException(LanguageConstants.COURSE_DELETE_FAIL);
            }
            List<Long> ids = new ArrayList<>();
            ids.add(reqModel.getId());
            if (Boolean.TRUE.equals(reqModel.getIsDeleteOther())) {
                //查询未开始的同课节同课程的课表信息，判斷相同的邏輯：科目名稱、老師 、教室都相同
                QueryWrapper<CourseScheduleEntity> wrapper = new QueryWrapper<>();
                wrapper.lambda().eq(CourseScheduleEntity::getSchoolId, entity.getSchoolId())
                        .eq(CourseScheduleEntity::getClassId, entity.getClassId())
                        .eq(CourseScheduleEntity::getLessonId, entity.getLessonId())
                        .eq(CourseScheduleEntity::getTeacherId, entity.getTeacherId())
                        .eq(CourseScheduleEntity::getSubjectId, entity.getSubjectId())
                        .ge(CourseScheduleEntity::getCourseDate, LocalDate.now());
                List<CourseScheduleEntity> list = this.list(wrapper);
                if (CollectionUtils.isNotEmpty(list)) {
                    ids.addAll(list.stream().map(CourseScheduleEntity::getId).collect(Collectors.toList()));
                }
            }
            this.removeBatchByIds(ids);
            if (CollectionUtils.isNotEmpty(ids)) {
                //删除相关代课记录
                QueryWrapper<SubstituteRecordEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().in(SubstituteRecordEntity::getCourseScheduleId, ids);
                substituteRecordService.remove(queryWrapper);
            }
        }
    }

    @Override
    public List<CourseScheduleClassListResModel> classListByStudent(Long schoolId, CourseScheduleClassListReqModel reqModel) {
        List<CourseScheduleClassListResModel> resList = new ArrayList<>();
        QueryWrapper<CourseScheduleEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(CourseScheduleEntity::getSchoolId, schoolId)
                .eq(CourseScheduleEntity::getClassId, reqModel.getClassId())
                .eq(reqModel.getPeriodId() != null && reqModel.getPeriodId() > 0, CourseScheduleEntity::getPeriodId, reqModel.getPeriodId())
                .ge(CourseScheduleEntity::getCourseDate, reqModel.getStartDate())
                .le(CourseScheduleEntity::getCourseDate, reqModel.getEndDate());
        List<CourseScheduleEntity> list = this.list(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            //获取教室信息
            Map<Long, ClassroomEntity> classroomMap = new HashMap<>();
            Map<Long, ClassroomTypeEntity> classroomTypeMap = new HashMap<>();
            List<Long> classroomIds = list.stream().map(CourseScheduleEntity::getClassroomId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(classroomIds)) {
                List<ClassroomEntity> classrooms = classroomService.listByIds(classroomIds);
                if (CollectionUtils.isNotEmpty(classrooms)) {
                    classroomMap = classrooms.stream().collect(Collectors.toMap(ClassroomEntity::getId, classroom -> classroom));
                    List<Long> classroomTypeIds = classrooms.stream().map(ClassroomEntity::getTypeId).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(classroomTypeIds)) {
                        List<ClassroomTypeEntity> classroomTypes = classroomTypeService.listByIds(classroomTypeIds);
                        if (CollectionUtils.isNotEmpty(classroomTypes)) {
                            classroomTypeMap = classroomTypes.stream().collect(Collectors.toMap(ClassroomTypeEntity::getId, classroomTypeEntity -> classroomTypeEntity));
                        }
                    }
                }
            }
            //获取班级相关代课信息
            List<LocalDate> courseDates = list.stream().map(CourseScheduleEntity::getCourseDate).collect(Collectors.toList());
            Map<String, SubstituteRecordEntity> substituteMap = new HashMap<>();
            Map<Long, UserSchoolRelEntity> userMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(courseDates)) {
                QueryWrapper<SubstituteRecordEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(SubstituteRecordEntity::getSchoolId, schoolId)
                        .eq(SubstituteRecordEntity::getClassId, reqModel.getClassId())
                        .ge(SubstituteRecordEntity::getSubstituteDate, reqModel.getStartDate())
                        .le(SubstituteRecordEntity::getSubstituteDate, reqModel.getEndDate());
                List<SubstituteRecordEntity> substitutes = substituteRecordService.list(queryWrapper);
                if (CollectionUtils.isNotEmpty(substitutes)) {
                    substituteMap = substitutes.stream().collect(Collectors.toMap(substituteRecordEntity -> substituteRecordEntity.getSubstituteDate().toString() + "_" + substituteRecordEntity.getLessonId(), substituteRecordEntity -> substituteRecordEntity));
                    //获取教师信息
                    List<Long> teacherIds = substitutes.stream().map(SubstituteRecordEntity::getSubstituteTeacherId).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(teacherIds)) {
                        List<UserSchoolRelEntity> users = userSchoolRelDao.selectBatchIds(teacherIds);
                        if (CollectionUtils.isNotEmpty(users)) {
                            userMap = users.stream().collect(Collectors.toMap(UserSchoolRelEntity::getId, userSchoolRelEntity -> userSchoolRelEntity));
                        }
                    }
                }
            }
            Map<LocalDate, List<CourseScheduleEntity>> courseScheduleMap = list.stream().collect(Collectors.groupingBy(CourseScheduleEntity::getCourseDate));
            Iterator<Map.Entry<LocalDate, List<CourseScheduleEntity>>> iterator = courseScheduleMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<LocalDate, List<CourseScheduleEntity>> entry = iterator.next();
                CourseScheduleClassListResModel resModel = new CourseScheduleClassListResModel();
                resModel.setCourseDate(entry.getKey());
                List<CourseScheduleEntity> courseSchedules = entry.getValue();
                List<CourseScheduleClassListDetailsResModel> courseDetails = new ArrayList<>();
                for (CourseScheduleEntity courseSchedule : courseSchedules) {
                    CourseScheduleClassListDetailsResModel courseDetail = new CourseScheduleClassListDetailsResModel();
                    BeanUtils.copyProperties(courseSchedule, courseDetail);
                    if (classroomMap.containsKey(courseSchedule.getClassroomId())) {
                        ClassroomEntity classroom = classroomMap.get(courseSchedule.getClassroomId());
                        if (classroomTypeMap.containsKey(classroom.getTypeId())) {
                            ClassroomTypeEntity classroomType = classroomTypeMap.get(classroom.getTypeId());
                            courseDetail.setClassroomTypeId(classroomType.getId());
                            courseDetail.setClassroomTypeName(classroomType.getName());
                            courseDetail.setClassroomTypeIsSystem(classroomType.getIsSystem());
                        }
                    }
                    SubstituteRecordEntity substituteRecord = substituteMap.get(entry.getKey().toString() + "_" + courseSchedule.getLessonId());
                    if (substituteRecord != null) {
                        //有代课信息，展示代课教师信息
                        courseDetail.setIsSubstitute(true);
                        courseDetail.setTeacherId(substituteRecord.getSubstituteTeacherId());
                        UserSchoolRelEntity user = userMap.get(substituteRecord.getSubstituteTeacherId());
                        if (user != null) {
                            courseDetail.setTeacherName(user.getUsername());
                        }
                    } else {
                        //无代课信息，展示原教师信息
                        courseDetail.setIsSubstitute(false);
                    }
                    courseDetails.add(courseDetail);
                }
                if (CollectionUtils.isNotEmpty(courseDetails)) {
                    //按开始时间排序
                    courseDetails.sort(Comparator.comparing(CourseScheduleClassListDetailsResModel::getStartTime));
                    resModel.setCourseDetails(courseDetails);
                }
                resList.add(resModel);
            }
        }
        if (CollectionUtils.isNotEmpty(resList)) {
            //按开始时间排序
            resList.sort(Comparator.comparing(CourseScheduleClassListResModel::getCourseDate));
        }
        return resList;
    }


        @Override
    public List<CourseScheduleClassListResModel> classList(Long schoolId, Long userId, CourseScheduleClassListReqModel reqModel) {
        List<CourseScheduleClassListResModel> resList = new ArrayList<>();
        //获取用户信息
        UserEntity userEntity = userService.getById(userId);
        if (userEntity == null) {
            throw new BusinessException(LanguageConstants.USER_NOT_EXISTS);
        }
        List<CourseScheduleEntity> list = new ArrayList<>();
        // 校验班级权限
        boolean commonUser = userAuthHelper.getCommonUser(userId, schoolId);
        List<Long> classIds = null;
        if(commonUser)
        {
            classIds = userAuthHelper.getUserClassIds(userId, schoolId);
            if(CollectionUtils.isEmpty(classIds))
            {
                return new ArrayList<>();
            }
        }
        // 查询课表信息
        QueryWrapper<CourseScheduleEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(CourseScheduleEntity::getSchoolId, schoolId)
                .eq(CourseScheduleEntity::getClassId, reqModel.getClassId())
                .in(commonUser,CourseScheduleEntity::getClassId, classIds)
                .eq(reqModel.getPeriodId() != null && reqModel.getPeriodId() > 0, CourseScheduleEntity::getPeriodId, reqModel.getPeriodId())
                .ge(CourseScheduleEntity::getCourseDate, reqModel.getStartDate())
                .le(CourseScheduleEntity::getCourseDate, reqModel.getEndDate());
        list = this.list(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            //获取教室信息
            Map<Long, ClassroomEntity> classroomMap = new HashMap<>();
            Map<Long, ClassroomTypeEntity> classroomTypeMap = new HashMap<>();
            List<Long> classroomIds = list.stream().map(CourseScheduleEntity::getClassroomId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(classroomIds)) {
                List<ClassroomEntity> classrooms = classroomService.listByIds(classroomIds);
                if (CollectionUtils.isNotEmpty(classrooms)) {
                    classroomMap = classrooms.stream().collect(Collectors.toMap(ClassroomEntity::getId, classroom -> classroom));
                    List<Long> classroomTypeIds = classrooms.stream().map(ClassroomEntity::getTypeId).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(classroomTypeIds)) {
                        List<ClassroomTypeEntity> classroomTypes = classroomTypeService.listByIds(classroomTypeIds);
                        if (CollectionUtils.isNotEmpty(classroomTypes)) {
                            classroomTypeMap = classroomTypes.stream().collect(Collectors.toMap(ClassroomTypeEntity::getId, classroomTypeEntity -> classroomTypeEntity));
                        }
                    }
                }
            }
            //获取班级相关代课信息
            List<LocalDate> courseDates = list.stream().map(CourseScheduleEntity::getCourseDate).collect(Collectors.toList());
            Map<String, SubstituteRecordEntity> substituteMap = new HashMap<>();
            Map<Long, UserSchoolRelEntity> userMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(courseDates)) {
                QueryWrapper<SubstituteRecordEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(SubstituteRecordEntity::getSchoolId, schoolId)
                        .eq(SubstituteRecordEntity::getClassId, reqModel.getClassId())
                        .ge(SubstituteRecordEntity::getSubstituteDate, reqModel.getStartDate())
                        .le(SubstituteRecordEntity::getSubstituteDate, reqModel.getEndDate());
                List<SubstituteRecordEntity> substitutes = substituteRecordService.list(queryWrapper);
                if (CollectionUtils.isNotEmpty(substitutes)) {
                    substituteMap = substitutes.stream().collect(Collectors.toMap(substituteRecordEntity -> substituteRecordEntity.getSubstituteDate().toString() + "_" + substituteRecordEntity.getLessonId(), substituteRecordEntity -> substituteRecordEntity));
                    //获取教师信息
                    List<Long> teacherIds = substitutes.stream().map(SubstituteRecordEntity::getSubstituteTeacherId).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(teacherIds)) {
                        List<UserSchoolRelEntity> users = userSchoolRelDao.selectBatchIds(teacherIds);
                        if (CollectionUtils.isNotEmpty(users)) {
                            userMap = users.stream().collect(Collectors.toMap(UserSchoolRelEntity::getId, userSchoolRelEntity -> userSchoolRelEntity));
                        }
                    }
                }
            }
            Map<LocalDate, List<CourseScheduleEntity>> courseScheduleMap = list.stream().collect(Collectors.groupingBy(CourseScheduleEntity::getCourseDate));
            Iterator<Map.Entry<LocalDate, List<CourseScheduleEntity>>> iterator = courseScheduleMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<LocalDate, List<CourseScheduleEntity>> entry = iterator.next();
                CourseScheduleClassListResModel resModel = new CourseScheduleClassListResModel();
                resModel.setCourseDate(entry.getKey());
                List<CourseScheduleEntity> courseSchedules = entry.getValue();
                List<CourseScheduleClassListDetailsResModel> courseDetails = new ArrayList<>();
                for (CourseScheduleEntity courseSchedule : courseSchedules) {
                    CourseScheduleClassListDetailsResModel courseDetail = new CourseScheduleClassListDetailsResModel();
                    BeanUtils.copyProperties(courseSchedule, courseDetail);
                    if (classroomMap.containsKey(courseSchedule.getClassroomId())) {
                        ClassroomEntity classroom = classroomMap.get(courseSchedule.getClassroomId());
                        if (classroomTypeMap.containsKey(classroom.getTypeId())) {
                            ClassroomTypeEntity classroomType = classroomTypeMap.get(classroom.getTypeId());
                            courseDetail.setClassroomTypeId(classroomType.getId());
                            courseDetail.setClassroomTypeName(classroomType.getName());
                            courseDetail.setClassroomTypeIsSystem(classroomType.getIsSystem());
                        }
                    }
                    SubstituteRecordEntity substituteRecord = substituteMap.get(entry.getKey().toString() + "_" + courseSchedule.getLessonId());
                    if (substituteRecord != null) {
                        //有代课信息，展示代课教师信息
                        courseDetail.setIsSubstitute(true);
                        courseDetail.setTeacherId(substituteRecord.getSubstituteTeacherId());
                        UserSchoolRelEntity user = userMap.get(substituteRecord.getSubstituteTeacherId());
                        if (user != null) {
                            courseDetail.setTeacherName(user.getUsername());
                        }
                    } else {
                        //无代课信息，展示原教师信息
                        courseDetail.setIsSubstitute(false);
                    }
                    courseDetails.add(courseDetail);
                }
                if (CollectionUtils.isNotEmpty(courseDetails)) {
                    //按开始时间排序
                    courseDetails.sort(Comparator.comparing(CourseScheduleClassListDetailsResModel::getStartTime));
                    resModel.setCourseDetails(courseDetails);
                }
                resList.add(resModel);
            }
        }
        if (CollectionUtils.isNotEmpty(resList)) {
            //按开始时间排序
            resList.sort(Comparator.comparing(CourseScheduleClassListResModel::getCourseDate));
        }
        return resList;
    }

    @Override
    public List<CourseScheduleClassListResModel> classQuery(Long schoolId, Long userId, CourseScheduleClassQueryReqModel reqModel) {
        List<CourseScheduleClassListResModel> resList = new ArrayList<>();
        QueryWrapper<CourseScheduleEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(CourseScheduleEntity::getSchoolId, schoolId)
                .eq(CourseScheduleEntity::getClassId, reqModel.getClassId())
                .ge(CourseScheduleEntity::getCourseDate, reqModel.getStartDate())
                .le(CourseScheduleEntity::getCourseDate, reqModel.getEndDate());
        List<CourseScheduleEntity> list = this.list(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            //获取教室信息
            Map<Long, ClassroomEntity> classroomMap = new HashMap<>();
            Map<Long, ClassroomTypeEntity> classroomTypeMap = new HashMap<>();
            List<Long> classroomIds = list.stream().map(CourseScheduleEntity::getClassroomId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(classroomIds)) {
                List<ClassroomEntity> classrooms = classroomService.listByIds(classroomIds);
                if (CollectionUtils.isNotEmpty(classrooms)) {
                    classroomMap = classrooms.stream().collect(Collectors.toMap(ClassroomEntity::getId, classroom -> classroom));
                    List<Long> classroomTypeIds = classrooms.stream().map(ClassroomEntity::getTypeId).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(classroomTypeIds)) {
                        List<ClassroomTypeEntity> classroomTypes = classroomTypeService.listByIds(classroomTypeIds);
                        if (CollectionUtils.isNotEmpty(classroomTypes)) {
                            classroomTypeMap = classroomTypes.stream().collect(Collectors.toMap(ClassroomTypeEntity::getId, classroomTypeEntity -> classroomTypeEntity));
                        }
                    }
                }
            }
            //获取班级相关代课信息
            List<LocalDate> courseDates = list.stream().map(CourseScheduleEntity::getCourseDate).collect(Collectors.toList());
            Map<String, SubstituteRecordEntity> substituteMap = new HashMap<>();
            Map<Long, UserSchoolRelEntity> userMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(courseDates)) {
                QueryWrapper<SubstituteRecordEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(SubstituteRecordEntity::getSchoolId, schoolId)
                        .eq(SubstituteRecordEntity::getClassId, reqModel.getClassId())
                        .ge(SubstituteRecordEntity::getSubstituteDate, reqModel.getStartDate())
                        .le(SubstituteRecordEntity::getSubstituteDate, reqModel.getEndDate());
                List<SubstituteRecordEntity> substitutes = substituteRecordService.list(queryWrapper);
                if (CollectionUtils.isNotEmpty(substitutes)) {
                    substituteMap = substitutes.stream().collect(Collectors.toMap(substituteRecordEntity -> substituteRecordEntity.getSubstituteDate().toString() + "_" + substituteRecordEntity.getLessonId(), substituteRecordEntity -> substituteRecordEntity));
                    //获取教师信息
                    List<Long> teacherIds = substitutes.stream().map(SubstituteRecordEntity::getSubstituteTeacherId).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(teacherIds)) {
                        List<UserSchoolRelEntity> users = userSchoolRelDao.selectBatchIds(teacherIds);
                        if (CollectionUtils.isNotEmpty(users)) {
                            userMap = users.stream().collect(Collectors.toMap(UserSchoolRelEntity::getId, userSchoolRelEntity -> userSchoolRelEntity));
                        }
                    }
                }
            }
            Map<LocalDate, List<CourseScheduleEntity>> courseScheduleMap = list.stream().collect(Collectors.groupingBy(CourseScheduleEntity::getCourseDate));
            Iterator<Map.Entry<LocalDate, List<CourseScheduleEntity>>> iterator = courseScheduleMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<LocalDate, List<CourseScheduleEntity>> entry = iterator.next();
                CourseScheduleClassListResModel resModel = new CourseScheduleClassListResModel();
                resModel.setCourseDate(entry.getKey());
                List<CourseScheduleEntity> courseSchedules = entry.getValue();
                List<CourseScheduleClassListDetailsResModel> courseDetails = new ArrayList<>();
                for (CourseScheduleEntity courseSchedule : courseSchedules) {
                    CourseScheduleClassListDetailsResModel courseDetail = new CourseScheduleClassListDetailsResModel();
                    BeanUtils.copyProperties(courseSchedule, courseDetail);
                    if (classroomMap.containsKey(courseSchedule.getClassroomId())) {
                        ClassroomEntity classroom = classroomMap.get(courseSchedule.getClassroomId());
                        if (classroomTypeMap.containsKey(classroom.getTypeId())) {
                            ClassroomTypeEntity classroomType = classroomTypeMap.get(classroom.getTypeId());
                            courseDetail.setClassroomTypeId(classroomType.getId());
                            courseDetail.setClassroomTypeName(classroomType.getName());
                            courseDetail.setClassroomTypeIsSystem(classroomType.getIsSystem());
                        }
                    }
                    SubstituteRecordEntity substituteRecord = substituteMap.get(entry.getKey().toString() + "_" + courseSchedule.getLessonId());
                    if (substituteRecord != null) {
                        //有代课信息，展示代课教师信息
                        courseDetail.setIsSubstitute(true);
                        courseDetail.setTeacherId(substituteRecord.getSubstituteTeacherId());
                        UserSchoolRelEntity user = userMap.get(substituteRecord.getSubstituteTeacherId());
                        if (user != null) {
                            courseDetail.setTeacherName(user.getUsername());
                        }
                    } else {
                        //无代课信息，展示原教师信息
                        courseDetail.setIsSubstitute(false);
                    }
                    courseDetails.add(courseDetail);
                }
                if (CollectionUtils.isNotEmpty(courseDetails)) {
                    //按开始时间排序
                    courseDetails.sort(Comparator.comparing(CourseScheduleClassListDetailsResModel::getStartTime));
                    resModel.setCourseDetails(courseDetails);
                }
                resList.add(resModel);
            }
        }
        if (CollectionUtils.isNotEmpty(resList)) {
            //按开始时间排序
            resList.sort(Comparator.comparing(CourseScheduleClassListResModel::getCourseDate));
        }
        return resList;
    }

    @Override
    public List<CourseScheduleHomeClassListResModel> homeClassList(Long schoolId, Long userId, CourseScheduleHomeClassListReqModel reqModel) {
        List<CourseScheduleHomeClassListResModel> resList = new ArrayList<>();
        //获取用户信息
        UserEntity userEntity = userService.getById(userId);
        if (userEntity == null) {
            throw new BusinessException(LanguageConstants.USER_NOT_EXISTS);
        }
        List<CourseScheduleEntity> courses = new ArrayList<>();
        boolean isNormalTeacher = false;//是否普通任课、代课教师，用于筛选出任课和代课教师看到不属于自己的课
        Long teacherId = null;
        if (UserTypeEnum.isSuperAdmin(userEntity.getUserType())) {
            //超级管理员
            //获取课表信息
            QueryWrapper<CourseScheduleEntity> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(CourseScheduleEntity::getSchoolId, schoolId)
                    .eq(CourseScheduleEntity::getGradeId, reqModel.getGradeId())
                    .eq(CourseScheduleEntity::getPeriodId, reqModel.getPeriodId())
                    .eq(CourseScheduleEntity::getCourseDate, reqModel.getCourseDate());
            courses = this.list(wrapper);
        } else {
            QueryWrapper<UserSchoolRelEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(UserSchoolRelEntity::getSchoolId, schoolId)
                    .eq(UserSchoolRelEntity::getUserId, userId);
            List<UserSchoolRelEntity> users = userSchoolRelDao.selectList(queryWrapper);
            if (!CollectionUtils.isNotEmpty(users)) {
                throw new BusinessException(LanguageConstants.USER_NOT_EXISTS);
            }
            UserSchoolRelEntity user = users.get(0);
            teacherId = user.getId();
            List<Long> userGroupIds = Arrays.stream(user.getUserGroupIds().split(",")).map(Long::parseLong).collect(Collectors.toList());
            List<UserGroupEntity> userGroups = userGroupService.listByIds(userGroupIds);
            if (CollectionUtils.isNotEmpty(userGroups)) {
                long count = userGroups.stream().filter(userGroupEntity -> StringUtils.isNotBlank(userGroupEntity.getCode()) && userGroupEntity.getCode().equals(UserGroupTypeEnum.SCHOOL_ADMIN.getCode())).count();
                if (count > 0) {
                    //拥有学校管理员权限
                    //获取课表信息
                    QueryWrapper<CourseScheduleEntity> wrapper = new QueryWrapper<>();
                    wrapper.lambda().eq(CourseScheduleEntity::getSchoolId, schoolId)
                            .eq(CourseScheduleEntity::getGradeId, reqModel.getGradeId())
                            .eq(CourseScheduleEntity::getPeriodId, reqModel.getPeriodId())
                            .eq(CourseScheduleEntity::getCourseDate, reqModel.getCourseDate());
                    courses = this.list(wrapper);
                } else {
                    List<Long> classIds = userAuthHelper.getUserClassIds(userId, schoolId);
                    if(CollectionUtils.isEmpty(classIds))
                    {
                        courses = new ArrayList<>();
                    }else {
                        QueryWrapper<CourseScheduleEntity> wrapper = new QueryWrapper<>();
                        wrapper.lambda().eq(CourseScheduleEntity::getSchoolId, schoolId)
                                .eq(CourseScheduleEntity::getGradeId, reqModel.getGradeId())
                                .eq(CourseScheduleEntity::getPeriodId, reqModel.getPeriodId())
                                .in(CollectionUtils.isNotEmpty(classIds),CourseScheduleEntity::getClassId, classIds)
                                .eq(CourseScheduleEntity::getCourseDate, reqModel.getCourseDate());
                        courses = this.list(wrapper);
                    }
                }
            }
        }
        //获取班级相关代课信息
        List<Long> addCourseScheduleIds = new ArrayList<>();
        Map<String, SubstituteRecordEntity> substituteMap = new HashMap<>();
        Map<Long, UserSchoolRelEntity> userMap = new HashMap<>();
        QueryWrapper<SubstituteRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SubstituteRecordEntity::getSchoolId, schoolId)
                .eq(SubstituteRecordEntity::getSubstituteDate, reqModel.getCourseDate());
        List<SubstituteRecordEntity> substitutes = substituteRecordService.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(substitutes)) {
            //若为代课教师把今天被代课的课都加入到courses列表中
            if (isNormalTeacher) {
                for (SubstituteRecordEntity substituteRecordEntity : substitutes) {
                    if (Objects.equals(substituteRecordEntity.getSubstituteTeacherId(), teacherId)) {
                        addCourseScheduleIds.add(substituteRecordEntity.getCourseScheduleId());
                    }
                }
                courses.addAll(this.listByIds(addCourseScheduleIds));
            }
            substituteMap = substitutes.stream().collect(Collectors.toMap(substituteRecordEntity -> substituteRecordEntity.getClassId() + "_" + substituteRecordEntity.getLessonId(), substituteRecordEntity -> substituteRecordEntity));
            //获取教师信息
            List<Long> teacherIds = substitutes.stream().map(SubstituteRecordEntity::getSubstituteTeacherId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(teacherIds)) {
                List<UserSchoolRelEntity> users = userSchoolRelDao.selectBatchIds(teacherIds);
                if (CollectionUtils.isNotEmpty(users)) {
                    userMap = users.stream().collect(Collectors.toMap(UserSchoolRelEntity::getId, userSchoolRelEntity -> userSchoolRelEntity));
                }
            }
        }
        if (CollectionUtils.isNotEmpty(courses)) {
            //获取班级信息
            List<Long> classIds = courses.stream().map(CourseScheduleEntity::getClassId).collect(Collectors.toList());
            Map<Long, SysClass> classMap = new HashMap<>();
            Map<Long, GradeGroup> gradeMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(classIds)) {
                List<SysClass> sysClasses = sysClassService.listByIds(classIds);
                if (CollectionUtils.isNotEmpty(sysClasses)) {
                    classMap = sysClasses.stream().collect(Collectors.toMap(SysClass::getId, sysClass -> sysClass));
                    List<Long> gradeIds = sysClasses.stream().map(SysClass::getGradeGroup).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(gradeIds)) {
                        List<GradeGroup> gradeGroups = gradeGroupService.listByIds(gradeIds);
                        if (CollectionUtils.isNotEmpty(gradeGroups)) {
                            gradeMap = gradeGroups.stream().collect(Collectors.toMap(GradeGroup::getId, gradeGroup -> gradeGroup));
                        }
                    }
                }
            }
            Map<Long, List<CourseScheduleEntity>> classCourseMap = courses.stream().collect(Collectors.groupingBy(CourseScheduleEntity::getClassId));
            Iterator<Map.Entry<Long, List<CourseScheduleEntity>>> iterator = classCourseMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Long, List<CourseScheduleEntity>> entry = iterator.next();
                SysClass sysClass = classMap.get(entry.getKey());
                if (sysClass != null) {
                    CourseScheduleHomeClassListResModel resModel = new CourseScheduleHomeClassListResModel();
                    resModel.setDepartment(sysClass.getDepartment());
                    resModel.setClassSerialNumber(sysClass.getClassSerialNumber());
                    resModel.setClassName(sysClass.getClassName());
                    GradeGroup gradeGroup = gradeMap.get(sysClass.getGradeGroup());
                    if (gradeGroup != null) {
                        resModel.setGradeName(gradeGroup.getGradeGroupName());
                    }
                    List<CourseScheduleClassListDetailsResModel> courseDetails = new ArrayList<>();
                    List<CourseScheduleEntity> courseSchedules = entry.getValue();
                    for (CourseScheduleEntity courseSchedule : courseSchedules) {
                        CourseScheduleClassListDetailsResModel courseDetail = new CourseScheduleClassListDetailsResModel();
                        BeanUtils.copyProperties(courseSchedule, courseDetail);
                        SubstituteRecordEntity substituteRecord = substituteMap.get(entry.getKey() + "_" + courseSchedule.getLessonId());
                        if (substituteRecord != null) {
                            //有代课信息，展示代课教师信息
                            courseDetail.setIsSubstitute(true);
                            courseDetail.setTeacherId(substituteRecord.getSubstituteTeacherId());
                            UserSchoolRelEntity teacherInfo = userMap.get(substituteRecord.getSubstituteTeacherId());
                            if (teacherInfo != null) {
                                courseDetail.setTeacherName(teacherInfo.getUsername());
                            }
                        } else {
                            //无代课信息，展示原教师信息
                            courseDetail.setIsSubstitute(false);
                        }
                        courseDetails.add(courseDetail);
                    }
                    if (CollectionUtils.isNotEmpty(courseDetails)) {
                        //按开始时间排序
                        courseDetails.sort(Comparator.comparing(CourseScheduleClassListDetailsResModel::getStartTime));
                        resModel.setCourseDetails(courseDetails);
                    }
                    resList.add(resModel);
                }
            }
            resList.sort(Comparator.comparing(CourseScheduleHomeClassListResModel::getDepartment).thenComparing(CourseScheduleHomeClassListResModel::getClassSerialNumber));
        }
        return resList;
    }

    @Override
    public List<CourseScheduleTeacherListResModel> teacherList(Long schoolId, CourseScheduleTeacherListReqModel reqModel) {
        List<CourseScheduleTeacherListResModel> resList = new ArrayList<>();
        //获取教师课表信息
        Map<LocalDate, List<CourseScheduleTeacherListDetailsResModel>> courseMap = new HashMap<>();
        QueryWrapper<CourseScheduleEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(CourseScheduleEntity::getSchoolId, schoolId)
                .eq(CourseScheduleEntity::getTeacherId, reqModel.getTeacherId())
                .eq(CourseScheduleEntity::getPeriodId, reqModel.getPeriodId())
                .ge(CourseScheduleEntity::getCourseDate, reqModel.getStartDate())
                .le(CourseScheduleEntity::getCourseDate, reqModel.getEndDate());
        List<CourseScheduleEntity> courses = this.list(wrapper);
        if (CollectionUtils.isNotEmpty(courses)) {
            //获取班级信息
            List<Long> classIds = courses.stream().map(CourseScheduleEntity::getClassId).collect(Collectors.toList());
            Map<Long, SysClass> classMap = new HashMap<>();
            Map<Long, GradeGroup> gradeMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(classIds)) {
                List<SysClass> sysClasses = sysClassService.listByIds(classIds);
                if (CollectionUtils.isNotEmpty(sysClasses)) {
                    classMap = sysClasses.stream().collect(Collectors.toMap(SysClass::getId, sysClass -> sysClass));
                    List<Long> gradeIds = sysClasses.stream().map(SysClass::getGradeGroup).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(gradeIds)) {
                        List<GradeGroup> gradeGroups = gradeGroupService.listByIds(gradeIds);
                        if (CollectionUtils.isNotEmpty(gradeGroups)) {
                            gradeMap = gradeGroups.stream().collect(Collectors.toMap(GradeGroup::getId, gradeGroup -> gradeGroup));
                        }
                    }
                }
            }
            Map<LocalDate, List<CourseScheduleEntity>> courseScheduleMap = courses.stream().collect(Collectors.groupingBy(CourseScheduleEntity::getCourseDate));
            Iterator<Map.Entry<LocalDate, List<CourseScheduleEntity>>> iterator = courseScheduleMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<LocalDate, List<CourseScheduleEntity>> entry = iterator.next();
                List<CourseScheduleEntity> courseSchedules = entry.getValue();
                List<CourseScheduleTeacherListDetailsResModel> courseDetails = new ArrayList<>();
                for (CourseScheduleEntity courseSchedule : courseSchedules) {
                    CourseScheduleTeacherListDetailsResModel courseDetail = new CourseScheduleTeacherListDetailsResModel();
                    BeanUtils.copyProperties(courseSchedule, courseDetail);
                    courseDetail.setIsSubstitute(false);
                    SysClass sysClass = classMap.get(courseSchedule.getClassId());
                    if (sysClass != null) {
                        courseDetail.setClassName(sysClass.getClassName());
                        GradeGroup gradeGroup = gradeMap.get(sysClass.getGradeGroup());
                        if (gradeGroup != null) {
                            courseDetail.setGradeName(gradeGroup.getGradeGroupName());
                        }
                    }
                    courseDetails.add(courseDetail);
                }
                courseMap.put(entry.getKey(), courseDetails);
            }
        }
        //获取教师代课信息
        QueryWrapper<SubstituteRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SubstituteRecordEntity::getSchoolId, schoolId)
                .eq(SubstituteRecordEntity::getSubstituteTeacherId, reqModel.getTeacherId())
                .ge(SubstituteRecordEntity::getSubstituteDate, reqModel.getStartDate())
                .le(SubstituteRecordEntity::getSubstituteDate, reqModel.getEndDate());
        List<SubstituteRecordEntity> substitutes = substituteRecordService.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(substitutes)) {
            //获取代课记录相关的课表信息
            List<Long> courseScheduleIds = substitutes.stream().map(SubstituteRecordEntity::getCourseScheduleId).collect(Collectors.toList());
            List<CourseScheduleEntity> courseSchedules = this.listByIds(courseScheduleIds);
            Map<Long, CourseScheduleEntity> courseScheduleMap = new HashMap<>();
            Map<Long, SysClass> classMap = new HashMap<>();
            Map<Long, GradeGroup> gradeMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(courseSchedules)) {
                courseScheduleMap = courseSchedules.stream().collect(Collectors.toMap(CourseScheduleEntity::getId, courseSchedule -> courseSchedule));
                //获取班级信息
                List<Long> classIds = courseSchedules.stream().map(CourseScheduleEntity::getClassId).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(classIds)) {
                    List<SysClass> sysClasses = sysClassService.listByIds(classIds);
                    if (CollectionUtils.isNotEmpty(sysClasses)) {
                        classMap = sysClasses.stream().collect(Collectors.toMap(SysClass::getId, sysClass -> sysClass));
                        List<Long> gradeIds = sysClasses.stream().map(SysClass::getGradeGroup).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(gradeIds)) {
                            List<GradeGroup> gradeGroups = gradeGroupService.listByIds(gradeIds);
                            if (CollectionUtils.isNotEmpty(gradeGroups)) {
                                gradeMap = gradeGroups.stream().collect(Collectors.toMap(GradeGroup::getId, gradeGroup -> gradeGroup));
                            }
                        }
                    }
                }
            }
            Map<LocalDate, List<SubstituteRecordEntity>> substituteMap = substitutes.stream().collect(Collectors.groupingBy(SubstituteRecordEntity::getSubstituteDate));
            Iterator<Map.Entry<LocalDate, List<SubstituteRecordEntity>>> iterator = substituteMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<LocalDate, List<SubstituteRecordEntity>> entry = iterator.next();
                List<SubstituteRecordEntity> substituteRecords = entry.getValue();
                List<CourseScheduleTeacherListDetailsResModel> courseDetails = new ArrayList<>();
                for (SubstituteRecordEntity substituteRecord : substituteRecords) {
                    CourseScheduleTeacherListDetailsResModel courseDetail = new CourseScheduleTeacherListDetailsResModel();
                    CourseScheduleEntity courseSchedule = courseScheduleMap.get(substituteRecord.getCourseScheduleId());
                    if (courseSchedule != null) {
                        BeanUtils.copyProperties(courseSchedule, courseDetail);
                        courseDetail.setIsSubstitute(true);
                        SysClass sysClass = classMap.get(courseSchedule.getClassId());
                        if (sysClass != null) {
                            courseDetail.setClassName(sysClass.getClassName());
                            GradeGroup gradeGroup = gradeMap.get(sysClass.getGradeGroup());
                            if (gradeGroup != null) {
                                courseDetail.setGradeName(gradeGroup.getGradeGroupName());
                            }
                        }
                        courseDetails.add(courseDetail);
                    }
                }
                List<CourseScheduleTeacherListDetailsResModel> resModels = courseMap.get(entry.getKey());
                if (CollectionUtils.isNotEmpty(resModels)) {
                    resModels.addAll(courseDetails);
                } else {
                    courseMap.put(entry.getKey(), courseDetails);
                }
            }
        }
        if (!courseMap.isEmpty()) {
            Iterator<Map.Entry<LocalDate, List<CourseScheduleTeacherListDetailsResModel>>> iterator = courseMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<LocalDate, List<CourseScheduleTeacherListDetailsResModel>> entry = iterator.next();
                CourseScheduleTeacherListResModel resModel = new CourseScheduleTeacherListResModel();
                resModel.setCourseDate(entry.getKey());
                List<CourseScheduleTeacherListDetailsResModel> courseDetails = entry.getValue();
                if (CollectionUtils.isNotEmpty(courseDetails)) {
                    //按开始时间排序
                    courseDetails.sort(Comparator.comparing(CourseScheduleTeacherListDetailsResModel::getStartTime));
                    resModel.setCourseDetails(courseDetails);
                }
                resList.add(resModel);
            }
        }
        if (CollectionUtils.isNotEmpty(resList)) {
            //按开始时间排序
            resList.sort(Comparator.comparing(CourseScheduleTeacherListResModel::getCourseDate));
        }
        return resList;
    }

    @Override
    @Transactional
    public void copyToWeeks(Long schoolId, CourseScheduleCopyReqModel reqModel) {
        QueryWrapper<CourseScheduleEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(CourseScheduleEntity::getSchoolId, schoolId)
                .eq(CourseScheduleEntity::getClassId, reqModel.getClassId())
                .eq(CourseScheduleEntity::getPeriodId, reqModel.getPeriodId())
                .ge(CourseScheduleEntity::getCourseDate, reqModel.getStartDate())
                .le(CourseScheduleEntity::getCourseDate, reqModel.getEndDate());
        List<CourseScheduleEntity> sourceCourseSchedules = this.list(wrapper);
        if (CollectionUtils.isNotEmpty(sourceCourseSchedules) && CollectionUtils.isNotEmpty(reqModel.getDateList())) {
            Map<Integer, List<CourseScheduleEntity>> weekCourseMap = sourceCourseSchedules.stream().collect(Collectors.groupingBy(entity -> entity.getCourseDate().getDayOfWeek().getValue()));
            List<CourseScheduleEntity> saveOrUpdateList = new ArrayList<>();
            List<String> errorList = new ArrayList<>();
            for (CourseScheduleDateCopyReqModel courseScheduleDateCopyReqModel : reqModel.getDateList()) {
                Map<LocalDate, List<CourseScheduleEntity>> courseMap = new HashMap<>();
                Map<LocalDate, List<SubstituteRecordEntity>> substituteMap = new HashMap<>();
                Map<Long, CourseScheduleEntity> courseScheduleMap = new HashMap<>();
                List<LocalDate> dateList = DateUtils.generateDates(courseScheduleDateCopyReqModel.getStartDate(), courseScheduleDateCopyReqModel.getEndDate());
                if (CollectionUtils.isNotEmpty(dateList)) {
                    //获取课表信息
                    QueryWrapper<CourseScheduleEntity> queryWrapper = new QueryWrapper<>();
                    queryWrapper.lambda().eq(CourseScheduleEntity::getSchoolId, schoolId)
                            .in(CourseScheduleEntity::getCourseDate, dateList);
                    List<CourseScheduleEntity> courses = this.list(queryWrapper);
                    if (CollectionUtils.isNotEmpty(courses)) {
                        courseMap = courses.stream().collect(Collectors.groupingBy(CourseScheduleEntity::getCourseDate));
                    }
                    //获取代课信息
                    QueryWrapper<SubstituteRecordEntity> queryWrapper1 = new QueryWrapper<>();
                    queryWrapper1.lambda().eq(SubstituteRecordEntity::getSchoolId, schoolId)
                            .in(SubstituteRecordEntity::getSubstituteDate, dateList);
                    List<SubstituteRecordEntity> substitutes = substituteRecordService.list(queryWrapper1);
                    if (CollectionUtils.isNotEmpty(substitutes)) {
                        substituteMap = substitutes.stream().collect(Collectors.groupingBy(SubstituteRecordEntity::getSubstituteDate));
                        List<Long> courseScheduleIds = substitutes.stream().map(SubstituteRecordEntity::getCourseScheduleId).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(courseScheduleIds)) {
                            List<CourseScheduleEntity> courseScheduleEntities = this.listByIds(courseScheduleIds);
                            if (CollectionUtils.isNotEmpty(courseScheduleEntities)) {
                                courseScheduleMap = courseScheduleEntities.stream().collect(Collectors.toMap(CourseScheduleEntity::getId, courseScheduleEntity -> courseScheduleEntity));
                            }
                        }
                    }
                    for (LocalDate courseScheduleDate : dateList) {
                        //获取当天的课程信息
                        List<CourseScheduleEntity> scheduleEntityList = courseMap.get(courseScheduleDate);
                        Map<String, CourseScheduleEntity> scheduleEntityMap = new HashMap<>();
                        if (CollectionUtils.isNotEmpty(scheduleEntityList)) {
                            scheduleEntityMap = scheduleEntityList.stream().collect(Collectors.toMap(courseScheduleEntity -> courseScheduleEntity.getClassId() + "_" + courseScheduleEntity.getLessonId(), courseScheduleEntity -> courseScheduleEntity));
                        }
                        //获取当天的代课信息
                        List<SubstituteRecordEntity> substituteRecords = substituteMap.get(courseScheduleDate);
                        List<CourseScheduleEntity> scheduleEntities = weekCourseMap.get(courseScheduleDate.getDayOfWeek().getValue());
                        if (CollectionUtils.isNotEmpty(scheduleEntities)) {
                            for (CourseScheduleEntity courseSchedule : scheduleEntities) {
                                if (CollectionUtils.isNotEmpty(scheduleEntityList)) {
                                    List<CourseScheduleEntity> classCourses = scheduleEntityList.stream().filter(courseScheduleEntity -> !courseScheduleEntity.getClassId().equals(reqModel.getClassId())).collect(Collectors.toList());
                                    if (CollectionUtils.isNotEmpty(classCourses)) {
                                        //其他班级课表时间是否冲突
                                        //教师课表时间是否冲突
                                        List<CourseScheduleEntity> teacherCourses = courses.stream().filter(courseScheduleEntity -> courseScheduleEntity.getTeacherId().equals(courseSchedule.getTeacherId())).collect(Collectors.toList());
                                        if (CollectionUtils.isNotEmpty(teacherCourses)) {
                                            long count = teacherCourses.stream().filter(courseScheduleEntity -> courseScheduleEntity.getCourseDate().equals(courseSchedule.getCourseDate()) && courseScheduleEntity.getStartTime().isBefore(courseSchedule.getEndTime()) && courseScheduleEntity.getEndTime().isAfter(courseSchedule.getStartTime())).count();
                                            if (count > 0) {
                                                errorList.add("[" + formatter.format(courseScheduleDate) + "-" + courseSchedule.getLessonName() + "]");
                                                break;
                                            }
                                        }
                                        //教室是否冲突
                                        long count = classCourses.stream().filter(courseScheduleEntity -> courseSchedule.getClassroomId() != null && courseScheduleEntity.getClassroomId().equals(courseSchedule.getClassroomId()) && courseScheduleEntity.getCourseDate()!=null && courseScheduleEntity.getCourseDate().equals(courseSchedule.getCourseDate()) && courseScheduleEntity.getStartTime().isBefore(courseSchedule.getEndTime()) && courseScheduleEntity.getEndTime().isAfter(courseSchedule.getStartTime())).count();
                                        if (count > 0) {
                                            errorList.add("[" + formatter.format(courseScheduleDate) + "-" + courseSchedule.getLessonName() + "]");
                                            break;
                                        }
                                    }
                                }
                                //教师代课是否冲突
                                if (CollectionUtils.isNotEmpty(substituteRecords)) {
                                    List<SubstituteRecordEntity> teacherSubstitutes = substituteRecords.stream().filter(substituteRecord -> substituteRecord.getSubstituteTeacherId().equals(courseSchedule.getTeacherId())).collect(Collectors.toList());
                                    if (CollectionUtils.isNotEmpty(teacherSubstitutes)) {
                                        for (SubstituteRecordEntity teacherSubstitute : teacherSubstitutes) {
                                            CourseScheduleEntity courseScheduleEntity = courseScheduleMap.get(teacherSubstitute.getCourseScheduleId());
                                            if (courseScheduleEntity != null && courseScheduleEntity.getCourseDate()!=null && courseScheduleEntity.getCourseDate().equals(courseSchedule.getCourseDate()) && courseScheduleEntity.getStartTime().isBefore(courseSchedule.getEndTime()) && courseScheduleEntity.getEndTime().isAfter(courseSchedule.getStartTime())) {
                                                errorList.add("[" + formatter.format(courseScheduleDate) + "-" + courseSchedule.getLessonName() + "]");
                                                break;
                                            }
                                        }
                                    }
                                }
                                CourseScheduleEntity entity = new CourseScheduleEntity();
                                CourseScheduleEntity courseScheduleEntity = scheduleEntityMap.get(reqModel.getClassId() + "_" + courseSchedule.getLessonId());
                                if (courseScheduleEntity != null) {
                                    entity.setId(courseScheduleEntity.getId());
                                }
                                entity.setSchoolId(schoolId);
                                entity.setPeriodId(courseSchedule.getPeriodId());
                                entity.setGradeId(courseSchedule.getGradeId());
                                entity.setClassId(courseSchedule.getClassId());
                                entity.setSubjectId(courseSchedule.getSubjectId());
                                entity.setSubjectName(courseSchedule.getSubjectName());
                                entity.setTeacherId(courseSchedule.getTeacherId());
                                entity.setTeacherName(courseSchedule.getTeacherName());
                                entity.setLessonId(courseSchedule.getLessonId());
                                entity.setLessonName(courseSchedule.getLessonName());
                                entity.setClassroomId(courseSchedule.getClassroomId());
                                entity.setClassroomName(courseSchedule.getClassroomName());
                                entity.setCourseDate(courseScheduleDate);
                                entity.setStartTime(courseSchedule.getStartTime());
                                entity.setEndTime(courseSchedule.getEndTime());
                                saveOrUpdateList.add(entity);
                            }
                        }
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(errorList)) {
                //教师或教室存在冲突
                throw new BusinessException(String.format(languageUtil.getMessage(LanguageConstants.COURSE_FORMAT_ERROR), String.join("、", errorList)));
            } else {
                if (CollectionUtils.isNotEmpty(saveOrUpdateList)) {
                    this.saveOrUpdateBatch(saveOrUpdateList);
                }
            }
        }
    }

    private void checkTeacherTimeConflict(CourseScheduleEntity entity, Long teacherId, Long id) {
        //查看教师课表时间是否冲突
        QueryWrapper<CourseScheduleEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(CourseScheduleEntity::getSchoolId, entity.getSchoolId())
                .eq(CourseScheduleEntity::getTeacherId, teacherId)
                .eq(CourseScheduleEntity::getCourseDate, entity.getCourseDate())
                .le(CourseScheduleEntity::getStartTime, entity.getEndTime())
                .ge(CourseScheduleEntity::getEndTime, entity.getStartTime())
                .ne(id != null, CourseScheduleEntity::getId, id);
        long courseCount = this.count(wrapper);
        if (courseCount > 0) {
            throw new BusinessException(LanguageConstants.COURSE_TIME_OVERLAP);
        }
        //查看教师代课时间是否冲突
        QueryWrapper<SubstituteRecordEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SubstituteRecordEntity::getSchoolId, entity.getSchoolId())
                .eq(SubstituteRecordEntity::getSubstituteTeacherId, teacherId)
                .eq(SubstituteRecordEntity::getSubstituteDate, entity.getCourseDate());
        List<SubstituteRecordEntity> substituteRecords = substituteRecordService.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(substituteRecords)) {
            List<Long> courseScheduleIds = substituteRecords.stream().map(SubstituteRecordEntity::getCourseScheduleId).collect(Collectors.toList());
            QueryWrapper<CourseScheduleEntity> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.lambda().in(CourseScheduleEntity::getId, courseScheduleIds)
                    .le(CourseScheduleEntity::getStartTime, entity.getEndTime())
                    .ge(CourseScheduleEntity::getEndTime, entity.getStartTime())
                    .ne(id != null, CourseScheduleEntity::getId, id);
            long substituteCount = this.count(wrapper);
            if (substituteCount > 0) {
                throw new BusinessException(LanguageConstants.COURSE_TIME_OVERLAP);
            }
        }
    }

    private void checkClassroomConflict(CourseScheduleEntity entity, Long classroomId, Long id) {
        QueryWrapper<CourseScheduleEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(CourseScheduleEntity::getSchoolId, entity.getSchoolId())
                .eq(CourseScheduleEntity::getClassroomId, classroomId)
                .eq(CourseScheduleEntity::getCourseDate, entity.getCourseDate())
                .le(CourseScheduleEntity::getStartTime, entity.getEndTime())
                .ge(CourseScheduleEntity::getEndTime, entity.getStartTime())
                .ne(id != null, CourseScheduleEntity::getId, id);
        long courseCount = this.count(wrapper);
        if (courseCount > 0) {
            throw new BusinessException(LanguageConstants.COURSE_CLASSROOM_OVERLAP);
        }
    }
}