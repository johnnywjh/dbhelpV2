package com.sesame.dbhelp.service;

import com.sesame.dbhelp.entity.Column;
import com.sesame.dbhelp.entity.DbInfo;
import com.sesame.dbhelp.entity.QueryDbTableReq;
import com.sesame.dbhelp.entity.Table;
import com.sesame.dbhelp.util.StringUtil;
import com.sesame.dbhelp.util.TableUtil;
import lombok.extern.apachecommons.CommonsLog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * 数据库处理,父方法
 *
 * @author wangjianghai (johnny_hzz@qq.com)
 * @date 2016年10月15日 下午4:27:27
 * @Description:
 */
@CommonsLog
public abstract class DBService {
    // ----------------------------------------------------------
    // -------------------- 定义公用的抽象函数
    // ------------------------------------------------------------

    /**
     * 获取这个数据库中的所有表名称
     */
    public abstract List<Table> getTables(Connection conn);

    /**
     * 解析表结构
     */
    public abstract Table parseTable(String tableName, Connection conn);

    /**
     * 表结构
     */
    public abstract String parseTableDDL(String tableName, Connection conn);

    /**
     * 查询表数据
     */
    public abstract List queryTableData(QueryDbTableReq req, Connection conn) throws Exception;

    /**
     * 匹配数据类型
     */
    public static void init(List<Column> list) {
        String str = "";
        for (Column c : list) {

            c.setJavaName(TableUtil.hump(c.getName(), false));
            c.setComment(c.getComment().trim());
            // -------------------------------------------------
            if (c.getType().contains("(")) {
                str = c.getType().substring(0, c.getType().indexOf('('));
            } else {
                str = c.getType();
            }
            if (StringUtil.equals(str, "int") || StringUtil.equals(str, "integer")) {
                c.setJavaType("Integer");
            } else if (StringUtil.equals(str, "long")) {
                c.setJavaType("Long");
            } else if (StringUtil.equals(str, "bigint")) {
                c.setJavaType("Long");
            } else if (StringUtil.equals(str, new String[]{"float", "double"})) {
                c.setJavaType("Double");
            } else if (StringUtil.equals(str, new String[]{"number", "decimal", "numeric", "real"})) {
                c.setJavaType("BigDecimal");
            } else if (StringUtil.equals(str, new String[]{"varchar", "varchar2", "char", "nvarchar", "nchar", "text"})) {
                c.setJavaType("String");
            } else if (StringUtil.equals(str, new String[]{"datetime", "date", "timestamp"})) {
//                c.setJavaType("Date");
                c.setJavaType("LocalDateTime");
            } else if (StringUtil.equals(str, new String[]{"tinyint"})) {
                if ("tinyint(1)".equals(c.getType())) {
                    c.setJavaType("Boolean");
                } else {
                    c.setJavaType("Integer");
                }

            } else {
                c.setJavaType("String");
                log.info("<<>>没有匹配到这个类型javatype:" + str);
            }
            // --------------------------------------------------------------------------------------------
            if (StringUtil.equals(str, "int") || StringUtil.equals(str, "integer")) {
                c.setJdbcType("INTEGER");
            } else if (StringUtil.equals(str, "long")) {
                c.setJdbcType("LONGVARBINARY");
            } else if (StringUtil.equals(str, "bigint")) {
                c.setJdbcType("BIGINT");
            } else if (StringUtil.equals(str, new String[]{"float", "double"})) {
                c.setJdbcType("DOUBLE");
            } else if (StringUtil.equals(str, new String[]{"number", "decimal", "numeric", "real"})) {
                c.setJdbcType("DOUBLE");
            } else if (StringUtil.equals(str, new String[]{"varchar", "varchar2", "nvarchar", "text"})) {
                c.setJdbcType("VARCHAR");
            } else if (StringUtil.equals(str, new String[]{"char", "nchar"})) {
                c.setJdbcType("CHAR");
            } else if (StringUtil.equals(str, new String[]{"datetime", "date", "timestamp"})) {
                c.setJdbcType("DATE");
            } else if (StringUtil.equals(str, new String[]{"tinyint"})) {
                c.setJdbcType("TINYINT");
            } else {
                c.setJdbcType("VARCHAR");
                log.info(">><<没有匹配到这个类型jdbctype:" + str);
            }
//            System.out.println(c.toString());
        }

    }

    /**
     * 获取数据库连接 @author wangjianghai (johnny_hzz@qq.com)
     *
     * @date 2016年10月15日 下午4:27:38 @throws
     */
    public static Connection getConn(DbInfo bean) {
        try {
            Class.forName(bean.getDbDriver());
            return DriverManager.getConnection(bean.getUrl(), bean.getName(), bean.getPwd());
        } catch (Exception e) {
            e.printStackTrace();
            log.info("数据库连接失败");
            log.info("url:" + bean.getUrl());
            log.info("name:" + bean.getName());
            log.info("pwd:" + bean.getPwd());
            return null;
        }
    }

    /**
     * 关闭数据库连接
     *
     * @author wangjianghai (johnny_hzz@qq.com)
     * @date 2016年10月15日 下午4:27:54
     */
    public static void closeConn(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                conn = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
