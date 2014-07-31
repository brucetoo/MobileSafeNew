package com.itheima.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Bruce
 * Data 2014/7/21
 * Time 17:45.
 * MD5加密
 */
public class MD5Utils {
    public static String md5Password(String password) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("md5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] bytes = digest.digest(password.getBytes());
        StringBuffer buffer = new StringBuffer();
        for (byte b : bytes) {
            int number = b & 0xff;//加盐
            String hex = Integer.toHexString(number);
            if (hex.length() == 1) {
                buffer.append("0");
            }
            buffer.append(hex);
        }
        //md5加密后的值
        return buffer.toString();
    }
}
