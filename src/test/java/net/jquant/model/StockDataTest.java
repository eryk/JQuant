package net.jquant.model;

import net.jquant.common.StockDataParseException;
import net.jquant.provider.Provider;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class StockDataTest {

    @Test
    public void testStockData() throws StockDataParseException {
        StockData stockData = Provider.realtimeData("000001");
        System.out.println("股票名称:" + stockData.name);
        System.out.println("股票代码:" + stockData.symbol);
        for(Map.Entry<String,Double> data : stockData.entrySet()){
            System.out.println(data.getKey() + "=" + data.getValue());
        }
        System.out.println(stockData);

    }
}