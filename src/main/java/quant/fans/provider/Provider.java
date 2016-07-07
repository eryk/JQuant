package quant.fans.provider;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quant.fans.Indicators;
import quant.fans.StrategyUtils;
import quant.fans.common.Conditions;
import quant.fans.common.DateRange;
import quant.fans.common.StockConstants;
import quant.fans.common.Utils;
import quant.fans.model.*;
import quant.fans.tools.StockCategory;
import quant.fans.tools.StockList;

import java.util.*;

/**
 * 成交量,单位：手
 * 成交金额,单位：万
 * 总市值,单位:亿
 * 流通市值,单位:亿
 * 外盘,单位:手
 * 内盘,单位:手
 * <p>
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-8-27.
 */
public class Provider {

    private static final Logger LOG = LoggerFactory.getLogger(Provider.class);

    private static Indicators indicators = new Indicators();

    /**
     * 获取指定时间段内的日线股票数据
     *
     * @param symbol
     * @param startDate 格式：yyyyMMdd
     * @param stopDate  格式：yyyyMMdd
     * @return
     */
    public static List<StockData> dailyData(String symbol, String startDate, String stopDate) {
        return DailyDataProvider.getFQ(symbol, startDate, stopDate);
    }

    public static List<StockData> dailyData(String symbol,String startDate,String stopDate,boolean isFQ){
        if(isFQ){
            return DailyDataProvider.getFQ(symbol,startDate,stopDate);
        }else{
            return DailyDataProvider.get(symbol,startDate,stopDate);
        }
    }

    /**
     * 获取日线级别最近250天历史数据
     *
     * @param symbol
     * @return
     */
    public static List<StockData> dailyData(String symbol) {
        return dailyData(symbol, true);
    }

    /**
     * 获取日线级别最近250天历史数据
     *
     * @param symbol
     * @return
     */
    public static List<StockData> dailyData(String symbol,boolean isFQ) {
        DateRange range = DateRange.getRange(250);
        return dailyData(symbol, range.start(), range.stop(),isFQ);
    }

    /**
     * 获取指定天数内的日线股票数据
     *
     * @param symbol 股票代码，例如：600001
     * @param period 时间长度，最近多少个交易日内的股票数据，单位：天
     * @return
     */
    public static List<StockData> dailyData(String symbol, int period) {
        return dailyData(symbol,period,true);
    }

