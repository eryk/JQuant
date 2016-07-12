package quant.fans.strategy;

import quant.fans.common.Context;
import quant.fans.model.StockData;

public interface Strategy {

    void init(Context context);

    void handleData(StockData stockData);
}
