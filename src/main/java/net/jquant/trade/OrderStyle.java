package net.jquant.trade;

public enum OrderStyle {
    //市价单: 不论价格, 直接下单, 直到交易全部完成
    MarketOrderStyle,
    //限价单: 指定一个价格, 买入时不能高于它, 卖出时不能低于它, 如果不满足, 则等待满足后再交易
    LimitOrderStyle;
}
