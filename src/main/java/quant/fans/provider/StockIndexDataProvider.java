package quant.fans.provider;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import quant.fans.common.Utils;
import quant.fans.downloader.Downloader;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 获取股票指标数据
 *      上证指数
 *      深证成指
 *      创业板指
 *      中小板指
 *      上证50
 *      中证500
 *      沪深300
 *      纳斯达克
 *      道琼斯
 *      标普指数
 */
public class StockIndexDataProvider {

    private static String stockIndexURL = "http://hq.sinajs.cn/rn=%s&list=s_sh000001,s_sz399001,s_sz399006,s_sz399005,s_sh000016,s_sh000905,s_sz399300"; //纳斯达克:gb_ixic,道琼斯:gb_dji,标普500:gb_inx

    public static List<Map<String,String>> get(){
        String url = String.format(stockIndexURL,new Date().getTime());
        String data = Downloader.download(url);
        List<Map<String,String>> result = Lists.newArrayList();
        String[] lines = data.split("\n");
        for(String line:lines){
            String[] fields = line.substring(line.indexOf("=\"")+1,line.length()-2).split(",");
            Map<String,String> map = Maps.newHashMap();
            map.put("name",fields[0]);
            map.put("close", fields[1]);
            map.put("changeAmount", fields[2]);
            map.put("change", fields[3]);
            map.put("volume", fields[4]);
            map.put("amount",fields[5]);  //单位：万元
            result.add(map);
        }
        return result;
    }

    public static void main(String[] args) {
        List<Map<String, String>> maps = StockIndexDataProvider.get();
        for(Map<String,String> map:maps){
            Utils.printMapStr(map);
        }
    }
}
