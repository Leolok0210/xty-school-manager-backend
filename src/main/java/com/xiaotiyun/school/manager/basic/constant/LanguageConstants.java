package com.xiaotiyun.school.manager.basic.constant;

public class LanguageConstants {

    //文件格式錯誤，請使用標準模板
    public static final String FILE_FORMAT_ERROR = "file.format.error";
    //表头不匹配，期望的表头是：%s，但实际是：%s
    public static final String HEADER_NOT_MATCH = "header.not.match";

    //账号不存在
    public static final String ACCOUNT_NOT_EXIST = "account.not.exist";
    //账号或密码错误
    public static final String ACCOUNT_OR_PASSWORD_ERROR = "account.or.password.error";
    //用户不存在
    public static final String USER_NOT_EXIST = "user.not.exists";
    //用户名已存在
    public static final String USERNAME_EXISTS = "username.exists";
    //手机号已存在
    public static final String MOBILE_EXISTS = "mobile.exists";
    //用户名一致，手机号不一致
    public static final String MOBILE_NOT_MATCH = "mobile.not.match";
    //用户名不一致，手机号一致
    public static final String USERNAME_NOT_MATCH = "username.not.match";
    //请输入旧密码
    public static final String OLD_PASSWORD_REQUIRED = "old.password.required";
    //旧密码错误
    public static final String OLD_PASSWORD_ERROR = "old.password.error";
    //微信登录错误请重试
    public static final String WX_LOGIN_ERROR = "wx.login.error";

    //座位号
    public static final String EXPORT_SEAT_NUMBER = "export.seat.number";
    //学生姓名
    public static final String EXPORT_STUDENT_NAME = "export.student.name";
    //学生不存在
    public static final String STUDENT_NOT_EXIST = "student.not.exist";
    //操行
    public static final String EXPORT_CONDUCT = "export.conduct";
    //大功
    public static final String EXPORT_MAJOR_MERIT = "export.major.merit";
    //小功
    public static final String EXPORT_MINOR_MERIT = "export.minor.merit";
    //优点
    public static final String EXPORT_MERIT_POINT = "export.merit.point";
    //大过
    public static final String EXPORT_MAJOR_DEMERIT = "export.major.demerit";
    //小过
    public static final String EXPORT_MINOR_DEMERIT = "export.minor.demerit";
    //缺点
    public static final String EXPORT_DEMERIT_POINT = "export.demerit.point";
    //评语
    public static final String EXPORT_COMMENTS = "export.comments";
    //迟到次数
    public static final String EXPORT_LATE_COUNT = "export.late.count";
    //请假次数
    public static final String EXPORT_LEAVE_COUNT = "export.leave.count";
    //缺席次数
    public static final String EXPORT_ABSENCE_COUNT = "export.absence.count";

    //记录不存在
    public static final String RECORD_NOT_EXIST = "record.not.exist";
    //处理中不可删除
    public static final String PROCESSING_CANNOT_DELETE = "processing.cannot.delete";
    //非待处理不可操作
    public static final String NOT_PROCESSING_CANNOT_OPERATE = "not.processing.cannot.operate";
    //比赛不存在
    public static final String COMPETITION_NOT_EXIST = "competition.not.exist";

    //个人赛：只可选择1个学生
    public static final String INDIVIDUAL_COMPETITION_SINGLE_STUDENT = "individual.competition.single.student";
    //团体赛，选择人数少于2人
    public static final String TEAM_COMPETITION_MIN_STUDENTS = "team.competition.min.students";
    //团体赛，选择人数需少于100人
    public static final String TEAM_COMPETITION_MAX_STUDENTS = "team.competition.max.students";

    //文件内容不能为空
    public static final String FILE_CONTENT_EMPTY = "file.content.empty";
    //文件名称不能为空
    public static final String FILE_NAME_EMPTY = "file.name.empty";

    //级组名重复
    public static final String GRADE_GROUP_NAME_DUPLICATE = "grade.group.name.duplicate";
    // 当前年级已添加班级，不可删除
    public static final String GRADE_GROUP_HAS_CLASS = "grade.group.has.class";
    // XX年级、XX年级已添加班级，不可修改分科方式
    public static final String GRADE_GROUP_HAS_CLASS_CANNOT_MODIFY = "grade.group.has.class.cannot.modify";
    // 当前学部下没有班级
    public static final String CLASS_NOT_FOUND = "class.not.found";

    //开始时间和结束时间必须同时设置或同时清空
    public static final String TIME_SETTING_BOTH_REQUIRED = "time.setting.both.required";
    //开始时间不能晚于结束时间
    public static final String TIME_SETTING_START_AFTER_END = "time.setting.start.after.end";

    //医院名称已存在
    public static final String HOSPITAL_NAME_EXISTS = "hospital.name.exists";
    //医院不存在
    public static final String HOSPITAL_NOT_EXISTS = "hospital.not.exists";

    //菜单不存在
    public static final String MENU_NOT_EXISTS = "menu.not.exists";
    //存在子菜单，不能删除
    public static final String MENU_HAS_CHILDREN = "menu.has.children";
    //上级菜单不存在
    public static final String PARENT_MENU_NOT_EXISTS = "parent.menu.not.exists";
    //上级菜单类型必须为菜单
    public static final String PARENT_MENU_TYPE_ERROR = "parent.menu.type.error";
    //同一层级下菜单名称不能重复
    public static final String MENU_NAME_DUPLICATE = "menu.name.duplicate";
    //学校ID不能为空
    public static final String SCHOOL_ID_REQUIRED = "school.id.required";

    //无权删除其他学校的评语规则
    public static final String NO_PERMISSION_DELETE_OTHER_SCHOOL_RULE = "no.permission.delete.other.school.rule";
    //无效的条件组合类型
    public static final String INVALID_CONDITION_COMBINE_TYPE = "invalid.condition.combine.type";
    //无效的条件项目
    public static final String INVALID_CONDITION_ITEM = "invalid.condition.item";
    //无效的运算符
    public static final String INVALID_OPERATOR = "invalid.operator";
    //评语模板变量格式错误
    public static final String COMMENT_TEMPLATE_VAR_FORMAT_ERROR = "comment.template.var.format.error";
    //无效的评语模板变量
    public static final String INVALID_COMMENT_TEMPLATE_VAR = "invalid.comment.template.var";
    //评语规则不存在
    public static final String COMMENT_RULE_NOT_EXISTS = "comment.rule.not.exists";
    //无权编辑其他学校的评语规则
    public static final String NO_PERMISSION_EDIT_OTHER_SCHOOL_RULE = "no.permission.edit.other.school.rule";

