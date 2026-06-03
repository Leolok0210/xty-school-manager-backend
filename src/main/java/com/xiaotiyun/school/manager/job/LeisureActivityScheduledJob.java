package com.xiaotiyun.school.manager.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaotiyun.school.manager.dao.LeisureActivityRecordDao;
import com.xiaotiyun.school.manager.model.entity.LeisureActivityRecordEntity;
import com.xiaotiyun.school.manager.service.ActivityStudentReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 余暇活动定时任务
 */
@Slf4j
@Component
public class LeisureActivityScheduledJob {

    @Resource
    private LeisureActivityRecordDao leisureActivityRecordDao;

    @Resource
    private ActivityStudentReportService activityStudentReportService;

    /**
     * 余暇活动定时任务
     * 每天凌晨1点执行
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void leisureActivityTask() {
        log.info("开始执行余暇活动定时任务");
        
        try {
            // 获取前一天结束的活动
            List<LeisureActivityRecordEntity> endedActivities = getEndedActivities();
            
            if (endedActivities.isEmpty()) {
                log.info("没有找到前一天结束的活动");
                return;
            }
            
            log.info("找到 {} 个前一天结束的活动", endedActivities.size());
            
            // 遍历处理每个活动
            for (LeisureActivityRecordEntity activity : endedActivities) {
                try {
                    activityStudentReportService.processActivity(activity,false);
                } catch (Exception e) {
                    log.error("处理活动失败，活动ID: {}", activity.getId(), e);
                }
            }
            
            log.info("余暇活动定时任务执行完成");
        } catch (Exception e) {
            log.error("余暇活动定时任务执行失败", e);
        }
    }

    /**
     * 获取前一天结束的活动
     */
    private List<LeisureActivityRecordEntity> getEndedActivities() {
        // 计算前一天的开始和结束时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date yesterdayStart = calendar.getTime();
        
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date yesterdayEnd = calendar.getTime();

        List<LeisureActivityRecordEntity> needDraw = new ArrayList<>();

        LambdaQueryWrapper<LeisureActivityRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LeisureActivityRecordEntity::getDeleted, 0L)
                .between(LeisureActivityRecordEntity::getEndTime, yesterdayStart, yesterdayEnd)
                .eq(LeisureActivityRecordEntity::getDrawStatus, 0);
        List<LeisureActivityRecordEntity> leisureActivityRecordEntities = leisureActivityRecordDao.selectList(queryWrapper);
        if (!leisureActivityRecordEntities.isEmpty()) {
            needDraw.addAll(leisureActivityRecordEntities);
        }
        LambdaQueryWrapper<LeisureActivityRecordEntity> queryWrapperSecond = new LambdaQueryWrapper<>();
        queryWrapperSecond.eq(LeisureActivityRecordEntity::getDeleted, 0L)
                .eq(LeisureActivityRecordEntity::getDrawStatus, 1)
                .isNotNull(LeisureActivityRecordEntity::getSecondEndTime)
                .between(LeisureActivityRecordEntity::getSecondEndTime, yesterdayStart, yesterdayEnd);
        List<LeisureActivityRecordEntity> leisureActivityRecordEntitiesSecond = leisureActivityRecordDao.selectList(queryWrapperSecond);
        if (!leisureActivityRecordEntitiesSecond.isEmpty()) {
            needDraw.addAll(leisureActivityRecordEntitiesSecond);
        }
        return needDraw;
    }
} 