package com.xiaotiyun.school.manager.basic.enums;

/**
 * 统一错误码
 */
public enum ResultCode {
    //系统
    SUCCESS(10000, "操作成功", "system.success"),
    FAILED(-10000, "操作失败","system.error"),
    VALIDATE_FAILED(400, "参数检验失败","param.check.error"),
    UNAUTHORIZED(401, "暂未登录或token已经过期","system.token.invalid"),
    FORBIDDEN(403, "没有相关权限","system.unauthorized"),
    NOT_FOUND(404, "资源不存在","system.resource.not.found"),

    SYSTEM_ERROR(405, "系统异常，请联系管理员","system.server.error"),

    //数据已存在，请勿重复添加
    DATA_EXIST(406, "数据已存在，请勿重复添加","system.data.exist"),
    FILE_UPLOAD_FAILED(407, "文件上传失败", "file.upload.failed"),

    //级组名重复
    DATA_GROUP_NAME_EXIST(1001, "级组名重复","grade.group.name.duplicate"),
    
    // 学校新增错误码
    SCHOOL_LANGUAGE_SETTING_NOT_FOUND(1102, "获取学校语言设置信息失败，导入失败", "school.language.setting.not.found"),
    SCHOOL_LANGUAGE_SETTING_EMPTY(1103, "获取学校语言设置信息失败，缺少有效的语言配置", "school.language.setting.empty"),
    SCHOOL_LANGUAGE_SETTING_INVALID(1104, "获取学校语言设置信息失败，语言配置错误", "school.language.setting.invalid"),
    
    // 专业相关错误码
    MAJOR_NAME_DUPLICATE(1205, "该学部下，专业名称不可重复", "major.name.duplicate"),

    //大功小功只能有一个有值
    DATA_GRADE_FUNCTION_ONLY_ONE(1301, "大功小功只能有一个有值", "data.grade.function.only.one"),


    //班级
    DATA_CLASS_NAME_EXIST(1401, "班级名称已存在", "class.name.exist"),

    //用户
    //账号不存在
    USER_ACCOUNT_NOT_EXIST(1501, "账号不存在", "account.not.exist"),
    //账号或密码错误
    USER_ACCOUNT_PASSWORD_ERROR(1502, "账号或密码错误", "account.or.password.error"),

    //幼稚园：最大可输入3
    DATA_YIKE_MAX_COUNT(1601, "最大可输入3", "yike.max.count"),

    //小学、中学：最大可输入6
    DATA_XIAOXUE_MAX_COUNT(1602, "最大可输入6", "xiaoxue.max.count"),

    //当前学部添加基础数据无法取消选择
    DEPARTMENT_HAS_DATA(1603, "当前学部添加基础数据无法取消选择","department.base.data.cannot.unselect");

    ;
    private int code;
    private String message;

    private String messageCode;

    ResultCode(int code, String message,String messageCode) {
        this.code = code;
        this.message = message;
        this.messageCode = messageCode;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
    public String getMessageCode() {
        return messageCode;
    }

    public static void main(String[] args) {
        for (ResultCode resultCode : ResultCode.values()) {
            System.out.print(resultCode.getMessage() + " ");
            System.out.println(resultCode.getMessageCode());
        }
    }
} 