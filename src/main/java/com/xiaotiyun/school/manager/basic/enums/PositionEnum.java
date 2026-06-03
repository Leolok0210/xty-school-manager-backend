package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public enum PositionEnum {
    PART_TIME_TEACHER(1, "兼职教師", "Part-time Teacher", "Professor Part-time", 1),
    PRESCHOOL_TEACHER(2, "小幼教師", "Kindergarten Teacher", "Professor de Jardim de Infância", 1),
    PRIMARY_TEACHER(3, "小學教師", "Primary Teacher", "Professor de Escola Primária", 1),
    MIDDLE_TEACHER(4, "中學教師", "Secondary Teacher", "Professor de Escola Secundária", 1),
    PART_TIME_STAFF(5, "兼职職員", "Part-time Staff", "Part-time", 2),
    PRESCHOOL_STAFF(6, "小幼職員", "Kindergarten Staff", "Funcionário de Jardim de Infância", 2),
    PRIMARY_STAFF(7, "小學職員", "Primary Staff", "Funcionário de Escola Primária", 2),
    SECONDARY_STAFF(8, "中學職員", "Secondary Staff", "Funcionário de Escola Secundária", 2),
    FULL_TIME_STAFF(9, "專職人員", "Professional", "Profissional", 3),
    FACILITY_WORKER(10, "工友", "Worker", "Trabalhador", 4);

    private int code;
    @Getter
    private final String zhTwValue;
    @Getter
    private final String enValue;
    @Getter
    private final String ptValue;
    @Getter
    private final Integer userType;

    PositionEnum(int code, String zhTwValue, String enValue, String ptValue, Integer userType) {
        this.code = code;
        this.zhTwValue = zhTwValue;
        this.enValue = enValue;
        this.ptValue = ptValue;
        this.userType = userType;
    }

    public static String getValue(int code, SchoolLanguageEnum language) {
        for (PositionEnum position : values()) {
            if (position.code == code) {
                switch (language) {
                    case EN_US:
                        return position.enValue;
                    case PT_PT:
                        return position.ptValue;
                    default:
                        return position.zhTwValue;
                }
            }
        }
        return "";
    }

    public static List<String> allValues(SchoolLanguageEnum language) {
        List<String> list = new ArrayList<>();
        for (PositionEnum position : values()) {
            switch (language) {
                case EN_US:
                    list.add(position.enValue);
                    break;
                case PT_PT:
                    list.add(position.ptValue);
                    break;
                default:
                    list.add(position.zhTwValue);
                    break;
            }
        }
        return list;
    }

    public static Integer getCode(String value, SchoolLanguageEnum language) {
        for (PositionEnum ele : values()) {
            switch (language) {
                case EN_US:
                    if (ele.enValue.equals(value)) return ele.code;
                    break;
                case PT_PT:
                    if (ele.ptValue.equals(value)) return ele.code;
                    break;
                default:
                    if (ele.zhTwValue.equals(value)) return ele.code;
            }
        }
        return null;
    }

    public static Integer getUserType(String value, SchoolLanguageEnum language) {
        for (PositionEnum ele : values()) {
            switch (language) {
                case EN_US:
                    if (ele.enValue.equals(value)) return ele.userType;
                    break;
                case PT_PT:
                    if (ele.ptValue.equals(value)) return ele.userType;
                    break;
                default:
                    if (ele.zhTwValue.equals(value)) return ele.userType;
            }
        }
        return null;
    }
}
