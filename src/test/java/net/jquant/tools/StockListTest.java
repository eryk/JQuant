package net.jquant.tools;

import com.google.common.collect.Lists;
import org.junit.Test;
import net.jquant.common.ParallelProcesser;
import net.jquant.model.StockData;

import java.util.List;

public class StockListTest {

    @Test
    public void get() throws Exception {
        ParallelProcesser.init(1,16);
        List<StockData> stockList = StockList.create().filter(Lists.newArrayList("st")).get();
        for(StockData stockData:stockList){
            System.out.println(stockData.name + "\t" + stockData.symbol);
        }
    }

}