    //学部的权重总和必须为100%
    public static final String DEPARTMENT_WEIGHT_MUST_BE_100 = "department.weight.must.be.100";
    //指标不存在或无权修改
    public static final String INDICATOR_NOT_EXISTS_OR_NO_PERMISSION = "indicator.not.exists.or.no.permission";
    //最小分值不能大于最大分值
    public static final String MIN_SCORE_GREATER_THAN_MAX = "min.score.greater.than.max";
    //评分标准不存在
    public static final String GRADE_STANDARD_NOT_EXISTS = "grade.standard.not.exists";

    //事项不存在
    public static final String EVENT_NOT_EXISTS = "event.not.exists";
    //关联校历不存在或已被删除
    public static final String CALENDAR_NOT_EXISTS_OR_DELETED = "calendar.not.exists.or.deleted";
    //事项日期需在校历时间范围内
    public static final String EVENT_DATE_OUT_OF_RANGE = "event.date.out.of.range";
    //已结束的月份下的日期事项不支持修改
    public static final String EVENT_MONTH_ALREADY_ENDED = "event.month.already.ended";

    //校历不存在
    public static final String CALENDAR_NOT_EXISTS = "calendar.not.exists";
    //校历时间不可有重叠
    public static final String CALENDAR_TIME_OVERLAP = "calendar.time.overlap";

    //学校名称已存在
    public static final String SCHOOL_NAME_EXISTS = "school.name.exists";
    //学校编号已存在
    public static final String SCHOOL_CODE_EXISTS = "school.code.exists";
    //学校不存在
    public static final String SCHOOL_NOT_EXISTS = "school.not.exists";
    //专业名称已存在
    public static final String MAJOR_NAME_EXISTS = "major.name.exists";

    //学段不存在
    public static final String SEMESTER_NOT_EXISTS = "semester.not.exists";
    //学段ID不存在
    public static final String SEMESTER_ID_NOT_EXISTS = "semester.id.not.exists";
    //无权操作学段
    public static final String NO_PERMISSION_OPERATE_SEMESTER = "no.permission.operate.semester";
    //学段[%s]和学段[%s]的时间段重叠
    public static final String SEMESTER_TIME_OVERLAP = "semester.time.overlap";

    //上午入校时间必须早于下午离校时间
    public static final String MORNING_IN_BEFORE_AFTERNOON_OUT = "morning.in.before.afternoon.out";
    //一个年级只能被添加到一个考勤规则中
    public static final String GRADE_SINGLE_ATTENDANCE_RULE = "grade.single.attendance.rule";

    //入校时间必须早于离校时间
    public static final String ATTENDANCE_TIME_INVALID = "attendance.time.invalid";
    //没有学校信息，导入失败
    public static final String NO_SCHOOL_FILE_CONTENT_EMPTY = "school.no.exit.file.error";
    //获取学校语言设置信息失败，导入失败
    public static final String GET_SCHOOL_LANGUAGE_SETTING_ERROR = "school.language.setting.not.found";
    //获取学校语言设置信息失败，缺少有效的语言配置
    public static final String GET_SCHOOL_LANGUAGE_SETTING_ERROR_NO_LANGUAGE_CONFIG = "school.language.setting.empty";
    //获取学校语言设置信息失败，语言配置错误
    public static final String GET_SCHOOL_LANGUAGE_SETTING_ERROR_LANGUAGE_CONFIG_ERROR = "school.language.setting.invalid";

    //成绩不存在
    public static final String EXAM_SCORE_NOT_EXISTS = "exam.score.not.exists";
    //未查询到成绩单，请先生成
    public static final String EXAM_SCORE_NOT_EXISTS_NO_SCORE = "exam.score.not.exists.no.score";

    //当前班级：%s 被限制无法录入成绩，请修改设置后再录入
    public static final String CLASS_RESTRICTED_NO_SCORE = "class.restricted.no.score";

    //当前班级：%s 被限制无法录入义工服务，请修改设置后再录入
    public static final String VOLUNTEER_RESTRICTED_NO_SCORE = "volunteer.restricted.no.score";

    //当前班级：%s 被限制无法录入奖惩记录，请修改设置后再录入
    public static final String REWARD_RESTRICTED_NO_SCORE = "reward.restricted.no.score";

    //当前班级：%s 被限制无法录入學年素質登記，请修改设置后再录入
    public static final String QUALITY_CLASS_RESTRICTED_NO_SCORE = "quality.class.restricted.no.score";

    //学生家庭信息已存在
    public static final String STUDENT_FAMILY_EXISTS = "student.family.exists";


    //毕业考试成绩不存在
    public static final String GRADUATE_EXAM_SCORE_NOT_EXISTS = "graduate.exam.score.not.exists";

    //文件读取失败，请检查文件格式
    public static final String FILE_READ_ERROR = "file.read.error";
    //學生照片格式錯誤，僅支持上傳100-300KB的jpg格式的圖片
    public static final String IMAGE_SIZE_ERROR = "image.format.error";
    //图片太大
    public static final String IMAGE_TOO_LARGE = "image.too.large";
    //图片保存失败
    public static final String IMAGE_SAVE_ERROR = "image.save.error";
    //图片必须是jpg格式
    public static final String IMAGE_FORMAT_ERROR = "image.format.error";
    //找到多个编号相同的学生，请检查后重新导入
    public static final String IMAGE_IMPORT_STUDENT_NO_REPEAT_ERROR = "image.import.student.no.repeat.error";
    //图片必须是jpg或Png格式
    //學生照片格式錯誤，僅支持上傳100-300KB的jpg格式的圖片
    public static final String IMAGE_FORMAT_ERROR_JPG_PNG = "image.format.error.jpg.png";
    //教青局编号已存在
    public static final String TEACHER_QINGJIA_CODE_EXISTS = "teacher.qingjia.code.exists";
    //该班级座位号已存在
    public static final String CLASS_SEAT_NO_EXISTS = "class.seat.no.exists";

    //科目ID不能为空
    public static final String SUBJECT_ID_REQUIRED = "subject.id.required";
    //科目ID不存在
    public static final String SUBJECT_ID_NOT_EXISTS = "subject.id.not.exists";
    //科目编号重复
    public static final String SUBJECT_NUMBER_EXISTS = "subject.number.exists";
    //科目名称重复
    public static final String SUBJECT_NAME_EXISTS = "subject.name.exists";
    // 科目已关联相关数据，无法删除
    public static final String SUBJECT_HAS_RELATION = "subject.has.relation";
    // 请设置评级规则
    public static final String SUBJECT_LEVEL_RULE_NOT_EXISTS = "subject.level.rule.not.exists";

    //上班时间必须早于下班时间
    public static final String CLOCK_IN_BEFORE_CLOCK_OUT = "clock.in.before.clock.out";
    //用户只能被添加到一个考勤规则中，不可重复选择
    public static final String USER_SINGLE_ATTENDANCE_RULE = "user.single.attendance.rule";

