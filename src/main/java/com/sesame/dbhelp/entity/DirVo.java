package com.sesame.dbhelp.entity;


import com.sesame.common.web.PrintFriendliness;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DirVo extends PrintFriendliness {

    private String outPath;
    private String fileDir;

    public DirVo(String outPath, String fileDir) {
        this.outPath = outPath;
        this.fileDir = fileDir;
    }

    public DirVo() {
    }
}
