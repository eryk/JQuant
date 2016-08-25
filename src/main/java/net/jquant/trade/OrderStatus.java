package net.jquant.trade;

public enum OrderStatus {
    //未成交
    open(0),
    //部分成交
    filled(1),
    //已撤销
    canceled(2),
    //交易所已拒绝
    rejected(3),
    //全部成交
    held(4);

    Integer status;

    OrderStatus(int status){
        this.status = status;
    }
}