    //用户组名称已存在
    public static final String USER_GROUP_NAME_EXISTS = "user.group.name.exists";
    //用户组不存在
    public static final String USER_GROUP_NOT_EXISTS = "user.group.not.exists";
    //预设用户组不允许修改
    public static final String PRESET_USER_GROUP_NO_MODIFY = "preset.user.group.no.modify";
    //预设用户组不允许删除
    public static final String PRESET_USER_GROUP_NO_DELETE = "preset.user.group.no.delete";
    //当前用户组下有用户在使用，不可删除
    public static final String USER_GROUP_IN_USE = "user.group.in.use";

    //该用户已关联此学校
    public static final String USER_ALREADY_BOUND_SCHOOL = "user.already.bound.school";
    //该用户编号重复
    public static final String USER_NUMBER_REPEAT = "user.number.repeat";
    //用户不存在
    public static final String USER_NOT_EXISTS = "user.not.exists";
    //用户未关联该学校
    public static final String USER_NOT_BOUND_SCHOOL = "user.not.bound.school";

    //开始时间必须早于结束时间
    public static final String START_TIME_BEFORE_END_TIME = "start.time.before.end.time";
    //服务时长必须大于0
    public static final String SERVICE_HOURS_GREATER_THAN_ZERO = "service.hours.greater.than.zero";

    //班级id不能为空
    public static final String CLASS_ID_REQUIRED = "class.id.required";
    //学段不能为空
    public static final String TERM_REQUIRED = "term.required";
    //学生id不能为空
    public static final String STUDENT_ID_REQUIRED = "student.id.required";
    //上课日期不能为空
    public static final String CLASS_DATE_REQUIRED = "class.date.required";
    //节数不能为空
    public static final String CLASS_SECTION_REQUIRED = "class.section.required";
    //课堂表现不能为空
    public static final String PERFORMANCE_REQUIRED = "performance.required";
    //登记人不能为空
    public static final String USER_ID_REQUIRED = "user.id.required";
    //参数错误
    public static final String PARAM_ERROR = "param.error";
    // 用户未登录
    public static final String SYSTEM_TOKEN_INVALID = "system.token.invalid";


    //"學生編號不能爲空"
    public static final String STUDENT_NO_REQUIRED = "student.no.required";
    //"学生证编号不能为空"
    public static final String EDUCATION_NO_REQUIRED = "education.no.required";
    // 学生证编号重复
    public static final String EDUCATION_NO_EXISTS = "education.no.exists";
    // 学生证编号在excel表中重复
    public static final String EDUCATION_NO_EXISTS_EXCEL = "education.no.exists.excel";
    // 学生证编号在系统中已存在，其对应的学生编号与系统中记录的不一致，请核对
    public static final String EDUCATION_NO_EXISTS_IN_SYSTEM = "education.no.exists.in.system";
    //"學生姓名不能爲空"
    public static final String STUDENT_NAME_REQUIRED = "student.name.required";
    //學生姓名和學生編號對應姓名不一致。
    public static final String STUDENT_NAME_NOT_MATCH = "student.name.not.match";
    //學生編號在該學校未找到
    public static final String STUDENT_NO_NOT_FOUND = "student.no.not.found";
    //提交失败，该学生已被其他企微绑定
    public static final String STUDENT_ALREADY_BOUND = "student.already.bound";
    //類型格式錯誤，請從下拉條件中選擇。
    public static final String TYPE_FORMAT_ERROR = "type.format.error";
    //類型不能爲空
    public static final String TYPE_REQUIRED = "type.required";
    //日期格式錯誤，正確格式年-月-日
    public static final String DATE_FORMAT_ERROR = "date.format.error";

    //日期不能爲空
    public static final String DATE_REQUIRED = "date.required";
    //時間格式錯誤，正確格式時：分：秒
    public static final String TIME_FORMAT_ERROR = "time.format.error";
    //時間不能爲空
    public static final String TIME_REQUIRED = "time.required";

    //中文姓名不能爲空
    public static final String CHINESE_NAME_REQUIRED = "chinese.name.required";

    //姓名-( %s )格式錯誤，不允許包含特殊字符
    public static final String NAME_FORMAT_ERROR = "name.format.error";

    //性别-(%s)格式錯誤，請選擇有效的性別
    public static final String GENDER_FORMAT_ERROR = "gender.format.error";

    //表中學生編號-( %s )重複
    public static final String STUDENT_NO_EXISTS = "student.no.exists";
    // 学生编号在系统中已存在，其对应的学生证编号与系统中记录的不一致，请核对
    public static final String STUDENT_NO_EXISTS_IN_SYSTEM = "student.no.exists.in.system";
    //座位號-( %s )格式錯誤，僅允許填寫數字
    public static final String SEAT_NO_FORMAT_ERROR = "seat.no.format.error";

    //"表中座位號-( %s )重複"
    public static final String SEAT_NO_EXISTS = "seat.no.exists";
    //"座位號-( %s )在班級中已存在"
    public static final String SEAT_NO_EXISTS_IN_CLASS = "seat.no.exists.in.class";
    //班級名稱-( %s )不存在，請輸入有效的班級
    public static final String CLASS_NAME_NOT_EXISTS = "class.name.not.exists";
    //機組不能爲空
    public static final String GRADE_GROUP_REQUIRED = "grade.group.empty";
    //所屬班級不能爲空
    public static final String CLASS_REQUIRED = "class.required";
    //緊急聯絡人聯絡電話不能爲空
    public static final String EMERGENCY_CONTACT_PHONE_REQUIRED = "emergency.contact.phone.required";
    //出生日期-( %s )格式錯誤，無法解析
    public static final String BIRTH_DATE_FORMAT_ERROR = "birth.date.format.error";
    //中文姓名-( %s ）在該學校未找到;
    public static final String STUDENT_NAME_NOT_FOUND = "student.name.not.found";
    //中文姓名-( %s ）和學生編號對應學生姓名不一致。;
    public static final String STUDENT_NAME_NOT_MATCH_STUDENT_NO = "student.name.not.match.student.no";
    //failureReason.append("學生編號-( %s ）在該學校未找到;");
    public static final String STUDENT_NO_NOT_FOUND_IN_SCHOOL = "student.no.not.found.in.school";
    //學生編號-( %s )和學生編號對應學生姓名不一致。;
    public static final String STUDENT_NO_NOT_MATCH_STUDENT_NAME = "student.no.not.match.student.name";
    //項目名稱-（ %s ）格式不正確，請輸入數字評分;
    public static final String QUALITY_PROJECT_FORMAT_ERROR = "quality.project.format.error";
    //科目編號不能爲空;
    public static final String SUBJECT_NUMBER_REQUIRED = "subject.number.required";
    //科目編號-( %s ）格式錯誤，不允許包含特殊字符;
    public static final String SUBJECT_NUMBER_FORMAT_ERROR = "subject.number.format.error";
    //科目編號-( %s ）錯誤，科目編號重复。;
    public static final String SUBJECT_NUMBER_EXISTS_ERROR = "subject.number.exists.error";
    //科目名稱不能爲空;
    public static final String SUBJECT_NAME_REQUIRED = "subject.name.required";
    //科目名稱-( %s ）格式錯誤，不允許包含特殊字符。;
    public static final String SUBJECT_NAME_FORMAT_ERROR = "subject.name.format.error";
    //科目名稱-( %s ）錯誤，科目名稱重复。;
    public static final String SUBJECT_NAME_EXISTS_ERROR = "subject.name.exists.error";
    //學部不能爲空;
    public static final String DEPARTMENT_REQUIRED = "department.required";
    //學部-( %s ）格式錯誤，請選擇有效的學部值。;
    public static final String DEPARTMENT_FORMAT_ERROR = "department.format.error";
    //是否計入學生平均分不能爲空;
    public static final String INCLUDE_IN_STUDENT_AVERAGE_SCORE_REQUIRED = "include.in.student.average.score.required";
    //是否計入學生平均分-( %s ）格式錯誤，請選擇有效的值。;
    public static final String INCLUDE_IN_STUDENT_AVERAGE_SCORE_FORMAT_ERROR = "include.in.student.average.score.format.error";
    //是否專業課程不能爲空;
    public static final String MAJOR_COURSE_REQUIRED = "major.course.required";
    //是否專業課程-( %s ）格式錯誤，請選擇有效的值。;
    public static final String MAJOR_COURSE_FORMAT_ERROR = "major.course.format.error";


