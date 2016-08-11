package net.jquant.strategy;

import net.jquant.common.Context;
import net.jquant.model.StockData;

public interface Strategy {

    void init(Context context);

    void handleData(StockData stockData);
}
