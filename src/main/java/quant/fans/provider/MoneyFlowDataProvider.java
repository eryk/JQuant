package quant.fans.provider;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quant.fans.common.StockConstants;
import quant.fans.common.Utils;
import quant.fans.downloader.Downloader;
import quant.fans.model.BoardType;
import quant.fans.model.StockData;
import quant.fans.model.StockMarketType;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-8-30.
 */
public class MoneyFlowDataProvider {
    private static final Logger LOG = LoggerFactory.getLogger(MoneyFlowDataProvider.class);
    //TODO 全部历史资金流向
    //http://quotes.money.163.com/trade/lszjlx_600199.html#01b08

    //第二个为6位随机数
    private static String moneyFlowURL = "http://hqchart.eastmoney.com/hq20/js/%s.js?%s";

    private static String moneyFlowHisURL = "http://data.eastmoney.com/zjlx/%s.html";

    private static String moneyFlowDapanHisURL = "http://data.eastmoney.com/zjlx/dpzjlx.html";

    //http://data.eastmoney.com/bkzj/hy.html
    private static String moneyFlowIndustryHisURL = "http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?cmd=C._BKHY&type=ct&st=(BalFlowMain)&&token=894050c76af8597a853f5b408b759f5d&sty=DCFFITABK&rt=%s";
    private static String moneyFlowIndustry5DayHisURL = "http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?cmd=C._BKHY&type=ct&st=(BalFlowMainNet5)&token=894050c76af8597a853f5b408b759f5d&sty=DCFFITABK5&rt=%s";
    private static String moneyFlowIndustry10DayHisURL = "http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?cmd=C._BKHY&type=ct&st=(BalFlowMainNet10)&token=894050c76af8597a853f5b408b759f5d&sty=DCFFITABK10&rt=%s";

    //http://data.eastmoney.com/bkzj/gn.html
    private static String moneyFlowConceptHisURL = "http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?cmd=C._BKGN&type=ct&st=(BalFlowMain)&sr=-1&p=1&ps=500&token=894050c76af8597a853f5b408b759f5d&sty=DCFFITABK&rt=%s";
    private static String moneyFlowConcept5DayHisURL = "http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?cmd=C._BKGN&type=ct&st=(BalFlowMainNet5)&sr=-1&p=1&ps=500&token=894050c76af8597a853f5b408b759f5d&sty=DCFFITABK5&rt=%s";
    private static String moneyFlowConcept10DayHisURL = "http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?cmd=C._BKGN&type=ct&st=(BalFlowMainNet10)&sr=-1&p=1&ps=500&token=894050c76af8597a853f5b408b759f5d&sty=DCFFITABK10&rt=%s";

    //http://data.eastmoney.com/bkzj/dy.html
    private static String moneyFlowRegionHisURL = "http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?cmd=C._BKDY&type=ct&st=(BalFlowMain)&sr=-1&p=1&ps=50&token=894050c76af8597a853f5b408b759f5d&sty=DCFFITABK&rt=%s";
    private static String moneyFlowRegion5DayHisURL = "http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?cmd=C._BKDY&type=ct&st=(BalFlowMainNet5)&sr=-1&p=1&ps=50&&token=894050c76af8597a853f5b408b759f5d&sty=DCFFITABK5&rt=%s";
    private static String moneyFlowRegion10DayHisURL = "http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?cmd=C._BKDY&type=ct&st=(BalFlowMainNet10)&sr=-1&p=1&ps=50&&token=894050c76af8597a853f5b408b759f5d&sty=DCFFITABK10&rt=%s";
    /**
     * 获取当天个股资金流数据
     * @param symbol
     * @return
     */
    public static StockData get(String symbol) {
        Map<String, String> map = collect(symbol);

        StockData stockData = new StockData();
        stockData.symbol = symbol;
        stockData.stockMarketType = StockMarketType.getType(symbol);
        stockData.boardType = BoardType.getType(symbol);

        String data = map.get("data");
        if (Strings.isNullOrEmpty(data)) {
            LOG.error("fail to get money flow data from " + symbol);
            return null;
        }
        String[] values = data.split(",");
        for (int i = 0; i < values.length; i++) {
            stockData.put(StockConstants.MONEYFLOW.get(i), Double.parseDouble(values[i]));
        }
        return stockData;
    }