    // 班級名稱-(%s）同一級組中不可重複
    public static final String CLASS_NAME_DUPLICATE = "class.name.duplicate";

    // 級組不能爲空
    public static final String GRADE_GROUP_EMPTY = "grade.group.empty";

    // 班級序號不能爲空
    public static final String CLASS_SERIAL_NUMBER_EMPTY = "class.serial.number.empty";

    // 班級序號-(%s）格式錯誤，必須爲數字
    public static final String CLASS_SERIAL_NUMBER_FORMAT = "class.serial.number.format";

    // 班級序號-(%s）錯誤，班級序号重复
    public static final String CLASS_SERIAL_NUMBER_DUPLICATE = "class.serial.number.duplicate";

    // 是否專業班不能爲空
    public static final String PROFESSIONAL_VERSION_EMPTY = "professional.version.empty";

    // 是否專業班-(%s）格式錯誤，請選擇有效的值
    public static final String PROFESSIONAL_VERSION_FORMAT = "professional.version.format";

    // 是文科/理科-(%s）格式錯誤，請選擇有效的值
    public static final String ARTS_SCIENCE_FORMAT = "arts.science.format";
    // 該班級無填寫文理科屬性
    public static final String CLASS_NO_ART_SCIENCE = "class.no.art.science";
    // 請填寫文理科屬性
    public static final String CLASS_ART_SCIENCE_REQUIRED = "class.art.science.required";
    // 專業班的專業名稱不能爲空
    public static final String PROFESSIONAL_NAME_EMPTY = "professional.name.empty";
    // 該班級無填寫專業名稱
    public static final String CLASS_NO_PROFESSIONAL_NAME = "class.no.professional.name";
    // 專業名稱-(%s）格式錯誤，請選擇有效的值
    public static final String PROFESSIONAL_NAME_FORMAT = "professional.name.format";
    //級組-( %s ）不存在，請填寫有效的級組值。;
    public static final String GRADE_GROUP_NOT_EXISTS = "grade.group.not.exists";
    //班主任手機號—（%s） 系統中不存在;
    public static final String HEAD_TEACHER_NOT_EXISTS = "head.teacher.not.exists";
    // 系統中未找到該用戶
    public static final String USER_NOT_FOUND = "user.not.found";
    // 教師手機號不能爲空
    public static final String TEACHER_PHONE_REQUIRED = "teacher.phone.required";

    // 教師姓名不能爲空
    public static final String TEACHER_NAME_REQUIRED = "teacher.name.required";
    // 請輸入班主任用戶編號
    public static final String HEAD_TEACHER_USER_NO_REQUIRED = "head.teacher.user.no.required";
    // 教師姓名和教師手機號對應姓名不一致
    public static final String TEACHER_NAME_NOT_MATCH = "teacher.name.not.match";

    // 教師手機號在該學校未找到
    public static final String TEACHER_PHONE_NOT_FOUND = "teacher.phone.not.found";

    // 教師用户编号在該學校未找到
    public static final String TEACHER_NUMBER_NOT_FOUND = "teacher.number.not.found";

    // 日期格式錯誤，正確格式年-月-日
    public static final String DATE_FORMAT_ERROR_YMD = "date.format.error";

    // 時間格式錯誤，正確格式時:分:秒
    public static final String TIME_FORMAT_ERROR_HMS = "time.format.error";


    //学生编号已存在
    public static final String STUDENT_NUMBER_EXISTS = "student.number.exists";
    //学生编号不存在
    public static final String STUDENT_NUMBER_NOT_EXISTS = "student.number.not.exists";
    //学生姓名错误
    public static final String STUDENT_NAME_ERROR = "student.name.error";
    //级组/班级信息错误，请修改
    public static final String GRADE_GROUP_CLASS_ERROR = "grade.group.class.error";

    //请求头中缺少学校ID
    public static final String SCHOOL_ID_NOT_FOUND = "school.id.not.found";
    //学校存在关联的学生，无法删除
    public static final String SCHOOL_HAS_STUDENT = "school.has.student";

    //该班级下已添加学生不可删除
    public static final String CLASS_HAS_STUDENT = "class.has.student";

    //当前学段已录入成绩数据,无法删除
    public static final String SEMESTER_HAS_SCORE = "semester.has.score";
    //开始时间必须早于结束时间
    public static final String START_TIME_AFTER_END_TIME = "start.time.before.end.time";
    //该时间段已存在公务记录
    public static final String OFFICIAL_RECORD_EXISTS = "official.record.exists";

    //%s的班级名称已存在
    public static final String CLASS_NAME_EXISTS = "class.param.name.exists";


    //的义工服务仅支持在如下时段录入
    public static final String VOLUNTEER_INPUT_TIME_RANGE = "volunteer.input.time.range";

    //的考试成绩仅支持在如下时段录入
    public static final String SCORE_INPUT_TIME_RANGE = "score.input.time.range";

    //的奖惩记录仅支持在如下时段录入
    public static final String REWARD_INPUT_TIME_RANGE = "reward.input.time.range";

    //的學年素質登記仅支持在如下时段录入
    public static final String QUALITY_SCORE_INPUT_TIME_RANGE = "quality.score.input.time.range";

