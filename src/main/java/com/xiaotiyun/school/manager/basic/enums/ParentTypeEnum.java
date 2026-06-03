package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;

@Getter
public enum ParentTypeEnum {

    FATHER(1, "父親","Father","Pai"),
    MOTHER(2, "母親","Mother","Mãe"),
    GRAND_FATHER(4, "爺爺","Paternal Grandfather","Avô paterno"),
    GRAND_MOTHER(4, "奶奶","Paternal Grandmother","Avó paterna"),
    UNCLE(4, "外公","Maternal Grandfather","Avô materno"),
    AUNT(4, "外婆","Maternal Grandmother","Avó materna"),
    OTHER(4, "其他","Other","Outro");

    private Integer code;

    private String zhName;

    private String enName;

    private String ptName;

    ParentTypeEnum(Integer code, String zhName, String enName, String ptName) {
        this.code = code;
        this.zhName = zhName;
        this.enName = enName;
        this.ptName = ptName;
    }

    /**
     * 语言匹配名称
     *
     */
    public static ParentTypeEnum getName(String name,SchoolLanguageEnum languageEnum) {
        switch (languageEnum){
            case ZH_MO:
                for (ParentTypeEnum value : ParentTypeEnum.values()) {
                    if (value.zhName.equals(name)) {
                        return value;
                    }
                }
                break;
            case EN_US:
                for (ParentTypeEnum value : ParentTypeEnum.values()) {
                    if (value.enName.equals(name)) {
                        return value;
                    }
                }
                break;
            case PT_PT:
                for (ParentTypeEnum value : ParentTypeEnum.values()) {
                    if (value.ptName.equals(name)) {
                        return value;
                    }
                }
                break;
        }
        return null;
    }


}
