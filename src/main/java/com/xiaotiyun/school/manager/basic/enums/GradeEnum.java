package com.xiaotiyun.school.manager.basic.enums;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

/**
 * 年级枚举
 */
@Getter
public enum GradeEnum {

    @ApiModelProperty("幼一")
    KINDERGARTEN_1(1, "幼一",1, 1),
    @ApiModelProperty("幼二")
    KINDERGARTEN_2(2, "幼二",2, 1),
    @ApiModelProperty("幼三")
    KINDERGARTEN_3(3, "幼三",3, 1),
    @ApiModelProperty("一年级")
    GRADE_1(4, "一年級",1, 2),
    @ApiModelProperty("二年级")
    GRADE_2(5, "二年級",2, 2),
    @ApiModelProperty("三年级")
    GRADE_3(6, "三年級",3, 2),
    @ApiModelProperty("四年级")
    GRADE_4(7, "四年級",4, 2),
    @ApiModelProperty("五年级")
    GRADE_5(8, "五年級",5, 2),
    @ApiModelProperty("六年级")
    GRADE_6(9, "六年級",6, 2),
    @ApiModelProperty("中一")
    MIDDLE_1(10, "中一",1, 3),
    @ApiModelProperty("中二")
    MIDDLE_2(11, "中二",2, 3),
    @ApiModelProperty("中三")
    MIDDLE_3(12, "中三",3, 3),
    @ApiModelProperty("中四")
    MIDDLE_4(13, "中四",4, 3),
    @ApiModelProperty("中五")
    MIDDLE_5(14, "中五",5, 3),
    @ApiModelProperty("中六")
    MIDDLE_6(15, "中六",6, 3);

    private final Integer code;
    private final String desc;

    private final Integer grade;
    private final Integer department;

    GradeEnum(Integer code, String desc,Integer grade,Integer department) {
        this.code = code;
        this.desc = desc;
        this.grade = grade;
        this.department = department;
    }

    public static GradeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (GradeEnum grade : values()) {
            if (grade.getCode().equals(code)) {
                return grade;
            }
        }
        return null;
    }

    //getBydesc
    public static GradeEnum getByDesc(String desc) {
        if (desc == null) {
            return null;
        }
        for (GradeEnum grade : values()) {
            if (grade.getDesc().equals(desc)) {
                return grade;
            }
        }
        return null;
    }

    //升级级组
    public static GradeEnum upgrade(Integer grade,Integer department) {
        if(grade == null || department == null)
        {
            return null;
        }
        if(department.equals(DepartmentEnum.KINDERGARTEN.getCode()))
        {
            if(grade + 1 > 4)
            {
                return null;
            }
            return GradeEnum.getByCode(grade + 1);
        }
        if(department.equals(DepartmentEnum.PRIMARY.getCode()))
        {
            if(grade + 1 > 10)
            {
                return null;
            }
            return GradeEnum.getByCode(grade + 1);
        }
        if(department.equals(DepartmentEnum.MIDDLE.getCode()))
        {
            if(grade + 1 > 15)
            {
                return null;
            }
            return GradeEnum.getByCode(grade + 1);
        }
        return null;
    }
}