    /**
     * 获取指定时间段股票资金流数据
     * @param symbol
     * @param startDate
     * @param stopDate
     * @return
     */
    public static List<StockData> get(String symbol, String startDate, String stopDate) {
        List<String[]> list = collectHis(symbol);
        List<StockData> stockDataList = Lists.newLinkedList();
        for (String[] columns : list) {
            StockData stockData = new StockData();
            stockData.symbol = symbol;
            stockData.stockMarketType = StockMarketType.getType(symbol);
            stockData.boardType = BoardType.getType(symbol);
            stockData.date = Utils.str2Date(columns[0], "yyyy-MM-dd");

            if (stockData.date.getTime() >= Utils.str2Date(startDate, "yyyyMMdd").getTime()
                    && stockData.date.getTime() <= Utils.str2Date(stopDate, "yyyyMMdd").getTime()) {
                for (int i = 1; i < columns.length; i++) {
                    double val = 0;
                    if (columns[i].contains("%")) {
                        val = Double.parseDouble(columns[i].replace("%", ""));
                    } else {
                        if(columns[i].contains("万")){
                            val = Utils.getAmount(columns[i].replaceAll("万",""));
                        }else if(columns[i].contains("亿")){
                            val = Utils.getAmount(columns[i].replaceAll("亿",""))*10000;
                        }else if(columns[i].equals("-")){
                            val = 0;
                        }else{
                            val = Utils.getAmount(columns[i]);
                        }
                    }
                    stockData.put(StockConstants.MONEYFLOW_HIS.get(i), val);
                }
                stockDataList.add(stockData);
            }
        }
        return stockDataList;
    }

    /**
     * 获取大盘资金流数据
     * @return
     */
    public static List<StockData> getDapan() {
        List<String[]> list = collectDaPan();
        List<StockData> stockDataList = Lists.newLinkedList();
        for (String[] columns : list) {
            StockData stockData = new StockData();
            stockData.date = Utils.str2Date(columns[0], "yyyy-MM-dd");

            for (int i = 1; i < columns.length; i++) {
                double val;
                if (columns[i].contains("%")) {
                    val = Double.parseDouble(columns[i].replace("%", ""));
                } else {
                    val = Utils.getAmount(columns[i]);
                }
                stockData.put(StockConstants.DAPAN_MONEYFLOW_HIS.get(i), val);
            }
            stockDataList.add(stockData);
        }
        return stockDataList;
    }

    /**
     * 获取行业版块资金流数据
     * @param type 1,5,10
     * @return
     */
    public static List<StockData> getIndustry(String type) {
        List<String> list = collectIndustry(type);
        List<StockData> stockDataList = Lists.newLinkedList();
        for (String line : list) {
            String[] columns = line.split(",", 16);
            StockData stockData = new StockData();
            stockData.symbol = columns[1];
            stockData.name = columns[2];
            for (int i = 3; i < 13; i++) {
                stockData.put(StockConstants.INDUSTRY_MONEYFLOW.get(i), Double.parseDouble(columns[i]));
            }
            stockDataList.add(stockData);
        }
        return stockDataList;
    }

    /**
     * 获取概念版块资金流数据
     * @return
     */
    public static List<StockData> getConcept(String type){
        List<String> list = collectConcept(type);
        List<StockData> stockDataList = Lists.newLinkedList();
        for (String line : list) {
            String[] columns = line.split(",", 16);
            StockData stockData = new StockData();
            stockData.symbol = columns[1];
            stockData.name = columns[2];
            for (int i = 3; i < 13; i++) {
                stockData.put(StockConstants.INDUSTRY_MONEYFLOW.get(i), Double.parseDouble(columns[i]));
            }
            stockDataList.add(stockData);
        }
        return stockDataList;
    }

    /**
     * 获取地区版块资金流数据
     * @return
     */
    public static List<StockData> getRegion(String type){
        List<String> list = collectRegion(type);
        List<StockData> stockDataList = Lists.newLinkedList();
        for (String line : list) {
            String[] columns = line.split(",", 16);
            StockData stockData = new StockData();
            stockData.symbol = columns[1];
            stockData.name = columns[2];
            for (int i = 3; i < 13; i++) {
                stockData.put(StockConstants.INDUSTRY_MONEYFLOW.get(i), Double.parseDouble(columns[i]));
            }
            stockDataList.add(stockData);
        }
        return stockDataList;
    }

