package net.jquant.trade;

import com.google.common.collect.Maps;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

public class Account {
    public static final String DEFAULT_NAME = "JQuant";
    private String name;

    private static Integer INIT_FUND = 100000;  //起始资金数量

    private Record status = new Record(LocalDateTime.MIN);

    //需要在初始化时赋值
    private RiskAnalysis riskAnalysis = new RiskAnalysis();

    private TreeMap<LocalDateTime,Record> records = new TreeMap<LocalDateTime,Record>();    //记录各个时间点账户状态

    private LocalDateTime ts;//最后更新record的时间点

    private Map<String,Position> positions = Maps.newConcurrentMap();   //记录账户当前持仓情况

    private class RiskAnalysis{

        public double start = 0;   //起始资产
        public double end = 0;     //期末资产

        public double pnl = 0;    //交易盈亏
        public double pnlRate = 0;    //收益率=交易盈亏/起始资产
        public double annualReturn = 0; //年化收益率= 交易盈亏 / (平均持股天数/交易总天数)

        public int avgPositionDays = 0;   //平均每只股票持仓天数
        public int totalPositionDays = 0;    //持仓总天数

        public double maxEarnPerOp = 0; //最大单笔盈利
        public double maxLossPerOp = 0; //最大单笔亏损
        public double meanEarnPerOp = 0; //平均每笔盈利

        public double continuousEarnOp = 0; //连续盈利次数
        public double continuousLossOp = 0; //连续亏损次数

        public int totalOperate = 0;  //总交易次数
        public int earnOperate = 0;   //总盈利次数
        public int lossOperate = 0; //总亏损交易次数
        public double accuracy = 0;  //操作正确率=总盈利次数/总交易次数

        public double sharpe = 0;   //夏普率
        public double sortino = 0;  //所提诺比率

        public double benchmarkBenfit = 0; //基准收益额，同期股价涨跌额,单位：元
        public double benchmarkBenfitPercent = 0;   //基准收益百分比

        public double strategyBenfit = 0; //策略收益额
        public double strategyBenfitPercent = 0;   //策略收益率

        public double marketIndexPercent = 0; //大盘涨跌幅,同期大盘涨跌百分比
        public double max = 0;     //最大资产
        public double min = 0;     //最小资产
        public double maxDrawdown = 0; //最大回撤
    }

    /**
     * 记录股票交易状态，持仓情况，以及风险分析指标
     */
    private class Record {
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

    //证券名称，证券数量，可卖数量，成本价，浮动盈亏，盈亏比例，最新市值，当前价，今买数量，今卖数量
    public class Position{
        public String stockName;   //证券名称
        public LocalDateTime ts;    //更新时间戳
        public String symbol;      //证券代码
        public int amount;      //证券数量
        public int canSell;     //可卖数量
        public double costPrice;   //成本价
        public double floatPnl;    //浮动盈亏
        public double pnlRatio;    //盈亏比例
        public double latestValue; //最新市值
        public double close;       //当前价
        public double buyAmount;   //今买数量
        public double sellAmount;  //今卖数量

        public Position(){}

        public Position(String symbol,LocalDateTime ts,int amount,double price,double close){
            this.symbol = symbol;
            this.ts = ts;
            this.amount = amount;
            this.canSell = amount;
            this.costPrice = price;
            this.floatPnl = amount*(price-close);
            this.pnlRatio = 0;
            this.latestValue = amount * price;
            this.close = close;
            this.buyAmount = amount;
            this.sellAmount = 0;
        }
    }

    public Account(String name, LocalDateTime startDate){
        this.name = name;
        this.ts = startDate;
        records.put(ts, new Record(ts));
    }

    public Account(){
        this(DEFAULT_NAME, LocalDateTime.MIN);
    }

    public Account(String name){
        this(name, LocalDateTime.MIN);
    }

    public Record getStatus() {
        return status;
    }

    public void setStatus(Record status) {
        this.status = status;
    }

    public Map<String, Position> getPositions() {
        return positions;
    }

    public void setPositions(Map<String,Position> positions){
        this.positions = positions;
    }

}
