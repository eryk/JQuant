package net.jquant.strategy;

import com.google.common.collect.Lists;
import net.jquant.Indicators;
import net.jquant.common.StockConstants;
import net.jquant.common.Utils;
import net.jquant.model.StockData;
import net.jquant.tools.Conditions;

import java.util.List;

/**
 * 策略公共方法
 */
public class StrategyUtils {

    private static TDXFunction function = new TDXFunction();

    /**
     * 计算macd指标金叉和死叉
     *      指标名称:macd_cross
     *      状态：
     *          1：金叉
     *          0：相交
     *          -1：死叉
     * @param stockDatas stock data list
     * @return stock data list
     */
    public static List<StockData> macdCross(List<StockData> stockDatas){
        if(stockDatas == null || stockDatas.size() == 0){
            return stockDatas;
        }
        if(stockDatas.size() > 0){
            if(stockDatas.get(0).get("dif") == null || stockDatas.get(0).get("dea") == null){
                return stockDatas;
            }
        }

        for(int i =0;i< stockDatas.size();i++){
            if(stockDatas.get(i).get("dif") > stockDatas.get(i).get("dea")){
                stockDatas.get(i).put("macd_cross",1d);
            }else if(stockDatas.get(i).get("dif") < stockDatas.get(i).get("dea")) {
                stockDatas.get(i).put("macd_cross", -1d);
            }else{
                stockDatas.get(i).put("macd_cross", 0d);
            }
        }
        return stockDatas;
    }

    /**
     * 判断最近n个时间周期内是否出现金叉,或者是当前的MACD值在（-0.1,0.1）之间并且dif，dea持续缩短
     *
     * @param stockDataList stock data list
     * @param period period
     * @return 如果是返回true
     */
    public static boolean isMACDGoldenCrossIn(List<StockData> stockDataList, int period) {
        int count = stockDataList.size();
        for (int i = count - 1; i > 0; i--) {
            StockData stockData = stockDataList.get(i);
            Double cross = stockData.get(StockConstants.MACD_CROSS);
            if (cross != null && count - i <= period && cross == 1  && stockDataList.get(i).get(StockConstants.DIF) < 0.1){
                return true;
            }
        }
        return false;
    }

    public static List<StockData> kdjCross(List<StockData> stockDatas){
        if(stockDatas == null || stockDatas.size() == 0){
            return stockDatas;
        }
        if(stockDatas.size() > 0){
            if(stockDatas.get(0).get("kdj_d") == null || stockDatas.get(0).get("kdj_d") == null){
                return stockDatas;
            }
        }

        for(int i =0;i< stockDatas.size();i++){
            if(stockDatas.get(i).get("kdj_j") > stockDatas.get(i).get("kdj_d")){
                stockDatas.get(i).put("kdj_cross",1d);
            }else if(stockDatas.get(i).get("kdj_j") < stockDatas.get(i).get("kdj_d")) {
                stockDatas.get(i).put("kdj_cross", -1d);
            }else{
                stockDatas.get(i).put("kdj_cross", 0d);
            }
        }
        return stockDatas;
    }

    /**
     * 布林线上下轨之差小于中轨的10%，标记为1
     * @param stockDatas stock data list
     * @return stock data list
     */
    public static List<StockData> bollThroat(List<StockData> stockDatas){
        if(stockDatas == null || stockDatas.size() == 0){
            return stockDatas;
        }
        if(stockDatas.size() > 0){
            if(stockDatas.get(0).get("boll_upper") == null || stockDatas.get(0).get("boll_lower") == null){
                return stockDatas;
            }
        }

        for(int i =0;i< stockDatas.size();i++){
            if((stockDatas.get(i).get("boll_upper") - stockDatas.get(i).get("boll_lower")) < (stockDatas.get(i).get("boll_lower") * 0.1)){
                stockDatas.get(i).put("boll_throat",1d);
            }else{
                stockDatas.get(i).put("boll_throat",0d);
            }
        }
        return stockDatas;
    }

