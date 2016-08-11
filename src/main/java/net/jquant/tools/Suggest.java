package net.jquant.tools;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import net.jquant.downloader.Downloader;

import java.util.List;
import java.util.Map;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-8-15.
 */
public class Suggest {

    private static final String suggestURL = "http://app.finance.ifeng.com/hq/suggest_v2.php?t=stock&q=%s";

    public static final String NAME = "n";
    public static final String CODE = "s";
    public static final String PIN_YIN = "p";
    public static final String TYPE = "t";

    /**
     * 输入中文或者英文片段查询股票,例如：中国,282等关键词
     * @param query query field
     * @return stock name and code
     */
    public static List<Map<String, String>> suggest(String query) {
        //例如：[{"c":"sz000009","s":"000009","n":"\u4e2d\u56fd\u5b9d\u5b89","p":"ZGBA","t":"stock"},{"c":"sz000035","s":"000035","n":"\u4e2d\u56fd\u5929\u6979","p":"ZGTY","t":"stock"},{"c":"sz000797","s":"000797","n":"\u4e2d\u56fd\u6b66\u5937","p":"ZGWY","t":"stock"},{"c":"sz000951","s":"000951","n":"\u4e2d\u56fd\u91cd\u6c7d","p":"ZGZQ","t":"stock"},{"c":"sz000996","s":"000996","n":"\u4e2d\u56fd\u4e2d\u671f","p":"ZGZQ","t":"stock"},{"c":"sz002116","s":"002116","n":"\u4e2d\u56fd\u6d77\u8bda","p":"ZGHC","t":"stock"},{"c":"sh600007","s":"600007","n":"\u4e2d\u56fd\u56fd\u8d38","p":"ZGGM","t":"stock"},{"c":"sh600028","s":"600028","n":"\u4e2d\u56fd\u77f3\u5316","p":"ZGSH","t":"stock"},{"c":"sh600050","s":"600050","n":"\u4e2d\u56fd\u8054\u901a","p":"ZGLT","t":"stock"},{"c":"sh600056","s":"600056","n":"\u4e2d\u56fd\u533b\u836f","p":"ZGYY","t":"stock"}]
        String data = Downloader.download(getPath(query));
        if(!data.contains("[")){
            return Lists.newLinkedList();
        }
        data = data.substring(data.indexOf("[") - 1, data.indexOf("]")+1).trim();
        Gson gson = new Gson();
        List<Map<String,String>> results = gson.fromJson(data,List.class);
        return results;
    }

    public static String getPath(String query) {
        return String.format(suggestURL, query);
    }

    public static void main(String[] args) {
        List<Map<String, String>> results = Suggest.suggest("282");
        System.out.println(results.size());
        for(Map<String,String> map:results){
            System.out.println(map.get(NAME) + ":"+map.get(CODE));
        }
    }
}
