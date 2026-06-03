package com.xiaotiyun.school.manager.basic.util;

import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 实体转换工具类
 */
public class BeanConvertUtil {

    /**
     * 单个对象转换
     *
     * @param source 源对象
     * @param targetClass 目标类
     * @param <T> 目标类型
     * @return 目标对象
     */
    public static <T> T convert(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        T target;
        try {
            target = targetClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("创建目标对象失败", e);
        }
        BeanUtils.copyProperties(source, target);
        return target;
    }

    /**
     * 列表对象转换
     *
     * @param sourceList 源对象列表
     * @param targetClass 目标类
     * @param <T> 目标类型
     * @return 目标对象列表
     */
    public static <T> List<T> convertList(List<?> sourceList, Class<T> targetClass) {
        if (sourceList == null) {
            return null;
        }
        return sourceList.stream()
            .map(source -> convert(source, targetClass))
            .collect(Collectors.toList());
    }
} 