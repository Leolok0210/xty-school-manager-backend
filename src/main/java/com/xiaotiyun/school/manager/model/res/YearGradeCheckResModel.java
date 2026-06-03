package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel("学年成绩检查响应参数")
public class YearGradeCheckResModel {

    @ApiModelProperty("班内号")
    private String classNumber;

    @ApiModelProperty("学生照片")
    private String photoUrl;

    @ApiModelProperty("学生姓名")
    private String studentName;

    @ApiModelProperty("平均分")
    private Double averageScore;

    @ApiModelProperty("名次")
    private Integer ranking;

    @ApiModelProperty("操行")
    private String conduct;

    @ApiModelProperty("操行分数")
    private Long conductScore;

    @ApiModelProperty("操行展示规则 1-展示分数")
    private Integer showConductScore;

    @ApiModelProperty("科目成绩列表")
    private List<SubjectYearScore> subjectScores;

    @ApiModelProperty("不合格科目数")
    private Integer unqualifiedSubjectCount;

    @Data
    @ApiModel("科目学年成绩")
    public static class SubjectYearScore {

        @ApiModelProperty("科目ID")
        private Long subjectId;

        @ApiModelProperty("科目名称")
        private String subjectName;

        @ApiModelProperty("科目成绩 * 100")
        private Integer score;

        @ApiModelProperty("科目等级")
        private String grade;

        @ApiModelProperty("展示规则 0-分数，1-评级")
        private Integer displayRule;
    }
}