    //yyyy年M月d日
    public static final String YEAR_MONTH_DAY = "time.format";

    //预设参数修改
    public static final String PRESET_PARAM_MODIFY = "preset.param.modify";
    //预设参数删除
    public static final String PRESET_PARAM_DELETE = "preset.param.delete";
    //備註內容不可重複
    public static final String PRESET_PARAM_DESC_EXISTS = "preset.param.desc.exists";
    //登記內容不可重複
    public static final String PRESET_PARAM_VALUE_EXISTS = "preset.param.value.exists";
    //表現內容不可重複
    public static final String PRESET_PARAM_NAME_EXISTS = "preset.param.name.exists";
    //編號不可重複
    public static final String PRESET_PARAM_CODE_EXISTS = "preset.param.code.exists";
    //該條登記內容已被使用，無法刪除
    public static final String PRESET_PARAM_ROUNDS_DESC_DELETE = "preset.param.rounds.desc.delete";
    //該條備註已被使用，無法刪除
    public static final String PRESET_PARAM_APPEARANCE_NAME_DELETE = "preset.param.appearance.name.delete";
    //該條大小息表現已被使用，無法刪除
    public static final String PRESET_PARAM_REST_VALUE_DELETE = "preset.param.rest.value.delete";
    //該條課堂表現已被使用，無法刪除
    public static final String PRESET_PARAM_PERF_VALUE_DELETE = "preset.param.perf.value.delete";
    //教室类型已被使用，无法删除
    public static final String CLASSROOM_TYPE_DELETE = "classroom.type.delete";
    //年级课节信息已存在
    public static final String GRADE_LESSON_ALREADY_EXISTS = "grade:lesson.already:exists";
    //年级课节名称重复
    public static final String LESSON_NAME_DUPLICATE_IN_GRADE = "grade:lesson.name:duplicate";
    //课节时间重叠
    public static final String LESSON_TIME_OVERLAP = "lesson.time.overlap";
    //课节信息不存在
    public static final String LESSON_NOT_FOUND = "lesson.not.found";
    //代课冲突
    public static final String SUBSTITUTE_COURSE_TIME_OVERLAP = "substitute.course.time.overlap";
    //该代课记录已开始或已结束，无法删除
    public static final String SUBSTITUTE_DELETE_FAIL = "substitute.delete.fail";
    //该代课记录已开始或已结束，无法更新
    public static final String SUBSTITUTE_UPDATE_FAIL = "substitute.update.fail";
    //该课表记录已开始或已结束，无法更新
    public static final String COURSE_UPDATE_FAIL = "course.update.fail";
    //该课表记录已开始或已结束，无法删除
    public static final String COURSE_DELETE_FAIL = "course.delete.fail";
    //科目任教信息不存在
    public static final String SUBJECT_TEACHING_NOT_FOUND = "subject.teaching.not.found";
    //科目信息不存在
    public static final String SUBJECT_NOT_FOUND = "subject.not.found";
    //当前科目的教师当前时段已安排其他课程
    public static final String COURSE_TIME_OVERLAP = "course.time.overlap";
    // 教师不存在
    public static final String TEACHER_NOT_FOUND = "teacher.not.found";
    //當前教室已經被同時間其他課選擇，請更換
    public static final String COURSE_CLASSROOM_OVERLAP = "course.classroom.overlap";
    //请求头中缺少用户id
    public static final String HEADER_USERID_NOT_FOUND = "header.userid.not.found";
    //保存失败，以下时段存在冲突：(%s)（任课教师或教室与其他班级冲突），请调整后再操作
    public static final String COURSE_FORMAT_ERROR = "course.format.error";
    //部分学生的 班内号未填写，请补齐后下载
    public static final String STUDENT_SEAT_NO_NOT_EXISTS = "student.seat.no.not.exists";
    //部分学生的 班内号/教青局编号未填写，请补齐后下载
    public static final String STUDENT_SEAT_NO_OR_EDUCATION_NO_NOT_EXISTS = "student.seat.no.or.education.no.not.exists";
    //学生相片下载失败，失败原因：(%s)
    public static final String STUDENT_PHOTO_EXPORT_FAIL = "student.photo.export.fail";
    //绑定超时
    public static final String BIND_TIMEOUT = "bind.timeout";
    //请输入拒绝原因
    public static final String REJECT_REASON_NOT_EMPTY = "reject.reason.not.empty";
    //该时间段已存在请假记录
    public static final String LEAVE_EXISTS = "leave.exists";
    //请设置科目规则
    public static final String SUBJECT_RULE_NOT_EXISTS = "subject.rule.not.exists";
    //请设置成绩规则
    public static final String SCORE_RULE_NOT_EXISTS = "score.rule.not.exists";
    //用户名不能为空
    public static final String LOGIN_NAME_REQUIRED = "login.name.required";
    //用户名格式错误
    public static final String LOGIN_NAME_FORMAT_ERROR = "login.name.format.error";
    //用户名在Excel表中重复
    public static final String LOGIN_NAME_DUPLICATED_IN_EXCEL = "login.duplicated.in.excel";
    //手机号地区必填
    public static final String MOBILE_REGION_REQUIRED = "mobile.region.required";
    //手机号在Excel表中重复
    public static final String MOBILE_DUPLICATED_IN_EXCEL = "mobile.duplicated.in.excel";
    //手机号位数不正确
    public static final String MOBILE_LENGTH_INVALID = "mobile.length.invalid";
    //用户姓名不能爲空
    public static final String USER_NAME_REQUIRED = "user.name.required";
    //用户姓名最长输入1-20个字
    public static final String USER_NAME_LENGTH_INVALID = "user.name.length.invalid";
    //用户组不能爲空
    public static final String USER_GROUP_REQUIRED = "user.group.required";
    //請输入有效的用户组
    public static final String USER_GROUP_NOT_EXIST = "user.group.not.exist";
    //用户编号在Excel表中重复
    public static final String USER_NUMBER_DUPLICATED_IN_EXCEL = "user.number.duplicated.in.excel";
    //职务不能为空
    public static final String USER_POSITION_REQUIRED = "user.position.required";
    //性别不能为空
    public static final String USER_GENDER_REQUIRED = "user.gender.required";
    //請输入有效的用户职务
    public static final String USER_POSITION_FORMAT_ERROR = "user.position.format.error";
    //手機號已绑定其他用户
    public static final String USER_PHONE_BOUND_ERROR = "user.phone.bound.error";
    //手机号已绑定的用户名和当前输入的不一致
    public static final String USER_PHONE_BOUND_CURRENT_ERROR = "user.phone.bound.current.error";
    //用户编号已存在
    public static final String USER_NUMBER_EXISTS = "user.number.exists";
    //用户编号不能为空
    public static final String USER_NUMBER_REQUIRED = "user.number.required";
    //weChat关联错误
    public static final String WECHAT_BIND_ERROR = "wechat.bind.error";
    //你在当前学校已绑定其他学生
    public static final String WECHAT_BIND_STUDENT_EXISTS = "wechat.bind.student.exists";
    //X月X日、X月X日 不是工作日，无需提交请假
    public static final String LEAVE_CONTAINS_NON_WORKING_DAYS = "leave.contains.non.working.days";
    //出生地格式錯誤，請输入有效地点
    public static final String BIRTH_PLACE_FORMAT_ERROR = "birth.place.format.error";
    //證件發出地點格式錯誤，請输入有效地点
    public static final String ID_ISSUE_PLACE_FORMAT_ERROR = "id.issue.place.format.error";
    //證件發出日期格式錯誤，無法解析
    public static final String ID_ISSUE_DATE_FORMAT_ERROR = "id.issue.date.format.error";
    //證件有效日期格式錯誤，無法解析
    public static final String ID_VALID_DATE_FORMAT_ERROR = "id.valid.date.format.error";
    //回鄉證編號格式错误，最长11位
    public static final String RE_ENTRY_PERMIT_NO_LENGTH_ERROR = "re.entry.permit.no.length.error";
    //逗留許可類型格式错误：請输入有效类型
    public static final String STAY_TYPE_FORMAT_ERROR = "stay.type.format.error";
    //逗留許可發出日期格式錯誤，無法解析
    public static final String STAY_ISSUE_DATE_FORMAT_ERROR = "stay.issue.date.format.error";
    //逗留許可有效日期格式錯誤，無法解析
    public static final String STAY_VALID_DATE_FORMAT_ERROR = "stay.valid.date.format.error";
    //国籍格式错误：請输入有效国籍
    public static final String NATIONALITY_FORMAT_ERROR = "nationality.format.error";
    //常用住址-地区格式錯誤，請输入有效地点
    public static final String PERMANENT_ADDRESS_FORMAT_ERROR = "permanent.address.format.error";
    //夜間留宿住址-地區格式錯誤，請输入有效地点
    public static final String NIGHT_ADDRESS_FORMAT_ERROR = "night.address.format.error";
    //監護人和学生关系格式错误：請输入有效值
    public static final String GUARDIAN_RELATION_FORMAT_ERROR = "guardian.relation.format.error";
    //監護人住址-地区格式錯誤，請输入有效地点
    public static final String GUARDIAN_ADDRESS_FORMAT_ERROR = "guardian.address.format.error";
    //與監護人同住格式錯誤，請输入有效值
    public static final String LIVE_WITH_GUARDIAN_FORMAT_ERROR = "live.with.guardian.format.error";
    //緊急聯絡人與學生關係格式错误：請输入有效值
    public static final String EMERGENCY_RELATION_FORMAT_ERROR = "emergency.relation.format.error";
    //緊急聯絡人住址-地區格式錯誤，請输入有效地点
    public static final String EMERGENCY_ADDRESS_FORMAT_ERROR = "emergency.address.format.error";
    //所属学年不能爲空
    public static final String SCHOOL_YEAR_REQUIRED = "school.year.required";
    // 学年格式错误
    public static final String SCHOOL_YEAR_FORMAT_ERROR = "school.year.format.error";
    // 活动名称不能为空
    public static final String ACTIVITY_NAME_REQUIRED = "activity.name.required";
    // 活动名称不可重复
    public static final String ACTIVITY_NAME_DUPLICATED = "activity.name.duplicated";
    // 机构名称不能为空
    public static final String ORGANIZATION_REQUIRED = "organization.required";
    //日期格式错误，正确格式：年-月-日
    public static final String SERVICE_DATE_FORMAT_ERROR = "service.date.format.error";
    // 服务时数不能为空
    public static final String SERVICE_HOURS_REQUIRED = "service.hours.required";
    // 服务时数格式错误
    public static final String SERVICE_HOURS_FORMAT_ERROR = "service.hours.format.error";
    // 請從下拉框中選擇有效值
    public static final String SELECT_VALID_VALUE = "select.valid.value";

