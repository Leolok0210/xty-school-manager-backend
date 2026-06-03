package com.xiaotiyun.school.manager.basic.annotations;

import com.xiaotiyun.school.manager.basic.enums.DataBusinessTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.DataOperationTypeEnum;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataOperationLog {
    DataOperationTypeEnum opType();// 操作类型（1-新增，2-修改）
    DataBusinessTypeEnum businessType();// 1-平时成绩登记，2-考试成绩登记，3-毕业考试登记，4-学年素质登记，5-奖励登记，6-惩罚登记，7-课堂表现登记，8-欠交作业登记，9-仪表不符登记，10-巡堂登记，11-大息小息表现登记，12-课外比赛登记，13-校外比赛登记，14-义工服务
}
