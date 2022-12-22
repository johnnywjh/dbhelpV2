package com.sesame.dbhelp.util;

import lombok.Data;

@Data
public class ThemeVo {
    /**  文件名称  */
    private String fileName;
    /**  文件绝对地址  */
    private String path;
    /**  文件相对地址  */
    private String dirPath;

    public ThemeVo() {
    }

    public ThemeVo(String fileName, String path) {
        this.fileName = fileName;
        this.path = path;
    }
}
