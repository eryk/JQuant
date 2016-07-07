package quant.fans.provider;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quant.fans.common.StockConstants;
import quant.fans.common.Utils;
import quant.fans.downloader.Downloader;
import quant.fans.model.StockData;
import quant.fans.model.Symbol;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static quant.fans.common.StockConstants.AMOUNT;
import static quant.fans.common.StockConstants.MARKET_VALUE;
import static quant.fans.common.StockConstants.TOTAL_VALUE;


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
     * @param symbol
     * @return
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
        stockData.put(AMOUNT,stockData.get(AMOUNT)/10000);  //成交金额,单位：万
        stockData.put(TOTAL_VALUE,stockData.get(TOTAL_VALUE)/100000000);    //总市值,单位:亿
        stockData.put(MARKET_VALUE, stockData.get(MARKET_VALUE) / 100000000);   //流通市值,单位:亿
    }

    /**
     * 获取原始数据对应的列名称
     * @param i
     * @return
     */
    private static String getColumnName(int i) {
        return StockConstants.REALTIME.get(i);
    }

    public static String getPath(String symbol) {
        Date date = new Date();
        return String.format(realTimeDateURL, Symbol.getSymbol(symbol, realTimeDateURL), date.getTime());
    }

    public static void main(String[] args) {
        StockData stock = RealTimeDataProvider.get("600376");
        System.out.println(stock);
    }
}
