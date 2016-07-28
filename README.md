# JQuant

# 示例

```java
package quant.fans;

import quant.fans.model.StockData;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        Quants quants = new Quants();
        List<String> list = quants.data.stockList();

        for (String stock : list) {
            List<StockData> stockDatas = quants.data.dailyData(stock);
            if (stockDatas == null || stockDatas.size() < 60) {
                continue;
            }
            quants.indicator.macd(stockDatas);
            quants.indicator.sma(stockDatas);
            quants.indicator.boll(stockDatas);
            quants.indicator.kdj(stockDatas);
            quants.strategy.macdCross(stockDatas);
            quants.strategy.kdjCross(stockDatas);
            quants.strategy.goldenSpider(stockDatas);
            quants.strategy.bollThroat(stockDatas);
            for (StockData stockData : stockDatas) {
                System.out.println(stockData);
            }
        }

    }
}
```
