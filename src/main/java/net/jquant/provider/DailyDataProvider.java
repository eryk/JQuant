package net.jquant.provider;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.jquant.common.StockConstants;
import net.jquant.common.StockDataParseException;
import net.jquant.common.Utils;
import net.jquant.downloader.Downloader;
import net.jquant.downloader.THSJSDownloader;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jquant.common.Constants;

import net.jquant.model.StockData;
import net.jquant.model.Symbol;

import java.util.*;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-8-4.
 * 注意：复权只对开盘价，收盘价，最高价，最低价复权
 * 昨日收盘价，
 */
public class DailyDataProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DailyDataProvider.class);

    public static final String DAILY_DATA_URL = "http://quotes.money.163.com/service/chddata.html?code=%s&start=%s&end=%s&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP";

    private static List<StockData> getDailyDataWithURL(String symbol,String url){
        List<StockData> stocks = Lists.newLinkedList();
        try {
            String data = Downloader.download(url, "gb2312");
            String[] lines = data.split("\n");

            for (int i = 1; i < lines.length; i++) {    //第一行是标题，跳过
                String[] line = lines[i].split(",");
                //股票数据15列，指数数据13列，少最后两列
                if ((line.length == 15 || line.length == 13) && !lines[i].contains("None")) {
                    try {
                        StockData stock = new StockData(line[1].replace("'", ""));

                        stock.date = Utils.str2Date(line[0], Constants.NETEASE_DATE_STYLE);
                        stock.name = line[2];
                        for (int j = 0; j < StockConstants.DAILY.size() - 1; j++) {
                            if(line.length>j+3){
                                stock.put(StockConstants.DAILY.get(j), Utils.str2Double(line[j + 3]));
                            }
                        }
                        stock.put("amplitude", Utils.formatDouble((stock.get("high") - stock.get("low")) / stock.get("lastClose")));
                        changeUnit(stock);
                        stocks.add(stock);
                    } catch (Exception e) {
                        LOG.warn(String.format("stock %s convert error %s", symbol,lines[i]),e);
                    }
                }
            }

        } catch (Exception e) {
            LOG.error(String.format("stock %s collect error", symbol), e);
        }
        //按照时间从最早到最新
        return Lists.reverse(stocks);
    }

    /**
     * 获取指数日线数据
     * 上证综指:0000001
     * 深证成指:1399001
     * 深证综指:1399106
     * 沪深300:0000300
     * 创业板指:1399006
     * 创业板综:1399102
     * 中小板指:1399005
     * 中小板综:1399101
     * @param symbol stock symbol
     * @param startDate yyyyMMdd
     * @param stopDate yyyyMMdd
     * @return data list
     */
    public static List<StockData> getIndex(String symbol, String startDate, String stopDate){
        String url;
        if(symbol.startsWith("3")){
            url = getPath(symbol,startDate,stopDate);
        }else{
            url = String.format(DAILY_DATA_URL,"0"+symbol,startDate,stopDate);
        }
        return getDailyDataWithURL(symbol,url);
    }

    /**
     * 获取未复权数据
     * @param symbol stock symbol
     * @param startDate yyyyMMdd
     * @param stopDate yyyyMMdd
     * @return data list
     */
    public static List<StockData> get(String symbol,String startDate,String stopDate) throws StockDataParseException {
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

    private static StockData today(String symbol) throws StockDataParseException{
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

    private static StockData parseDailyData(String symbol,String line){
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

    //http://d.10jqka.com.cn/v2/line/hs_600133/01/2015.js
    public static String FQ_URL = "http://d.10jqka.com.cn/v2/line/hs_%s/01/%s.js";
    /**
     * 获取前复权数据
     * @param symbol stock symbol
     * @param startDate yyyyMMdd
     * @param stopDate yyyyMMdd
     * @return fq data list
     */
    public static List<StockData> getFQ(String symbol, String startDate, String stopDate){
        List<StockData> stockDataList = Lists.newLinkedList();
        List<String> years = Utils.getYearBetween(startDate,stopDate);
        for(String year:years){
            String url = String.format(FQ_URL, symbol, year);
            String data = Downloader.download(url);
            if(Strings.isNullOrEmpty(data)){
               continue;
            }
            String[] records = data.substring(47, data.length() - 3).split(";");
            for(int i =0;i<records.length;i++){
                String[] record = records[i].split(",");
                if(record.length==8){
                    StockData stockData = new StockData(symbol);
                    stockData.date = Utils.str2Date(record[0],"yyyyMMdd");
                    stockData.put(StockConstants.OPEN,Utils.str2Double(record[1]));
                    stockData.put(StockConstants.HIGH,Utils.str2Double(record[2]));
                    stockData.put(StockConstants.LOW,Utils.str2Double(record[3]));
                    stockData.put(StockConstants.CLOSE,Utils.str2Double(record[4]));
                    stockData.put(StockConstants.VOLUME,Utils.str2Double(record[5])/100);
                    stockData.put(StockConstants.AMOUNT,Utils.str2Double(record[6])/10000);
                    stockData.put(StockConstants.TURNOVER_RATE,Utils.str2Double(record[7]));
                    try{
                        if(stockDataList.size()>0){
                            stockData.put(StockConstants.LAST_CLOSE,stockDataList.get(i-1).get(StockConstants.CLOSE));
                            stockData.put(StockConstants.CHANGE_AMOUNT,stockData.get(StockConstants.CLOSE) - stockDataList.get(i-1).get(StockConstants.CLOSE));
                            stockData.put(StockConstants.AMPLITUDE, Utils.formatDouble((stockData.get(StockConstants.HIGH) - stockData.get(StockConstants.LOW)) / stockData.get(StockConstants.LAST_CLOSE))*100);
                            stockData.put(StockConstants.CHANGE, Utils.formatDouble((stockData.get(StockConstants.CHANGE_AMOUNT)) / stockData.get(StockConstants.LAST_CLOSE))*100);
                        }
                    }catch(Exception e){
                        //TODO CHECK
                    }
                    stockDataList.add(stockData);
                }else{
                    LOG.error(Joiner.on(",").join(record));
                }
            }
        }
        return stockDataList;
    }

    private static void changeUnit(StockData stockData) {
        stockData.put(StockConstants.VOLUME, stockData.get(StockConstants.VOLUME) / 100);    //成交量,单位：手
        stockData.put(StockConstants.AMOUNT, stockData.get(StockConstants.AMOUNT) / 10000);  //成交金额,单位：万
        //指数数据没有下面两项
        if(stockData.get(StockConstants.TOTAL_VALUE)!=null){
            stockData.put(StockConstants.TOTAL_VALUE, stockData.get(StockConstants.TOTAL_VALUE) / 100000000);    //总市值,单位:亿
        }
        if(stockData.get(StockConstants.MARKET_VALUE)!=null){
            stockData.put(StockConstants.MARKET_VALUE, stockData.get(StockConstants.MARKET_VALUE) / 100000000);   //流通市值,单位:亿
        }
    }

    /**
     * @param symbol stock symbol
     * @param startDate yyyyMMdd
     * @param stopDate  yyyyMMdd
     * @return url path with start and stop date
     */
    public static String getPath(String symbol, String startDate, String stopDate) {
        return String.format(DAILY_DATA_URL, Symbol.getSymbol(symbol, DAILY_DATA_URL), startDate, stopDate);
    }

}
