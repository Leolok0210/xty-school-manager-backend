package com.xiaotiyun.school.manager.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaotiyun.school.manager.basic.util.DateUtils;
import com.xiaotiyun.school.manager.basic.util.SemesterUtils;
import com.xiaotiyun.school.manager.model.entity.SysClass;
import com.xiaotiyun.school.manager.service.SysClassService;
import com.xiaotiyun.school.manager.service.UserRewardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRewardJob {

    private final UserRewardService userRewardService;


    private final SysClassService sysClassService;

    /**
     * 每周周一凌晨3点开始
     */
    @Scheduled(cron = "0 0 3 ? * MON")
    public void autoUpdateUserRewards() {
        log.info("开始导出pdf");
        //获取所有的班级
        Date beforeDay = DateUtils.getBeforeDay(new Date(), 7);
        LambdaQueryWrapper<SysClass> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysClass::getUpgrade, 0);
        String currentSemesterName = SemesterUtils.getCurrentSemesterName(DateUtils.toLocalDateTime(beforeDay).toLocalDate());
        queryWrapper.eq(SysClass::getSid,currentSemesterName);
        List<SysClass> list = sysClassService.list(queryWrapper);
        if (list == null || list.isEmpty())
        {
            log.info("没有需要导出的班级");
            return;
        }
        Date weekStart = DateUtils.getWeekStart(beforeDay);
        Date weekEnd = DateUtils.getWeekEnd(beforeDay);
        for (SysClass sysClass : list) {
            log.info("开始导出班级 {} 的pdf", sysClass.getId());
            userRewardService.exportPdf(sysClass.getSchoolId(), sysClass.getId(), weekStart, weekEnd);
        }
        log.info("结束导出pdf");
    }
}
