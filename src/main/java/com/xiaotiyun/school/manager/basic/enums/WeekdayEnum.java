package com.xiaotiyun.school.manager.basic.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 多语言週枚举类，支持中文、英文週与数字 1 - 7 的相互转换
 */
public enum WeekdayEnum {
    MONDAY(1, "週一", "Monday"),
    TUESDAY(2, "週二", "Tuesday"),
    WEDNESDAY(3, "週三", "Wednesday"),
    THURSDAY(4, "週四", "Thursday"),
    FRIDAY(5, "週五", "Friday"),
    SATURDAY(6, "週六", "Saturday"),
    SUNDAY(7, "週日", "Sunday");

    private final int number;
    private final String chineseName;
    private final String englishName;

    private static final Map<Integer, WeekdayEnum> NUMBER_MAP = new HashMap<>();
    private static final Map<String, WeekdayEnum> CHINESE_MAP = new HashMap<>();
    private static final Map<String, WeekdayEnum> ENGLISH_MAP = new HashMap<>();

    static {
        for (WeekdayEnum weekday : values()) {
            NUMBER_MAP.put(weekday.number, weekday);
            CHINESE_MAP.put(weekday.chineseName, weekday);
            ENGLISH_MAP.put(weekday.englishName, weekday);
        }
    }

    WeekdayEnum(int number, String chineseName, String englishName) {
        this.number = number;
        this.chineseName = chineseName;
        this.englishName = englishName;
    }

    /**
     * 根据数字获取对应的週枚举
     * @param number 1 - 7 的数字
     * @return 对应的週枚举，若未找到则返回 null
     */
    public static WeekdayEnum getByNumber(int number) {
        return NUMBER_MAP.get(number);
    }

    /**
     * 根据中文名称获取对应的週枚举
     * @param chineseName 中文週名称
     * @return 对应的週枚举，若未找到则返回 null
     */
    public static WeekdayEnum getByChineseName(String chineseName) {
        return CHINESE_MAP.get(chineseName);
    }

    /**
     * 根据英文名称获取对应的週枚举
     * @param englishName 英文週名称
     * @return 对应的週枚举，若未找到则返回 null
     */
    public static WeekdayEnum getByEnglishName(String englishName) {
        return ENGLISH_MAP.get(englishName);
    }

    /**
     * 获取週对应的数字
     * @return 1 - 7 的数字
     */
    public int getNumber() {
        return number;
    }

    /**
     * 获取週的中文名称
     * @return 中文週名称
     */
    public String getChineseName() {
        return chineseName;
    }

    /**
     * 获取週的英文名称
     * @return 英文週名称
     */
    public String getEnglishName() {
        return englishName;
    }
}