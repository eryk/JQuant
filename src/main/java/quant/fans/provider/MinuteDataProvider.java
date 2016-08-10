package quant.fans.provider;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quant.fans.common.Utils;
import quant.fans.downloader.Downloader;
import quant.fans.model.BoardType;
import quant.fans.model.StockData;
import quant.fans.model.StockMarketType;
import quant.fans.model.Symbol;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-8-30.
 */
public class MinuteDataProvider {
    private static final Logger LOG = LoggerFactory.getLogger(MinuteDataProvider.class);

    private static final String minuteDataURL = "http://money.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_MarketData.getKLineData?symbol=%s&scale=%s&ma=no&datalen=1023";

    /**
     *  获取一段时间内股票数据，受数据源限制
     * @param symbol stock symbol
     * @param startDate yyyyMMdd
     * @param stopDate  yyyyMMdd
     * @param type      5,15,30,60
     * @return stock data list
     */
    public static List<StockData> get(String symbol, String startDate, String stopDate, String type){
        Map<String,Map<String,String>> results = collect(symbol, Utils.str2Date(startDate,"yyyyMMdd"),Utils.str2Date(stopDate,"yyyyMMdd"),type);

        List<StockData> stockList = Lists.newLinkedList();
        for(Map.Entry<String, Map<String,String>> entry:results.entrySet()) {
            StockData stockData = new StockData();
            stockData.stockMarketType = StockMarketType.getType(symbol);
            stockData.boardType = BoardType.getType(symbol);
            stockData.symbol = symbol;
            stockData.date = Utils.str2Date(entry.getValue().get("day"), "yyyy-MM-dd HH:mm:ss");

            stockData.put("open",Utils.str2Double(entry.getValue().get("open")));
            stockData.put("high",Utils.str2Double(entry.getValue().get("high")));
            stockData.put("low",Utils.str2Double(entry.getValue().get("low")));
            stockData.put("close",Utils.str2Double(entry.getValue().get("close")));
            stockData.put("volume",Utils.str2Double(entry.getValue().get("volume"))/100); //成交量 单位：手

            stockList.add(stockData);
        }
        return stockList;
    }

    private static Map<String, Map<String,String>> collect(String symbol,Date startDate,Date stopDate,String type) {
        String url = getPath(symbol,type);
        Map<String,Map<String,String>> stocks = Maps.newTreeMap();
        String data = Downloader.download(url);

        Pattern pattern = Pattern.compile("\\{([\\w|\"|,|:|\\s|.|-]*)\\}");
        Matcher matcher = pattern.matcher(data.trim());
        Gson gson = new Gson();
        while (matcher.find()) {
            //{day:"2015-08-13 13:55:00",open:"16.300",high:"16.320",low:"16.270",close:"16.290",volume:"390800"}
            Map<String, String> map = gson.fromJson(matcher.group(), Map.class);
            Date date = Utils.str2Date(map.get("day"), "yyyy-MM-dd HH:mm:ss");
            if (date.getTime() >= startDate.getTime() && date.getTime() <=stopDate.getTime()) {
                stocks.put(Utils.toString(Utils.getRowkeyWithMd5PrefixAndDateSuffix(symbol, Utils.formatDate(date, "yyyyMMddHHmm"))), map);
            }
        }

        return stocks;
    }

    public static String getPath(String symbol,String type) {
        return String.format(minuteDataURL, Symbol.getSymbol(symbol, minuteDataURL), type);
    }
}
