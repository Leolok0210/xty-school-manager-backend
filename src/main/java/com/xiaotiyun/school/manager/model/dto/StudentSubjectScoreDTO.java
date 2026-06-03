package com.xiaotiyun.school.manager.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentSubjectScoreDTO {

    private Long semesterId;//学段ID

    private String semesterName;//学段名称

    private Long subjectId;//科目ID

    private String subjectName;//科目名称

    private Long classId;//班级ID

    private String className;//班级名称

    private Long studentId;//学生ID

    private String studentName;//学生名称

    private Double score;//成绩*100 = 科目平均分 = 科目成绩 = 学段下 每个类型平时成绩平均分*权重 求和 + 学段下全部考试成绩求和/考试次数 * 权重

    private Double usuallyScore;//科目平时成绩*100 = 学段下 每个类型平时成绩平均分*权重 求和

    private Double examScore;//科目考试*100 = 学段下全部考试成绩求和/考试次数 * 权重

    private Integer artsScience; //1文科or理工科 2理科 3商科
}
