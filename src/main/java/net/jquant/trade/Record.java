package net.jquant.trade;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 记录股票交易状态，持仓情况，以及风险分析指标
 */
public class Record {
    private Map<String,Position> positions;

    private RiskAnalysis riskAnalysis;

    private double todayBuy = 0;    //当日买入额
    private double todaySell = 0;    //当日卖出额
    private double todayEarn = 0;   //当日盈利，不包括亏损数额
    private double todayLoss = 0;   //当日亏损

    private double txnFees = 0; //税费总计

    private LocalDateTime ts;

    public Record(LocalDateTime ts) {
        this.ts = ts;
    }
}
