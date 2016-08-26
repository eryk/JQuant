package net.jquant.trade;

import net.jquant.common.Context;
import net.jquant.tools.StockList;

public class TradingSystem {

    private Context context;
    private StockList stockList;
    private Commission commission = new Commission();
    private Slippage slippage = new FixedSlippage();

    public void setContext(Context context){
        this.context = context;
    }

    public void setStockList(StockList stockList){
        this.stockList = stockList;
    }

    public void setCommission(Commission commission){
        this.commission = commission;
    }

    public void setSlippage(Slippage slippage){
        this.slippage = slippage;
    }
}
