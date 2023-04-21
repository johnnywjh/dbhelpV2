package com.sesame.dbhelp.service.impl;

import com.sesame.dbhelp.entity.Column;
import com.sesame.dbhelp.entity.QueryDbTableReq;
import com.sesame.dbhelp.entity.Table;
import com.sesame.dbhelp.service.DBService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
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

                String id = tableName + name;
                bean = new Column(id, index, name, type, primary, empty, comment);
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

    @Override
    public List queryTableData(QueryDbTableReq req, Connection conn) throws Exception {
        String orderSql = "";
        if (StringUtils.isNoneEmpty(req.getOrderByName())) {
            orderSql = " order by " + req.getOrderByName();
            if (req.isOrderByAsc()) {
                orderSql = orderSql + " asc ";
            } else {
                orderSql = orderSql + " desc ";
            }
        }
        String whereText = StringUtils.isEmpty(req.getWhereText()) ? "" : " where " + req.getWhereText() + " ";
//        String strsql = " select " + req.getColumns() + " from " + req.getTableName() + orderSql + " limit " + req.getLimit();
        String strsql = String.format("select %s from %s %s %s  limit %s", req.getColumns(), req.getTableName(), whereText, orderSql, req.getLimit());
        log.info("当前执行sql查询 : {}", strsql);

        List list = new ArrayList();
        PreparedStatement pstmt = conn.prepareStatement(strsql);
        ResultSet rs = pstmt.executeQuery(strsql);
        ResultSetMetaData md = rs.getMetaData();
        int columnCount = md.getColumnCount();

//            int index = 1;
        while (rs.next()) {
            Map rowData = new HashMap();
            for (int i = 1; i <= columnCount; i++) {
                rowData.put(md.getColumnName(i), rs.getObject(i));
            }
//                rowData.put("index",index);
            list.add(rowData);
//                index++;
        }
        return list;

    }


}
