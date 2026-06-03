package com.xiaotiyun.school.manager.basic.util;

import com.xiaotiyun.school.manager.basic.enums.SchoolLanguageEnum;

public class VolunteerNumberUtil {

    /**
     * 根据数字获取对应志愿的国际化显示
     *
     * @param number   志愿序号
     * @param language 语言类型
     * @return 对应语言的志愿文本
     */
    public static String getVolunteerNumber(int number, SchoolLanguageEnum language) {
        if (number <= 0) {
            return "";
        }

        switch (language) {
            case EN_US:
                return getEnglishVolunteerNumber(number);
            case PT_PT:
                return getPortugueseVolunteerNumber(number);
            default:
                return "第" + convertNumberToChinese(number) + "志願";
        }
    }

    /**
     * 将数字转换为中文形式（一、二、三等）
     *
     * @param number 要转换的数字
     * @return 中文数字表示
     */
    private static String convertNumberToChinese(int number) {
        if (number <= 0) {
            return "";
        }

        // 只处理100以下的情况，100以上无需处理
        if (number >= 100) {
            return String.valueOf(number);
        }

        String[] chineseNumbers = {
                "", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十",
                "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十"
        };

        if (number <= 20) {
            return chineseNumbers[number];
        }

        if (number < 100) {
            int tens = number / 10;
            int ones = number % 10;

            if (ones == 0) {
                if (tens == 2) {
                    return "二十";
                } else {
                    return chineseNumbers[tens] + "十";
                }
            } else {
                if (tens == 2) {
                    return "二十" + chineseNumbers[ones];
                } else {
                    return chineseNumbers[tens] + "十" + chineseNumbers[ones];
                }
            }
        }

        // 对于更大的数字，使用简单表示法
        return String.valueOf(number);
    }

    /**
     * 获取英语志愿表达
     */
    private static String getEnglishVolunteerNumber(int number) {
        return getEnglishOrdinal(number) + " Preference";
    }

    /**
     * 获取葡萄牙语志愿表达
     */
    private static String getPortugueseVolunteerNumber(int number) {
        return getPortugueseOrdinal(number) + " Preferência";
    }

    /**
     * 获取英语序数词（支持任意数字）
     */
    private static String getEnglishOrdinal(int number) {
        String[] specialCases = {
                "", "First", "Second", "Third", "Fourth", "Fifth",
                "Sixth", "Seventh", "Eighth", "Ninth", "Tenth",
                "Eleventh", "Twelfth", "Thirteenth", "Fourteenth", "Fifteenth",
                "Sixteenth", "Seventeenth", "Eighteenth", "Nineteenth", "Twentieth"
        };

        if (number < specialCases.length) {
            return specialCases[number];
        }

        // 处理21及以上的情况，只处理到99，100以上使用简单后缀
        if (number < 100) {
            String[] tens = {
                    "", "", "Twenty", "Thirty", "Forty", "Fifty",
                    "Sixty", "Seventy", "Eighty", "Ninety"
            };

            int tensDigit = number / 10;
            int onesDigit = number % 10;

            if (tensDigit < tens.length) {
                if (onesDigit == 0) {
                    return tens[tensDigit] + "th";
                } else {
                    // 特殊情况：21, 22, 23等
                    if (tensDigit == 2) {
                        switch (onesDigit) {
                            case 1:
                                return "Twenty-first";
                            case 2:
                                return "Twenty-second";
                            case 3:
                                return "Twenty-third";
                            default:
                                return tens[tensDigit] + "-" + getEnglishOrdinal(onesDigit).toLowerCase();
                        }
                    } else if (tensDigit == 3) {
                        switch (onesDigit) {
                            case 1:
                                return "Thirty-first";
                            case 2:
                                return "Thirty-second";
                            case 3:
                                return "Thirty-third";
                            default:
                                return tens[tensDigit] + "-" + getEnglishOrdinal(onesDigit).toLowerCase();
                        }
                    } else {
                        return tens[tensDigit] + "-" + getEnglishOrdinal(onesDigit).toLowerCase();
                    }
                }
            }
        }

        // 对于更大的数字或者未处理的情况，使用简单后缀
        String numberStr = String.valueOf(number);
        if (numberStr.endsWith("1") && !numberStr.endsWith("11")) {
            return numberStr + "st";
        } else if (numberStr.endsWith("2") && !numberStr.endsWith("12")) {
            return numberStr + "nd";
        } else if (numberStr.endsWith("3") && !numberStr.endsWith("13")) {
            return numberStr + "rd";
        } else {
            return numberStr + "th";
        }
    }

    /**
     * 获取葡萄牙语序数词（支持任意数字）
     */
    private static String getPortugueseOrdinal(int number) {
        String[] specialCases = {
                "", "Primeira", "Segunda", "Terceira", "Quarta", "Quinta",
                "Sexta", "Sétima", "Oitava", "Nona", "Décima",
                "Décima Primeira", "Décima Segunda"
        };

        if (number < specialCases.length) {
            return specialCases[number];
        }

        // 对于更大的数字，使用简单表示法
        return number + "ª";
    }
}
