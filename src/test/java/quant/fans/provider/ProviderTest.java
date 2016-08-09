package quant.fans.provider;

import org.junit.Test;
import quant.fans.model.StockData;

import static org.junit.Assert.*;

public class ProviderTest {

    @Test
    public void testRealTimeData(){
        StockData stockData = Provider.realtimeData("000403");
        System.out.println(stockData);
    }
}