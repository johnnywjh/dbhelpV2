package com.sesame.dbhelp.util;

import com.google.common.base.CaseFormat;
import com.sesame.dbhelp.entity.Column;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 数据库表 工具类
 */
@Slf4j
public class TableUtil {

    /*
// 连接符转驼峰，首字母小写 结果testData
System.out.println(CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, "test-data"));

// 连接符转驼峰，首字母大写 结果TestData
System.out.println(CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, "test-data"));

// 下划线转驼峰，首字母小写 结果testData
System.out.println(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "test_data"));

// 下划线转驼峰，首字母大写 结果TestData
System.out.println(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, "test_data"));

// 驼峰转小写下划线 结果test_data
System.out.println(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "testData"));

// 驼峰转大写下划线 结果TEST_DATA
System.out.println(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, "TestData"));

// 驼峰转小写连接符 结果test-data
System.out.println(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, "testData"));
*/

    public static void main(String[] args) {
        String str = "ps_product__sales";

// 下划线转驼峰，首字母小写 结果testData
        System.out.println(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, str));

// 下划线转驼峰，首字母大写 结果TestData
        System.out.println(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, str));

    }

    /**
     * 驼峰名称  sys_user==>SysUser
     */
    public static String hump(String str, boolean flg) {
        if (StringUtils.isEmpty(str)) {
            return "";
        }
        if (str.contains("-")) {
            str = str.replaceAll("-", "_");
        }
        try {
            if (flg) {
                return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, str);
            } else {
                return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, str);
            }
        } catch (Exception e) {
            log.error("驼峰转换失败 : str:{} , flg:{}", str, flg);
            return str;
        }

//        String res = "";
//        try {
//            if (str.length() > 0) {
//                if (str.contains("_")) {
//                    String[] keys = str.toLowerCase().split("_");
//                    StringBuffer keySub = new StringBuffer();
//                    int i = 0;
//                    for (String k : keys) {
//                        if (i == 0) {
//                            keySub.append(k);
//                        } else {
//                            keySub.append(k.substring(0, 1).toUpperCase());
//                            keySub.append(k.substring(1));
//                        }
//                        i++;
//                    }
//                    res = keySub.toString();
//                } else {
//                    res = str.toLowerCase();
//                }
//                if (flg) {
//                    res = res.substring(0, 1).toUpperCase() + res.substring(1);
//                } else {
//                    res = res.substring(0, 1).toLowerCase() + res.substring(1);
//                }
//            }
//        } catch (Exception e) {
//            log.error("hump error : str:{} , flg:{}", str, flg);
//            return str;
//        }
//        return res;
    }

    /**
     * 找到表里的主键
     */
    public static String getPrimary(List<Column> list) {
        String str = "id";
        for (Column c : list) {
            if (c.isPrimary()) {
                str = c.getName();
                break;
            }
        }
        return str;
    }

}
