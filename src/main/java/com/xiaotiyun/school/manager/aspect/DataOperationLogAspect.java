package com.xiaotiyun.school.manager.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiaotiyun.school.manager.basic.annotations.DataOperationLog;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.DataBusinessTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.DataOperationTypeEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class DataOperationLogAspect {
    @Lazy
    @Autowired
    private DataOperationLogService dataOperationLogService;
    @Lazy
    @Autowired
    private StudentUsuallyTaskService studentUsuallyTaskService;
    @Lazy
    @Autowired
    private StudentUsuallyScoreService studentUsuallyScoreService;
    @Lazy
    @Autowired
    private StudentExamTaskService studentExamTaskService;
    @Lazy
    @Autowired
    private StudentExamScoreService studentExamScoreService;
    @Lazy
    @Autowired
    private StudentGraduateExamTaskService studentGraduateExamTaskService;
    @Lazy
    @Autowired
    private StudentGraduateExamScoreService studentGraduateExamScoreService;

    // 定义切点，匹配所有带有@DataOperationLog注解的方法
    @Around("@annotation(com.xiaotiyun.school.manager.basic.annotations.DataOperationLog)")
    public Object dataOperationLogSave(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取session中用户信息
        UserEntity userInfo = (UserEntity) StpUtil.getSession().get("userInfo");
        if (userInfo == null) {
            log.error("数据录入记录打点失败！未获取到用户信息！");
            throw new BusinessException(LanguageConstants.SYSTEM_TOKEN_INVALID);
        }
        // 获取被拦截的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 获取注解实例
        DataOperationLog annotation = method.getAnnotation(DataOperationLog.class);
        // 获取注解属性值
        DataBusinessTypeEnum dataBusinessTypeEnum = annotation.businessType();
        DataOperationTypeEnum dataOperationTypeEnum = annotation.opType();
        if (dataBusinessTypeEnum == null || dataOperationTypeEnum == null) {
            log.error("数据录入记录打点失败！未获取到业务类型或操作类型！");
            throw new BusinessException(LanguageConstants.PARAM_ERROR);
        }
        // 获取方法入参名称
        String[] parameterNames = signature.getParameterNames();
        // 获取方法入参
        Object[] args = joinPoint.getArgs();
        try {
            // 执行原方法
            Object result = joinPoint.proceed();
            // 从返回中获取数据存日志
            if (result != null) {
                List<Long> businessIds = new ArrayList<>();
                // 批量新增
                if (result instanceof Collection) {
                    JSONArray list = JSON.parseArray(JSON.toJSONString(result));
                    List<DataOperationLogEntity> addList = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        addList.add(DataOperationLogEntity.builder()
                                .businessId(list.getJSONObject(i).getLong("id"))
                                .businessType(dataBusinessTypeEnum.getValue())
                                .operationType(dataOperationTypeEnum.getValue())
                                .operatorId(userInfo.getId())
                                .operatorName(userInfo.getUsername())
                                .build());
                    }
                    if (!addList.isEmpty()) {
                        dataOperationLogService.batchAdd(addList);
                        businessIds = addList.stream().map(DataOperationLogEntity::getBusinessId).collect(Collectors.toList());
                    }
                }
                // 单条新增 或 单条更新
                if (result instanceof BaseEntity) {
                    BaseEntity entity = (BaseEntity) result;
                    if (entity.getId() != null) {
                        dataOperationLogService.add(DataOperationLogEntity.builder()
                                .businessId(entity.getId())
                                .businessType(dataBusinessTypeEnum.getValue())
                                .operationType(dataOperationTypeEnum.getValue())
                                .operatorId(userInfo.getId())
                                .operatorName(userInfo.getUsername())
                                .build());
                        businessIds.add(entity.getId());
                    }
                }
                updateIdChange(businessIds, userInfo, dataBusinessTypeEnum);
            }
            return result;
        } catch (Throwable e) {
            log.error("数据操作日志保存失败,方法名：{},方法入参：{}", method.getName(), args, e);
            // 异常处理
            throw e;
        }
    }

    private void updateIdChange(List<Long> businessIds, UserEntity userInfo, DataBusinessTypeEnum dataBusinessTypeEnum) {
        if (CollectionUtils.isNotEmpty(businessIds)) {
            switch (dataBusinessTypeEnum) {
                case DAILY_GRADE:
                    studentUsuallyScoreService.update(Wrappers.<StudentUsuallyScoreEntity>lambdaUpdate()
                            .in(StudentUsuallyScoreEntity::getId, businessIds)
                            .set(StudentUsuallyScoreEntity::getUpdateId, userInfo.getId()));
                    break;
                case USUALLY_TASK:
                    studentUsuallyTaskService.update(Wrappers.<StudentUsuallyTaskEntity>lambdaUpdate()
                            .in(StudentUsuallyTaskEntity::getId, businessIds)
                            .set(StudentUsuallyTaskEntity::getUpdateId, userInfo.getId()));
                    break;
                case EXAM_GRADE:
                    studentExamScoreService.update(Wrappers.<StudentExamScoreEntity>lambdaUpdate()
                            .in(StudentExamScoreEntity::getId, businessIds)
                            .set(StudentExamScoreEntity::getUpdateId, userInfo.getId()));
                    break;
                case EXAM_TASK:
                    studentExamTaskService.update(Wrappers.<StudentExamTaskEntity>lambdaUpdate()
                            .in(StudentExamTaskEntity::getId, businessIds)
                            .set(StudentExamTaskEntity::getUpdateId, userInfo.getId()));
                    break;
                case GRADUATION_EXAM:
                    studentGraduateExamScoreService.update(Wrappers.<StudentGraduateExamScoreEntity>lambdaUpdate()
                            .in(StudentGraduateExamScoreEntity::getId, businessIds)
                            .set(StudentGraduateExamScoreEntity::getUpdateId, userInfo.getId()));
                    break;
                case GRADUATE_EXAM_TASK:
                    studentGraduateExamTaskService.update(Wrappers.<StudentGraduateExamTaskEntity>lambdaUpdate()
                            .in(StudentGraduateExamTaskEntity::getId, businessIds)
                            .set(StudentGraduateExamTaskEntity::getUpdateId, userInfo.getId()));
                    break;
            }
        }
    }
}
