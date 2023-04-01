package com.sesame.dbhelp.util;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AESHelp {


    public static AES aes = null;

    static {
        try {
            aes = new AES(Mode.CTS, Padding.PKCS5Padding,
                    // 密钥，可以自定义
                    "T9HOF7pgqKkkFDHc".getBytes(),
                    // iv加盐，按照实际需求添加
                    "FbrRHExNNbhmvVgh".getBytes()
            );
        } catch (Exception e) {
            log.error("AES 加载失败 {}", e.getMessage());
        }
    }


}
