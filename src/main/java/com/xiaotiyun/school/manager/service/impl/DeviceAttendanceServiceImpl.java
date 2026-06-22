package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.dao.DeviceAttendanceDao;
import com.xiaotiyun.school.manager.dao.StudentMapper;
import com.xiaotiyun.school.manager.dao.UserSchoolRelDao;
import com.xiaotiyun.school.manager.model.entity.DeviceAttendanceEntity;
import com.xiaotiyun.school.manager.model.entity.StudentEntity;
import com.xiaotiyun.school.manager.model.entity.UserSchoolRelEntity;
import com.xiaotiyun.school.manager.model.req.DeviceAttendanceReqModel;
import com.xiaotiyun.school.manager.model.res.DeviceAttendanceResModel;
import com.xiaotiyun.school.manager.service.DeviceAttendanceService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
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
        return entities.stream().map(e -> {
            DeviceAttendanceResModel res = new DeviceAttendanceResModel();
            BeanUtils.copyProperties(e, res);
            res.setTime(e.getAttendanceTime());
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
        List<DeviceAttendanceResModel> list = result.getRecords().stream().map(e -> {
            DeviceAttendanceResModel res = new DeviceAttendanceResModel();
            BeanUtils.copyProperties(e, res);
            res.setTime(e.getAttendanceTime());
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
        long normal = 0, late = 0, early = 0, missing = 0;
        for (DeviceAttendanceResModel r : records) {
            String s = r.getStatus();
            if (s == null) {
                missing++;
            } else {
                s = s.trim();
                if ("正常".equals(s) || "签到".equals(s) || "Normal".equalsIgnoreCase(s)
                        || "已打卡".equals(s) || "入校".equals(s) || "出校".equals(s)) {
                    normal++;
                } else if ("迟到".equals(s) || "Late".equalsIgnoreCase(s)) {
                    late++;
                } else if ("早退".equals(s) || "Early".equalsIgnoreCase(s)) {
                    early++;
                } else if ("缺卡".equals(s) || "Missing".equalsIgnoreCase(s)) {
                    missing++;
                } else {
                    missing++;
                }
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("type", type);
        result.put("date", date);
        result.put("total", total);
        result.put("normal", normal);
        result.put("late", late);
        result.put("early", early);
        result.put("missing", missing);
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
}
