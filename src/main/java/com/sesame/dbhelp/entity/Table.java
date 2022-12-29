package com.sesame.dbhelp.entity;

import com.sesame.dbhelp.util.TableUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 数据库中的表
 * 
 * @author wangjianghai
 * @date 2016年3月31日 下午1:35:06
 * @Description:
 */
@Data
public class Table implements Serializable {

	private String tableName;// 表名,不能为空
	private String className;// 生成的java类名,默认 sys_user => SysUser
	private String comment;// 表注释
	private String exJson;// 扩展参数

	private List<Column> columns;

	private String ddl;
	private String dir1;
	private String dir2;

	public Table() {
		super();
	}

	public Table(String tableName) {
		super();
		this.tableName = tableName;
		this.className = TableUtil.hump(tableName, true);

		//this.packageName = className.toLowerCase();
//		if(tableName.contains("_")){
//			String[] split = tableName.split("_");
//			this.packageName = split[split.length-1].toLowerCase();
//		}else{
//			this.packageName = className.toLowerCase();
//		}
	}


}
