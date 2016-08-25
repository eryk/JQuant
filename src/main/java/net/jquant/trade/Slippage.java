package net.jquant.trade;

public interface Slippage {

    double slip(OrderType type,double price);
}
