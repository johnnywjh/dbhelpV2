package com.sesame.dbhelp.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sesame.common.controller.AbstractWebController;
import com.sesame.common.exception.BizException;
import com.sesame.common.response.Response;
import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController  extends AbstractWebController {

    /**
     * 读取数据库配置文件
     */
    @RequestMapping("/readDbCofig")
    public Response readDbCofig(HttpServletRequest request, HttpServletResponse response, MultipartFile file) throws Exception {
        if (file.getSize() > 0) {
            String str = new String(file.getBytes());
            JSONObject json = JSON.parseObject(str);

            return returnSuccess(json);
        } else {
            throw new BizException("上传的文件为空");
        }
    }

}
