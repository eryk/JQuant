package net.jquant.trade;

import org.joda.time.DateTime;

//买卖订单
public class Order {
    private OrderStatus status; //状态, 一个OrderStatus值
    private DateTime add_time; //订单添加时间, datetime.datetime对象
    private OrderType orderType; //买还是卖
    private long amount; //下单数量, 不管是买还是卖, 都是正数
    private long filled; //已经成交的股票数量, 正数
    private String symbol; //股票代码
    private String order_id; //订单ID
    private double price; //平均成交价格, 已经成交的股票的平均成交价格(一个订单可能分多次成交)
    private double avg_cost; //卖出时表示下卖单前的此股票的持仓成本, 用来计算此次卖出的收益. 买入时表示此次买入的均价(等同于price).
}
