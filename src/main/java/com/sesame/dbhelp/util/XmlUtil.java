package com.sesame.dbhelp.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 解析xml
 * 
 * @author wangjianghai
 * @date 2016年2月22日 下午2:36:41
 * @Title: XmlHelper
 * @ClassName: XmlHelper
 * @Description:
 */
public class XmlUtil {

	public static HashMap<String, Object> parse(String xmlName) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		InputStream inputStream = null;
		try {
			// 工厂创建解析器
			DocumentBuilder db = dbf.newDocumentBuilder();
			// 通过输入源构造一个Document
			inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(xmlName);
			Document doc = db.parse(inputStream);

			putNodes(map, doc, "params");

			HashMap<String, String> list_map = null;
			String key = "";
			NodeList param_list = doc.getElementsByTagName("table");
			String[] arr = { "tableName", "className", "packageName", "annotation", "jsp", "serviceImpl", "add", "delete", "update", "select", "selectPage", "selectList" };
			for (int i = 0; i < param_list.getLength(); i++) {
				list_map = new HashMap<String, String>();
				Element t = (Element) param_list.item(i);
				for (int ii = 0; ii < arr.length; ii++) {
					key = arr[ii];
					list_map.put(key, t.getAttribute(key));
				}
				list.add(list_map);
			}
			map.put("list", list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return map;
	}//

	private static void putNodes(HashMap<String, Object> map, Document doc, String str) {
		NodeList param_list = doc.getElementsByTagName(str);
		for (int i = 0; i < param_list.getLength(); i++) {
			Element t = (Element) param_list.item(i);
			// map.put(t.getAttribute("name"), t.getTextContent());
			map.put(t.getAttribute("name"), t.getAttribute("value"));
		}
	}

	private static void putNode(HashMap<String, String> map, Document doc, String str) {
		String value = doc.getElementsByTagName(str).item(0).getTextContent();
		map.put(str, value);
	}

}
