package com.sesame.dbhelp.service;

import com.sesame.dbhelp.util.FileUtil;
import com.sesame.dbhelp.util.ThemeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.File;
import java.util.HashMap;

@Slf4j
public class Generate {

    public static void file(HashMap<String, Object> params, ThemeVo vo, String path) {

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

            String fileName = className +  arr[0] + "." + arr[1];

            Template temp = cfg.getTemplate(vo.getFileName());
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(temp, params);
            FileUtil.createFile(path+vo.getDirPath(), fileName, content);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