    // 活动匹配导入相关
    //导入数据为空
    public static final String IMPORT_DATA_EMPTY = "import.data.empty";
    //添加人数超出课程名额限制
    public static final String ACTIVITY_COURSE_QUOTA_FULL = "activity.course.quota.full";
    //学生不存在
    public static final String STUDENT_NOT_FOUND = "student.not.found";

    //保存数据失败
    public static final String SAVE_ERROR = "save.error";

    //学生已经含有课程信息
    public static final String ACTIVITY_STUDENT_RECORD_ERROR = "activity.student.record.error";
    //余暇活動評級未設置
    public static final String LEISURE_ACTIVITIES_RATING_NOT_SET = "leisure.activities.rating.not.set";
    //课程名称不能为空
    public static final String COURSE_NAME_REQUIRED = "course.name.required";
    //课程不存在
    public static final String COURSE_NAME_NOT_FOUND = "course.name.not.found";
    // 出席次数不能为空
    public static final String ATTEND_COUNT_REQUIRED = "attend.count.required";
    // 出席次数必须为正整数
    public static final String ATTEND_COUNT_MUST_POSITIVE_INTEGER = "attend.count.must.positive.integer";
    // 课节表现分数不能为空
    public static final String LESSON_SCORE_REQUIRED = "lesson.score.required";
    // 课节表现分数必须为非负整数
    public static final String LESSON_SCORE_MUST_NON_NEGATIVE_INTEGER = "lesson.score.must.non.negative.integer";
    // 授课教师不可为空
    public static final String TEACHER_REQUIRED = "teacher.required";
    // 上课地点不可为空
    public static final String CLASSROOM_REQUIRED = "classroom.required";
    // 课程名额不可为空
    public static final String COURSE_QUOTA_REQUIRED = "course.quota.required";
    // 课程名额必须为正整数
    public static final String COURSE_QUOTA_MUST_POSITIVE_INTEGER = "course.quota.must.positive.integer";
    // 课程名额必须为数字
    public static final String COURSE_QUOTA_MUST_NUMBER = "course.quota.must.number";
    // 课程次数不可为空
    public static final String COURSE_TIMES_NOT_EMPTY = "course.times.not.empty";
    // 课程次数必须为正整数
    public static final String COURSE_TIMES_MUST_POSITIVE_INTEGER = "course.times.must.positive.integer";
    // 课程次数必须为数字
    public static final String COURSE_TIMES_MUST_NUMBER = "course.times.must.number";
    // 课程时间不可为空
    public static final String COURSE_TIME_NOT_EMPTY = "course.time.not.empty";
    // 课程开始时间不可为空
    public static final String COURSE_START_TIME_NOT_EMPTY = "course.start.time.not.empty";
    // 课程结束时间不可为空
    public static final String COURSE_END_TIME_NOT_EMPTY = "course.end.time.not.empty";
    // 课程不存在
    public static final String COURSE_NOT_FOUND = "course.not.found";
    // 课程数量小于志愿数,无法发布
    public static final String COURSE_QUOTA_LESS_THAN_VOLUNTEER_NUM = "course.quota.less.than.volunteer.num";
    // 课程名额总数不可小于学部下学生总人数（%s人）
    public static final String COURSE_QUOTA_LESS_THAN_STUDENT_NUM = "course.quota.less.than.student.num";
    // 课程名称重复，请修改后再保存
    public static final String COURSE_NAME_DUPLICATED = "course.name.duplicated";
    // 课程名称不可重复
    public static final String COURSE_NAME_DUPLICATED_IN_ACTIVITY = "course.name.duplicated.in.activity";
    // 复制成功，其中%s个课程复制失败，请前往课程管理模块进行管理
    public static final String COURSE_COPY_SUCCESS_WITH_ERROR = "course.copy.success.with.error";
    // 出席次数不可超过课程总次数
    public static final String ATTEND_COUNT_OUT_OF_COURSE_RANGE = "attend.count.out.of.course.range";
    // 课节表现分数不可超过课程参与占比
    public static final String LESSON_SCORE_EXCEED_CLASS_PARTICIPATION_RATIO = "lesson.score.exceed.class.participation.ratio";

