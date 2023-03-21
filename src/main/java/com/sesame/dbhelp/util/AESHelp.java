package com.sesame.dbhelp.util;

import kim.sesame.common.encryption.AESUtil;
import kim.sesame.common.encryption.Base64Util;
import kim.sesame.common.encryption.MD5;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AESHelp {
    static String appsecret = "";

    static {
        try {
            appsecret = MD5.md5("dbhelp.database.user.pwd");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加密
     */
    public static String encryption(String txt) {
        try {
            byte[] bytes = AESUtil.encryptAndDecrypt(txt.getBytes("UTF-8"), appsecret, 1);
            return Base64Util.encode(bytes);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 解密
     */
    public static String deciphering(String txt) {
        try {
            byte[] decode = AESUtil.encryptAndDecrypt(Base64Util.decode(txt), appsecret, 2);
            return new String(decode, "UTF-8");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
