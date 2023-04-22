package com.sesame.dbhelp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "code.data")
public class BaseConfig {

    /**
     * 基本目录
     * code.data.base-path=${user.home}
     */
    private String basePath = System.getProperty("user.home")+"/ars/dbhelp";

    /**
     * 主题目录名称
     */
    private String themeDicName = "themeList";

    private boolean gitEnable = false;
    private String gitUrl = "https://gitee.com/resources1/themeList.git";
    private String gitUser;
    private String gitPwd;
    private String branch;


}
