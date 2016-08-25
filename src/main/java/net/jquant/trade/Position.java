package net.jquant.trade;

import java.time.LocalDateTime;

public class Position {
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
