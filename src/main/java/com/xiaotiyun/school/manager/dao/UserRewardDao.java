package com.xiaotiyun.school.manager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaotiyun.school.manager.model.entity.UserReward;
import com.xiaotiyun.school.manager.model.req.UserRewardAllListReqModel;
import com.xiaotiyun.school.manager.model.req.UserRewardCountReqModel;
import com.xiaotiyun.school.manager.model.req.UserRewardPendingReqModel;
import com.xiaotiyun.school.manager.model.req.UserRewardQueryReqModel;
import com.xiaotiyun.school.manager.model.res.UserRewardAllListPageResModel;
import com.xiaotiyun.school.manager.model.res.UserRewardCountResModel;
import com.xiaotiyun.school.manager.model.res.UserRewardDetailResModel;
import com.xiaotiyun.school.manager.model.res.UserRewardPendingPageResModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface UserRewardDao extends BaseMapper<UserReward> {

    //连表sys_user查询奖励信息
    List<UserRewardDetailResModel> getUserReward(UserRewardQueryReqModel reqModel);

    List<UserRewardCountResModel> getUserRewardCount(UserRewardCountReqModel reqModel);

    List<UserRewardPendingPageResModel> pending(@Param("schoolId") Long schoolId, @Param("userId") Long userId, @Param("reqModel") UserRewardPendingReqModel reqModel);

    List<UserRewardPendingPageResModel> approved(@Param("schoolId") Long schoolId, @Param("userId") Long userId, @Param("reqModel") UserRewardPendingReqModel reqModel);

    List<UserRewardAllListPageResModel> allList(@Param("schoolId") Long schoolId, @Param("userId") Long userId, @Param("classIds") List<Long> classIds, @Param("reqModel") UserRewardAllListReqModel reqModel);


    List<UserReward> getUserRewardByActApproval(@Param("studentIds") List<Long> studentIds, @Param("terms") List< Long> terms,
                                                @Param("schoolId") Long schoolId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);
}