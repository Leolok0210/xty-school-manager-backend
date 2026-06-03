package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.basic.enums.LeisureActivitiesNoticeConsultEnum;
import com.xiaotiyun.school.manager.basic.enums.LeisureActivitiesNoticeMatchResultEnum;
import com.xiaotiyun.school.manager.dao.LeisureActivitiesNoticeDao;
import com.xiaotiyun.school.manager.model.dto.LeisureActivitiesNoticeSendDTO;
import com.xiaotiyun.school.manager.model.entity.LeisureActivitiesNoticeEntity;
import com.xiaotiyun.school.manager.model.entity.LeisureActivityCoursesRecordEntity;
import com.xiaotiyun.school.manager.model.res.StudentLeisureActivitiesResultResModel;
import com.xiaotiyun.school.manager.service.LeisureActivitiesNoticeService;
import com.xiaotiyun.school.manager.service.LeisureActivityCoursesRecordService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 余暇活动匹配结果通知Service层实现类
 */
@Service
@RequiredArgsConstructor
public class LeisureActivitiesNoticeServiceImpl extends ServiceImpl<LeisureActivitiesNoticeDao, LeisureActivitiesNoticeEntity> implements LeisureActivitiesNoticeService {
    private final LeisureActivityCoursesRecordService leisureActivityCoursesRecordService;

    @Override
    public StudentLeisureActivitiesResultResModel notice(Long studentId, Long periodId) {
        LeisureActivitiesNoticeEntity notice = this.getBaseMapper().notice(studentId, periodId);
        if (notice != null && notice.getDeleted() == 0L && notice.getConsult() == LeisureActivitiesNoticeConsultEnum.NOT_VIEWED.getCode()) {
            //未查阅时显示
            StudentLeisureActivitiesResultResModel result = new StudentLeisureActivitiesResultResModel();
            result.setActivityId(notice.getActivityId());
            result.setCourseId(notice.getCourseId());
            if (notice.getCourseId() != null && notice.getCourseId() > 0L) {
                LeisureActivityCoursesRecordEntity courses = leisureActivityCoursesRecordService.getById(notice.getCourseId());
                if (courses != null) {
                    result.setCourseName(courses.getName());
                }
            }
            result.setMatchResult(notice.getMatchResult());
            //更新为已查阅
            notice.setConsult(LeisureActivitiesNoticeConsultEnum.VIEWED.getCode());
            this.updateById(notice);
            return result;
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendNotice(LeisureActivitiesNoticeSendDTO sendDTO) {
        if (CollectionUtils.isNotEmpty(sendDTO.getPublishResults())) {
            List<LeisureActivitiesNoticeEntity> insertList = new ArrayList<>();
            sendDTO.getPublishResults().forEach(publishResult -> {
                LeisureActivitiesNoticeEntity notice = new LeisureActivitiesNoticeEntity();
                notice.setSchoolId(sendDTO.getSchoolId());
                notice.setStudentId(publishResult.getStudentId());
                notice.setActivityId(sendDTO.getActivityId());
                notice.setPeriodId(sendDTO.getPeriodId());
                if (publishResult.getCourseId() != null) {
                    //已匹配上课程
                    notice.setCourseId(publishResult.getCourseId());
                    notice.setMatchResult(LeisureActivitiesNoticeMatchResultEnum.MATCH_SUCCESS.getCode());
                } else {
                    //未匹配上课程
                    notice.setMatchResult(LeisureActivitiesNoticeMatchResultEnum.MATCH_FAIL.getCode());
                }
                insertList.add(notice);
            });
            if (CollectionUtils.isNotEmpty(insertList)) {
                this.saveBatch(insertList);
            }
        }
    }
}