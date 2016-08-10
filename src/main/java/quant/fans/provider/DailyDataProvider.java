package quant.fans.provider;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quant.fans.common.Constants;

import quant.fans.common.StockConstants;
import quant.fans.common.Utils;
import quant.fans.downloader.Downloader;
import quant.fans.model.StockData;
import quant.fans.model.Symbol;

import java.util.*;

import static quant.fans.common.StockConstants.*;
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

    public static final String DAILY_PRICE_HFQ_URl = "http://vip.stock.finance.sina.com.cn/api/json.php/BasicStockSrv.getStockFuQuanData?symbol=%s&type=hfq";

    public static final String DAILY_HFQ_URL = "http://vip.stock.finance.sina.com.cn/corp/go.php/vMS_FuQuanMarketHistory/stockid/%s.phtml"; //?year=%s&jidu=%s

    /**
     * 参数1：6位代码
     * year：年份
     * jidu：季度，1，2，3，4
     */
    public static final String DAILY_HFQ_PARAM_URL = "http://vip.stock.finance.sina.com.cn/corp/go.php/vMS_FuQuanMarketHistory/stockid/%s.phtml?year=%s&jidu=%s";

    /**
     * 获取前复权数据
     */
    private static Map<String, StockData> qfqData(String symbol, String startDate, String stopDate) {
        Map<String, StockData> stockDataMap = Maps.newHashMap();

        String hfqURL = String.format(DAILY_HFQ_URL, symbol);
        String data = Downloader.download(hfqURL, "gb2312");
        try {
            Elements select = Jsoup.parse(data).getElementById("con02-4").getElementsByTag("select").get(0).getElementsByTag("option");
            List<String> pages = Lists.newArrayListWithCapacity(50);
            for (Element option : select) {
                pages.add(option.text() + "" + "1231");
                pages.add(option.text() + "" + "0930");
                pages.add(option.text() + "" + "0630");
                pages.add(option.text() + "" + "0331");
            }
            Collections.reverse(pages);


            for (String page : pages) {
                Date date = Utils.str2Date(page, "yyyyMMdd");
                //TODO date < stop
                if (date.getTime() > Utils.str2Date(startDate, "yyyyMMdd").getTime() /*&& date.getTime() < Utils.str2Date(stopDate,"yyyyMMdd").getTime()*/) {
                    String url = String.format(DAILY_HFQ_PARAM_URL, symbol, Utils.formatDate(date, "yyyy"), getQuarter(date));
                    data = Downloader.download(url, "gb2312");
                    Elements tr = Jsoup.parse(data).getElementById("FundHoldSharesTable").getElementsByTag("tbody").get(0).getElementsByTag("tr");
                    for (int i = 1; i < tr.size(); i++) {
                        Elements td = tr.get(i).getElementsByTag("td");
                        Date stockDate = Utils.str2Date(td.get(0).text(), "yyyy-MM-dd");
                        if (stockDate.getTime() >= Utils.str2Date(startDate, "yyyyMMdd").getTime() && stockDate.getTime() <= Utils.str2Date(stopDate, "yyyyMMdd").getTime()) {
                            StockData stockData = new StockData(symbol);

                            stockData.date = Utils.str2Date(td.get(0).text(), "yyyy-MM-dd");
                            stockData.put(OPEN, Double.parseDouble(td.get(1).text()));
                            stockData.put(HIGH, Double.parseDouble(td.get(2).text()));
                            stockData.put(StockConstants.CLOSE, Double.parseDouble(td.get(3).text()));
                            stockData.put(StockConstants.LOW, Double.parseDouble(td.get(4).text()));
                            stockData.put(StockConstants.VOLUME, Double.parseDouble(td.get(5).text()) / 100);  //单位：手
                            stockData.put(StockConstants.AMOUNT, Double.parseDouble(td.get(6).text()) / 10000);//单位：万
                            stockData.put(StockConstants.FACTOR, Double.parseDouble(td.get(7).text()));
                            stockDataMap.put(td.get(0).text().replaceAll("-", ""), stockData);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("fail to get from url " + hfqURL);
        }

        return stockDataMap;
    }

    /**
     * 根据时间获取季度
     * @param date date
     * @return quarter
     */
    public static int getQuarter(Date date) {
        return (date.getMonth() / 3) + 1;
    }

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

    private static List<StockData> getDailyDataWithOutFQ(String symbol, String startDate, String stopDate) {
        String url = getPath(symbol, startDate, stopDate);
        return getDailyDataWithURL(symbol,url);
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
    public static List<StockData> getZS(String symbol,String startDate,String stopDate){
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
    public static List<StockData> get(String symbol,String startDate,String stopDate){
        return getDailyDataWithOutFQ(symbol, startDate, stopDate);
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
                    stockData.put(OPEN,Utils.str2Double(record[1]));
                    stockData.put(HIGH,Utils.str2Double(record[2]));
                    stockData.put(LOW,Utils.str2Double(record[3]));
                    stockData.put(CLOSE,Utils.str2Double(record[4]));
                    stockData.put(VOLUME,Utils.str2Double(record[5])/100);
                    stockData.put(AMOUNT,Utils.str2Double(record[6])/10000);
                    stockData.put(TURNOVER_RATE,Utils.str2Double(record[7]));
                    try{
                        if(stockDataList.size()>0){
                            stockData.put(LAST_CLOSE,stockDataList.get(i-1).get(CLOSE));
                            stockData.put(CHANGE_AMOUNT,stockData.get(CLOSE) - stockDataList.get(i-1).get(CLOSE));
                            stockData.put(AMPLITUDE, Utils.formatDouble((stockData.get(HIGH) - stockData.get(LOW)) / stockData.get(LAST_CLOSE))*100);
                            stockData.put(CHANGE, Utils.formatDouble((stockData.get(CHANGE_AMOUNT)) / stockData.get(LAST_CLOSE))*100);
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
        stockData.put(VOLUME, stockData.get(VOLUME) / 100);    //成交量,单位：手
        stockData.put(AMOUNT, stockData.get(AMOUNT) / 10000);  //成交金额,单位：万
        //指数数据没有下面两项
        if(stockData.get(TOTAL_VALUE)!=null){
            stockData.put(TOTAL_VALUE, stockData.get(TOTAL_VALUE) / 100000000);    //总市值,单位:亿
        }
        if(stockData.get(MARKET_VALUE)!=null){
            stockData.put(MARKET_VALUE, stockData.get(MARKET_VALUE) / 100000000);   //流通市值,单位:亿
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
