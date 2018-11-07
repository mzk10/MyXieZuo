package com.meng.hui.android.xiezuo.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.meng.hui.android.xiezuo.entity.ResponseData;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Auth: LK
 * Date: 2017-03-08
 * Time: 11:16
 */
public class GsonUtils {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(
                    new TypeToken<Map<String, Object>>(){}.getType(),
                    new JsonDeserializer<Map<String, Object>>() {

                        @Override
                        public Map<String, Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                            Map<String, Object> resultMap = new HashMap();
                            JsonObject jsonObject = json.getAsJsonObject();
                            Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
                            for (Map.Entry<String, JsonElement> entry : entrySet) {
                                Object ot = entry.getValue();
                                if (ot instanceof JsonPrimitive) {
                                    resultMap.put(entry.getKey(), ((JsonPrimitive) ot).getAsString());
                                } else {
                                    resultMap.put(entry.getKey(), ot);
                                }
                            }
                            return resultMap;
                        }

                    }
            ).disableHtmlEscaping().create();

    /**
     * <pre>
     * JSON字符串转换为List数组, 提供两种方式(主要解决调用的容易程度)
     * 1. TypeToken<List<T>> token 参数转换
     * 2. Class<T> cls 方式转换
     *
     * @param json
     * @return List<T>
     *
     * <pre>
     */
    public static <T> List<T> convertList(String json, TypeToken<List<T>> token) {
        if (TextUtils.isEmpty(json)) {
            return new ArrayList<T>();
        }
        return gson.fromJson(json, token.getType());
    }


    public static <T> ResponseData<T> convertEntity(String json, TypeToken<ResponseData<T>> token) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            return gson.fromJson(json, token.getType());
        }catch (Exception e){
            return null;
        }
    }

    /**
     * <pre>
     * Json格式转换, 由JSON字符串转化到制定类型T
     *
     * @param json
     * @param cls
     * @return T
     *
     * <pre>
     */
    public static <T> T convertObj(String json, Class<T> cls) {
        try {
            if (TextUtils.isEmpty(json)) {
                return null;
            }
            return gson.fromJson(json, cls);
        }catch (Exception e){
            return null;
        }
    }



    /**
     * <pre>
     * java对象转化JSON
     *
     * @return String
     *
     * <pre>
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return "";
        }
        return gson.toJson(obj);
    }

    /**
     * get String from jsonObject
     *
     * @param jsonObject
     * @param key
     * @param defaultValue
     * @return <ul>
     * <li>if jsonObject is null, return defaultValue</li>
     * <li>if key is null or empty, return defaultValue</li>
     * <li>if {@link JSONObject#getString(String)} exception, return defaultValue</li>
     * <li>return {@link JSONObject#getString(String)}</li>
     * </ul>
     */
    public static String getString(JSONObject jsonObject, String key, String defaultValue) {
        if (jsonObject == null || TextUtils.isEmpty(key)) {
            return defaultValue;
        }

        try {
            return jsonObject.getString(key);
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    /**
     * get Int from jsonObject
     *
     * @param jsonObject
     * @param key
     * @param defaultValue
     * @return <ul>
     * <li>if jsonObject is null, return defaultValue</li>
     * <li>if key is null or empty, return defaultValue</li>
     * <li>if {@link JSONObject#getInt(String)} exception, return defaultValue</li>
     * <li>return {@link JSONObject#getInt(String)}</li>
     * </ul>
     */
    public static Integer getInt(JSONObject jsonObject, String key, Integer defaultValue) {
        if (jsonObject == null || TextUtils.isEmpty(key)) {
            return defaultValue;
        }

        try {
            return jsonObject.getInt(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
}
