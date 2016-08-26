package net.jquant.trade;

//百分比滑点
public class PriceRelatedSlippage implements Slippage{
    private double slip = 0.002;

    public PriceRelatedSlippage(double slip){
        this.slip = slip;
    }

    @Override
    public double slip(OrderType type,double price) {
        if(type == OrderType.BUY){
            return price * (1 + slip);
        }else{
            return price * (1 - slip);
        }
    }
}
