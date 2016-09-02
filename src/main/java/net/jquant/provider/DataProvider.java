package net.jquant.provider;

import net.jquant.downloader.THSJSDownloader;
import net.jquant.model.StockData;

import java.util.List;
import java.util.Map;

/**
 * 成交量,单位：手
 * 成交金额,单位：万
 * 总市值,单位:亿
 * 流通市值,单位:亿
 * 外盘,单位:手
 * 内盘,单位:手
 */
public class DataProvider {

    /**
     * http://d.10jqka.com.cn/v2/fiverange/hs_300033/last.js
     *
     * @return 五挡买卖数据
     */
    private StockData fiveRange(String symbol) {
        String url = "http://d.10jqka.com.cn/v2/fiverange/hs_%s/last.js";
        Map<String, Object> result = THSJSDownloader.download(url, symbol);
        Map<String, String> parser = (Map<String, String>) result.get("items");
        StockData stockData = new StockData(symbol);
        if(parser != null){
            if (parser.size() > 0) {
                stockData.put("buy1", Double.parseDouble(parser.get("24")));
                stockData.put("buy1Volume", Double.parseDouble(parser.get("25")) / 100);
                stockData.put("buy2", Double.parseDouble(parser.get("26")));
                stockData.put("buy2Volume", Double.parseDouble(parser.get("27")) / 100);
                stockData.put("buy3", Double.parseDouble(parser.get("28")));
                stockData.put("buy3Volume", Double.parseDouble(parser.get("29")) / 100);
                stockData.put("buy4", Double.parseDouble(parser.get("150")));
                stockData.put("buy4Volume", Double.parseDouble(parser.get("151")) / 100);
                stockData.put("buy5", Double.parseDouble(parser.get("154")));
                stockData.put("buy5Volume", Double.parseDouble(parser.get("155")) / 100);
                stockData.put("sell1", Double.parseDouble(parser.get("30")));
                stockData.put("sell1Volume", Double.parseDouble(parser.get("31")) / 100);
                stockData.put("sell2", Double.parseDouble(parser.get("32")));
                stockData.put("sell2Volume", Double.parseDouble(parser.get("33")) / 100);
                stockData.put("sell3", Double.parseDouble(parser.get("34")));
                stockData.put("sell3Volume", Double.parseDouble(parser.get("35")) / 100);
                stockData.put("sell4", Double.parseDouble(parser.get("152")));
                stockData.put("sell4Volume", Double.parseDouble(parser.get("153")) / 100);
                stockData.put("sell5", Double.parseDouble(parser.get("156")));
                stockData.put("sell5Volume", Double.parseDouble(parser.get("157")) / 100);
                return stockData;
            }
        }
        return stockData;
    }

    /**
     * 获取实时股票数据
     * @param symbol 股票代码
     * @return 实时股票数据
     */
    public StockData realtime(String symbol) {
        String url = "http://d.10jqka.com.cn/v2/realhead/hs_%s/last.js";
        Map<String,Object> result = THSJSDownloader.download(url,symbol);
        StockData stockData = new StockData(symbol);
        if(result != null){

        }
        stockData = RealTimeDataProvider.get(symbol);
        return stockData;
    }

    public List<StockData> daily(String symbol){
        String lastUrl = "http://d.10jqka.com.cn/v2/line/hs_%s/01/last.js";
        Map<String, Object> stringObjectMap = THSJSDownloader.download(lastUrl, symbol);
        Map<String,String> years = (Map<String, String>) stringObjectMap.get("year");
        String yearUrl = "http://d.10jqka.com.cn/v2/line/hs_%s/01/%s.js";
        for(String year : years.keySet()){
//            Map<String,String> data = THSJSDownloader.download(String.format())
        }
        return null;
    }

    public static void main(String[] args) {
        DataProvider dataProvider = new DataProvider();
        StockData fiverange = dataProvider.fiveRange("002121");
        System.out.println(fiverange);
    }
}
