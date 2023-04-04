package com.sesame.dbhelp.util;

import com.alibaba.fastjson.JSONObject;
import com.sesame.dbhelp.config.BaseConfig;
import com.sesame.dbhelp.config.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * 主题
 *
 * @author wangjianghai
 * @date 2016年3月31日 下午3:47:34
 * @Description:
 */
@Slf4j
@Component
public class Theme {

    public static String resourcePath = null;

    private static List<String> themes = null;//所有的主题
    public static Map<String, String> themeMap = null;

    static Lock lock = new ReentrantLock();

    @Autowired
    private BaseConfig config;

    public List<String> getThemes() {
        if (themes == null) {
            reloadTheme();
        }
        return themes;
    }

    public void reloadTheme() {
        try {
            lock.lock();
            log.info("获取锁-开始加载模块");

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
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            lock.unlock();
            log.info("释放锁-加载模块结束 : {}", JSONObject.toJSONString(themes));
        }
    }

    private String getCommonField(File f) {
        List<String> list = new ArrayList<>();
        try {
            list = FileUtils.readLines(new File(f.getAbsolutePath() + "/common-field.txt"), "UTF-8");
        } catch (IOException e) {
//            log.error(e.getMessage());
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
                if (!f.getName().equals("common-field.txt")) {
                    listAll.add(new ThemeVo(f.getName(), f.getAbsolutePath().replaceAll("\\\\", "/")));
                }
            }
        }

    }

}
