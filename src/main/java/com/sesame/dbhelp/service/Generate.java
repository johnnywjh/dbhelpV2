package com.sesame.dbhelp.service;

import com.sesame.dbhelp.util.FileContentCache;
import com.sesame.dbhelp.util.FileUtil;
import com.sesame.dbhelp.util.StringUtil;
import com.sesame.dbhelp.util.ThemeVo;
import lombok.extern.slf4j.Slf4j;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Generate {

    static StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();

    public static void file(HashMap<String, Object> params, ThemeVo vo, String path, boolean tableNameGruop) {

        String tableName = params.get("tableName").toString();
        String fileName = StringUtil.substringReplaceLast(vo.getFileName(), ".", 0);

        String outTargetPath = tableNameGruop ? path + "/" + tableName + vo.getDirPath() : path + vo.getDirPath();

        if (outTargetPath.indexOf("$") > -1) {
            outTargetPath = convertPath(params, outTargetPath);
        }
        if (fileName.indexOf("$") > -1) {
            fileName = convertPath(params, fileName);
        }

        try {

            // 模板内容
            String templateStr = FileContentCache.getFileContent(vo.getPath());

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

    private static String convertPath(Map<String, Object> params, String path) {
        try {
//            StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
            Configuration cfg = Configuration.defaultConfiguration();
            GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
            //获取模板
            Template t = gt.getTemplate(path);
            t.binding(params);
            return t.render();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return path;
    }
}
