package net.jquant.downloader;

import com.google.common.collect.Maps;

import java.util.Map;

public class THSJSDownloader {

    public static Map<String,String> get(String urlBase,String symbol){
        Map<String,String> header = Maps.newHashMap();
        header.put("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1941.0 Safari/537.36");
        String value = BasicDownloader.download(String.format(urlBase, symbol),header);
        Map<String, Object> ret = JsonpParser.parser(value);
        if (ret.size() == 0) {
            return null;
        }
        Map<String, String> items = (Map<String, String>) ret.get("items");
        return items;
    }
}
