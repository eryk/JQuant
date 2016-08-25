package net.jquant.trade;

import org.joda.time.DateTime;

//订单的一次交易记录,一个订单可能分多次交易
public class Trade {
    private DateTime time; //交易时间, datetime.datetime对象
    private long amount; //交易数量
    private double price; //交易价格
    private String trade_id; //交易记录id
    private String order_id; //对应的订单id
}
