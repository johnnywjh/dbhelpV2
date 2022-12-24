package com.sesame.dbhelp.entity;

import com.sesame.common.web.BaseRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

    private List<Table> tables; // 表的集合
    private boolean tableNameGruop;// 是否按表名分组

    /**
     * 根据数据库连接自动判断数据库类型
     *
     * @默认mysql
     */
    public void viferyDbType() {
        if (this.url.toLowerCase().contains("mysql")) {
            this.dbDriver = "com.mysql.cj.jdbc.Driver";
        } else if (this.url.toLowerCase().contains("oracle")) {
            this.dbDriver = "oracle.jdbc.driver.OracleDriver";
        } else {
            this.dbDriver = "com.mysql.cj.jdbc.Driver";
        }
    }
}
