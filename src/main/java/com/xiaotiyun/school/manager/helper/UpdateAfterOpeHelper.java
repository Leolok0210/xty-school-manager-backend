package com.xiaotiyun.school.manager.helper;

import com.xiaotiyun.school.manager.model.entity.SystemDefaultParameterEntity;
import com.xiaotiyun.school.manager.service.BigLittleRestService;
import com.xiaotiyun.school.manager.service.ClassPerformanceService;
import com.xiaotiyun.school.manager.service.DressCodeViolationService;
import com.xiaotiyun.school.manager.service.PatrolRegistrationService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class UpdateAfterOpeHelper {

    @Resource
    private ClassPerformanceService classPerformanceService;
    @Resource
    private BigLittleRestService bigLittleRestService;
    @Resource
    private DressCodeViolationService dressCodeViolationService;
    @Resource
    private PatrolRegistrationService patrolRegistrationService;

    public void updateSystemDefaultParamAfterOpe(SystemDefaultParameterEntity entity) {
        switch (entity.getTypeGroup()){
            case "PERF":
                classPerformanceService.updatePerformanceById(entity.getId(), entity.getValue());
                break;
            case "REST":
                bigLittleRestService.updateRegistrationContentById(entity.getId(), entity.getValue());
                break;
            case "APPEARANCE":
                dressCodeViolationService.updateRemarkById(entity.getId(), entity.getValue());
                break;
            case "ROUNDS":
                patrolRegistrationService.updateRegistrationContentById(entity.getId(), entity.getValue());
                break;
        }
    }
}
