package quant.fans.model;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * 股票版块
 */
public class StockBlock {

    //http://quote.eastmoney.com/center/list.html#28002458_0_2, id=28002458
    public String id;

    public String name;

    public String type; //版块类型：概念，地域，行业

    public String url;

    public List<String> symbolList = Lists.newLinkedList();

    public void add(String symbol){
        symbolList.add(symbol);
    }

    @Override
    public String toString() {
        return "StockBlock{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
