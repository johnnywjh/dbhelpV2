package com.sesame.dbhelp.util;

import java.io.File;

public class FileContentCache {

    public static String getFileContent(String path) {
        try {
            return org.apache.commons.io.FileUtils.readFileToString(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
