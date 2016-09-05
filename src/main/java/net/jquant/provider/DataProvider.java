package net.jquant.provider;

import com.google.common.collect.Lists;
import net.jquant.common.StockDataParseException;
import net.jquant.common.Utils;
import net.jquant.downloader.THSJSDownloader;
import net.jquant.model.StockData;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

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
        if (parser != null) {
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
     *
     * @param symbol 股票代码
     * @return 实时股票数据
     */
    public StockData realtime(String symbol) throws StockDataParseException{
        String url = "http://d.10jqka.com.cn/v2/realhead/hs_%s/last.js";
        Map<String, Object> result = THSJSDownloader.download(url, symbol);
        StockData stockData = new StockData(symbol);
        if (result != null) {

        }
        stockData = RealTimeDataProvider.get(symbol);
        return stockData;
    }

    private StockData today(String symbol) throws StockDataParseException{
        String url = "http://d.10jqka.com.cn/v2/line/hs_%s/01/today.js";
        Map<String, Object> download = THSJSDownloader.download(url, symbol);
        if(download == null || download.size() == 0){
            throw new StockDataParseException();
        }
        Map<String,Object> values = (Map<String, Object>) download.get("hs_" + symbol);
        if(values == null || values.size() == 0){
            throw new StockDataParseException();
        }
        StockData stockData = new StockData(symbol);
        stockData.date = DateTimeFormat.forPattern("yyyyMMddHHmm").parseLocalDateTime(
                String.valueOf(values.get("1")) + String.valueOf(values.get("dt"))).toDate();
        stockData.put("open",Double.parseDouble((String) values.get("7")));
        stockData.put("high",Double.parseDouble((String) values.get("8")));
        stockData.put("low",Double.parseDouble((String) values.get("9")));
        stockData.put("close",Double.parseDouble((String) values.get("11")));
        stockData.put("volume", (Double) values.get("13") /100);
        stockData.put("amount",Double.parseDouble((String) values.get("19")) /10000);
        stockData.put("turnover",Double.parseDouble((String) values.get("1968584")));
        return stockData;
    }

    /**
     * 日线数据获取
     * @param symbol
     * @return 返回日线数据股票列表
     * @throws StockDataParseException
     */
    public List<StockData> daily(String symbol) throws StockDataParseException{
        String lastUrl = "http://d.10jqka.com.cn/v2/line/hs_%s/01/last.js";
        Map<String, Object> stringObjectMap = THSJSDownloader.download(lastUrl, symbol);
        Map<String, String> years = (Map<String, String>) stringObjectMap.get("year");
        String yearUrl = "http://d.10jqka.com.cn/v2/line/hs_%s/01/%s.js";
        List<StockData> stockDatas = Lists.newLinkedList();
        for (String year : years.keySet()) {
            Map<String, Object> tmp = THSJSDownloader.download(String.format(yearUrl, symbol, year));
            if (tmp == null || tmp.size() == 0){
                throw new StockDataParseException();
            }
            String data = (String) tmp.get("data");
            if(data == null){
                throw new StockDataParseException();
            }
            String[] array = data.split(";");
            for(String line:array){
                StockData stockData = parseDailyData(symbol,line);
                if(stockData != null){
                    stockDatas.add(stockData);
                }else{
                    throw new StockDataParseException();
                }
            }
        }
        if(stockDatas.size() > 0){
            StockData stockData = stockDatas.get(stockDatas.size()-1);
            if(!Utils.isToday(stockData.date)){
                stockDatas.add(today(symbol));
            }
        }
        return stockDatas;
    }

    public StockData parseDailyData(String symbol,String line){
        String[] fields = line.split(",");
        if(fields.length != 8){
            return null;
        }
        DateTime dateTime = DateTimeFormat.forPattern("yyyyMMdd").parseDateTime(fields[0]);
        StockData stockData = new StockData(symbol);
        stockData.date = dateTime.toDate();
        stockData.put("open",Double.parseDouble(fields[1]));
        stockData.put("high",Double.parseDouble(fields[2]));
        stockData.put("low",Double.parseDouble(fields[3]));
        stockData.put("close",Double.parseDouble(fields[4]));
        stockData.put("volume",Double.parseDouble(fields[5])/100);
        stockData.put("amount",Double.parseDouble(fields[6])/10000);
        stockData.put("turnover",Double.parseDouble(fields[7]));
        return stockData;
    }

    public static void main(String[] args) throws StockDataParseException {
        DataProvider dataProvider = new DataProvider();
        List<StockData> fiverange = dataProvider.daily("002121");
        for(StockData stockData :fiverange){
            System.out.println(stockData);
        }

    }
}
