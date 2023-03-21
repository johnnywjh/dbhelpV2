package com.sesame.dbhelp.util;


import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.symmetric.SM4;

import javax.crypto.SecretKey;

public class RSAUtil {

    public final static String key = "46cace210248dc61d0a0a4b57115bbe8";

    public static String decryptStr(String str){
        SM4 sm4 = new SM4(HexUtil.decodeHex(key));
        return sm4.decryptStr(str);
    }

    public static void main(String[] args) {
        SM4 sm4 = new SM4();
        SecretKey secretKey = sm4.getSecretKey();
        byte[] encoded = secretKey.getEncoded();
        String encodeHexStr = HexUtil.encodeHexStr(encoded);
        System.out.println(encodeHexStr);
    }
}

