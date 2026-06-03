package com.xiaotiyun.school.manager.job;

import com.xiaotiyun.school.manager.service.FileService;
import com.xiaotiyun.school.manager.service.NoticeService;
import com.xiaotiyun.school.manager.service.UserRewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledTasksJob {
    private final FileService fileService;
    private final UserRewardService userRewardService;
    private final NoticeService noticeService;

    /**
     * 无用文件删除定时任务(删除学校目录下上传的zip包、无用学生照片及下载文件夹下的文件)
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void deleteUselessFile() {
        fileService.deleteUselessFile();
    }

    /*
     * 每日自己计算惩罚(每天1点执行)
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void calculatePunishment() {
        // 调用惩罚计算方法
        userRewardService.autoUpdateUserRewards();
    }

    /**
     * 发送企业微信通知
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void sendEnterpriseWechatNotice() {
        noticeService.sendEnterpriseWechatNotice();
    }
}
