package com.xiaotiyun.school.manager.basic.util;

import com.xiaotiyun.school.manager.model.entity.TeacherAttendanceEntity;
import com.xiaotiyun.school.manager.model.entity.TeacherAttendanceRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 日期工具类
 */
@Slf4j
public class DateUtils {

    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_PATTERN_TIME = "HH:mm:ss";
    public static final String DEFAULT_PATTERN_SHORT_TIME = "HH:mm";
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_PATTERN);

    /**
     * 格式化日期时间为字符串
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DEFAULT_FORMATTER) : null;
    }

    /**
     * 将字符串解析为日期时间
     */
    public static LocalDateTime parse(String dateTimeStr) {
        return dateTimeStr != null ? LocalDateTime.parse(dateTimeStr, DEFAULT_FORMATTER) : null;
    }

    public static Date formatStringToDate(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(date);
        } catch (ParseException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.toString());
        }
    }

    public static LocalTime formatStringToLocalTime(String time, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalTime.parse(time, formatter);
    }

    public static String formatDateToString(LocalDateTime date, String format) {
        return date != null ? date.format(DateTimeFormatter.ofPattern(format)) : null;
    }

    public static List<LocalDate> generateDates(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) { // 包含 endDate
            dates.add(current);
            current = current.plusDays(1);
        }
        return dates;
    }

    /**
     * 验证时间是否合法
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static boolean isTimeValid(LocalTime startTime, LocalTime endTime) {
        return endTime.isAfter(startTime);
    }

    /**
     * 验证时间是否合法
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static boolean isTimeValid(LocalDateTime startTime, LocalDateTime endTime) {
        return endTime.isAfter(startTime);
    }

    /**
     * 检查早退考勤记录中其他情况的开始结束时间，是否满足于应考勤时间
     * @param startTime
     * @param endTime
     * @param today
     * @param teacherAttendanceRule
     * @param attendanceEntity
     * @return 返回ture说明其他情况都不满足本次考勤异常抵消情况
     */
    public static boolean getIsEarly(LocalDateTime startTime, LocalDateTime endTime, LocalDate today, TeacherAttendanceRule teacherAttendanceRule, TeacherAttendanceEntity attendanceEntity) {
        // 若开始时间是今天，结束时间不是今天，看开始时间是否早于或等于应离校时间和实际离校时间
        if (startTime.toLocalDate().equals(today) && !endTime.toLocalDate().equals(today) &&
                (startTime.toLocalTime().isBefore(teacherAttendanceRule.getClockOutTime()) || startTime.toLocalTime().equals(teacherAttendanceRule.getClockOutTime())) &&
                (startTime.toLocalTime().isBefore(attendanceEntity.getClockOutTime()) || startTime.toLocalTime().equals(attendanceEntity.getClockOutTime())))
            return false;
        // 若开始和结束时间是今天，看开始时间是否早于或等于应离校时间，结束时间晚于或等于应离校时间和实际离校时间
        if (startTime.toLocalDate().equals(today) && endTime.toLocalDate().equals(today) &&
                (startTime.toLocalTime().isBefore(teacherAttendanceRule.getClockOutTime()) || startTime.toLocalTime().equals(teacherAttendanceRule.getClockOutTime())) &&
                (endTime.toLocalTime().isAfter(teacherAttendanceRule.getClockOutTime()) || endTime.toLocalTime().equals(teacherAttendanceRule.getClockOutTime())) &&
                (endTime.toLocalTime().isAfter(attendanceEntity.getClockOutTime()) || endTime.toLocalTime().equals(attendanceEntity.getClockOutTime())))
            return false;
        // 若开始时间不是今天，结束时间是今天，看开始时间是否早于或等于应离校时间和实际离校时间
        if (!startTime.toLocalDate().equals(today) && endTime.toLocalDate().equals(today) &&
                (startTime.toLocalTime().isBefore(teacherAttendanceRule.getClockOutTime()) || startTime.toLocalTime().equals(teacherAttendanceRule.getClockOutTime())) &&
                (endTime.toLocalTime().isAfter(attendanceEntity.getClockOutTime()) || endTime.toLocalTime().equals(attendanceEntity.getClockOutTime())))
            return false;
        return true;
    }

    /**
     * 检查迟到考勤记录中其他情况的开始结束时间，是否满足于应考勤时间
     * @param startTime
     * @param endTime
     * @param today
     * @param teacherAttendanceRule
     * @param attendanceEntity
     * @return 返回ture说明其他情况都不满足本次考勤异常抵消情况
     */
    public static boolean getIsLate(LocalDateTime startTime, LocalDateTime endTime, LocalDate today, TeacherAttendanceRule teacherAttendanceRule, TeacherAttendanceEntity attendanceEntity) {
        // 若开始时间是今天，结束时间不是今天，看开始时间是否早于等于应入校时间
        if (startTime.toLocalDate().equals(today) && !endTime.toLocalDate().equals(today) &&
                (startTime.toLocalTime().isBefore(teacherAttendanceRule.getClockInTime()) || startTime.toLocalTime().equals(teacherAttendanceRule.getClockInTime())))
            return false;
        // 若开始和结束时间是今天，看开始时间是否早于等于应入校时间，结束时间晚于等于应入校时间和实际入校时间
        if (startTime.toLocalDate().equals(today) && endTime.toLocalDate().equals(today) &&
                (startTime.toLocalTime().isBefore(teacherAttendanceRule.getClockInTime()) || startTime.toLocalTime().equals(teacherAttendanceRule.getClockInTime())) &&
                (endTime.toLocalTime().isAfter(teacherAttendanceRule.getClockInTime()) || endTime.toLocalTime().equals(teacherAttendanceRule.getClockInTime())) &&
                (endTime.toLocalTime().isAfter(attendanceEntity.getClockInTime()) || endTime.toLocalTime().equals(attendanceEntity.getClockInTime())))
            return false;
        // 若开始时间不是今天，结束时间是今天，结束时间晚于等于应入校时间和实际入校时间
        if (!startTime.toLocalDate().equals(today) && endTime.toLocalDate().equals(today) &&
                (endTime.toLocalTime().isAfter(teacherAttendanceRule.getClockInTime()) || endTime.toLocalTime().equals(teacherAttendanceRule.getClockInTime())) &&
                (endTime.toLocalTime().isAfter(attendanceEntity.getClockInTime()) || endTime.toLocalTime().equals(attendanceEntity.getClockInTime())))
            return false;
        return true;
    }

    public static LocalDate convertImportDate(String date) {
        LocalDate result = null;
        if (StringUtils.isNotBlank(date)) {
            if (StringUtils.isNumeric(date)) {
                //execl日期格式解析为全数字，如：43444
                Date javaDate = DateUtil.getJavaDate(Double.parseDouble(date));
                result = javaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            } else {
                // 字符串格式日期，如 2024/12/18 或 2024-12-18
                try {
                    if (date.contains("/")) {
                        result = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy/M/d"));
                    } else {
                        result = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-M-d"));
                    }
                } catch (DateTimeParseException e) {
                    log.error("日期格式错误: {}", date, e);
                }
            }
        }
        return result;
    }

    public static LocalDateTime toLocalDateTime(Date startTime) {
        return startTime == null ? null : startTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }


    /**
     * 获取传入时间的周开始时间
     */
    public static Date getWeekStart(Date date) {
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime startOfWeek = localDateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .with(LocalTime.MIN);
        return Date.from(startOfWeek.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获取传入时间的周日 23：59：59时间
     */
    public static Date getWeekEnd(Date date) {
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime endOfWeek = localDateTime.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                .with(LocalTime.MAX);
        return Date.from(endOfWeek.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 往前推几天
     */
    public static Date getBeforeDay(Date date, int day) {
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return Date.from(localDateTime.minusDays(day).atZone(ZoneId.systemDefault()).toInstant());
    }

}