    /**
     * 获取指定天数内的日线股票数据
     *
     * @param symbol 股票代码，例如：600001
     * @param period 时间长度，最近多少个交易日内的股票数据，单位：天
     * @return
     */
    public static List<StockData> dailyData(String symbol, int period,boolean isFQ) {
        DateRange dateRange = DateRange.getRange(period);
        return dailyData(symbol, dateRange.start(), dateRange.stop(),isFQ);
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
     * @param symbol
     * @param startDate
     * @param stopDate
     * @return
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
     *
     * @param symbol
     * @return
     */
    public static StockData realtimeData(String symbol) {
        return RealTimeDataProvider.get(symbol);
    }

    /**
     * 获取最新10天股票分钟级别数据
     *
     * @param symbol
     * @param type   参数值：5,15,30,60
     * @return
     */
    public static List<StockData> minuteData(String symbol, String type) {
        DateRange range = DateRange.getRange(120);//数据源不够30天
        List<StockData> stockDataList = minuteData(symbol, range.start(), range.stop(), type);
        return stockDataList;
    }

    /**
     * 获取指定时间段内历史分钟级别数据，受数据源限制
     *
     * @param symbol
     * @param startDate 格式：yyyyMMdd
     * @param stopDate  格式：yyyyMMdd
     * @param type
     * @return
     */
    public static List<StockData> minuteData(String symbol, String startDate, String stopDate, String type) {
        return MinuteDataProvider.get(symbol, startDate, stopDate, type);
    }

    /**
     * 获取个股当日资金流数据
     *
     * @param symbol
     * @return
     */
    public static StockData moneyFlowData(String symbol) {
        return MoneyFlowDataProvider.get(symbol);
    }

    /**
     * 获取个股指定时间段内资金流数据，受数据源限制
     *
     * @param symbol
     * @param startDate 格式：yyyyMMdd
     * @param stopDate  格式：yyyyMMdd
     * @return
     */
    public static List<StockData> moneyFlowData(String symbol, String startDate, String stopDate) {
        return MoneyFlowDataProvider.get(symbol, startDate, stopDate);
    }

    /**
     * 大盘资金流向历史数据
     *
     * @return
     */
    public static List<StockData> moneyFlowDapanData() {
        return MoneyFlowDataProvider.getDapan();
    }

    /**
     * 获取今天、5日、10日行业版块资金流数据
     *
     * @param type 1,5,10
     * @return
     */
    public static List<StockData> moneyFlowIndustryData(String type) {
        return MoneyFlowDataProvider.getIndustry(type);
    }

    /**
     * 获取今天、5日、10日行业版块资金流数据
     *
     * @param type 输入值：1,5,10
     * @return
     */
    public static List<StockData> moneyFlowConceptData(String type) {
        return MoneyFlowDataProvider.getConcept(type);
    }

    /**
     * 获取今天、5日、10日行业版块资金流数据
     *
     * @param type 1,5,10
     * @return
     */
    public static List<StockData> moneyFlowRegionData(String type) {
        return MoneyFlowDataProvider.getRegion(type);
    }

    /**
     * 历史财报
     *
     * @param symbol
     * @param startDate 格式：yyyyMMdd
     * @param stopDate  格式：yyyyMMdd
     * @return
     */
    public static List<StockData> financeData(String symbol, String startDate, String stopDate) {
        return FinanceDataProvider.get(symbol, startDate, stopDate);
    }

    /**
     * 最新一期财报数据
     *
     * @param symbol
     * @return
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
     *
     * @param symbol
     * @return
     */
    public static List<StockData> financeYearData(String symbol) {
        return FinanceDataProvider.getYear(symbol);
    }

    /**
     * 获取最新一天股票逐笔数据
     *
     * @param symbol
     * @return
     */
    public static List<Tick> tickData(String symbol) {
        return TickDataProvider.get(symbol);
    }

    /**
     * 获取指定日期逐笔股票数据
     *
     * @param symbol
     * @param date   格式: yyyyMMdd
     * @return
     */
    public static List<Tick> tickData(String symbol, String date) {
        String _date = Utils.formatDate(Utils.str2Date(date, "yyyyMMdd"), "yyyy-MM-dd");
        return TickDataProvider.get(symbol, _date);
    }

    /**
     * 获取股票版块数据
     * map key: 股票的版块分类名称，包含三项：概念，地区，行业
     * value: list是版块分类下的版块，每个版块包含一个股票列表
     *
     * @return
     */
    public static Map<String, List<StockBlock>> stockBlock() {
        return StockCategory.getCategory();
    }

    /**
     * 获取某个股票在大分类下的具体分类
     *
     * @param type 概念，行业，地域
     * @return
     */
    public static Map<String, Set<String>> stockCategory(String type) {
        return StockCategory.getStockCategory(type);
    }

    /**
     * 获取股票列表
     *
     * @return
     */
    public static List<String> stockList() {
        return StockList.getList();
    }

    /**
     * 获取交易中的股票列表，剔除了退市和停牌的股票
     *
     * @return
     */
    public static List<String> tradingStockList() {
        return StockList.getTradingStockList();
    }

    /**
     * 按照条件过滤,获取交易中的股票列表
     *
     * @param conditions
     * @return
     */
    public static List<String> tradingStockList(Conditions conditions) {
        return StockList.getTradingStockList(conditions);
    }

    public static List<String> tradingStockList(List<String> stockList){
        return StockList.getTradingStockList(stockList);
    }

    public static List<String> getStockListWithConditions(List<String> stockList, Conditions conditions){
        return StockList.getStockListWithConditions(stockList, conditions);
    }

    /**
     * 获取参数列表中可融资股票列表
     * @param stockList
     * @return
     */
    public static List<String> marginTradingStockList(List<String> stockList){
        return StockList.getMarginTradingStockList(stockList);
    }

    /**
     * 获取融资融券股票列表
     * @return
     */
    public static List<String> marginTradingStockList(){
        return StockList.getMarginTradingStockList();
    }

    /**
     * 股东人数变化
     * mkt=市场
     * 1=沪深A股
     * 2=沪市A股
     * 3=深市A股
     * 4=中小板
     * 5=创业板
     * fd=时间,格式yyyy-MM-dd,季度末
     */
    public static List<StockData> getShareHolderCountData(String market, String data) {
        return ReferenceDataProvider.getShareHolderCountData(market, data);
    }

    /**
     * 获取个股股东人数变化数据
     * @param symbol
     * @return
     */
    public static List<StockData> getShareHolderCountData(String symbol) {
        return ReferenceDataProvider.getShareHolderCountData(symbol);
    }

    /**
     * 获取分配预案数据
     *
     * @param year 年份
     * @return
     */
    public static List<StockData> getFPYA(String year) {
        return ReferenceDataProvider.getFPYA(year);
    }

    /**
     * 获取大盘融资融券数据
     *
     * @return
     */
    public static List<StockData> getTotalMarginTrade() {
        return ReferenceDataProvider.getTotalMarginTrade();
    }

    /**
     * 获取两市融资融券数据
     * 市场融资融券交易总量＝本日融资余额＋本日融券余量金额
     * 本日融资余额＝前日融资余额＋本日融资买入额－本日融资偿还额；
     * 本日融资偿还额＝本日直接还款额＋本日卖券还款额＋本日融资强制平仓额＋本日融资正权益调整－本日融资负权益调整；
     * 本日融券余量=前日融券余量+本日融券卖出数量-本日融券偿还量；
     * 本日融券偿还量＝本日买券还券量＋本日直接还券量＋本日融券强制平仓量＋本日融券正权益调整－本日融券负权益调整－本日余券应划转量；
     * 融券单位：股（标的证券为股票）/份（标的证券为基金）/手（标的证券为债券）。
     *
     * @return
     */
    public static List<StockData> getMarginTrade() {
        return ReferenceDataProvider.getMarginTrade();
    }

    /**
     * 个股研究报告
     *
     * @param startDate 报告起始时间，格式:yyyyMMdd
     * @return
     */
    public static List<StockData> getStockReportData(String startDate) {
        return ReportDataProvider.getStockReportData(startDate);
    }

    /**
     * 获取盈利预期数据
     */
    public static List<StockData> getExpectEarnings() {
        return ReportDataProvider.getExpectEarnings();
    }

    /**
     * 获取股票指标数据
     * 上证指数
     * 深证成指
     * 创业板指
     * 中小板指
     * 上证50
     * 中证500
     * 沪深300
     * 纳斯达克
     * 道琼斯
     * 标普指数
     */
    public static List<Map<String, String>> getStockIndexData() {
        return StockIndexDataProvider.get();
    }

    /**
     * 每日龙虎榜详情
     *
     * @param date yyyyMMdd
     * @return
     */
    public static List<StockData> getDailyTopList(String date) {
        return TopListDataProvider.getDailyTopList(date);
    }

    /**
     * 个股龙虎榜统计
     *
     * @param dayCount 取值:5,10,30,60
     * @return
     */
    public static List<StockData> getStockRanking(int dayCount) {
        return TopListDataProvider.getStockRanking(dayCount);
    }

    //TODO
    public static List<StockData> getOrganizationRanking(int dayCount) {
        return TopListDataProvider.getOrganizationRanking(dayCount);
    }

    /**
     * 机构席位成交明细
     *
     * @param count 获取最近多少条
     * @return
     */
    public static List<StockData> getOrganizationDetailRanking(int count) {
        return TopListDataProvider.getOrganizationDetailRanking(count);
    }

    /**
     * 计算给定股票一段时间内的macd数据，时间周期由传入的list决定
     *
     * @param stockDataList
     * @return
     */
    public static List<StockData> computeMACD(List<StockData> stockDataList) {
        List<StockData> macdStockDataList = Lists.newArrayListWithCapacity(stockDataList.size());

        double[] closes = Utils.getArrayFrom(stockDataList, StockConstants.CLOSE);
        double[][] macd = indicators.macd(closes);

        for (int i = 0; i < stockDataList.size(); i++) {
            StockData stockData = stockDataList.get(i);
            double dif = macd[0][i];
            double dea = macd[1][i];
            double macdRtn = (dif - dea) * 2;
            stockData.put(StockConstants.DIF, Utils.formatDouble(macd[0][i], "#.##"));
            stockData.put(StockConstants.DEA, Utils.formatDouble(macd[1][i], "#.##"));
            stockData.put(StockConstants.MACD, Utils.formatDouble(macdRtn, "#.##"));
            //判断是否金叉
            if (i >= 1) {
                double dif_old = macd[0][i - 1];
                double dea_old = macd[1][i - 1];
                if (dea_old > dif_old && dea < dif) {//判断是否金叉
                    stockData.put(StockConstants.MACD_CROSS, 1d);
                } else if (dea_old < dif_old && dea > dif) {//判断是否死叉
                    stockData.put(StockConstants.MACD_CROSS, 0d);
                }
            }

            macdStockDataList.add(stockData);
        }
        return macdStockDataList;
    }

    /**
     * 计算给定股票一段时间内的macd数据
     *
     * @param period 时间长度，多少个交易日的数据，单位，天
     * @return
     */
    public static List<StockData> computeDailyMACD(String symbol, int period) {
        /**
         * 周期+60
         * 1. 测试发现周期过短，指标数值不准
         * 2. 初始值26天无数据
         */
        List<StockData> dailyData = Provider.dailyData(symbol, period + 60);
        List<StockData> stockDataList = computeMACD(dailyData);
        if(stockDataList.size()>60){
            stockDataList = stockDataList.subList(60, stockDataList.size());
        }else{
            return Lists.newArrayList();
        }
        return Lists.newArrayList(stockDataList);
    }

    public static List<StockData> computeDailyBoll(String symbol, int period) {
        List<StockData> dailyData = Provider.dailyData(symbol, period + 60);
        List<StockData> stockDataList = computeBoll(dailyData);
        stockDataList = stockDataList.subList(60, stockDataList.size());
        return stockDataList;
    }

    public static List<StockData> computeBoll(List<StockData> stockDataList) {
        List<StockData> bollStockDatas = Lists.newArrayListWithCapacity(stockDataList.size());
        double[] closes = Utils.getArrayFrom(stockDataList, StockConstants.CLOSE);
        double[][] bbands = indicators.boll(closes);
        for (int i = 0; i < stockDataList.size(); i++) {
            StockData stockData = stockDataList.get(i);
            double upper = Utils.formatDouble(bbands[0][i], "#.##");
            double mid = Utils.formatDouble(bbands[1][i], "#.##");
            double lower = Utils.formatDouble(bbands[2][i], "#.##");
            stockData.put(StockConstants.UPPER, upper);
            stockData.put(StockConstants.MID, mid);
            stockData.put(StockConstants.LOWER, lower);
            bollStockDatas.add(stockData);
        }
        return bollStockDatas;
    }

    public static List<StockData> computeDailyMA(String symbol, int period, String columnName) {
        List<StockData> dailyData = Provider.dailyData(symbol, period + 60);
        List<StockData> stockDataList = computeMA(dailyData, columnName);
        stockDataList = stockDataList.subList(60, stockDataList.size());
        return stockDataList;
    }

    /**
     * 计算ma指标，包含5,10,13,20,30,34,55,60
     * @param stockDataList
     * @param columnName
     * @return
     */
    public static List<StockData> computeMA(List<StockData> stockDataList, String columnName) {
        List<StockData> maStockDatas = Lists.newArrayListWithCapacity(stockDataList.size());
        double[] values = Utils.getArrayFrom(stockDataList, columnName);
        double[] ma3Arr = indicators.sma(values, 3);
        double[] ma5Arr = indicators.sma(values, 5);
        double[] ma10Arr = indicators.sma(values, 10);
        double[] ma13Arr = indicators.sma(values, 13);
        double[] ma20Arr = indicators.sma(values, 20);
        double[] ma30Arr = indicators.sma(values, 30);
        double[] ma34Arr = indicators.sma(values, 34);
        double[] ma40Arr = indicators.sma(values, 40);
        double[] ma55Arr = indicators.sma(values, 55);
        double[] ma60Arr = indicators.sma(values, 60);
        for (int i = 0; i < stockDataList.size(); i++) {
            StockData stockData = stockDataList.get(i);
            double ma3 = Utils.formatDouble(ma3Arr[i], "#.###");
            double ma5 = Utils.formatDouble(ma5Arr[i], "#.###");
            double ma10 = Utils.formatDouble(ma10Arr[i], "#.###");
            double ma13 = Utils.formatDouble(ma13Arr[i], "#.###");
            double ma20 = Utils.formatDouble(ma20Arr[i], "#.###");
            double ma30 = Utils.formatDouble(ma30Arr[i], "#.###");
            double ma34 = Utils.formatDouble(ma34Arr[i], "#.###");
            double ma40 = Utils.formatDouble(ma40Arr[i], "#.###");
            double ma55 = Utils.formatDouble(ma55Arr[i], "#.###");
            double ma60 = Utils.formatDouble(ma60Arr[i], "#.###");

            stockData.put(columnName + "_ma3", ma3);
            stockData.put(columnName + "_ma5", ma5);
            stockData.put(columnName + "_ma10", ma10);
            stockData.put(columnName + "_ma13", ma13);
            stockData.put(columnName + "_ma20", ma20);
            stockData.put(columnName + "_ma30", ma30);
            stockData.put(columnName + "_ma34", ma34);
            stockData.put(columnName + "_ma40", ma40);
            stockData.put(columnName + "_ma55", ma55);
            stockData.put(columnName + "_ma60", ma60);

            maStockDatas.add(stockData);
        }
        return maStockDatas;
    }

    /**
     * 计算均线粘合程度
     * @param stockDataList
     * @param threshold 建议设置成0.005
     * @return
     */
    public static List<StockData> computeAverageBond(List<StockData> stockDataList,double threshold){
        return StrategyUtils.averageBond(stockDataList,threshold);
    }

    public static List<StockData> computeDailyAll(String symbol, int period){
        List<StockData> stockDataList = Provider.dailyData(symbol, period + 60,true);
        stockDataList = computeMACD(stockDataList);
        stockDataList = computeMA(stockDataList,"close");
        stockDataList = computeMA(stockDataList,"volume");
        stockDataList = computeBoll(stockDataList);
        stockDataList = computeAverageBond(stockDataList,0.005);
        return stockDataList;
    }

    /**
     * 根据逐笔数据计算不同分钟级别k线数据
     * 注意：tick数据为天级别，所以此方法只能计算分钟级别的k线数据
     * @param symbol
     * @param date
     * @param periodType  分钟周期
     * @return
     */
    public static List<Bar> computeBar(String symbol, String date, PeriodType periodType){
        List<Tick> ticks = tickData(symbol, date);
        Map<Long,Bar> barMap = Maps.newTreeMap();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Utils.str2Date(date,"yyyyMMdd"));
        calendar.set(Calendar.HOUR_OF_DAY,9);
        calendar.set(Calendar.MINUTE,30);
        long startTime = calendar.getTime().getTime();
        calendar.set(Calendar.HOUR_OF_DAY,11);
        calendar.set(Calendar.MINUTE,30);
        long midEnd = calendar.getTime().getTime();
        calendar.set(Calendar.HOUR_OF_DAY,13);
        calendar.set(Calendar.MINUTE,0);
        long midStart = calendar.getTime().getTime();
        calendar.set(Calendar.HOUR_OF_DAY,15);
        calendar.set(Calendar.MINUTE,0);
        long endTime = calendar.getTime().getTime()-1;
        System.out.println(Utils.formatDate(calendar.getTime(),"yyyyMMdd HH:mm:ss"));

        for(Tick tick :ticks){
            long curTime = tick.date.getTime();
            if(curTime<startTime){
                curTime = startTime;
            }else if(curTime>endTime){
                curTime = endTime;
            }else if(curTime>midEnd && curTime<midStart){
                curTime = midEnd;
            }
            Long tmpTimeLot = 0l;
            if(curTime<=midEnd){
                tmpTimeLot = getTimeSlot(curTime, startTime, periodType.getIntValue());
            }else if(curTime >= midStart){
                tmpTimeLot = getTimeSlot(curTime, midStart, periodType.getIntValue()) + (midEnd-startTime)/periodType.getIntValue();
            }
            Bar bar = barMap.get(tmpTimeLot);
            if(bar==null){
                bar = new Bar(tmpTimeLot,tick);
            }else{
                bar.addTick(tick);
            }
            barMap.put(tmpTimeLot,bar);
        }

        return Lists.newArrayList(barMap.values());
    }

    //计算是今天第几个bar，从1开始
    private static long getTimeSlot(long curTime, long startTime, int interval) {
        return (curTime - startTime) / interval;
    }
}
