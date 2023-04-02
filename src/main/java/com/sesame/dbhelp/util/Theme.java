package com.sesame.dbhelp.util;

import com.sesame.dbhelp.config.BaseConfig;
import com.sesame.dbhelp.config.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 主题
 *
 * @author wangjianghai
 * @date 2016年3月31日 下午3:47:34
 * @Description:
 */
@Slf4j
public class Theme {

    public static String resourcePath = null;

    public static List<String> themes = null;//所有的主题
    public static Map<String, String> themeMap = null;

    static {
        BaseConfig config = SpringContextUtil.getApplicationContext().getBean(BaseConfig.class);
        resourcePath = config.getBasePath() + "/" + config.getThemeDicName();
        themes = new ArrayList<>();
        themeMap = new HashMap<>();

        List<String> excludeList = new ArrayList<>();
        try {
            excludeList = FileUtils.readLines(new File(resourcePath + "/exclude.config.txt"), "UTF-8");
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        File[] array = new File(resourcePath).listFiles();
        for (int i = 0; i < array.length; i++) {
            if (array[i].isDirectory()) {
                String name = array[i].getName();
                if (!excludeList.contains(name)) {
                    themes.add(name);
                    themeMap.put(name, getCommonField(array[i]));
                }
            }
        }
    }

    private static String getCommonField(File f) {
        List<String> list = new ArrayList<>();
        try {
            list = FileUtils.readLines(new File(f.getAbsolutePath() + "/common-field.txt"), "UTF-8");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        if (list.size() == 0) {
            return "deleted,createTime,createUserId,createUserName,modifyTime,modifyUserId,modifyUserName";
        }
        return list.stream()
                .distinct()
                .filter(StringUtils::isNotEmpty)
                .map(String::trim)
                .collect(Collectors.joining(","));
    }

    public static List<ThemeVo> getTheme(String fkType) {
        final String path = Theme.resourcePath + "/" + fkType;
        File file = new File(path);
        List<ThemeVo> listAll = new ArrayList<>();
        searchfile(file, listAll);
        listAll.stream().forEach(vo -> {
            vo.setDirPath(vo.getPath().replaceAll(path, "").replaceAll(vo.getFileName(), ""));
        });
        return listAll;
    }

    private static void searchfile(File file, List<ThemeVo> listAll) {

        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (f.isDirectory()) {
                searchfile(f, listAll);
            } else {
                if(!f.getName().equals("common-field.txt")){
                    listAll.add(new ThemeVo(f.getName(), f.getAbsolutePath()));
                }
            }
        }

    }

}