    //活动不存在
    public static final String ACTIVITY_NOT_EXISTS = "activity.not.exists";
    //活动尚未公布，无法发起二次报名
    public static final String ACTIVITY_NOT_PUBLISHED_CANNOT_SECOND_APPLY = "activity.not.published.cannot.second.apply";
    //二次报名结束时间不能早于当前时间
    public static final String SECOND_APPLY_END_TIME_BEFORE_NOW = "second.apply.end.time.before.now";
    //活动ID不能为空
    public static final String ACTIVITY_ID_REQUIRED = "activity.id.required";

    //Excel里面学生编号重复
    public static final String EXCEL_STUDENT_NO_DUPLICATE = "excel.student.no.duplicate";

    //课程信息不存在
    public static final String COURSE_NOT_EXISTS = "course.not.exists";

    //查询参数不能为空
    public static final String QUERY_PARAM_EMPTY = "query.param.empty";

    //课程ID不能为空
    public static final String COURSE_ID_REQUIRED = "course.id.required";

    //已发布的活动不能移除
    public static final String ACTIVITY_PUBLISHED_CANNOT_REMOVE = "activity.published.cannot.remove";

    //活动未截止，不能发布结果
    public static final String ACTIVITY_NOT_ENDED_CANNOT_PUBLISH = "activity.not.ended.cannot.publish";

    //活动未结束，不能发起二次报名
    public static final String ACTIVITY_NOT_ENDED_CANNOT_START_SECOND_APPLY = "activity.not.ended.cannot.start.second.apply";

    //活动已经发布过
    public static final String ACTIVITY_HAS_PUBLISH = "activity.has.publish";

    // 报名相关
    //活动不在进行中或二次报名时间范围内
    public static final String ACTIVITY_NOT_IN_PROGRESS_OR_SECOND_APPLY_TIME = "activity.not.in.progress.or.second.apply.time";
    //学生已匹配，不能报名
    public static final String STUDENT_ALREADY_MATCHED = "student.already.matched";
    //学生已报名，不能重复报名
    public static final String STUDENT_ALREADY_APPLIED = "student.already.applied";
    //志愿列表不能为空
    public static final String VOLUNTEER_LIST_NOT_EMPTY = "volunteer.list.not.empty";
    //志愿数量超出限制
    public static final String VOLUNTEER_NUM_EXCEED_LIMIT = "volunteer.num.exceed.limit";
    //课程不存在
    public static final String COURSE_NOT_EXISTS_IN_ACTIVITY = "course.not.exists.in.activity";
    //课程已满员
    public static final String COURSE_QUOTA_FULL = "course.quota.full";
    //报名成功
    public static final String APPLY_SUCCESS = "apply.success";

    // 志愿数量必须等于活动设定数量
    public static final String VOLUNTEER_NUM_MUST_EQUAL_ACTIVITY_SETTING = "volunteer.num.must.equal.activity.setting";
    // 志愿不能重复
    public static final String VOLUNTEER_DUPLICATE_NOT_ALLOWED = "volunteer.duplicate.not.allowed";
    // 活动下没有未录满的课程
    public static final String ACTIVITY_NO_COURSE = "activity.no.course";
    //學生學部和活動不匹配
    public static final String STUDENT_DEPARTMENT_NOT_MATCH = "student.department.not.match";
    //學生学年不匹配
    public static final String STUDENT_YEAR_NOT_MATCH = "student.school.year.not.match";

    // 只能删除没有成员的部门, 需要先删除部门下的员工，再删除该部门!
    public static final String CANNOT_DELETE_DEPARTMENT_WITH_MEMBERS = "cannot.delete.department.with.members";
    // 名称最长20字
    public static final String NAME_MAX_LENGTH = "name.max.length";
    // 部门名称已存在
    public static final String DEPARTMENT_NAME_EXISTS = "department.name.exists";
    // 部门不存在
    public static final String DEPARTMENT_NOT_EXISTS = "department.not.exists";
    // 部门名称不可为空
    public static final String DEPARTMENT_NAME_NOT_EMPTY = "department.name.not.empty";
    //审批名称已存在
    public static final String ACT_PROCESS_TEMPLATE_NAME_EXISTS = "act.process.template.name.exists";
    //流程定义不存在
    public static final String PROCESS_DEFINITION_NOT_EXISTS = "process.definition.not.exists";
    //任务不存在或已处理
    public static final String TASK_NOT_EXISTS_OR_PROCESSED = "task.not.exists.or.processed";
    //节点信息获取失败
    public static final String NODE_INFO_GET_FAILED = "node.info.get.failed";
    //该任务不属于你
    public static final String USER_NOT_IN_APPROVER_LIST = "user.not.in.approver.list";

    // 處理內容不能為空
    public static final String PROCESS_CONTENT_NOT_EMPTY = "process.content.not.empty";
    // 備註內容不能為空
    public static final String REMARK_CONTENT_NOT_EMPTY = "remark.content.not.empty";
    // 存在該症狀，請輸入“是”
    public static final String EXISTING_SYMPTOM_PLEASE_INPUT_YES = "existing.symptom.please.input.yes";
    // 格式錯誤，只允許輸入數字
    public static final String FORMAT_ERROR_ONLY_ALLOW_NUMERIC_INPUT = "format.error.only.allow.numeric.input";
    //该年级下，该科目已存在
    public static final String SUBJECT_EXISTS_IN_GRADE = "subject.exists.in.grade";
    //该科目已录入相关数据，无法删除
    public static final String SUBJECT_HAS_DATA = "subject.has.data";

