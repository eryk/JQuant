package net.jquant.model;

import com.google.common.collect.Maps;
import net.jquant.common.Utils;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-8-15.
 */
public class StockData extends LinkedHashMap<String, Double> implements Comparable<StockData> {

    /**
     * 股票代码
     */
    public String symbol;

    /**
     * 股票名称
     */
    public String name;

    /**
     * 数据时间点
     */
    public Date date;

    /**
     * 版块信息：主版，中小板，创业板
     */
    public BoardType boardType;

    /**
     * 市场：深市，沪市
     */
    public StockMarketType stockMarketType;

    /**
     * 属性值
     */
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