    /**
     * 判断收盘价两条线是否相交，快线从下上传慢线叫金叉，快慢线名称请参考StockConstants类
     *
     * @param stockDataList stock data list
     * @param maFast        例如：close_ma5
     * @param maSlow        例如：close_ma10
     * @param period period
     * @return stock data list
     */
    public static boolean isGoldenCrossIn(List<StockData> stockDataList, String maFast, String maSlow, int period) {
        int size = stockDataList.size() - 1;
        double[] maFastArr = Utils.getArrayFrom(stockDataList, maFast);
        double[] maSlowArr = Utils.getArrayFrom(stockDataList, maSlow);
        for (int i = size; i > 0; i--) {
            if (size - i > period) {
                break;
            }
            if (maFastArr[i] > maSlowArr[i] && maFastArr[i-1] < maSlowArr[i-1]) {
                return true;
            }
        }
        return false;
    }

    public static double goldenCrossPrice(List<StockData> stockDataList, String maFast, String maSlow, int period){
        int size = stockDataList.size() - 1;
        double[] maFastArr = Utils.getArrayFrom(stockDataList, maFast);
        double[] maSlowArr = Utils.getArrayFrom(stockDataList, maSlow);
        for (int i = size; i > 0; i--) {
            if (size - i < period) {
                break;
            }
            if (maFastArr[i] > maSlowArr[i] && maFastArr[i-1] < maSlowArr[i-1]) {
                return stockDataList.get(i).get(StockConstants.CLOSE);
            }
        }
        return -1;
    }

    /**
     * 判断最近n个时间周期内是否出现死叉
     *
     * @param stockDataList stock data list
     * @param period period
     * @return 如果是返回true
     */
    public static boolean isMACDDiedCrossIn(List<StockData> stockDataList, int period) {
        int count = stockDataList.size();
        for (int i = count - 1; i > 0; i--) {
            StockData stockData = stockDataList.get(i);
            Double cross = stockData.get(StockConstants.MACD_CROSS);
            if (cross != null && count - i <= period && cross == 0)
                return true;
        }
        return false;
    }

    /**
     * 计算有几条均线粘合,粘合数加入到AVERAGE_BOND参数中
     * @param stockDataList stock data list
     * @param threshold threshold
     * @return stock data list
     */
    public static List<StockData> averageBond(List<StockData> stockDataList, double threshold) {
        List<StockData> result = Lists.newLinkedList();
        for (int i = 0; i < stockDataList.size(); i++) {
            StockData stockData = stockDataList.get(i);
            double ma5 = stockData.get(StockConstants.CLOSE_MA5);
            double ma10 = stockData.get(StockConstants.CLOSE_MA10);
            double ma20 = stockData.get(StockConstants.CLOSE_MA20);
            double ma30 = stockData.get(StockConstants.CLOSE_MA30);
            double ma40 = stockData.get(StockConstants.CLOSE_MA40);
            double ma60 = stockData.get(StockConstants.CLOSE_MA60);
            if (ma5 == 0 || ma10 == 0 || ma20 == 0 || ma30 == 0 || ma40 == 0 || ma60 == 0) {
                continue;
            }
            double maCount = 0;
            double[] maArr = {ma5, ma10, ma20, ma30, ma40, ma60};
            for (int j = 0; j < 5; j++) {
                for (int k = j + 1; k < 6; k++) {
                    if (Math.abs(maArr[j] / maArr[k] - 1) < threshold) {
                        maCount++;
                    }
                }
            }
            stockData.put(StockConstants.AVERAGE_BOND, maCount);
            result.add(stockData);
        }
        return result;
    }

