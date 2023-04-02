package com.sesame.dbhelp.util;

import lombok.Data;

/**
 * 放入beetl模板的方法
 */
@Data
public class BeetlStringUtil {

    private String info = "${@strUtil.uncapFirst(fkJava)}:首字母小写 , ${@strUtil.capFirst(fkJava)}:首字母大写";

    /**
     * 首字母小写
     */
    public static String uncapFirst(String name) {
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    /**
     * 首字母大写
     */
    public static String capFirst(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
