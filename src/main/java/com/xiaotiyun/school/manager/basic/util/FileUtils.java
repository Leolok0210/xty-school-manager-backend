package com.xiaotiyun.school.manager.basic.util;


import org.apache.commons.lang3.StringUtils;

public class FileUtils {

    /*
     * Java文件操作 获取文件扩展名
     */
    public static String getFileSuffix(String filename) {
        if (StringUtils.isBlank(filename) || filename.length() <= 3) {
            throw new NullPointerException("filename is null");
        } else {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /*
     * Java文件操作 获取不带扩展名的文件名
     */
    public static String getFileNameNoSuffix(String filename) {
        if (StringUtils.isBlank(filename) || filename.length() <= 3) {
            throw new NullPointerException("filename is null");
        }
        int dot = filename.lastIndexOf('.');
        if ((dot > -1) && (dot < (filename.length()))) {
            return filename.substring(0, dot);
        }
        return filename;
    }
}
