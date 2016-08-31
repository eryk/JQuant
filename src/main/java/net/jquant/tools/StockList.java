package net.jquant.tools;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.*;
import net.jquant.common.Utils;
import net.jquant.downloader.Downloader;
import net.jquant.model.StockData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.jquant.common.ParallelProcesser;
import net.jquant.provider.RealTimeDataProvider;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 非线程安全
 */
public class StockList {
    private static final Logger LOG = LoggerFactory.getLogger(StockList.class);

    private static String stockURL = "http://quote.eastmoney.com/stocklist.html";

    private static String stockDetailURL = "http://hqchart.eastmoney.com/hq20/js/%s.js?%s";

    private static String marginTradingURL = "http://app.finance.ifeng.com/hq/trade/rzrq_list.php?type=day";

    private static int RETRY_TIMES = 3;
    private static int SLEEP_INTERVAL_MS = 3000;

    private Map<String, String> stockMap;
    private List<StockData> stockDataList;
    private List<String> blackList = Lists.newLinkedList();

    public static StockList create(){
        return new StockList();
    }

    private StockList(){
        init();
    }

    private StockList init() {
        if (stockMap == null) {
            stockMap = getStockName();
        }
        if (stockDataList == null){
            stockDataList = getStockDataList(Lists.newArrayList(stockMap.keySet()));
        }
        blackList = Lists.newLinkedList();
        return this;
    }

    /**
     * @param column column name
     * @param order  取值为asc或desc
     * @return stocklist
     */
    public StockList orderBy(String column, String order) {
        if ("asc".equals("order") || "desc".equals("order")) {
            Ordering<StockData> ordering = new Ordering<StockData>() {
                @Override
                public int compare(StockData sd1, StockData sd2) {
                    if (sd1.get(column) != null && sd2.get(column) != null) {
                        return sd1.get(column).compareTo(sd2.get(column));
                    } else {
                        return 0;
                    }
                }
            };

            List<StockData> results = Lists.newLinkedList();
            if (order.equals("desc")) {
                results.addAll(ordering.reverse().sortedCopy(stockDataList));
            } else {
                results.addAll(ordering.sortedCopy(stockDataList));
            }
            stockDataList = results;
        }
        return this;
    }

    public StockList blacklist(String symbol){
        blackList.add(symbol);
        return this;
    }

    public StockList blacklist(List<String> symbols){
        blackList.addAll(symbols);
        return this;
    }

    public StockList filter(List<String> filters) {
        List<StockData> stockDatas = Lists.newLinkedList();
        for(StockData stockData:stockDataList){
            if(filter(stockData,filters)){
                stockDatas.add(stockData);
            }
        }
        stockDataList = stockDatas;
        return this;
    }

    public StockList condition(Conditions conditions) {
        List<StockData> stockDatas = Collections.synchronizedList(new LinkedList<StockData>());
        for (StockData stockData : stockDataList) {
            if (conditions.check(stockData)) {
                stockDatas.add(stockData);
            }
        }
        stockDataList = stockDatas;
        return this;
    }

    public List<StockData> get() {
        return stockDataList;
    }

    public List<String> get(List<StockData> stockDatas){
        List<String> stockList = Lists.newLinkedList();
        for(StockData stockData : stockDatas){
            if(Strings.isNullOrEmpty(stockData.symbol)){
                continue;
            }
            stockList.add(stockData.symbol);
        }
        return stockList;
    }

