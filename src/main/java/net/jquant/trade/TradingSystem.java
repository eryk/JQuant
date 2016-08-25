package net.jquant.trade;

public class TradingSystem {

    private Commission commission;
    private Slippage slippage;

    public void setCommission(Commission commission){
        this.commission = commission;
    }

    public void setSlippage(Slippage slippage){
        this.slippage = slippage;
    }
}
