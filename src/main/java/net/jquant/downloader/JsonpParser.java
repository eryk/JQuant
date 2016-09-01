package net.jquant.downloader;

import com.google.common.base.Strings;
import com.google.gson.Gson;

import java.util.Map;

public class JsonpParser {

    public static Map<String, Object> parser(String jsonStr) {
        if (Strings.isNullOrEmpty(jsonStr)) {
            return null;
        }
        String json = jsonStr.substring(jsonStr.indexOf("(") + 1 , jsonStr.length() -1);
        Gson gson = new Gson();
        return gson.fromJson(json, Map.class);
    }
}
