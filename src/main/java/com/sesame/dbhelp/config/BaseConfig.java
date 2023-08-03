package com.sesame.dbhelp.config;

import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "code.data")
public class BaseConfig  implements InitializingBean {

    /**
     * 基本目录
     * code.data.base-path=${user.home}
     */
    private String basePath = System.getProperty("user.home")+"/ars/dbhelp";

    /**
     * 主题目录名称
     */
    private String themeDicName = "themeList";

    /**
     * 布尔类型的字段集合
     */
    private String booleanList = "deleted,iz_";

    private boolean gitEnable = false;
    private String gitUrl = "https://gitee.com/resources1/themeList.git";
    private String gitUser;
    private String gitPwd;
    private String branch;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info(">>>>>> 配置 code.data -- start");
        log.info("\n{}", JSONUtil.toJsonPrettyStr(this));
        log.info(">>>>>> 配置 code.data -- end");
    }
}
