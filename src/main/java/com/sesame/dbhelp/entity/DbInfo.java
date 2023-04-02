package com.sesame.dbhelp.entity;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.symmetric.AES;
import com.sesame.dbhelp.config.SpringContextUtil;
import kim.sesame.common.req.BaseRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 数据库信息
 */
@Getter
@Setter
public class DbInfo extends BaseRequest {

    private String dbDriver; // 数据库类型
    private String url; // 数据库连接
    private String name; // 数据库用户名
    private String pwd; // 数据库密码

    private String fkType;// 模板
    private String tableName;
    private Map<String,String> exMap;

    private List<Table> tables; // 表的集合
    private boolean tableNameGruop;// 是否按表名分组
    private String packagePath;// 总包地址

    /**
     * 根据数据库连接自动判断数据库类型
     *
     * @默认mysql
     */
    public void viferyDbType() {
        AES aes = SpringContextUtil.getBean(AES.class);
        this.url = aes.decryptStr(this.url, CharsetUtil.CHARSET_UTF_8);
        this.name = aes.decryptStr(this.name, CharsetUtil.CHARSET_UTF_8);
        this.pwd = aes.decryptStr(this.pwd, CharsetUtil.CHARSET_UTF_8);

        if (this.url.toLowerCase().contains("mysql")) {
            this.dbDriver = "com.mysql.cj.jdbc.Driver";
        } else if (this.url.toLowerCase().contains("oracle")) {
            this.dbDriver = "oracle.jdbc.driver.OracleDriver";
        } else {
            this.dbDriver = "com.mysql.cj.jdbc.Driver";
        }
    }
}
