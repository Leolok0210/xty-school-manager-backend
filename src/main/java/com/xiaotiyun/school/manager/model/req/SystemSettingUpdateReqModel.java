package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
@ApiModel("系统设置更新请求")
public class SystemSettingUpdateReqModel {

    /**
     * 学校ID
     */
    @NotNull(message = "学校ID不能为空")
    @ApiModelProperty(value = "学校ID", required = true)
    private Long schoolId;

    /**
     * 配置项Map, key为配置项键名,value为配置值
     */
    @NotNull(message = "配置项不能为空")
    @ApiModelProperty(value = "配置项Map key为配置项键名,value为配置值\n" +
            "可用的key包括:\n" +
            "1. logo - 网站logo图片地址,限制:图片大小2MB内,格式支持.jpg .png\n" +
            "2. dateFormat - 日期格式,可选值:\n" +
            "   - YYYY-MM-DD (2025-01-11,ISO标准格式)\n" +
            "   - DD/MM/YYYY (11/01/2025,常见于欧洲和亚洲)\n" +
            "   - MM/DD/YYYY (01/11/2025,美式格式)\n" +
            "   - DD-MMM-YYYY (11-Jan-2025,缩写月份)\n" +
            "   - MMMM DD, YYYY (January 11, 2025,完整月份名称)\n" +
            "3. timeFormat - 时间格式,可选值:\n" +
            "   - HH:mm:ss (24小时制,15:30:45)\n" +
            "   - hh:mm:ss a (12小时制,03:30:45 PM)\n" +
            "   - HH:mm (24小时制,无秒数,15:30)\n" +
            "   - hh:mm a (12小时制,无秒数,03:30 PM)\n" +
            "4. language - 默认语言,可选值:\n" +
            "   - zh_MO (繁体中文)\n" +
            "   - en_US (英文)\n" +
            "   - pt_PT (葡萄牙语)\n" +
            "5. departments - 学部设置,格式为JSON字符串,例如:\n" +
            "   [{\"code\":\"K\",\"name\":\"幼稚园\",\"years\":3},\n" +
            "    {\"code\":\"P\",\"name\":\"小学\",\"years\":6},\n" +
            "    {\"code\":\"S\",\"name\":\"中学\",\"years\":6}]\n" +
            "6. evaluationComment - 默认素质评语,格式为JSON字符串,例如:\n" +
            "   {\"type\":\"CN\",\"comment\":\"你本学期表现平稳，请继续努力！\"}\n" +
            "7. schoolDepartments - 校部设置, 包含学部、校部名称和校部编号, 格式为JSON字符串, 例如:\n" +
            "   [{\"schoolDepartmentName\":\"幼稚园\",\"schoolDepartmentCode\":\"K1\",\"department\":\"幼稚园\"},\n" +
            "    {\"schoolDepartmentName\":\"小学\",\"schoolDepartmentCode\":\"P1\",\"department\":\"小学\"},\n" +
            "    {\"schoolDepartmentName\":\"中学\",\"schoolDepartmentCode\":\"S1\",\"department\":\"中学\"}]\n" +
            "8. penaltyRules - 惩罚规则设置, 包含学部、校部名称和校部编号, 格式为JSON字符串, 例如:\n" +
            "   [{\"type\":1(表示上课违规-次数),\"frequency\":2(表示次数),\"quantity\":1(表示个数),\"penaltyType\":3(1.大过;2.小过;3.缺点),\"specialConfigs（特殊配置）\":[{\"times\":1(第几次),\"frequency\":8,\"quantity\":2}]},\n" +
            "    {\"type\":2(表示欠作业-次数),\"frequency\":2(表示次数),\"quantity\":1(表示个数),\"penaltyType\":2(1.大过;2.小过;3.缺点)},\n" +
            "    {\"type\":3(表示仪表不符-次数),\"frequency\":2(表示次数),\"quantity\":1(表示个数),\"penaltyType\":3(1.大过;2.小过;3.缺点)},\n" +
            "    {\"type\":4(表示迟到（入校+课堂）-次数),\"frequency\":10(表示次数),\"quantity\":1(表示个数),\"penaltyType\":1(1.大过;2.小过;3.缺点)}," +
            "    {\"type\":5(表示欠课本-次数),\"frequency\":10(表示次数),\"quantity\":1(表示个数),\"penaltyType\":1(1.大过;2.小过;3.缺点)}," +
            "    {\"type\":6(表示缺席-节数),\"frequency\":10(表示节数),\"quantity\":1(表示个数),\"penaltyType\":1(1.大过;2.小过;3.缺点)}," +
            "    {\"type\":7(表示欠回条-次数),\"frequency\":10(表示次数),\"quantity\":1(表示个数),\"penaltyType\":1(1.大过;2.小过;3.缺点)}]\n" +
            "9. leisureActivitiesRating - 余暇活动评级规则设置, 包含出勤率占比、课堂表现占比、分数区间和参考评分等级, 格式为JSON字符串, 例如:\n" +
            "   [{\"department\":1,\"attendanceRatio\":70(出勤率占比70%),\"classParticipationRatio\":30(课堂表现占比30%)},\"scoreRange\":[{\"minValue\":0(最小值*100),\"maxValue\":10000(最大值*100),\"level\":\"A+(参考评分等级)\"}]},\n" +
            "    {\"department\":2,\"attendanceRatio\":70(出勤率占比70%),\"classParticipationRatio\":30(课堂表现占比30%)},\"scoreRange\":[{\"minValue\":0(最小值*100),\"maxValue\":10000(最大值*100),\"level\":\"A+(参考评分等级)\"}]}] \n" +
            "10. unconventionalPerformance - 非常规表现设定, 格式为JSON字符串, 例如:\n" +
            "   {\"category\":1(1.奖励;2.惩罚),\"describe\":\"拾金不昧\"(表現描述),\"type\":1(1.大过;2.小过;3.缺点;4.大功;5.小功;6.优点),\"frequency\":2(表示次数)} \n" +
            "11. usualTypeRelSub - 平时成绩类型是否关联科目, 格式为数字:0-不关联、1-关联、不存在-不关联, 例如: 0 ", required = true)
    private Map<String, String> settings;
}