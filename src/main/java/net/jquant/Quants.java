package net.jquant;

import net.jquant.provider.Provider;
import net.jquant.tools.StockList;
import net.jquant.strategy.StrategyUtils;

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
    public final StrategyUtils strategy = new StrategyUtils();

    public final StockList stocks = StockList.create();
    /**
     * 使用外部存储，支持：
     * 文件系统(csv)
     * redis
     * hbase
     *
     * @param config config
     * @return quants instance
     */
    public Quants db(Map<String, String> config) {
        //TODO
        return this;
    }
}
