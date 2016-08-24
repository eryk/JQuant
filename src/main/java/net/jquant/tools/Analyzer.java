package net.jquant.tools;

import com.google.common.base.Strings;
import net.jquant.Quants;
import net.jquant.common.ParallelProcesser;
import net.jquant.model.StockData;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Analyzer {

    Quants quant;

    public Analyzer(){
        quant = new Quants();
    }

    public void analyze() throws IOException {
        List<StockData> stockDatas = StockList.create().get();

        Map<String, Set<String>> gn = StockCategory.getStockCategory("概念");
        Map<String, Set<String>> hy = StockCategory.getStockCategory("行业");

        for(StockData stockData:stockDatas){
            if(!Strings.isNullOrEmpty(stockData.symbol)) {
                StockData realtimeData = quant.data.realtimeData(stockData.symbol);
                if(realtimeData.get("change") <9.95){
                    continue;
                }
                if(realtimeData.get("close") > 40){
                    continue;
                }
                List<StockData> stockDatas1 = quant.data.dailyData(stockData.symbol,false);
                if(stockDatas1.size() < 60){
                    continue;
                }
                System.out.println(stockData.name + "," + stockData.symbol + "," + hy.get(stockData.symbol) + "," + gn.get(stockData.symbol));
            }
        }
    }

    public static void main(String[] args) throws IOException {
        ParallelProcesser.init(5,10);
        Analyzer analyzer = new Analyzer();
        analyzer.analyze();
        ParallelProcesser.close();
    }
}
