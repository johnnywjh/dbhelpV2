package com.sesame.dbhelp.entity;

import kim.sesame.common.req.BaseRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class QueryDbTableReq extends BaseRequest {

    private int limit;
    private boolean orderByAsc;
    private String orderByName;
    private String tableName;
    private String columns;

    private String dbDriver; // 数据库类型
    private String url; // 数据库连接
    private String name; // 数据库用户名
    private String pwd; // 数据库密码

}
