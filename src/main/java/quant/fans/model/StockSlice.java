package quant.fans.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import quant.fans.common.Constants;
import quant.fans.common.Utils;

import java.util.List;
import java.util.Map;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-8-9.
 */
public class StockSlice {
    public String symbol;
    public String startDate;
    public String stopDate;
    public PeriodType type;

    public List<StockData> stocks;


    public Map<String, List<Double>> points;

    public static StockSlice getSlice(String symbol,List<StockData> stocks, String start, String stop) {
        return new StockSlice(symbol,stocks, start, stop);
    }

    private StockSlice(String symbol,List<StockData> stocks, String start, String stop) {
        this.symbol = symbol;
        this.startDate = start;
        this.stopDate = stop;
        this.stocks = stocks;
        points = Maps.newHashMapWithExpectedSize(stocks.size());
        setClosePrice();
        setVolumes();
    }

    public List<StockData> getStocks(){
        return stocks;
    }

    public void setClosePrice() {
        List<Double> closes = points.get(Utils.toString(Constants.CLOSE));
        if(closes==null){
            closes = Lists.newLinkedList();
        }
        for (StockData stock : stocks) {
            closes.add(stock.get("close"));
        }
        points.put(Utils.toString(Constants.CLOSE), closes);
    }

    public double[] getClose(){
        return getValues(Utils.toString(Constants.CLOSE));
    }

    public void setVolumes(){
        List<Double> volumes = points.get(Utils.toString(Constants.VOLUME));
        if(volumes==null){
            volumes = Lists.newLinkedList();
        }
        for (StockData stock : stocks) {
            volumes.add(stock.get("volume"));
        }
        points.put(Utils.toString(Constants.VOLUME), volumes);
    }

    public double[] getVolumes(){
        return getValues(Utils.toString(Constants.VOLUME));
    }

    public double[] getValues(String property) {
        List<Double> values = points.get(property);
        if (values == null) {
            values = Lists.newArrayList();
        }
        double[] result = new double[values.size()];
        for(int i=0;i<values.size();i++){
            System.out.println("value"+i+":"+values.get(i));
            result[i] = values.get(i);
        }
        return result;
    }
}
