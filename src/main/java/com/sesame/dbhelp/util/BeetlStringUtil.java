package com.sesame.dbhelp.util;

import com.google.common.base.CaseFormat;
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
        if (name == null) {
            return "";
        }
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    /**
     * 首字母大写
     */
    public static String capFirst(String name) {
        if (name == null) {
            return "";
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static String getPath(String name) {
        if (name == null) {
            return "";
        }
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name).replaceAll("_", "-");
    }

}
