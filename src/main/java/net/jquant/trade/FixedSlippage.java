package net.jquant.trade;

//固定值滑点
public class FixedSlippage implements Slippage{

    private double slip = 0.02;

    public FixedSlippage(double slip){
        this.slip = slip;
    }

    @Override
    public double slip(OrderType type,double price) {
        if(type == OrderType.BUY){
            return price + slip;
        }else{
            return price - slip;
        }
    }
}
