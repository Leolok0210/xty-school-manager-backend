package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class LeisureActivityCourseImportModel {

    @ExcelProperty(index = 0)
    private String courseName;// 课程名称

    @ExcelProperty(index = 1)
    private String teacherName;// 教师名称(id)

    @ExcelProperty(index = 2)
    private String classRoomName;// 教室名称

    @ExcelProperty(index = 3)
    private String quotaTotal;// 课程名额

    @ExcelProperty(index = 4)
    private String coursesNum;// 课程次数

    @ExcelProperty(index = 5)
    private String courseTimeWeeks;// 课程周几

    @ExcelProperty(index = 6)
    private String courseTimeStart;// 课程开始时间

    @ExcelProperty(index = 7)
    private String courseTimeEnd;// 课程结束时间
}
