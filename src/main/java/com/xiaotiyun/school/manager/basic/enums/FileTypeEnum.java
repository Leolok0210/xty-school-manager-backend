package com.xiaotiyun.school.manager.basic.enums;

public enum FileTypeEnum {
    STUDENT_IMAGE(1, "student-image"),
    STUDENT_IMAGE_ZIP(2, ""),
    SCHOOL_LOGO(3, "school-logo"),
    EXPORT(4, "export"),
    LEAVE(5, "leave"),
    INTENTION_PROOF(6, "intention-proof"),

    //pdf
    PDF(7, "pdf");

    private int type;
    private String typePath;

    private FileTypeEnum(int type, String typePath) {
        this.type = type;
        this.typePath = typePath;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypePath() {
        return typePath;
    }

    public void setTypePath(String typePath) {
        this.typePath = typePath;
    }

    public static String getValue(int type) {
        for (FileTypeEnum ele : values()) {
            if (ele.getType() == type) return ele.getTypePath();
        }
        return null;
    }

    public static FileTypeEnum getEnum(int type) {
        for (FileTypeEnum ele : values()) {
            if (ele.getType() == (type)) return ele;
        }
        return null;
    }
}
