package com.sesame.dbhelp.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.sesame.dbhelp.util.AESHelp;
import kim.sesame.common.web.controller.AbstractWebController;
import kim.sesame.common.exception.BizException;
import kim.sesame.common.result.ApiResult;
import com.sesame.dbhelp.util.Theme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController  extends AbstractWebController {

    /**
     * 读取数据库配置文件
     */
    @PostMapping("/readDbCofig")
    public ApiResult readDbCofig(HttpServletRequest request, HttpServletResponse response, MultipartFile file) throws Exception {
        if (file.getSize() > 0) {
            String str = new String(file.getBytes());
//            JSONObject json = JSON.parseObject(str);

            // 保持key的有序
            LinkedHashMap<String, Object> linkedHashMap = JSON.parseObject(str,LinkedHashMap.class, Feature.OrderedField);
            JSONObject json=new JSONObject(true);
            json.putAll(linkedHashMap);

            for(String key : json.keySet()){
                JSONObject obj = json.getJSONObject(key);
                // 加密
                obj.put("name", AESHelp.aes.encryptHex(obj.getString("name")));
                obj.put("url", AESHelp.aes.encryptHex(obj.getString("url")));
                obj.put("pwd", AESHelp.aes.encryptHex(obj.getString("pwd")));
            }
            return success(json);


        } else {
            throw new BizException("上传的文件为空");
        }
    }
    @PostMapping("/getThemes")
    public ApiResult getThemes(){
        return  success(Theme.themes);
    }
}
