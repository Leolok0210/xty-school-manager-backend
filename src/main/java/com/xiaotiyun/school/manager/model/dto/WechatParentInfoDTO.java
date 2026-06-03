package com.xiaotiyun.school.manager.model.dto;
import lombok.Data;

import java.util.List;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WechatParentInfoDTO {
    /**
     * 家长UserID。学校内必须唯一，可以与企业通讯录内成员UserID相同。
     * 不区分大小写，长度为1~64个字节。只能由数字、字母和“_-@.”四种字符组成，且第一个字符必须是数字或字母。
     * 是必填字段。
     */
    private String parent_userid;

    /**
     * 更新的家长UserID，更新时可传
     * 更新的家长UserID。不能与已经存在的家长UserID相同。每个家长仅能更新一次。
     */
    private String new_parent_userid;

    /**
     * 家长手机号
     * 是必填字段。
     */
    private String mobile;

    /**
     * 是否发起邀请，默认为true，仅验证的学校才能发起邀请。创建时可传
     * 是可选字段。
     */
    private Boolean to_invite;

    /**
     * 家长的孩子列表，最多10个
     * 是必填字段。
     */
    private List<WechatParentChildrenDTO> children;
}
