package quant.fans;

import quant.fans.model.StockData;
import quant.fans.provider.Provider;
import quant.fans.strategy.Strategies;

import java.util.List;
import java.util.Map;

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
    public final Strategies strategy = new Strategies();

    /**
     * 使用外部存储，支持：
     * 文件系统(csv)
     * redis
     * hbase
     *
     * @param config
     * @return
     */
    public Quants db(Map<String, String> config) {
        //TODO
        return this;
    }

    public static void main(String[] args) {
        Quants quants = new Quants();
        List<String> list = quants.data.stockList();
        quants.data.tradingStockList();

        for (String stock : list) {
            List<StockData> stockDatas = quants.data.dailyData(stock);
            if(stockDatas == null || stockDatas.size() < 60){
                continue;
            }
            quants.indicator.macd(stockDatas);
            quants.indicator.sma(stockDatas);
            quants.indicator.boll(stockDatas);
            quants.indicator.kdj(stockDatas);
            quants.strategy.macdCross(stockDatas);
            quants.strategy.kdjCross(stockDatas);
            quants.strategy.goldenSpider(stockDatas);
            quants.strategy.bollThroat(stockDatas);
            for (StockData stockData : stockDatas) {
                if (stockData.get("boll_throat") > 0) {
                    System.out.println(stockData);
                }
            }
        }

    }
}
