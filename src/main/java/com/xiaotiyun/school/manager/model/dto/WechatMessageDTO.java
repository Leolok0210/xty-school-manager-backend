package com.xiaotiyun.school.manager.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WechatMessageDTO {

    /**
     * 指定发送对象
     * 是否必填：否
     * 0表示发送给家长，1表示发送给学生，2表示发送给家长和学生，默认为0
     */
    private Integer recv_scope;

    /**
     * 家校通讯录家长列表
     * 是否必填：否
     * recv_scope为0或2表示发送给对应的家长，recv_scope为1忽略
     * 最多支持1000个
     */
    private List<String> to_parent_userid;

    /**
     * 家校通讯录学生列表
     * 是否必填：否
     * recv_scope为0表示发送给学生的所有家长，recv_scope为1表示发送给学生，recv_scope为2表示发送给学生和学生的所有家长
     * 最多支持1000个
     */
    private List<String> to_student_userid;

    /**
     * 家校通讯录部门列表
     * 是否必填：否
     * recv_scope为0表示发送给班级的所有家长，recv_scope为1表示发送给班级的所有学生，recv_scope为2表示发送给班级的所有学生和家长
     * 最多支持100个
     */
    private List<String> to_party;

    /**
     * 全部发送标志
     * 是否必填：否
     * 1表示字段生效，0表示字段无效
     * recv_scope为0表示发送给学校的所有家长，recv_scope为1表示发送给学校的所有学生，recv_scope为2表示发送给学校的所有学生和家长
     * 默认为0
     */
    private Integer toall;

    /**
     * 企业应用的id
     * 是否必填：是
     * 可在应用的设置页面查看
     */
    private Integer agentid;

    /**
     * 消息类型
     * 是否必填：是
     * 此时固定为：mpnews
     */
    private String msgtype = "mpnews";

    /**
     * 小程序信息
     */
    private WechatMessageMiniprogramDTO miniprogram;

    /**
     * 文章信息
     */
    private WechatMessageMiniprogramNewsDTO mpnews;

    /**
     * id转译开关
     * 是否必填：否
     * 0表示否，1表示是，默认0
     */
    private Integer enable_id_trans;

    /**
     * 重复消息检查开关
     * 是否必填：否
     * 0表示否，1表示是，默认0
     */
    private Integer enable_duplicate_check;

    /**
     * 重复消息检查时间间隔
     * 是否必填：否
     * 默认1800s，最大不超过4小时
     */
    private Integer duplicate_check_interval;
}
