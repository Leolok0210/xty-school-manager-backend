package com.xiaotiyun.school.manager.model.dto;


import lombok.Data;


import java.util.List;

@Data
public class ConditionGroupDTO {
//    @NotBlank(message = "条件组合类型不能为空：AND 或 OR")
//    @ApiModelProperty(value = "条件组合类型(AND:满足所有条件 OR:满足任一条件)", required = true, example = "AND")
    private String combineType;

    private List<ConditionItem> items;


    @Data
    public static class ConditionItem {
//        @NotBlank(message = "条件项目不能为空")
//        @ApiModelProperty(value = "条件项目(CONDUCT:操行,MAJOR_MERIT:大功,MINOR_MERIT:小功,MERIT_POINT:优点," +
//                "TOTAL_MERIT:总功劳,MAJOR_DEMERIT:大过,MINOR_DEMERIT:小过,DEMERIT_POINT:缺点," +
//                "TOTAL_DEMERIT:总惩罚,LEAVE:请假,LATE:迟到,ABSENT:缺席)",
//                required = true, example = "CONDUCT")
        private String item;

//        @NotBlank(message = "运算符不能为空")
//        @ApiModelProperty(value = "运算符(>:大于, >=:大于等于, <:小于, <=:小于等于)",
//                required = true, example = ">=")
        private String operator;

//        @NotNull(message = "条件值不能为空")
//        @ApiModelProperty(value = "条件值", required = true, example = "90")
        private Integer value;
    }
}
