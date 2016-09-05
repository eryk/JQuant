package net.jquant.strategy;

import com.google.common.collect.Lists;
import net.jquant.Quants;
import net.jquant.common.ParallelProcesser;
import net.jquant.common.StockDataParseException;
import net.jquant.model.StockData;
import net.jquant.provider.Provider;
import net.jquant.tools.Sleeper;

import java.util.Date;
import java.util.List;

public class TStragegy {

    public static void t(String symbol) throws StockDataParseException {
        Quants quants = new Quants();
        List<StockData> stockDatas = quants.data.minuteData(symbol, "15");
        System.out.println(stockDatas.size());
        stockDatas = quants.indicator.macd(stockDatas);
        stockDatas = quants.indicator.kdj(stockDatas);
        stockDatas = quants.indicator.boll(stockDatas);

        for (StockData stock : stockDatas) {
            System.out.println(stock);
        }
    }

    public static void print(List<String> symbolList) throws StockDataParseException {
        while (true) {
            for(String symbol:symbolList){

                StockData stockData = Provider.realtimeData(symbol);
//                System.out.println(stockData.get("buy5") + "\t"+ stockData.get("sell5") + "\t" + (stockData.get("buy5Volume") - stockData.get("sell5Volume")));
//                System.out.println(stockData.get("buy4") + "\t"+ stockData.get("sell4") + "\t"+ (stockData.get("buy4Volume") - stockData.get("sell4Volume")));
//                System.out.println(stockData.get("buy3") + "\t"+ stockData.get("sell3") + "\t"+ (stockData.get("buy3Volume") - stockData.get("sell3Volume")));
//                System.out.println(stockData.get("buy2") + "\t"+ stockData.get("sell2") + "\t"+ (stockData.get("buy2Volume") - stockData.get("sell2Volume")));
                System.out.println("-------" + stockData.name + "(" + stockData.symbol + ")" + "-------");
                System.out.println(stockData.get("buy1") + "\t"+ stockData.get("sell1") + "\t"+ (stockData.get("buy1Volume") - stockData.get("sell1Volume")));
            }
            System.out.println(new Date());
            Sleeper.sleep(30000);
        }
    }

    public static void main(String[] args) throws StockDataParseException {
        ParallelProcesser.init(1, 2);
        TStragegy.print(Lists.newArrayList("002121","600444","600152","002627","600605","600561"));
        ParallelProcesser.close();
    }
}
