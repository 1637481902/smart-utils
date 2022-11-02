package com.sichengbo.su.utils.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.ObjectUtils;

/**
 * 对象转JSON
 */
public class JsonHandlerUtils {

    /**
     * JSONObject
     *
     * @param param
     * @return JSONObject
     */
    public static JSONObject changeJsonObject(Object param) {
        JSONObject resultStr = new JSONObject();
        if (!ObjectUtils.isEmpty(param)) {
            if (param instanceof String && JSONObject.isValid(param.toString())) {
                resultStr = JSONObject.parseObject(param.toString());
            }
        }
        return resultStr;
    }

    /**
     * JSONOArray
     *
     * @param param
     * @return JSONArray
     */
    public static JSONArray changeJsonArray(Object param) {
        JSONArray resultStr = new JSONArray();
        if (!ObjectUtils.isEmpty(param)) {
            if (param instanceof String) {
                resultStr = JSONArray.parseArray(JSON.toJSONString(param.toString().split(",")));
            }
        }
        return resultStr;
    }
}
