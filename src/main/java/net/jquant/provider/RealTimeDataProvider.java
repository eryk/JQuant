package net.jquant.provider;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

import net.jquant.common.StockConstants;
import net.jquant.common.Utils;
import net.jquant.downloader.Downloader;
import net.jquant.downloader.THSJSDownloader;
import net.jquant.model.StockData;
import net.jquant.model.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-8-30.
 */
public class RealTimeDataProvider {
    private static final Logger LOG = LoggerFactory.getLogger(RealTimeDataProvider.class);

    private static String realTimeDateURL = "http://nuff.eastmoney.com/EM_Finance2015TradeInterface/JS.ashx?id=%s&_=%s";

    private static Map<String, List<String>> collect(String symbol) {
        String data = Downloader.download(getPath(symbol));
        if (Strings.isNullOrEmpty(data)) {
            LOG.error("fail to get real time data from " + getPath(symbol));
            return Maps.newHashMap();
        }
        data = data.substring(9, data.length() - 1);
        Gson gson = new Gson();
        Map<String, List<String>> map = gson.fromJson(data, Map.class);
        return map;
    }

    /**
     * 获取实时股票交易数据
     * @param symbol stock symbol
     * @return stock data list
     */
    public static StockData get(String symbol){

        Map<String, List<String>> map = collect(symbol);

        if(map.size()==0){
            return new StockData();
        }
        List<String> columns = map.get("Value");
        if (columns.size() != 53) {
            LOG.warn(String.format("stock [%s] data is not format",symbol));
            return new StockData();
        }

        StockData stockData = new StockData(symbol);
        stockData.name = columns.get(2);
        stockData.date = Utils.str2Date(columns.get(49), "yyyy-MM-dd HH:mm:ss");

        for (int i = 3; i < columns.size() - 1; i++) {
            if (getColumnName(i).equals("amount")) {
                stockData.put(getColumnName(i), Utils.getAmount(columns.get(i)));
            } else if (!getColumnName(i).equals("")) {
                if (Utils.isDouble(columns.get(i))) {
                    stockData.put(getColumnName(i), Double.parseDouble(columns.get(i)));
                }
            }
        }
        changeUnit(stockData);
        return stockData;
    }

    private static void changeUnit(StockData stockData) {
        stockData.put(StockConstants.AMOUNT,stockData.get(StockConstants.AMOUNT)/10000);  //成交金额,单位：万
        stockData.put(StockConstants.TOTAL_VALUE,stockData.get(StockConstants.TOTAL_VALUE)/100000000);    //总市值,单位:亿
        stockData.put(StockConstants.MARKET_VALUE, stockData.get(StockConstants.MARKET_VALUE) / 100000000);   //流通市值,单位:亿
    }

    /**
     * 获取原始数据对应的列名称
     * @param i column id
     * @return column name
     */
    private static String getColumnName(int i) {
        return StockConstants.REALTIME.get(i);
    }

    public static String getPath(String symbol) {
        Date date = new Date();
        return String.format(realTimeDateURL, Symbol.getSymbol(symbol, realTimeDateURL), date.getTime());
    }

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

    public static void main(String[] args) {
        StockData stock = RealTimeDataProvider.get("000001");
        System.out.println(stock);
    }
}