    private boolean filter(StockData stockData,List<String> filters){
        for(String filter:filters){
            if("st".equals(filter.toLowerCase().trim())){
                if(Strings.isNullOrEmpty(stockData.name)){
                    LOG.error(stockData.toString());
                    continue;
                }else if(stockData.name.toLowerCase().trim().contains("st")){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 获取股票状态，分为三种情况：已退市，停牌中，交易中
     * @param symbol stock symbol
     * @throws IOException IOException
     * @return stock status
     */
    public static String getStockStatus(String symbol) throws IOException {
        Random random = new Random();
        String url = String.format(stockDetailURL, symbol, random.nextInt(999999));
        String content = Downloader.download(url);
        if (Strings.isNullOrEmpty(content)) {
            return "delisted"; //退市
        } else {
            Pattern pattern = Pattern.compile("data:\"(.*)\",update");
            Matcher matcher = pattern.matcher(content);
            if (matcher.find() && !matcher.group(1).contains("-")) {
                return "suspended";  //停牌
            }
        }
        return "trading";  //交易中
    }

    public static List<String> getSymbols() {
        return Lists.newArrayList(getStockName().keySet());
    }


    /**
     * key:symbol
     * value:stock name
     */
    private static Map<String, String> getStockName() {
        int zxb = 0;
        int sh = 0;
        int sz = 0;
        int cyb = 0;
        int other = 0;
        //key:code,val:name
        Map<String, String> stockMap = Maps.newTreeMap();
        int retryTimes = 0;
        while (retryTimes < RETRY_TIMES) {
            try {
                Document doc = Jsoup.connect(stockURL).get();
                Elements stocks = doc.select("div[id=quotesearch] li a");
                for (Element stock : stocks) {
                    String url = stock.attr("href");
                    if (url.contains("sh6")) {
                        ++sh;
                    } else if (url.contains("sz000")) {
                        ++sz;
                    } else if (url.contains("sz002")) {
                        ++zxb;
                    } else if (url.contains("sz300")) {
                        ++cyb;
                    } else {
                        ++other;
                        continue;
                    }
                    String[] stockArr = stock.text().split("\\(");
                    //key:name,value:symbol
                    if (stockArr.length == 2) {
                        stockMap.put(stockArr[1].replaceAll("\\)", ""), stockArr[0]);
                    } else {
                        LOG.error("can't split:" + stock.text());
                    }
                }
                LOG.info("6:" + sh + ",000:" + sz + ",002:" + zxb + ",300:" + cyb + ",other:" + other);
                LOG.info("total:" + (sh + sz + zxb + cyb));
                return stockMap;
            } catch (IOException e) {
                LOG.error("fail to get stock list", e);
                retryTimes++;
                try {
                    Thread.sleep(SLEEP_INTERVAL_MS);
                } catch (InterruptedException e1) {
                    LOG.error("fail to sleep " + SLEEP_INTERVAL_MS + "ms");
                }
            }
        }
        return Maps.newLinkedHashMap();
    }

    /**
     * 获取中小板股票列表
     */
    private List<String> getSMEStockList() {
        return getSMEStockList(getSymbols());
    }

    private List<String> getSMEStockList(List<String> stockList) {
        Collection<String> list = Collections2.filter(stockList, (String input) -> input.startsWith("002"));
        return Lists.newArrayList(list);
    }

    /**
     * 获取创业板股票列表
     */
    private List<String> getGEMStockList() {
        Collection<String> list = Collections2.filter(getSymbols(), (String input) -> input.startsWith("300"));
        return Lists.newArrayList(list);
    }

    private List<String> getSTStockList() {
        Map<String, String> map = getStockName();
        List<String> list = Lists.newArrayList();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().toLowerCase().contains("st"))
                list.add(entry.getKey());
        }
        return list;
    }

    /**
     * 获取股票列表中“交易中”的股票,即未退市和停牌的股票列表
     * @return stock list
     */
    public static List<String> getTradingStockList() {
        return getTradingStockList(getSymbols());
    }

    public static  List<String> getTradingStockList(Conditions conditions) {
        return getStockListWithConditions(getTradingStockList(), conditions);
    }

    public static  List<String> getStockListWithConditions(List<String> stockList, Conditions conditions) {
        ExecutorService service = Executors.newFixedThreadPool(10);

        List<String> tradingStockList = stockList;
        CountDownLatch countDownLatch = new CountDownLatch(tradingStockList.size());
        List<String> stocks = Collections.synchronizedList(new LinkedList<String>());
        for (String stock : tradingStockList) {
            service.execute(() -> {
                StockData stockData = RealTimeDataProvider.get(stock);
                if (conditions.check(stockData)) {
                    stocks.add(stock);
                }
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Utils.closeThreadPool(service);
        return stocks;
    }

    /**
     * 按照代码获取股票最新状态
     * @param stockList stock list
     * @return stock list
     */
    private static List<StockData> getStockDataList(List<String> stockList) {
        final List<StockData> stockDatas = Collections.synchronizedList(new LinkedList<StockData>());
        CountDownLatch countDownLatch = new CountDownLatch(stockList.size());
        for (String stock : stockList) {
            ParallelProcesser.process(() -> {
                StockData stockData = RealTimeDataProvider.get(stock);
                if (stockData != null) {
                    stockDatas.add(stockData);
                }
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return stockDatas;
    }

    /**
     * 融资融券股票列表
     * @return stock list
     */
    public static List<String> getMarginTradingStockList() {
        List<String> marginTradingStockList = Lists.newLinkedList();
        String data = Downloader.download(marginTradingURL);
        Elements element = Jsoup.parse(data).select("div[class=tab01]").get(0).getElementsByTag("table").get(0).getElementsByTag("tr");
        for (int i = 1; i < element.size(); i++) {
            String symbol = element.get(i).getElementsByTag("td").get(0).text();
            marginTradingStockList.add(symbol);
        }

        return marginTradingStockList;
    }

    private List<String> getMarginTradingStockList(List<String> list) {
        Set<String> marginTradingSet = Sets.newHashSet(getMarginTradingStockList());
        Collection<String> results = Collections2.filter(list, new Predicate<String>() {
            @Override
            public boolean apply(String symbol) {
                return marginTradingSet.contains(symbol);
            }
        });
        return Lists.newArrayList(results);
    }

    /**
     * 获取指定股票列表中交易中的股票
     * @param list stock list
     * @return stock list
     */
    public static List<String> getTradingStockList(List<String> list) {
        final List<String> stockList = Collections.synchronizedList(new LinkedList<String>());
        ExecutorService threadPool = Executors.newFixedThreadPool(30);
        final CountDownLatch countDownLatch = new CountDownLatch(list.size());
        for (final String symbol : list) {
            threadPool.execute(() -> {
                try {
                    if (getStockStatus(symbol).equals("trading")) {
                        stockList.add(symbol);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Utils.closeThreadPool(threadPool);
        return stockList;
    }

}
