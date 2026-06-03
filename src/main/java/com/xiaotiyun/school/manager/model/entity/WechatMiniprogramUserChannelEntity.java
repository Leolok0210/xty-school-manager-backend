package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 小程序用户渠道关联表实体类
 * @author generated
 * @since 2023-09-01
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("wechat_miniprogram_user_channel")
public class WechatMiniprogramUserChannelEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 渠道名称
     */
    @TableField("channel_id")
    private Long channelId;

    /**
     * 用户openId
     */
    @TableField("open_id")
    private String openId;

    /**
     * 学生名称
     */
    @TableField("student_name")
    private String studentName;

    /**
     * 学生ID
     */
    @TableField("student_id")
    private Long studentId;

}
