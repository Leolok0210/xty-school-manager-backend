package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;

public enum MiniWxAppMessageType {

    H5(1, "H5"),
    MINI_PROGRAM(2, "小程序");

    @Getter
    private final Integer code;
    @Getter
    private final String desc;

    MiniWxAppMessageType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
