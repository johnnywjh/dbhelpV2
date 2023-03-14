package com.sesame.dbhelp.service.impl;

import com.sesame.dbhelp.entity.Column;
import com.sesame.dbhelp.entity.QueryDbTableReq;
import com.sesame.dbhelp.entity.Table;
import com.sesame.dbhelp.service.DBService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OracleService extends DBService {

    /**
     * 获取这个数据库中的所有表名称
     */
    @Override
    public List<Table> getTables(Connection conn) {
        String sql = " select table_name from user_tables ";
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
                sql = " select COMMENTS from user_tab_comments where Table_Name='" + table.getTableName() + "'";
                pstmt = conn.prepareStatement(sql);
                rs = pstmt.executeQuery(sql);
                while (rs.next()) {
                    comment = rs.getString("COMMENTS"); // 字段名称
                }
                comment = comment == null ? "null" : comment;
                table.setComment(comment);//给每一张表设置一个注释
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //closeConn(conn);
        }
        return list;
    }

    /**
     * 解析oracle表结构
     */
    @Override
    public Table parseTable(String tableName, Connection conn) {
        List<Column> list = new ArrayList<Column>();
        Column bean = null;
        String name = null; // 字段名称
        String type = null; // 类型
        boolean primary = false; // 是否是主键
        boolean empty = false; // 是否为空
        String comment = null; // 列注释

        String strsql = "select a.tablespace_name," + " a.table_name," + " b.column_name," + " b.data_type," + " b.data_length," + "  b.nullable," + " c.comments" + " from user_tables a, user_tab_cols b, user_col_comments c " + " where a.table_name = b.table_name "
                + " and b.column_name = c.column_name  " + " and b.table_name = c.table_name " + " and a.table_name = '" + tableName + "'";
        try {
            System.out.println(strsql);
            PreparedStatement pstmt = conn.prepareStatement(strsql);
            ResultSet rs = pstmt.executeQuery(strsql);
            int index = 1;
            while (rs.next()) {
                name = rs.getString("column_name"); // 字段名称
                type = rs.getString("data_type"); // 类型
                primary = false; // 是否是主键
                empty = rs.getString("nullable").equals("Y") ? true : false; // 是否为空
                comment = rs.getString("comments"); // 表注释
                comment = comment == null ? "null" : comment;

                String id = tableName + name;
                bean = new Column(id, index, name, type, primary, empty, comment);
                list.add(bean);
                index++;
            }
            strsql = "select cu.table_name,cu.column_name,cu.owner,cu.constraint_name,cu.position " + " from user_cons_columns cu, user_constraints au " + " where cu.constraint_name = au.constraint_name " + " and au.constraint_type = 'P' " + " and au.table_name = '" + tableName + "'";
            pstmt = conn.prepareStatement(strsql);
            rs = pstmt.executeQuery(strsql);
            String fk = "";
            while (rs.next()) {
                fk = rs.getString("column_name");
            }
            pstmt.close();
            for (Column c : list) {
                if (c.getName().equals(fk)) {
                    c.setPrimary(true);
                    break;
                }
            }
            init(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //closeConn(conn);
        }
        Table table = new Table();
        table.setColumns(list);
        return table;
    }

    @Override
    public String parseTableDDL(String tableName, Connection conn) {
        return null;
    }

    @Override
    public List queryTableData(QueryDbTableReq req, Connection conn) {
        return null;
    }
}
