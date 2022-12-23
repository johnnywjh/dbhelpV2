package com.sesame;

import com.sesame.dbhelp.entity.DbInfo;
import com.sesame.dbhelp.service.DBService;

import java.sql.Connection;

public class Test {
    public static void main(String[] args) {
        DbInfo bean = new DbInfo();
        bean.setUrl("jdbc:mysql://db.node.cn:3306/mall_user?useUnicode=true&characterEncoding=UTF-8&useSSL=false");
        bean.setName("root");
        bean.setPwd("ZT6rfogb9TJswyKL");

        bean.viferyDbType();
        Connection conn = DBService.getConn(bean);
    }
}
