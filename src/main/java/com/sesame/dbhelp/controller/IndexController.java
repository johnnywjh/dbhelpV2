package com.sesame.dbhelp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/")
public class IndexController {

//    @RequestMapping("/")
//    public String index() {
//        return "index";
//    }

    // 点火
    @ResponseBody
    @RequestMapping("/warmup")
    public String warmup() {
        return "ok";
    }

//    @ResponseBody
//    @RequestMapping("/test")
//    public JSONObject warmup(String name) {
//
//        JSONObject json = new JSONObject();
//        json.put("name",name);
//        json.put("time", DateUtil.formatString(new Date()));
//
//        return json;
//    }

}
