package com.sesame.dbhelp.service;

import com.sesame.dbhelp.util.FileUtil;
import com.sesame.dbhelp.util.ThemeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Generate {

    public static void file(HashMap<String, Object> params, ThemeVo vo, String path, boolean tableNameGruop) {

        String tableName = params.get("tableName").toString();
        String className = params.get("className").toString();
        // Controller.java.flt
        String[] arr = vo.getFileName().split("\\.");
        try {
            Configuration cfg = new Configuration();
            // 设置FreeMarker的模版文件夹位置
            String fk_path = vo.getPath().replaceAll(vo.getFileName(), "");
            cfg.setDirectoryForTemplateLoading(new File(fk_path));
            cfg.setDefaultEncoding("UTF-8");

            String fileName = null;
            if (arr[1].equals("js") || arr[1].equals("vue")){
                fileName = arr[0] + "." + arr[1];
            } else{
                fileName = className + arr[0] + "." + arr[1];
            }

            Template temp = cfg.getTemplate(vo.getFileName());
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(temp, params);

            String outTargetPath = tableNameGruop ?
                    path + "/" + tableName + vo.getDirPath()
                    : path + vo.getDirPath();
            if (outTargetPath.contains("$")) {
                outTargetPath = convertPath(params, outTargetPath);
            }
            FileUtil.createFile(outTargetPath, fileName, content);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private static String convertPath(Map<String, Object> params, String path) {
        Configuration cfg = new Configuration();
        cfg.setTemplateLoader(new StringTemplateLoader(path));
        cfg.setDefaultEncoding("UTF-8");
        try {
            Template template = cfg.getTemplate("");
            StringWriter writer = new StringWriter();
            template.process(params, writer);
//            log.info("转换路径 : {}", writer.toString());
            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }
}
