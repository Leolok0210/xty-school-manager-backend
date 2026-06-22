package com.xiaotiyun.school.manager.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.dao.DeviceAttendanceDao;
import com.xiaotiyun.school.manager.dao.StudentMapper;
import com.xiaotiyun.school.manager.dao.StudentAttendanceRuleDao;
import com.xiaotiyun.school.manager.dao.SysClassDao;
import com.xiaotiyun.school.manager.dao.TeacherAttendanceRuleDao;
import com.xiaotiyun.school.manager.dao.UserDeptRelDao;
import com.xiaotiyun.school.manager.dao.UserSchoolRelDao;
import com.xiaotiyun.school.manager.model.entity.DeviceAttendanceEntity;
import com.xiaotiyun.school.manager.model.entity.StudentAttendanceRuleEntity;
import com.xiaotiyun.school.manager.model.entity.StudentEntity;
import com.xiaotiyun.school.manager.model.entity.SysClass;
import com.xiaotiyun.school.manager.model.entity.TeacherAttendanceRule;
import com.xiaotiyun.school.manager.model.entity.UserDeptRelEntity;
import com.xiaotiyun.school.manager.model.entity.UserSchoolRelEntity;
import com.xiaotiyun.school.manager.model.req.DeviceAttendanceReqModel;
import com.xiaotiyun.school.manager.model.res.DeviceAttendanceResModel;
import com.xiaotiyun.school.manager.service.DeviceAttendanceService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.TimeZone;