    // 科目单位格式错误
    public static final String SUBJECT_UNIT_FORMAT_ERROR = "subject.unit.format.error";
    // 原因不能為空
    public static final String REWARD_REASON_REQUIRED = "reward.reason.required";
    // 次数不能為空
    public static final String REWARD_FREQUENCY_REQUIRED = "reward.frequency.required";
    // 格式錯誤，只允許輸入：%s
    public static final String REWARD_TYPE_FORMAT_ERROR = "reward.type.format.error";
    // 請輸入欠作業次數，沒有則輸入0
    public static final String CONVENTIONAL_PERFORMANCE_MISSING_HOMEWORK_REQUIRED = "conventional.performance.missing.homework.required";
    // 請輸入欠課本次數，沒有則輸入0
    public static final String CONVENTIONAL_PERFORMANCE_MISSING_TEXTBOOK_REQUIRED = "conventional.performance.missing.textbook.required";
    // 請輸入上课违规次數，沒有則輸入0
    public static final String CONVENTIONAL_PERFORMANCE_CLASS_VIOLATION_REQUIRED = "conventional.performance.class.violation.required";
    // 請輸入仪表不符次數，沒有則輸入0
    public static final String CONVENTIONAL_PERFORMANCE_UNIFORM_NON_COMPLIANCE_REQUIRED = "conventional.performance.uniform.non.compliance.required";
    // 请输入欠回条次數，沒有則輸入0
    public static final String CONVENTIONAL_PERFORMANCE_MISSING_RETURN_STICKER_REQUIRED = "conventional.performance.missing.return.sticker.required";
    //班级
    public static final String EXPORT_CLASS_NAME = "export.class.name";
    //班内号
    public static final String EXPORT_SEAT_NO = "export.seat.no";
    //学生编号
    public static final String EXPORT_STUDENT_NO = "export.student.no";
    //报名阶段
    public static final String EXPORT_REGISTRATION_STAGE = "export.registration.stage";
    //报名时间
    public static final String EXPORT_REGISTRATION_TIME = "export.registration.time";

    // 没有匹配企微信息
    public static final String NO_MATCHING_WECHAT_INFO = "no.matching.wechat.info";
    // 企微信息已被匹配
    public static final String WECHAT_INFO_ALREADY_MATCHED = "wechat.info.already.matched";

    // 学生微信不能为空
    public static final String STUDENT_WECHAT_REQUIRED = "student.wechat.required";
    // 学生微信家长1手机号不能为空
    public static final String STUDENT_WECHAT_PARENT_PHONE_REQUIRED = "student.wechat.parent.phone.required";
    // 学生微信家长手机号重复
    public static final String STUDENT_WECHAT_PARENT_PHONE_REPEAT = "student.wechat.parent.phone.repeat";

    // 本学期已提交
    public static final String HEALTH_DECLARE_EACH_YEAR_ONLY_ONCE = "health.declare.each.year.only.once";

    // 该平时成绩类型名称已存在
    public static final String STUDENT_USUALLY_TYPE_NAME_EXISTS = "student.usually.type.name.exists";
    // 该平时成绩类型已被使用，不可删除
    public static final String STUDENT_USUALLY_TYPE_USED = "student.usually.type.used";

    // 校外活动范畴名称已存在
    public static final String EXTERNAL_COMPETITION_CATEGORY_NAME_EXISTS = "external.competition.category.name.exists";
    // 校外活动奖项评级名称已存在
    public static final String EXTERNAL_COMPETITION_AWARDS_NAME_EXISTS = "external.competition.awards.name.exists";
    // 校外活动导出规则已存在
    public static final String EXTERNAL_COMPETITION_EXPORT_RULE_EXISTS = "external.competition.export.rule.exists";
    // 校外活动范畴已被使用，不可删除
    public static final String EXTERNAL_COMPETITION_CATEGORY_USED = "external.competition.category.used";
    // 校外活动奖项评级已被使用，不可删除
    public static final String EXTERNAL_COMPETITION_AWARDS_USED = "external.competition.awards.used";

    //%次
    public static final String DEMERIT_TIMES = "demerit.times";
    // 校外活动组别名称已存在
    public static final String EXTERNAL_COMPETITION_GROUP_NAME_EXISTS = "external.competition.group.name.exists";
    // 奖励信息不存在
    public static final String USER_REWARD_NOT_EXIST = "user.reward.not.exist";
    // 紧急联系人名字不能为空
    public static final String EMERGENCY_CONTACT_REQUIRED= "emergency.contact.required";
    // 没有可导出的信息
    public static final String NO_INFORMATION_AVAILABLE_TO_EXPORT= "no.information.available.to.export";

    // 欠作業備註不能填寫
    public static final String CONVENTIONAL_PERFORMANCE_MISSING_HOMEWORK_REMARK_CANNOT_BE_USED = "conventional.performance.missing.homework.remark.cannot.be.used";
    // 欠課本備註不能填寫
    public static final String CONVENTIONAL_PERFORMANCE_MISSING_TEXTBOOK_REMARK_CANNOT_BE_USED = "conventional.performance.missing.textbook.remark.cannot.be.used";
    // 上課違規備註不能填寫
    public static final String CONVENTIONAL_PERFORMANCE_CLASS_VIOLATION_REMARK_CANNOT_BE_USED = "conventional.performance.class.violation.remark.cannot.be.used";
    // 儀表不符備註不能填寫
    public static final String CONVENTIONAL_PERFORMANCE_UNIFORM_NON_COMPLIANCE_REMARK_CANNOT_BE_USED = "conventional.performance.uniform.non.compliance.remark.cannot.be.used";
    // 欠回条備註不能填寫
    public static final String CONVENTIONAL_PERFORMANCE_MISSING_RETURN_STICKER_REMARK_CANNOT_BE_USED = "conventional.performance.missing.return.sticker.remark.cannot.be.used";

    // 级组名不可为空
    public static final String GROUP_NAME_REQUIRED = "group.name.required";
    // 权重不可为空
    public static final String WEIGHT_REQUIRED = "weight.required";
    // 级组名不存在
    public static final String GROUP_NAME_NOT_EXIST = "group.name.not.exist";
    // 科目名不存在
    public static final String SUBJECT_NAME_NOT_EXIST = "subject.name.not.exist";
    // 类型不存在
    public static final String TYPE_NOT_EXIST = "type.not.exist";
    // %s级组，权重错误
    public static final String GROUP_WEIGHT_ERROR = "group.weight.error";
    // %s科目，权重错误
    public static final String SUBJECT_WEIGHT_ERROR = "subject.weight.error";
    // 权重只能是数字，且范围为[0,100]
    public static final String WEIGHT_ERROR = "weight.error";
}
