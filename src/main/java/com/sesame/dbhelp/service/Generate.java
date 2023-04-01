package com.sesame.dbhelp.service;

import com.sesame.dbhelp.util.FileContentCache;
import com.sesame.dbhelp.util.FileUtil;
import com.sesame.dbhelp.util.ThemeVo;
import lombok.extern.slf4j.Slf4j;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.exception.BeetlException;
import org.beetl.core.exception.ErrorInfo;
import org.beetl.core.resource.StringTemplateResourceLoader;

import java.util.HashMap;

@Slf4j
public class Generate {


    public static void file(HashMap<String, Object> params, ThemeVo vo, String path, boolean tableNameGruop) {

        String tableName = params.get("tableName").toString();
        String className = params.get("className").toString();
        // Controller.java.txt
        String[] arr = vo.getFileName().split("\\.");

        String fk_path = vo.getPath().replaceAll(vo.getFileName(), "");

        String fileName = null;
        if (arr[1].equals("js") || arr[1].equals("vue")) {
            fileName = arr[0] + "." + arr[1];
        } else {
            fileName = className + arr[0] + "." + arr[1];
        }

        String outTargetPath = tableNameGruop ?
                path + "/" + tableName + vo.getDirPath()
                : path + vo.getDirPath();
        if (outTargetPath.contains("$")) {
//                outTargetPath = convertPath(params, outTargetPath);
        }
        if (fileName.contains("dir1") || fileName.contains("dir2")) {
            String dir1 = params.get("dir1").toString();
            String dir2 = params.get("dir2").toString();
            fileName = fileName.replaceAll("dir1", dir1).replaceAll("dir2", dir2);
        }

        try {

            // 模板内容
            String templateStr = FileContentCache.getFileContent(vo.getPath());

            StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
            Configuration cfg = Configuration.defaultConfiguration();
            GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
            //获取模板
            Template t = gt.getTemplate(templateStr);
            t.binding(params);
            String content = t.render();

            FileUtil.createFile(outTargetPath, fileName, content);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

//    private static String convertPath(Map<String, Object> params, String path) {
//        Configuration cfg = new Configuration();
//        cfg.setTemplateLoader(new StringTemplateLoader(path));
//        cfg.setDefaultEncoding("UTF-8");
//        try {
//            Template template = cfg.getTemplate("");
//            StringWriter writer = new StringWriter();
//            template.process(params, writer);
////            log.info("转换路径 : {}", writer.toString());
//            return writer.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return path;
//    }
}
