package net.jquant.trade;

public class RiskAnalysis {
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
