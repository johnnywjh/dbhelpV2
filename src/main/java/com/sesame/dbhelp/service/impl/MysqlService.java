package com.sesame.dbhelp.service.impl;

import com.sesame.dbhelp.entity.Column;
import com.sesame.dbhelp.entity.Table;
import com.sesame.dbhelp.service.DBService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class MysqlService extends DBService {

    /**
     * 获取这个数据库中的所有表名称
     */
    @Override
    public List<Table> getTables(Connection conn) {
        String sql = "show tables";
        List<Table> list = new ArrayList<Table>();

        try {
            // 查询所有的表
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery(sql);
            while (rs.next()) {
                list.add(new Table(rs.getString(1)));
            }
            String comment = "";
            // 根据每个表查询标注是
            for (Table table : list) {
                sql = " SHOW TABLE STATUS WHERE NAME='" + table.getTableName() + "'";
                pstmt = conn.prepareStatement(sql);
                rs = pstmt.executeQuery(sql);
                while (rs.next()) {
                    comment = rs.getString("Comment"); // 字段名称
                }
                comment = comment == null ? "null" : comment;
                table.setComment(comment);//给每一张表设置一个注释

//                table.setDdl(this.parseTableDDL(table.getTableName(), conn));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //closeConn(conn);
        }
        return list;
    }

    /**
     * 解析表结构
     * mysql
     */
    @Override
    public Table parseTable(String tableName, Connection conn) {
        Table table = new Table();
        List<Column> list = new ArrayList<Column>();
        Column bean = null;
        String name = null; // 字段名称
        String type = null; // 类型
        boolean primary = false; // 是否是主键
        boolean empty = false; // 是否为空
        String comment = null; // 列注释

        String strsql = " show full columns from " + tableName;
        try {
            PreparedStatement pstmt = conn.prepareStatement(strsql);
            ResultSet rs = pstmt.executeQuery(strsql);
            int index = 1;
            while (rs.next()) {
                name = rs.getString("Field"); // 字段名称
                type = rs.getString("Type"); // 类型
                primary = rs.getString("Key").equals("PRI") ? true : false; // 是否是主键
                empty = rs.getString("Null").equals("YES") ? true : false; // 是否为空
                comment = rs.getString("Comment"); // 列注释
                comment = comment == null ? "null" : comment;

                bean = new Column(index, name, type, primary, empty, comment);
                list.add(bean);
                index++;
            }
            init(list);
            pstmt.close();

            String ddl = this.parseTableDDL(tableName, conn);
            table.setDdl(ddl);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //closeConn(conn);
        }
        table.setColumns(list);
        return table;
    }

    @Override
    public String parseTableDDL(String tableName, Connection conn) {
        String strsql = " show create table " + tableName;
        String ddlStr = null;
        try {
            PreparedStatement pstmt = conn.prepareStatement(strsql);
            ResultSet rs = pstmt.executeQuery(strsql);
            while (rs.next()) {
                ddlStr = rs.getString("Create Table");
            }
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //closeConn(conn);
        }
        return ddlStr;
    }


}
