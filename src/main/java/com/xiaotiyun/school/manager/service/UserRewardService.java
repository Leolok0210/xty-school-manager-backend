package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.UserReward;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

public interface UserRewardService extends IService<UserReward> {
    void addUserRewards(Long schoolId, Long userId, UserRewardAddReqModel reqModel);

    List<UserReward> createUserRewards(List<UserReward> userRewards);

    UserReward updateUserReward(UserReward userReward);

    void deleteUserReward(Long id);

    UserReward getUserRewardById(Long id);

    PageInfo<UserRewardDetailResModel> getUserRewardList(UserRewardQueryReqModel reqModel);

    List<UserRewardCountResModel> getUserRewardCount(UserRewardCountReqModel reqModel);

    /**
     * 查询学段内是否存在用户奖励
     */
    boolean hasReward(Long periodId);

    void autoUpdateUserRewards();

    /**
     * 导入记录
     */
    Long importRecord(Long schoolId, Long userId, Long templateId, Long definitionId, String sid, Long term, Integer type, List<ActApprovalInstancePreviewApproverReqModel> approver, MultipartFile file);

    /**
     * 待审批列表
     *
     * @param schoolId
     * @param userId
     * @param reqModel
     * @return
     */
    PageInfo<UserRewardPendingPageResModel> pending(Long schoolId, Long userId, UserRewardPendingReqModel reqModel);

    /**
     * 已审批列表
     *
     * @param schoolId
     * @param userId
     * @param reqModel
     * @return
     */
    PageInfo<UserRewardPendingPageResModel> approved(Long schoolId, Long userId, UserRewardPendingReqModel reqModel);

    /**
     * 全部列表
     *
     * @param schoolId
     * @param userId
     * @param reqModel
     * @return
     */
    PageInfo<UserRewardAllListPageResModel> allList(Long schoolId, Long userId, UserRewardAllListReqModel reqModel);

    /**
     * 详情
     *
     * @param schoolId
     * @param id
     * @return
     */
    UserRewardInfoResModel info(Long schoolId, Long id);

    List<StudentPerformanceTotalResModel> getTotal(StudentPerformanceTotalReqModel reqModel);


    /**
     * 导出pdf
     * @param schoolId
     * @param classId
     * @return
     */
    Long exportPdf(Long schoolId, Long classId, Date startTime, Date endTime);
}