@Service
public class DeviceAttendanceServiceImpl extends ServiceImpl<DeviceAttendanceDao, DeviceAttendanceEntity>
        implements DeviceAttendanceService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private UserSchoolRelDao userSchoolRelDao;

    @Autowired
    @Lazy
    private TeacherAttendanceRuleDao teacherAttendanceRuleDao;

    @Autowired
    @Lazy
    private StudentAttendanceRuleDao studentAttendanceRuleDao;

    @Autowired
    @Lazy
    private UserDeptRelDao userDeptRelDao;

    @Autowired
    @Lazy
    private SysClassDao sysClassDao;

    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

    @Override
    public DeviceAttendanceResModel record(DeviceAttendanceReqModel reqModel) {
        DeviceAttendanceEntity entity = new DeviceAttendanceEntity();
        entity.setStudentId(reqModel.getStudentId());
        entity.setName(reqModel.getName());
        entity.setAttendanceTime(reqModel.getTime());
        entity.setStatus(reqModel.getStatus());
        entity.setClassId(reqModel.getClassId());
        entity.setDeviceSn(reqModel.getDeviceSn());
        entity.setPersonType(resolvePersonType(reqModel.getStudentId()));
        save(entity);

        DeviceAttendanceResModel res = new DeviceAttendanceResModel();
        BeanUtils.copyProperties(entity, res);
        res.setTime(entity.getAttendanceTime());
        res.setId(entity.getId());
        return res;
    }

    @Override
    public List<String> batchRecord(List<DeviceAttendanceReqModel> records) {
        List<String> ids = new ArrayList<>();
        for (DeviceAttendanceReqModel req : records) {
            DeviceAttendanceResModel res = record(req);
            ids.add(String.valueOf(res.getId()));
        }
        return ids;
    }

    @Override
    public List<DeviceAttendanceResModel> queryRecords(String date, Long classId) {
        return queryRecordsByType(date, classId, "student");
    }

    private List<DeviceAttendanceResModel> queryRecordsByType(String date, Long classId, String personType) {
        LambdaQueryWrapper<DeviceAttendanceEntity> wrapper = buildQueryWrapper(date, classId, personType);
        wrapper.orderByDesc(DeviceAttendanceEntity::getAttendanceTime);
        List<DeviceAttendanceEntity> entities = list(wrapper);
        DeriveContext ctx = new DeriveContext();
        return entities.stream().map(e -> {
            DeviceAttendanceResModel res = new DeviceAttendanceResModel();
            BeanUtils.copyProperties(e, res);
            res.setTime(e.getAttendanceTime());
            enrich(res, e, ctx);
            return res;
        }).collect(Collectors.toList());
    }

    /**
     * 依打卡上送的编号判断人员类型: 命中学生学号→student; 否则命中教师工号→teacher; 都不中→student(安全预设)
     */
    private String resolvePersonType(String number) {
        if (number == null || number.isEmpty()) {
            return "student";
        }
        boolean isStudent = studentMapper.exists(new LambdaQueryWrapper<StudentEntity>()
                .eq(StudentEntity::getStudentNo, number));
        if (isStudent) {
            return "student";
        }
        boolean isTeacher = userSchoolRelDao.exists(new LambdaQueryWrapper<UserSchoolRelEntity>()
                .eq(UserSchoolRelEntity::getUserNumber, number));
        return isTeacher ? "teacher" : "student";
    }

    @Override
    public Object stats(String type, String date, Long classId) {
        List<DeviceAttendanceResModel> records = queryRecords(date, classId);
        // Deduplicate: keep latest record per student per day
        Map<String, DeviceAttendanceResModel> unique = new LinkedHashMap<>();
        for (DeviceAttendanceResModel r : records) {
            String key = r.getStudentId() != null ? r.getStudentId() : "";
            DeviceAttendanceResModel existing = unique.get(key);
            if (existing == null || r.getTime() > existing.getTime()) {
                unique.put(key, r);
            }
        }
        Collection<DeviceAttendanceResModel> uniqueRecords = unique.values();
        long total = uniqueRecords.size();
        long present = 0, late = 0, absent = 0;
        for (DeviceAttendanceResModel r : uniqueRecords) {
            String s = r.getStatus();
            if (s == null) {
                absent++;
            } else {
                s = s.trim();
                if ("已打卡".equals(s) || "正常".equals(s) || "签到".equals(s) || "Normal".equalsIgnoreCase(s)
                        || "入校".equals(s) || "出校".equals(s)) {
                    present++;
                } else if ("迟到".equals(s) || "Late".equalsIgnoreCase(s)) {
                    late++;
                } else if ("早退".equals(s) || "Early".equalsIgnoreCase(s)) {
                    absent++;
                } else {
                    absent++;
                }
            }
        }
        double rate = total > 0 ? (double) present / total : 0.0;
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("type", type);
        result.put("date", date);
        result.put("total", total);
        result.put("present", present);
        result.put("late", late);
        result.put("absent", absent);
        result.put("rate", rate);
        return result;
    }

    @Override
    public Map<String, Object> queryRecordsPage(int pageNum, int pageSize, String date, Long classId, String personType) {
        LambdaQueryWrapper<DeviceAttendanceEntity> wrapper = buildQueryWrapper(date, classId, personType);
        wrapper.orderByDesc(DeviceAttendanceEntity::getAttendanceTime);
        Page<DeviceAttendanceEntity> mpPage = new Page<>(pageNum, pageSize);
        Page<DeviceAttendanceEntity> result = page(mpPage, wrapper);
        DeriveContext ctx = new DeriveContext();
        List<DeviceAttendanceResModel> list = result.getRecords().stream().map(e -> {
            DeviceAttendanceResModel res = new DeviceAttendanceResModel();
            BeanUtils.copyProperties(e, res);
            res.setTime(e.getAttendanceTime());
            enrich(res, e, ctx);
            return res;
        }).collect(Collectors.toList());
        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("total", result.getTotal());
        return map;
    }

    @Override
    public Map<String, Object> statsForManage(String type, String date, Long classId, String personType) {
        List<DeviceAttendanceResModel> records = queryRecordsByType(date, classId, personType);
        long total = records.size();
        long normal = 0, late = 0, early = 0;
        for (DeviceAttendanceResModel r : records) {
            String s = r.getDerivedStatus();
            if ("normal".equals(s)) {
                normal++;
            } else if ("late".equals(s)) {
                late++;
            } else if ("early".equals(s)) {
                early++;
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("type", type);
        result.put("date", date);
        result.put("total", total);
        result.put("normal", normal);
        result.put("late", late);
        result.put("early", early);
        result.put("missing", 0L);
        if (classId != null) {
            result.put("classId", classId);
        }
        return result;
    }

    private LambdaQueryWrapper<DeviceAttendanceEntity> buildQueryWrapper(String date, Long classId, String personType) {
        LambdaQueryWrapper<DeviceAttendanceEntity> wrapper = new LambdaQueryWrapper<>();
        if (date != null && !date.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
                Date parsed = sdf.parse(date);
                long startOfDay = parsed.getTime();
                Calendar cal = Calendar.getInstance();
                cal.setTime(parsed);
                cal.add(Calendar.DAY_OF_MONTH, 1);
                long endOfDay = cal.getTimeInMillis();
                wrapper.ge(DeviceAttendanceEntity::getAttendanceTime, startOfDay);
                wrapper.lt(DeviceAttendanceEntity::getAttendanceTime, endOfDay);
            } catch (Exception ignored) {
            }
        }
        if (classId != null) {
            wrapper.eq(DeviceAttendanceEntity::getClassId, classId);
        }
        if ("teacher".equals(personType)) {
            wrapper.eq(DeviceAttendanceEntity::getPersonType, "teacher");
        } else {
            // 学生范围: 含历史 null 数据
            wrapper.and(w -> w.ne(DeviceAttendanceEntity::getPersonType, "teacher")
                    .or().isNull(DeviceAttendanceEntity::getPersonType));
        }
        return wrapper;
    }

    /**
     * 逐笔推导: 设置 direction(原方向字串) 与 derivedStatus(normal/late/early; 无规则或无法判方向时为 null)。
     */
    private void enrich(DeviceAttendanceResModel res, DeviceAttendanceEntity e, DeriveContext ctx) {
        res.setDirection(e.getStatus());
        String dir = parseDirection(e.getStatus());
        if (dir == null || e.getAttendanceTime() == null) {
            res.setDerivedStatus(null);
            return;
        }
        java.time.ZonedDateTime zdt = Instant.ofEpochMilli(e.getAttendanceTime()).atZone(ZONE);
        LocalTime punch = zdt.toLocalTime();
        int dayOfWeek = zdt.getDayOfWeek().getValue();
        LocalTime[] times;
        if ("teacher".equals(e.getPersonType())) {
            times = teacherRuleTimes(e.getStudentId(), dayOfWeek, ctx);
        } else {
            times = studentRuleTimes(e.getClassId(), ctx);
        }
        if (times == null) {
            res.setDerivedStatus(null);
            return;
        }
        res.setDerivedStatus(classify(dir, punch, times[0], times[1]));
    }

    private String parseDirection(String status) {
        if (status == null) {
            return null;
        }
        String s = status.trim();
        if (s.isEmpty()) {
            return null;
        }
        if (s.contains("入") || s.contains("签到")) {
            return "in";
        }
        if (s.contains("出")) {
            return "out";
        }
        String low = s.toLowerCase();
        if (low.contains("in")) {
            return "in";
        }
        if (low.contains("out")) {
            return "out";
        }
        return null;
    }

    private String classify(String dir, LocalTime punch, LocalTime inTime, LocalTime outTime) {
        if ("in".equals(dir)) {
            if (inTime == null) {
                return null;
            }
            return punch.isAfter(inTime) ? "late" : "normal";
        }
        if ("out".equals(dir)) {
            if (outTime == null) {
                return null;
            }
            return punch.isBefore(outTime) ? "early" : "normal";
        }
        return null;
    }

    /**
     * 教师: 工号→sys_user_school_rel→(schoolId, teacherId=userId, relPk=id)→sys_user_dept_rel(isMaster=1)→deptId
     * →依规则解析(星期/特殊优先/部门或个人命中)→clockInTime/clockOutTime。
     */
    private LocalTime[] teacherRuleTimes(String userNumber, int dayOfWeek, DeriveContext ctx) {
        if (userNumber == null || userNumber.isEmpty()) {
            return null;
        }
        UserSchoolRelEntity rel = ctx.relByNumber.get(userNumber);
        if (rel == null && !ctx.relByNumber.containsKey(userNumber)) {
            rel = userSchoolRelDao.selectOne(new LambdaQueryWrapper<UserSchoolRelEntity>()
                    .eq(UserSchoolRelEntity::getUserNumber, userNumber)
                    .last("limit 1"));
            ctx.relByNumber.put(userNumber, rel);
        }
        if (rel == null || rel.getSchoolId() == null) {
            return null;
        }
        Long schoolId = rel.getSchoolId();
        Long teacherId = rel.getUserId();
        Long relPk = rel.getId();
        Long deptId = ctx.deptByRelPk.get(relPk);
        if (deptId == null && !ctx.deptByRelPk.containsKey(relPk)) {
            UserDeptRelEntity deptRel = userDeptRelDao.selectOne(new LambdaQueryWrapper<UserDeptRelEntity>()
                    .eq(UserDeptRelEntity::getSchoolId, schoolId)
                    .eq(UserDeptRelEntity::getUserId, relPk)
                    .eq(UserDeptRelEntity::getIsMaster, 1)
                    .last("limit 1"));
            deptId = deptRel == null ? null : deptRel.getDeptId();
            ctx.deptByRelPk.put(relPk, deptId);
        }
        List<TeacherAttendanceRule> rules = ctx.teacherRulesBySchool.get(schoolId);
        if (rules == null) {
            rules = teacherAttendanceRuleDao.selectList(new LambdaQueryWrapper<TeacherAttendanceRule>()
                    .eq(TeacherAttendanceRule::getSchoolId, schoolId));
            ctx.teacherRulesBySchool.put(schoolId, rules);
        }
        TeacherAttendanceRule rule = getTeacherAttendanceRule(deptId, teacherId, dayOfWeek, rules);
        if (rule == null) {
            return null;
        }
        return new LocalTime[]{rule.getClockInTime(), rule.getClockOutTime()};
    }

    /**
     * 学生: classId→sys_class(gradeGroup, schoolId)→student_attendance_rule(grade JSON 含 gradeGroup)
     * →morningInTime/afternoonOutTime。
     */
    private LocalTime[] studentRuleTimes(Long classId, DeriveContext ctx) {
        if (classId == null) {
            return null;
        }
        SysClass clazz = ctx.classById.get(classId);
        if (clazz == null && !ctx.classById.containsKey(classId)) {
            clazz = sysClassDao.selectById(classId);
            ctx.classById.put(classId, clazz);
        }
        if (clazz == null || clazz.getSchoolId() == null || clazz.getGradeGroup() == null) {
            return null;
        }
        Long schoolId = clazz.getSchoolId();
        Long gradeGroup = clazz.getGradeGroup();
        List<StudentAttendanceRuleEntity> rules = ctx.studentRulesBySchool.get(schoolId);
        if (rules == null) {
            rules = studentAttendanceRuleDao.selectList(new LambdaQueryWrapper<StudentAttendanceRuleEntity>()
                    .eq(StudentAttendanceRuleEntity::getSchoolId, schoolId));
            ctx.studentRulesBySchool.put(schoolId, rules);
        }
        for (StudentAttendanceRuleEntity rule : rules) {
            String grade = rule.getGrade();
            if (isNotBlank(grade)) {
                List<Long> ids = JSON.parseArray(grade, Long.class);
                if (ids != null && ids.contains(gradeGroup)) {
                    return new LocalTime[]{rule.getMorningInTime(), rule.getAfternoonOutTime()};
                }
            }
        }
        return null;
    }

    /**
     * 重用既有教师规则解析演算法: 当天星期过滤 → 特殊(type=1)优先、同型新者优先 → 命中 deptId∈depIds 或 teacherId∈userIds。
     */
    private TeacherAttendanceRule getTeacherAttendanceRule(Long deptId, Long teacherId, Integer dayOfWeek,
                                                           List<TeacherAttendanceRule> attendanceRules) {
        if (attendanceRules == null || attendanceRules.isEmpty()) {
            return null;
        }
        List<TeacherAttendanceRule> effectiveRules = attendanceRules.stream()
                .filter(rule -> isNotBlank(rule.getEffectiveScope())
                        && JSON.parseArray(rule.getEffectiveScope(), Integer.class).contains(dayOfWeek))
                .collect(Collectors.toList());
        if (effectiveRules.isEmpty()) {
            return null;
        }
        effectiveRules.sort((r1, r2) -> {
            if (!r1.getType().equals(r2.getType())) {
                return Integer.compare(r2.getType(), r1.getType());
            }
            return r2.getCreateTime().compareTo(r1.getCreateTime());
        });
        for (TeacherAttendanceRule rule : effectiveRules) {
            if (deptId != null && isNotBlank(rule.getDepIds())
                    && JSON.parseArray(rule.getDepIds(), Long.class).contains(deptId)) {
                return rule;
            }
            if (isNotBlank(rule.getUserIds())
                    && JSON.parseArray(rule.getUserIds(), Long.class).contains(teacherId)) {
                return rule;
            }
        }
        return null;
    }

    private boolean isNotBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    /** 单次查询内的解析快取, 避免逐笔重复查库。 */
    private static class DeriveContext {
        final Map<String, UserSchoolRelEntity> relByNumber = new HashMap<>();
        final Map<Long, Long> deptByRelPk = new HashMap<>();
        final Map<Long, List<TeacherAttendanceRule>> teacherRulesBySchool = new HashMap<>();
        final Map<Long, List<StudentAttendanceRuleEntity>> studentRulesBySchool = new HashMap<>();
        final Map<Long, SysClass> classById = new HashMap<>();
    }
}
