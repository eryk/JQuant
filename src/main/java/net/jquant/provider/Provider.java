package net.jquant.provider;

import net.jquant.Indicators;
import net.jquant.common.StockDataParseException;
import net.jquant.common.Utils;
import net.jquant.model.StockBlock;
import net.jquant.model.StockData;
import net.jquant.model.Tick;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.jquant.common.DateRange;
import net.jquant.tools.StockCategory;
import net.jquant.tools.StockList;

import java.util.*;

/**
 * 成交量,单位：手
 * 成交金额,单位：万
 * 总市值,单位:亿
 * 流通市值,单位:亿
 * 外盘,单位:手
 * 内盘,单位:手
 */
public class Provider {

    private static final Logger LOG = LoggerFactory.getLogger(Provider.class);

    /**
     * 获取指定时间段内的日线股票数据
     *
     * @param symbol stock symbol
     * @param startDate 格式：yyyyMMdd
     * @param stopDate  格式：yyyyMMdd
     * @return stock data list
     */
    public static List<StockData> dailyData(String symbol, String startDate, String stopDate) throws StockDataParseException {
        return DailyDataProvider.get(symbol, startDate, stopDate);
    }

    /**
     * 获取日线级别最近250天历史数据
     *
     * @param symbol stock symbol
     * @return stock data list
     */
    public static List<StockData> dailyData(String symbol) throws StockDataParseException {
        DateRange range = DateRange.getRange(250);
        return dailyData(symbol, range.start(),range.stop());
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
     * @param symbol index symbol
     * @param startDate yyyyMMdd
     * @param stopDate yyyyMMdd
     * @return stock data list
     */
    public static List<StockData> dailyDataZS(String symbol,String startDate,String stopDate){
        return DailyDataProvider.getZS(symbol, startDate, stopDate);
    }

    public static List<StockData> dailyDataZS(String symbol,int period){
        DateRange dateRange = DateRange.getRange(period);
        return dailyDataZS(symbol, dateRange.start(), dateRange.stop());
    }

    public static List<StockData> dailyDataZS(String symbol){
        DateRange dateRange = DateRange.getRange(250);
        return dailyDataZS(symbol, dateRange.start(), dateRange.stop());
    }

    /**
     * 获取实时数据股票数据
     * @param symbol stock symbol
     * @return stock data list
     */
    public static StockData realtimeData(String symbol) {
        return RealTimeDataProvider.get(symbol);
    }

    /**
     * 获取最新10天股票分钟级别数据
     *
     * @param symbol stock symbol
     * @param type   参数值：5,15,30,60
     * @return stock data list
     */
    public static List<StockData> minuteData(String symbol, String type) {
        DateRange range = DateRange.getRange(120);//数据源不够30天
        List<StockData> stockDataList = minuteData(symbol, range.start(), range.stop(), type);
        return stockDataList;
    }

    /**
     * 获取指定时间段内历史分钟级别数据，受数据源限制
     *
     * @param symbol stock symbol
     * @param startDate 格式：yyyyMMdd
     * @param stopDate  格式：yyyyMMdd
     * @param type 5,15,30,60
     * @return stock data list
     */
    public static List<StockData> minuteData(String symbol, String startDate, String stopDate, String type) {
        return MinuteDataProvider.get(symbol, startDate, stopDate, type);
    }

    /**
     * 获取个股当日资金流数据
     * @param symbol stock symbol
     * @return stock data list
     */
    public static StockData moneyFlowData(String symbol) {
        return MoneyFlowDataProvider.get(symbol);
    }

    /**
     * 获取个股指定时间段内资金流数据，受数据源限制
     *
     * @param symbol stock symbol
     * @param startDate 格式：yyyyMMdd
     * @param stopDate  格式：yyyyMMdd
     * @return stock data list
     */
    public static List<StockData> moneyFlowData(String symbol, String startDate, String stopDate) {
        return MoneyFlowDataProvider.get(symbol, startDate, stopDate);
    }

    /**
     * 大盘资金流向历史数据
     * @return stock data list
     */
    public static List<StockData> moneyFlowDapanData() {
        return MoneyFlowDataProvider.getDapan();
    }

    /**
     * 获取今天、5日、10日行业版块资金流数据
     *
     * @param type 1,5,10
     * @return stock data list
     */
    public static List<StockData> moneyFlowIndustryData(String type) {
        return MoneyFlowDataProvider.getIndustry(type);
    }

    /**
     * 获取今天、5日、10日行业版块资金流数据
     *
     * @param type 输入值：1,5,10
     * @return stock data list
     */
    public static List<StockData> moneyFlowConceptData(String type) {
        return MoneyFlowDataProvider.getConcept(type);
    }

    /**
     * 获取今天、5日、10日行业版块资金流数据
     *
     * @param type 1,5,10
     * @return stock data list
     */
    public static List<StockData> moneyFlowRegionData(String type) {
        return MoneyFlowDataProvider.getRegion(type);
    }

    /**
     * 历史财报
     *
     * @param symbol stock symbol
     * @param startDate 格式：yyyyMMdd
     * @param stopDate  格式：yyyyMMdd
     * @return stock data list
     */
    public static List<StockData> financeData(String symbol, String startDate, String stopDate) {
        return FinanceDataProvider.get(symbol, startDate, stopDate);
    }

    /**
     * 最新一期财报数据
     * @param symbol stock symbol
     * @return stock data list
     */
    public static StockData financeData(String symbol) {
        DateRange range = DateRange.getRange(365);
        List<StockData> stockDataList = FinanceDataProvider.get(symbol, range.start(), range.stop());
        if (stockDataList.size() >= 1) {
            return stockDataList.get(stockDataList.size() - 1);
        } else {
            return new StockData(symbol);
        }
    }

    /**
     * 股票年报数据
     * @param symbol stock symbol
     * @return stock data list
     */
    public static List<StockData> financeYearData(String symbol) {
        return FinanceDataProvider.getYear(symbol);
    }

    /**
     * 获取最新一天股票逐笔数据
     *
     * @param symbol stock symbol
     * @return tick list
     */
    public static List<Tick> tickData(String symbol) {
        return TickDataProvider.get(symbol);
    }

    /**
     * 获取指定日期逐笔股票数据
     *
     * @param symbol stock symbol
     * @param date   格式: yyyyMMdd
     * @return tick data list
     */
    public static List<Tick> tickData(String symbol, String date) {
        String _date = Utils.formatDate(Utils.str2Date(date, "yyyyMMdd"), "yyyy-MM-dd");
        return TickDataProvider.get(symbol, _date);
    }

    /**
     * 获取股票版块数据
     * map key: 股票的版块分类名称，包含三项：概念，地区，行业
     * value: list是版块分类下的版块，每个版块包含一个股票列表
     * @return stock data list
     */
    public static Map<String, List<StockBlock>> stockBlock() {
        return StockCategory.getCategory();
    }

    /**
     * 获取某个股票在大分类下的具体分类:概念，行业，地域
     * @param type category
     * @return stock data list
     */
    public static Map<String, Set<String>> stockCategory(String type) {
        return StockCategory.getStockCategory(type);
    }

    /**
     * 获取股票列表
     * @return stock data list
     */
    public static List<String> stockList() {
        return StockList.getSymbols();
    }

    //计算是今天第几个bar，从1开始
    private long getTimeSlot(long curTime, long startTime, int interval) {
        return (curTime - startTime) / interval;
    }
}
