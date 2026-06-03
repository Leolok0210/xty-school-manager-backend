package com.xiaotiyun.school.manager.model.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetStudentSubjectScoreReqModel {
    private Long schoolId;//学校ID 必传

    private String schoolYear;//学年 必传

    private Integer department;//学部(1:幼稚园 2:小学 3:中学) 必传

    private List<Long> semesterId;//学段ID

    private List<Long> subjectId;//科目ID

    private List<Long> classId;//班级ID

    private List<Long> studentId;//学生ID

    private Long groupId;
}