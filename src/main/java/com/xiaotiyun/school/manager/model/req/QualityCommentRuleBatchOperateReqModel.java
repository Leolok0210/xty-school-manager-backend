package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

@Data
@ApiModel("评语规则批量操作请求")
public class QualityCommentRuleBatchOperateReqModel {
    
    @Valid
    @ApiModelProperty(value = "新增或编辑的规则列表")
    private List<QualityCommentRuleItem> saveRules;
    
    @ApiModelProperty(value = "删除的规则ID列表")
    private List<Long> deleteIds;
    
    @Data
    @ApiModel("评语规则项")
    public static class QualityCommentRuleItem {
        @ApiModelProperty(value = "规则ID(新增时不传,编辑时必传)", example = "1")
        private Long id;
        
        @NotBlank(message = "规则名称不能为空")
        @Size(max = 50, message = "规则名称不能超过50个字")
        @ApiModelProperty(value = "规则名称", required = true, example = "优秀学生评语")
        private String ruleName;
        
        @NotNull(message = "优先级不能为空")
        @Min(value = 1, message = "优先级必须大于0")
        @ApiModelProperty(value = "优先级(数字越小优先级越高)", required = true, example = "1")
        private Integer priority;
        
        @NotEmpty(message = "条件设置不能为空")
        @Valid
        @ApiModelProperty(value = "条件设置", required = true)
        private List<ConditionGroup> conditions;
        
        @NotBlank(message = "评语模板不能为空")
        @Size(max = 500, message = "评语模板不能超过500个字")
        @ApiModelProperty(value = "评语模板(支持变量:1=中文姓名,2=英文姓名)", required = true, 
                example = "{1}同学表现优异,继续保持!")
        private String commentTemplate;

        @NotNull(message = "评语状态不能为空")
        @ApiModelProperty(value = "评语状态0开启，1关闭", required = true)
        private Integer status;
    }
    
    @Data
    @ApiModel("条件组")
    public static class ConditionGroup {
        @NotBlank(message = "条件组合类型不能为空：AND 或 OR")
        @ApiModelProperty(value = "条件组合类型(AND:满足所有条件 OR:满足任一条件)", required = true, example = "AND")
        private String combineType;
        
        @NotEmpty(message = "条件项不能为空")
        @Valid
        @ApiModelProperty(value = "条件项列表", required = true)
        private List<ConditionItem> items;
    }
    
    @Data
    @ApiModel("条件项")
    public static class ConditionItem {
        @NotBlank(message = "条件项目不能为空")
        @ApiModelProperty(value = "条件项目(CONDUCT:操行,MAJOR_MERIT:大功,MINOR_MERIT:小功,MERIT_POINT:优点," +
                "TOTAL_MERIT:总功劳,MAJOR_DEMERIT:大过,MINOR_DEMERIT:小过,DEMERIT_POINT:缺点," +
                "TOTAL_DEMERIT:总惩罚,LEAVE:请假,LATE:迟到,ABSENT:缺席)", 
                required = true, example = "CONDUCT")
        private String item;
        
        @NotBlank(message = "运算符不能为空")
        @ApiModelProperty(value = "运算符(>:大于, >=:大于等于, <:小于, <=:小于等于)", 
                required = true, example = ">=")
        private String operator;
        
        @NotNull(message = "条件值不能为空")
        @ApiModelProperty(value = "条件值", required = true, example = "90")
        private Integer value;
    }
} 