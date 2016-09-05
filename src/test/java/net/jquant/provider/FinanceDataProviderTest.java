package net.jquant.provider;

import net.jquant.model.StockData;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class FinanceDataProviderTest {
    @Test
    public void getYear() throws Exception {
        List<StockData> data = FinanceDataProvider.getYear("002121");
        for(StockData stockData:data){
            System.out.println(stockData);
        }
    }

}