package quant.fans.model;

import com.google.common.collect.Maps;
import quant.fans.common.Utils;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-8-15.
 */
public class StockData extends LinkedHashMap<String, Double> implements Comparable<StockData> {

    public String symbol;     //代码

    public String name;       //名称

    public Date date;       //时间

    public BoardType boardType;  //版块信息：主版，中小板，创业板

    public StockMarketType stockMarketType; //市场：深市，沪市

    public Map<String, String> attribute = Maps.newHashMap();

    public StockData() {
    }

    public StockData(String symbol) {
        this.symbol = symbol;
        this.stockMarketType = StockMarketType.getType(symbol);
        this.boardType = BoardType.getType(symbol);
    }

    public StockData(Map<String, Double> map) {
        this.putAll(map);
    }

    public void attr(String key, String val) {
        attribute.put(key, val);
    }

    public String attr(String key) {
        return attribute.get(key);
    }

    @Override
    public String toString() {
        return "StockData{" +
                "symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", date=" + Utils.formatDate(date, "yyyy-MM-dd HH:mm:ss") +
                ", boardType=" + boardType +
                ", stockMarketType=" + stockMarketType +
                ", stockData=" + Utils.map2Json(this) +
                ", stockAttribute= " + Utils.map2Json(attribute) +
                '}';
    }

    @Override
    public int compareTo(StockData stockData) {
        int compare = this.symbol.compareTo(stockData.symbol);
        if (compare != 0) {
            return compare;
        }
        return this.date.compareTo(stockData.date);
    }
}
