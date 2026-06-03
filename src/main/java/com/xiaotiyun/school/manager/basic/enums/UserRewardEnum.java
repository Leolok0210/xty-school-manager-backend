package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;

@Getter
public enum UserRewardEnum {
    //0-上课违规，1-欠作业，2-仪表不符，3-迟到次数，4-欠课本  需要实现国际化 英文和葡萄牙
    CONVENTIONAL_PERFORMANCE(0, "上課違規", "Conventional Performance","violação de presença"),
    MISSING_HOMEWORK(1, "欠作業", "Missing Homework","trabalho pendente"),
    MISCONDUCT(2, "儀表不符", "Misconduct","desacato"),
    LATE_TIMES(3, "遲到次數", "Late Times","tempo de atraso"),
    LACK_OF_BOOK(4, "欠課本", "Lack of Book","falta de livro"),
    //缺席
    ABSENT(5, "缺席", "Absent","ausente"),
    MISSED_CALLBACK(6, "欠回條", "Missed Callback","callback pendente");

    private Integer code;
    private String name;
    private String nameEn;
    private String namePt;
    UserRewardEnum(Integer code, String name, String nameEn, String namePt) {
        this.code = code;
        this.name = name;
        this.nameEn = nameEn;
        this.namePt = namePt;
    }
    public static String getName(Integer code,SchoolLanguageEnum currentLanguage) {
        for (UserRewardEnum value : UserRewardEnum.values()) {
            if (value.code.equals(code)) {
                switch (currentLanguage){
                    case EN_US:
                        return value.nameEn;
                    case PT_PT:
                        return value.namePt;
                    default:
                        return value.name;
                }
            }
        }
        return null;
    }
}
