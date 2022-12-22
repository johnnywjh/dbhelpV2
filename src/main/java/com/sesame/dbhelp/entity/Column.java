package com.sesame.dbhelp.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 列
 * 
 * @author wangjianghai
 * @date 2016年2月23日 下午4:41:21
 * @Title: Column
 * @ClassName: Column
 * @Description:
 */
@Data
public class Column implements Serializable {

	private String name; // 字段名称
	private String type; // 类型
	private boolean primary; // 是否是主键
	private boolean empty; // 是否为空
	private String comment; // 列注释
	
	private String javaType;//对应的java类型
	private String jdbcType;//对应的jdbc类型
	private String javaName;//java属性名
	
	public Column() {

	}
	public Column(String name, String type, boolean primary, boolean empty, String comment) {
		this.name = name;
		this.type = type;
		this.primary = primary;
		this.empty = empty;
		this.comment = comment;
	}
	
	@Override
	public String toString() {
		return "Column [name=" + name + ", type=" + type + ", primary=" + primary + ", empty=" + empty + ", comment=" + comment + ", javaType=" + javaType + ", jdbcType=" + jdbcType + ", javaName=" + javaName + "]";
	}
	
}
