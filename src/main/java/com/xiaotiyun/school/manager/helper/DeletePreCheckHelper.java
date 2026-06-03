package com.xiaotiyun.school.manager.helper;

import com.xiaotiyun.school.manager.basic.enums.DefaultParamEnum;
import com.xiaotiyun.school.manager.service.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class DeletePreCheckHelper {

    @Resource
    private StudentExamTaskService studentExamTaskService;
    @Resource
    private ClassPerformanceService classPerformanceService;
    @Resource
    private StudentGraduateExamTaskService studentGraduateExamTaskService;
    @Resource
    private StudentQualityScoreService studentQualityScoreService;
    @Resource
    private StudentUsuallyTaskService studentUsuallyTaskService;
    @Resource
    private UserRewardService userRewardService;
    @Resource
    private MissingAssignmentPerformanceService missingAssignmentPerformanceService;
    @Resource
    private BigLittleRestService bigLittleRestService;
    @Resource
    private DressCodeViolationService dressCodeViolationService;
    @Resource
    private PatrolRegistrationService patrolRegistrationService;

    /**
     * 删除学段前置校验
     *
     * @param id 要删除的学段id
     * @throws
     */
    public boolean validateBeforeDeleteSemester(Long id) {
        return classPerformanceService.hasPerformance(id)
                || studentExamTaskService.hasScore(id)
                || studentGraduateExamTaskService.hasScore(id)
                || studentQualityScoreService.hasScore(id)
                || studentUsuallyTaskService.hasScore(id)
                || userRewardService.hasReward(id)
                || missingAssignmentPerformanceService.hasPerformance(id);
    }

    /**
     * 删除系统预设参数前置校验，不存在返回true
     * @param id 需要删除的系统预设参数id
     */
    public boolean validateBeforeDeleteSysParam(Long id, DefaultParamEnum defaultParamEnum) {
        if (defaultParamEnum == DefaultParamEnum.PERF) {
            return classPerformanceService.canRemovePerformanceId(id);
        } else if (defaultParamEnum == DefaultParamEnum.APPEARANCE) {
            return dressCodeViolationService.canRemoveRemarkId(id);
        }else if (defaultParamEnum == DefaultParamEnum.ROUNDS) {
            return patrolRegistrationService.canRemoveRegistrationId(id);
        } else if (defaultParamEnum == DefaultParamEnum.REST) {
            return bigLittleRestService.canRemoveRegistrationId(id);
        }
        return false;
    }
}

   