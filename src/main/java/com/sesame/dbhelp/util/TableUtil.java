package com.sesame.dbhelp.util;


import com.sesame.dbhelp.entity.Column;

import java.util.List;

/**
 * 数据库表 工具类
 * @author wangjianghai
 * @date 2016年2月22日 下午12:46:17
 * @Title: NameUtil
 * @ClassName: NameUtil
 * @Description:
 */
public class TableUtil {

//	public static void main(String[] args) {
//		String resultStr = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "UserName");
//		System.out.println("转换后结果是："+resultStr);
//	}



	/**
	 * 驼峰名称  sys_user==>SysUser
	 */
	public static String hump(String str,boolean flg) {
		String res = "";
		if (str.length() > 0) {
			if (str.contains("_")) {
				String[] keys = str.toLowerCase().split("_");
				StringBuffer keySub = new StringBuffer();
				int i = 0;
				for (String k : keys) {
					if (i == 0) {
						keySub.append(k);
					} else {
						keySub.append(k.substring(0, 1).toUpperCase());
						keySub.append(k.substring(1));
					}
					i++;
				}
				res = keySub.toString();
			} else {
				res = str.toLowerCase();
			}
			if(flg){
				res = res.substring(0, 1).toUpperCase() + res.substring(1);	
			}else{
				res = res.substring(0, 1).toLowerCase() + res.substring(1);
			}
		}
		return res;
	}
	
	/**
	 * 找到表里的主键
	 */
	public static String getPrimary(List<Column> list){
		String str = "id";
		for (Column c : list) {
			if(c.isPrimary()){
				str = c.getName();
				break;
			}
		}
		return str;
	}

}
