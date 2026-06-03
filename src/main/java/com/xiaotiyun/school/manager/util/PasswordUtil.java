package com.xiaotiyun.school.manager.util;

import cn.dev33.satoken.secure.BCrypt;
import org.apache.commons.codec.digest.DigestUtils;

public class PasswordUtil {
    private static final String SALT_PREFIX = "salt_mBDwFRq_";

    /**
     * 对密码进行加密
     * 1. 先进行SHA-256加盐加密
     * 2. 再进行BCrypt加密
     *
     * @param plainPassword 明文密码
     * @return 加密后的密码
     */
    public static String encryptPassword(String plainPassword) {
        // 1. SHA-256加盐
        String saltedPassword = SALT_PREFIX + plainPassword;
        String sha256Password = DigestUtils.sha256Hex(saltedPassword);

        // 2. BCrypt加密
        return BCrypt.hashpw(sha256Password, BCrypt.gensalt());
    }


    public static void main(String[] args) {
        String saltedPassword = SALT_PREFIX + "741852";
        String sha256Password = DigestUtils.sha256Hex(saltedPassword);
        System.out.println(sha256Password);
        System.out.println(PasswordUtil.encryptPassword("741852"));
    }
} 