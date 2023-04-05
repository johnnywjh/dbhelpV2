package com.sesame.dbhelp.controller;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/")
public class IndexController {

//    @RequestMapping("/jdkconfig")
//    @ResponseBody
//    public JSONObject jdkconfig() {
//        JSONObject json = new JSONObject();
//        // 获取jdk的详细版本号， 例如：1.8.0_91 ， 1.7.0_79，1.6.0
//        json.put("java.version", System.getProperty("java.version"));
//        // 获取 jdk的标准版本 ，例如： 1.8 , 1.7 , 1.6
//        json.put("java.specification.version", System.getProperty("java.specification.version"));
//        // 获取JDK的位数
//        // 包含 "64",即可64位 JDK , 否则 32位
//        json.put("java.vm.name", System.getProperty("java.vm.name"));
//        // 64位JDK：amd64 ，32位JDK：x86
//        json.put("os.arch", System.getProperty("os.arch"));
//        // 64位JDK：64 ，32位JDK：32
//        json.put("sun.arch.data.model", System.getProperty("sun.arch.data.model"));
//
//        json.put("time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
//        return json;
//    }

    // 点火
    @ResponseBody
    @RequestMapping("/warmup")
    public String warmup() {
        return "ok";
    }

}
