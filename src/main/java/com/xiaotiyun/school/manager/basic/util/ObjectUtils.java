package com.xiaotiyun.school.manager.basic.util;

import java.lang.reflect.Field;

public class ObjectUtils {
    public static boolean areAllFieldsEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        // 获取对象的类
        Class<?> objClass = obj.getClass();
        // 遍历对象的所有属性
        for (Field field : objClass.getDeclaredFields()) {
            field.setAccessible(true); // 允许访问私有属性
            try {
                Object value = field.get(obj);
                // 检查属性是否为null或空字符串
                if (value != null && !(value instanceof String && ((String) value).isEmpty())) {
                    return false; // 一旦发现非空属性，立即返回false
                }
            } catch (IllegalAccessException e) {
                // 处理属性访问异常
                e.printStackTrace();
                // 根据实际需求，你可以选择抛出异常或进行其他处理
                // 此处为简化处理，我们仅打印异常信息并继续检查下一个属性
            }
        }
        // 若所有属性均为空，则返回true
        return true;
    }
}
