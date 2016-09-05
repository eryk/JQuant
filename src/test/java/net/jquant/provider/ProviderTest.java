package net.jquant.provider;

import net.jquant.model.StockData;
import org.junit.Test;

import java.util.List;

public class ProviderTest {
    @Test
    public void dailyData() throws Exception {

    }

    @Test
    public void dailyDataZS() throws Exception {
        List<StockData> stockDatas = Provider.dailyDataZS("000001");
        for(StockData stockData:stockDatas){
            System.out.println(stockData);
        }
    }

    @Test
    public void dailyDataZS1() throws Exception {

    }

    @Test
    public void dailyDataZS2() throws Exception {

    }

    @Test
    public void realtimeData() throws Exception {

    }

    @Test
    public void minuteData() throws Exception {

    }

    @Test
    public void minuteData1() throws Exception {

    }

    @Test
    public void moneyFlowData() throws Exception {

    }

    @Test
    public void moneyFlowData1() throws Exception {

    }

    @Test
    public void moneyFlowDapanData() throws Exception {

    }

    @Test
    public void moneyFlowIndustryData() throws Exception {

    }

    @Test
    public void moneyFlowConceptData() throws Exception {

    }

    @Test
    public void moneyFlowRegionData() throws Exception {

    }

    @Test
    public void financeData() throws Exception {

    }

    @Test
    public void financeData1() throws Exception {

    }

    @Test
    public void financeYearData() throws Exception {

    }

    @Test
    public void tickData() throws Exception {

    }

    @Test
    public void tickData1() throws Exception {

    }

    @Test
    public void stockBlock() throws Exception {

    }

    @Test
    public void stockCategory() throws Exception {

    }

    @Test
    public void stockList() throws Exception {

    }

    @Test
    public void testRealTimeData(){
        StockData stockData = Provider.realtimeData("000403");
        System.out.println(stockData);
    }
}