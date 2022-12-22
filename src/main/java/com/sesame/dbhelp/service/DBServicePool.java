package com.sesame.dbhelp.service;

import com.sesame.dbhelp.service.impl.MysqlService;
import com.sesame.dbhelp.service.impl.OracleService;

import java.util.HashMap;

/**
 * 数据库类别服务类 对象池
 * @author wangjianghai (johnny_hzz@qq.com)
 * @date 2016年10月15日 下午5:34:44
 * @Description:
 */
public class DBServicePool {

	private static  HashMap<String, DBService> dbservicepool = null;
	
	static{
		dbservicepool = new HashMap<String, DBService>();
		
		dbservicepool.put("com.mysql.cj.jdbc.Driver", new MysqlService());
		dbservicepool.put("oracle.jdbc.driver.OracleDriver", new OracleService());
	}

	public static DBService getDbService(String driver){
		return dbservicepool.get(driver);
	}
}
