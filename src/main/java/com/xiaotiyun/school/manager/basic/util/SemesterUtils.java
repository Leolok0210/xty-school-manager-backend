package com.xiaotiyun.school.manager.basic.util;

import java.time.LocalDate;

public class SemesterUtils {

    /**
     * 获取当前学年名称
     * @return 学年名称，例如 "2024-2025"
     */
    public static String getCurrentSemesterName(LocalDate now) {
        int year = now.getMonthValue() >= 9 ? now.getYear() : now.getYear() - 1;
        return year + "-" + (year + 1);
    }

    //获取上一个学年名称
    public static String getPreviousSemesterName(LocalDate now) {
        int year = now.getMonthValue() >= 9 ? now.getYear() - 1 : now.getYear() - 2;
        return year + "-" + (year + 1);
    }
}