    private static List<String> collectRegion(String type) {
        String url;
        Random random = new Random();
        int i = random.nextInt(99999999);
        if (type.equals("5")) {
            url = String.format(moneyFlowRegion5DayHisURL, i);
        } else if (type.equals("10")) {
            url = String.format(moneyFlowRegion10DayHisURL, i);
        } else {
            url = String.format(moneyFlowRegionHisURL, i);
        }
        String data = Downloader.download(url);
        String s = data.substring(1, data.length() - 1);
        Gson gson = new Gson();
        List<String> list = gson.fromJson(s, List.class);
        return list;
    }

    private static List<String> collectConcept(String type) {
        String url;
        Random random = new Random();
        int i = random.nextInt(99999999);
        if (type.equals("5")) {
            url = String.format(moneyFlowConcept5DayHisURL, i);
        } else if (type.equals("10")) {
            url = String.format(moneyFlowConcept10DayHisURL, i);
        } else {
            url = String.format(moneyFlowConceptHisURL, i);
        }
        String data = Downloader.download(url);
        String s = data.substring(1, data.length() - 1);
        Gson gson = new Gson();
        List<String> list = gson.fromJson(s, List.class);
        return list;
    }

    private static List<String> collectIndustry(String type) {

        String url;
        Random random = new Random();
        int i = random.nextInt(99999999);
        if (type.equals("5")) {
            url = String.format(moneyFlowIndustry5DayHisURL, i);
        } else if (type.equals("10")) {
            url = String.format(moneyFlowIndustry10DayHisURL, i);
        } else {
            url = String.format(moneyFlowIndustryHisURL, i);
        }
        String data = Downloader.download(url);
        String s = data.substring(1, data.length() - 1);
        Gson gson = new Gson();
        List<String> list = gson.fromJson(s, List.class);
        return list;
    }

    private static List<String[]> collectDaPan() {
        String data = Downloader.downloadAjaxData(moneyFlowDapanHisURL);
        Elements doc = Jsoup.parse(data).getElementById("dt_1").getElementsByTag("tbody").get(0).getElementsByTag("tr");

        List<String[]> stockDataList = Lists.newLinkedList();
        for (Element tr : doc) {
            Elements tds = tr.getElementsByTag("td");
            String[] columnValues = new String[15];
            for (int i = 0; i < 15; i++) {
                columnValues[i] = tds.get(i).text();
            }
            stockDataList.add(columnValues);
        }
        return Lists.reverse(stockDataList);
    }

    private static List<String[]> collectHis(String symbol) {
        String url = String.format(moneyFlowHisURL, symbol);
        String data = Downloader.download(url,"gb2312");
        Elements doc = Jsoup.parse(data).getElementById("dt_1").getElementsByTag("tbody").get(0).getElementsByTag("tr");

        List<String[]> stockDataList = Lists.newLinkedList();
        for (Element tr : doc) {
            Elements tds = tr.getElementsByTag("td");
            String[] columnValues = new String[13];
            for (int i = 0; i < 13; i++) {
                columnValues[i] = tds.get(i).text();
            }
            stockDataList.add(columnValues);
        }
        return Lists.reverse(stockDataList);
    }

    private static Map<String, String> collect(String symbol) {
        Map<String, String> map = Maps.newLinkedHashMap();
        String data = Downloader.download(getPath(symbol));
        Pattern pattern = Pattern.compile("(\\{.*})");
        Matcher matcher = pattern.matcher(data);
        if (matcher.find()) {
            Gson gson = new Gson();
            map.putAll(gson.fromJson(matcher.group(), Map.class));
        }
        return map;
    }

    public static String getPath(String symbol) {
        Random random = new Random();
        int i = random.nextInt(999999);
        return String.format(moneyFlowURL, symbol, i);
    }

    public static void main(String[] args) {
//        List<StockData> stockDataList = MoneyFlowDataProvider.get("600376", "20150801", "20150830");
//        for(StockData stockData :stockDataList){
//            System.out.println(stockData);
//            Utils.printMap(stockData);
//        }

        List<StockData> industry = MoneyFlowDataProvider.getIndustry("1");
        for (StockData stockData : industry) {
            System.out.println(stockData);
            Utils.printMap(stockData);
        }

    }
}
