package quant.fans;

import quant.fans.model.StockData;
import quant.fans.provider.Provider;
import quant.fans.strategy.StrategyUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Quants {

    /**
     * 获取股票数据接口
     */
    public final Provider data = new Provider();

    /**
     * 指标计算接口
     */
    public final Indicators indicator = new Indicators();

    /**
     * 策略计算接口
     */
    public final StrategyUtils strategy = new StrategyUtils();


    public static void main(String[] args) {
        Quants quants = new Quants();
        List<StockData> stockDatas = quants.data.dailyData("600560");
        for(StockData stockData:stockDatas){
            System.out.println(stockData);
        }
        Stream<Double> closes = stockDatas.parallelStream().map(new Function<StockData, Double>() {
            @Override
            public Double apply(StockData stockData) {
                return stockData.get("close");
            }
        });
        List<Double> doubles = closes.collect(Collectors.toList());
        quants.indicator.macd(doubles);
    }
}