    /**
     * 金蜘蛛策略
     * @param stockDataList stock data list
     * @return stock data list
     */
    public static List<StockData> goldenSpider(List<StockData> stockDataList) {
        Indicators indicators = new Indicators();
        List<StockData> result = Lists.newLinkedList();
        double[] closes = Utils.getArrayFrom(stockDataList, StockConstants.CLOSE);
        double[] volume = Utils.getArrayFrom(stockDataList, StockConstants.VOLUME);
        double[] ma2 = indicators.ema(closes, 2);
        double[] ma5 = indicators.ema(closes, 5);
        double[] ma13 = indicators.ema(closes, 13);
        double[] ma34 = indicators.ema(closes, 34);
        double[] ma55 = indicators.ema(closes, 55);
        double[][] macd = indicators.macd(closes);
        double[] volume5 = indicators.ema(volume,5);

        for (int i = 0; i < stockDataList.size(); i++) {
            StockData stockData = stockDataList.get(i);
            stockData.put(StockConstants.GOLDEN_SPIDER, 0d);
            stockData.put(StockConstants.GOLDEN_SPIDER_RANGE,0d);
            if (i > 0 && ma5[i] > ma5[i - 1]) {
                double close = stockData.get(StockConstants.CLOSE);
                double open = stockData.get(StockConstants.OPEN);
                double max1 = function.max(ma5[i], ma13[i], ma34[i]);
                double min1 = function.min(ma5[i], ma13[i], ma34[i]);
                double max2 = function.max(ma5[i], ma13[i], ma34[i], ma55[i]);
                double min2 = function.min(ma5[i], ma13[i], ma34[i], ma55[i]);
                if (max1 < close && open < min1 && ma2[i] > ma2[i - 1]) {
                    if(!Utils.isNearTradingTime()){//如果是盘后，则计算成交量是否大于前一天成交量，并且今天成交量大于当天volume5
                        if(stockData.get(StockConstants.VOLUME) > stockDataList.get(i-1).get(StockConstants.VOLUME)*1.2 && stockData.get(StockConstants.VOLUME) > volume5[i]){
                            stockData.put(StockConstants.GOLDEN_SPIDER, 3d);
                        }
                    }else{
                        stockData.put(StockConstants.GOLDEN_SPIDER, 3d);
                    }
                } else if (max2 < close && open < min2 && ma2[i] > ma2[i - 1]) {
                    if(!Utils.isNearTradingTime()){//如果是盘后，则计算成交量是否大于前一天成交量，并且今天成交量大于当天volume5
                        if(stockData.get(StockConstants.VOLUME) > stockDataList.get(i-1).get(StockConstants.VOLUME)*1.2 && stockData.get(StockConstants.VOLUME) > volume5[i]){
                            stockData.put(StockConstants.GOLDEN_SPIDER, 4d);
                        }else{
                            stockData.put(StockConstants.GOLDEN_SPIDER, 0d);
                        }
                    }else{
                        stockData.put(StockConstants.GOLDEN_SPIDER, 4d);
                    }
                } else if(macd[2][i]>0 && macd[2][i] < 1){    //macd金叉，并且三天内出现金蜘蛛
                    double lastClose = stockData.get(StockConstants.CLOSE);
                    for(int j=i-1;j >= 0 && j >i-3;j--){
                        Double spider = result.get(j).get(StockConstants.GOLDEN_SPIDER);
                        if(spider!=null){
                            if(lastClose > result.get(j).get(StockConstants.CLOSE) && lastClose < result.get(j).get(StockConstants.CLOSE) * 1.1 && spider>0){
                                stockData.put(StockConstants.GOLDEN_SPIDER_RANGE,1d);
                            }
                        }
                    }
                }
            }
            result.add(stockData);
        }
        return result;
    }

    /**
     * 计算最近count天内每天涨跌幅是否出现过conditions
     * @param stockDataList stock data list
     * @param count n day
     * @param conditions conditions
     * @return true if conditions is true
     */
    public static boolean change(List<StockData> stockDataList,int count,Conditions conditions){
        int size = stockDataList.size();
        for(int i=size-1;i>=size-count;i--){
            StockData stockData = stockDataList.get(i);
            if(conditions.check(stockData)){
                return true;
            }
        }
        return false;
    }
}
