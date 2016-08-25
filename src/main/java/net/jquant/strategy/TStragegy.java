package net.jquant.strategy;

import net.jquant.Quants;
import net.jquant.common.ParallelProcesser;
import net.jquant.model.StockData;
import net.jquant.provider.Provider;
import net.jquant.tools.Sleeper;

import java.util.List;

public class TStragegy {

    public static void t(String symbol){
        Quants quants = new Quants();
        List<StockData> stockDatas = quants.data.minuteData(symbol, "15");
        System.out.println(stockDatas.size());
        stockDatas = quants.indicator.macd(stockDatas);
        stockDatas = quants.indicator.kdj(stockDatas);
        stockDatas = quants.indicator.boll(stockDatas);

        for(StockData stock:stockDatas){
            System.out.println(stock);
        }
    }

    public static void print(String symbol){
        while(true){
            System.out.println(Provider.realtimeData(symbol));
            Sleeper.sleep(30000);
        }
    }

    public static void main(String[] args) {
        ParallelProcesser.init(5,10);
        TStragegy.print("002121");
        ParallelProcesser.close();
    }
}
