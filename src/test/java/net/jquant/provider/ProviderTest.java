package net.jquant.provider;

import net.jquant.model.StockData;
import org.junit.Test;

public class ProviderTest {

    @Test
    public void testRealTimeData(){
        StockData stockData = Provider.realtimeData("000403");
        System.out.println(stockData);
    }
}