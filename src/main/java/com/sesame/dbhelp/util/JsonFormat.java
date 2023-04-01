package com.sesame.dbhelp.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JsonFormat {
    public static  String pretty(String jsonString){
        JSONObject object = JSONObject.parseObject(jsonString);
        String pretty = JSON.toJSONString(object,
                SerializerFeature.PrettyFormat,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat
        );
        return pretty;
    }
    public static  String pretty(JSONObject jsonObj){
        return pretty(jsonObj.toJSONString());
    }

}
