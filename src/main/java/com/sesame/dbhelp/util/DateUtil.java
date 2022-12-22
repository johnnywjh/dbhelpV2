package com.sesame.dbhelp.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间类
 * 
 * @author wangjianghai
 * @date 2015年12月26日 下午6:35:33
 * @Title: DataUtils
 * @ClassName: DataUtils
 * @Description:
 */
public class DateUtil {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static String formatString(Date date) {

		return sdf.format(date);
	}
	public static String formatString(Date date,String patter) {
		SimpleDateFormat sdf1 = new SimpleDateFormat(patter);

		return sdf1.format(date);
	}

	public static Date formatDate(String time) throws Exception {
		
		return sdf.parse(time);
	}
}
