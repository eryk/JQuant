package net.jquant.provider;

import com.google.common.collect.Lists;
import com.google.gson.Gson;

import net.jquant.common.StockConstants;
import net.jquant.common.Utils;
import net.jquant.downloader.BasicDownloader;
import net.jquant.downloader.Downloader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import net.jquant.model.StockData;
import net.jquant.model.Symbol;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-9-12.
 */
public class ReferenceDataProvider {

    /**
     * 分配预案数据源
     */
    private static String FPYA_URL = "http://quotes.money.163.com/data/caibao/fpyg.html?reportdate=%s&sort=declaredate&order=desc&page=%s";

    private static String MARGINTRADE_SH_URL = "http://data.eastmoney.com/rzrq/sh.html";

    private static String MARGINTRADE_SZ_URL = "http://data.eastmoney.com/rzrq/sz.html";

    //http://data.eastmoney.com/rzrq/total.html
    private static String MARGINTRADE_TOTAL_URL = "http://datainterface.eastmoney.com/EM_DataCenter/JS.aspx?type=FD&sty=SHSZHSSUM&st=0&sr=1&p=1&ps=10000&rt=48069399";

    /**
     * 股东人数变化
     * mkt=市场
     * 1=沪深A股
     * 2=沪市A股
     * 3=深市A股
     * 4=中小板
     * 5=创业板
     * fd=时间,格式yyyy-MM-dd
     */
    private static String SHAREHOLDER_COUNT_URL = "http://datainterface.eastmoney.com/EM_DataCenter/JS.aspx?type=GG&sty=GDRS&st=2&sr=-1&p=1&ps=5000&mkt=%s&fd=%s&rt=48094965";

    private static String SHAREHOLDER_STOCK_URL = "http://f10.eastmoney.com/f10_v2/ShareholderResearch.aspx?code=%s";

    /**
     * 股东人数变化，周期为季度
     *
     * @param market market
     * @param date date
     * @return stock data list
     */
    public static List<StockData> getShareHolderCountData(String market, String date) {
        List<StockData> stockDataList = Lists.newLinkedList();

        Date day = Utils.str2Date(date, "yyyyMMdd");
        String url = String.format(SHAREHOLDER_COUNT_URL, market, Utils.formatDate(day, "yyyy-MM-dd"));
        String data = Downloader.download(url);
        Gson gson = new Gson();
        List<String> records = gson.fromJson(data.substring(1, data.length() - 1), List.class);
        for (String record : records) {
            String[] fields = record.split(",", 16);
            StockData stockData = new StockData(fields[0]);
            stockData.name = fields[1];
            stockData.date = Utils.str2Date(fields[14], "yyyy-MM-dd");
            stockData.put("股东人数(户)", Double.parseDouble(fields[2]));
            stockData.put("较上期变化", Double.parseDouble(fields[3]));
            stockData.put("人均流通股(股)", Double.parseDouble(fields[4]));
            stockData.put("前十大流通股东_持股数量", Double.parseDouble(fields[5]));
            stockData.put("前十大流通股东_占流通股本比例", Double.parseDouble(fields[6]));
            stockData.put("十大股东_持股数量", Double.parseDouble(fields[7]));
            stockData.put("十大股东_占流通股本比例", Double.parseDouble(fields[8]));
            stockData.put("机构持仓_持股数量", Double.parseDouble(fields[9]));
            stockData.put("机构持仓_占流通股本比例", Double.parseDouble(fields[10]));
            stockData.attr("筹码集中度", fields[11]);
            stockDataList.add(stockData);
        }
        return stockDataList;
    }

    public static List<StockData> getShareHolderCountData(String symbol) {
        String url = String.format(SHAREHOLDER_STOCK_URL, Symbol.getSymbol(symbol, SHAREHOLDER_STOCK_URL));
        String data = Downloader.download(url);
        Elements trList = Jsoup.parse(data).getElementById("Table0").getElementsByTag("tbody").get(0).getElementsByTag("tr");
        List<List<String>> rows = Lists.newArrayList();

        Elements header = trList.get(0).getElementsByTag("th");
        List<String> headerList = Lists.newArrayList();
        for(int i=1;i<header.size();i++){
            headerList.add(header.get(i).text());
        }
        rows.add(headerList);

        for(int i=1;i<trList.size();i++){
            Elements tdList = trList.get(i).getElementsByTag("td");
            List<String> fields = Lists.newArrayList();
            for(int j=0;j<tdList.size();j++){
                fields.add(tdList.get(j).text());
            }
            rows.add(fields);
        }

        List<StockData> stockDataList = Lists.newArrayListWithCapacity(20);
        if(rows.size()==0){
            return Lists.newLinkedList();
        }
        int columnCount = rows.get(0).size();

        for(int i=0;i<columnCount;i++){
            StockData stockData = new StockData(symbol);
            stockData.date = Utils.str2Date(rows.get(0).get(i),"yy-MM-dd");
            if(rows.get(1).get(i).contains("万")){
                stockData.put("股东人数(户)",Utils.str2Double(rows.get(1).get(i).replaceAll("万",""))*10000);
            }else{
                stockData.put("股东人数(户)",Utils.str2Double(rows.get(1).get(i)));
            }
            stockData.put("股东人数较上期变化(%)",Utils.str2Double(rows.get(2).get(i)));

            if(rows.get(3).get(i).contains("万")){
                stockData.put("人均流通股(股)",Utils.str2Double(rows.get(3).get(i).replaceAll("万",""))*10000);
            }else{
                stockData.put("人均流通股(股)",Utils.str2Double(rows.get(3).get(i)));
            }
            stockData.put("人均流通股较上期变化(%)",Utils.str2Double(rows.get(4).get(i)));
            stockData.attr("筹码集中度",rows.get(5).get(i));
            stockData.put("股价",Utils.str2Double(rows.get(6).get(i)));

            if(rows.get(3).get(i).contains("万")){
                stockData.put("人均持股金额(元)",Utils.str2Double(rows.get(7).get(i).replaceAll("万",""))*10000);
            }else{
                stockData.put("人均持股金额(元)",Utils.str2Double(rows.get(7).get(i)));
            }
            stockData.put("前十大股东持股合计(%)",Utils.str2Double(rows.get(8).get(i)));
            stockData.put("前十大流通股东持股合计(%)",Utils.str2Double(rows.get(9).get(i)));
            stockDataList.add(stockData);
        }

        return stockDataList;
    }

    /**
     * 获取分配预案数据
     *
     * @param year 年份
     * @return stock data list
     */
    public static List<StockData> getFPYA(String year) {
        Integer pageCount = getPageCount(year);
        List<StockData> stockDataList = Lists.newLinkedList();
        for (int i = 0; i < pageCount; i++) {
            String url = String.format(FPYA_URL, year, i);
            stockDataList.addAll(collectFPYA(url));
        }
        return stockDataList;
    }

    public static String FHRZ_URL = "http://f10.eastmoney.com/f10_v2/BonusFinancing.aspx?code=%s";

    /**
     * 获取分红融资数据
     * @param symbol stock symbol
     * @return stock data list
     */
    public static List<StockData> getFHRZ(String symbol) {
        String url = String.format(FHRZ_URL, Symbol.getSymbol(symbol, FHRZ_URL));
        String data = Downloader.download(url);
        Elements elements = Jsoup.parse(data).getElementById("BonusDetailsTable").getElementsByTag("tbody").get(0).getElementsByTag("tr");
        List<StockData> stockDataList = Lists.newLinkedList();
        for (int i = 1; i < elements.size(); i++) {
            Elements tr = elements.get(i).getElementsByTag("td");
            if (!tr.get(3).text().equals("--")) {
                StockData stockData = new StockData(symbol);
                stockData.date = Utils.str2Date(tr.get(3).text(), "yyyy-MM-dd");
                stockData.attr("分红方案", tr.get(1).text());
                stockDataList.add(stockData);
            }
        }
        return stockDataList;
    }

    public static String FHPG_URL = "http://quotes.money.163.com/f10/fhpg_%s.html";

    /**
     * 获取分红配股数据
     * @param symbol stock symbol
     * @return stock data list
     */
    public static List<StockData> getFHPG(String symbol) {
        String url = String.format(FHPG_URL, symbol);
        String data = Downloader.download(url);
        Elements elements = Jsoup.parse(data).select("table[class=table_bg001 border_box limit_sale]").get(0).getElementsByTag("tbody").get(0).getElementsByTag("tr");
        List<StockData> stockDataList = Lists.newLinkedList();
        for (int i = 0; i < elements.size(); i++) {
            Elements tr = elements.get(i).getElementsByTag("td");
            StockData stockData = new StockData(symbol);
            stockData.date = Utils.str2Date(tr.get(5).text(), "yyyy-MM-dd");    //股权登记日
            stockData.attr(StockConstants.DATE_OF_DECLARATION, tr.get(0).text());
            stockData.attr(StockConstants.ANNUAL, tr.get(1).text());
            if(isNotNull(tr.get(2).text())){
                stockData.put(StockConstants.STOCK_SHARES,Utils.str2Double(tr.get(2).text()));
            }
            if(isNotNull(tr.get(3).text())){
                stockData.put(StockConstants.STOCK_TRANSFERRED,Utils.str2Double(tr.get(3).text()));
            }
            if(isNotNull(tr.get(4).text())){
                stockData.put(StockConstants.STOCK_DIVIDEND,Utils.str2Double(tr.get(4).text()));
            }
            if(isNotNull(tr.get(5).text())){
                stockData.attr(StockConstants.DATE_OF_RECORD, tr.get(5).text());
            }
            if(isNotNull(tr.get(6).text())){
                stockData.attr(StockConstants.DATE_OF_EX_DIVIDEND, tr.get(6).text());
            }
//            if(isNotNull(tr.get(7).text())){
//                stockData.attr("红股上市日", tr.get(7).text());
//            }
            stockDataList.add(stockData);
        }
        return stockDataList;
    }

    public static boolean isNotNull(String text){
        if(!"--".equals(text)){
            return true;
        }
        return false;
    }

    private static Integer getPageCount(String year) {
        String url = String.format(FPYA_URL, year, 0);
        String data = Downloader.download(url);
        Elements pageURLs = Jsoup.parse(data).select("div[class=mod_pages] a");
        Element lastPageURL = pageURLs.get(pageURLs.size() - 2);
        int pageCount = Integer.parseInt(lastPageURL.text());
        return pageCount;
    }

    public static List<StockData> collectFPYA(String url) {
        List<StockData> stockDataList = Lists.newLinkedList();

        String data = Downloader.download(url);
        Elements table = Jsoup.parse(data).getElementById("plate_performance").getElementsByTag("tbody").get(0).getElementsByTag("tr");
        for (Element tr : table) {
            Elements td = tr.getElementsByTag("td");
            StockData stockData = new StockData(td.get(1).text());
            stockData.name = td.get(2).text();
            stockData.date = Utils.str2Date(td.get(5).text(), "yyyy-MM-dd");

            stockData.put("dividend", getDividend(td.get(4).text().trim()));//分红
            stockData.put("shares", getShares(td.get(4).text().trim()));//转增和送股
            stockDataList.add(stockData);
        }
        return stockDataList;
    }

    /**
     * 获取分红数据
     *
     * @param plan
     * @return
     */
    private static Double getDividend(String plan) {
        Pattern pattern = Pattern.compile("分红(.*?)元");
        Matcher matcher = pattern.matcher(plan);
        if (matcher.find()) {
            String group = matcher.group(1);
            return Double.parseDouble(group);
        }
        return 0d;
    }

    /**
     * 获取转增和送股数
     *
     * @return
     */
    private static Double getShares(String plan) {
        Pattern pattern = Pattern.compile("[增|股](.*?)股");
        Matcher matcher = pattern.matcher(plan);
        Double count = 0d;
        while (matcher.find()) {
            String group = matcher.group(1);
            count += Double.parseDouble(group);
        }
        return count;
    }

    /**
     * 获取大盘融资融券数据
     *
     * @return stock data list
     */
    public static List<StockData> getTotalMarginTrade() {
        List<StockData> stockDataList = Lists.newLinkedList();

        String data = BasicDownloader.download(MARGINTRADE_TOTAL_URL);
        System.out.println(data);
        Gson gson = new Gson();
        List<String> records = gson.fromJson(data.substring(1, data.length() - 1), List.class);

        for (String record : records) {
            String[] fields = record.split(",");
            StockData stockData = new StockData();
            stockData.date = Utils.str2Date(fields[0].replaceAll("\"", "").trim(), "yyyy-MM-dd");
            stockData.put("rzye_sh", getYI(fields[1]));
            stockData.put("rzye_sz", getYI(fields[2]));
            stockData.put("rzye_total", getYI(fields[3]));
            stockData.put("rzmre_sh", getYI(fields[4]));
            stockData.put("rzmre_sz", getYI(fields[5]));
            stockData.put("rzmre_total", getYI(fields[6]));
            stockData.put("rqylye_sh", getYI(fields[7]));
            stockData.put("rqylye_sz", getYI(fields[8]));
            stockData.put("rqylye_total", getYI(fields[9]));
            stockData.put("rzrqye_sh", getYI(fields[10]));
            stockData.put("rzrqye_sz", getYI(fields[11]));
            stockData.put("rzrqye_total", getYI(fields[12]));

            stockDataList.add(stockData);
        }
        return stockDataList;
    }

    public static Double getYI(String text) {
        if (text.trim().equals("-")) {
            return 0d;
        }
        return Double.parseDouble(text.trim());
    }

    /**
     * 获取两市融资融券数据
     * 市场融资融券交易总量＝本日融资余额＋本日融券余量金额
     * 本日融资余额＝前日融资余额＋本日融资买入额－本日融资偿还额；
     * 本日融资偿还额＝本日直接还款额＋本日卖券还款额＋本日融资强制平仓额＋本日融资正权益调整－本日融资负权益调整；
     * 本日融券余量=前日融券余量+本日融券卖出数量-本日融券偿还量；
     * 本日融券偿还量＝本日买券还券量＋本日直接还券量＋本日融券强制平仓量＋本日融券正权益调整－本日融券负权益调整－本日余券应划转量；
     * 融券单位：股（标的证券为股票）/份（标的证券为基金）/手（标的证券为债券）。
     *
     * @return stock data list
     */
    public static List<StockData> getMarginTrade() {
        List<StockData> stockDataList = Lists.newArrayListWithCapacity(1000);
        stockDataList.addAll(getMarginTrade(MARGINTRADE_SH_URL));
        stockDataList.addAll(getMarginTrade(MARGINTRADE_SZ_URL));
        return stockDataList;
    }

    /**
     * 获取融资融券数据
     * @param url url
     * @return stock data list
     */
    public static List<StockData> getMarginTrade(String url) {
        List<StockData> stockDataList = Lists.newLinkedList();

        String data = Downloader.downloadAjaxData(url);
        Elements table = Jsoup.parse(data).getElementById("dt_1").getElementsByTag("tbody").get(0).getElementsByTag("tr");
        for (Element tr : table) {
            Elements td = tr.getElementsByTag("td");
            StockData stockData = new StockData(td.get(0).text());
            stockData.name = td.get(1).text();
            //融资余额(元)
            stockData.put("rzye", getYuan(td.get(3).text()));
            //融券余额(元)
            stockData.put("rqye", getYuan(td.get(4).text()));
            //融资买入额(元)
            stockData.put("rzmre", getYuan(td.get(5).text()));
            //融资偿还额(元)
            stockData.put("rzche", getYuan(td.get(6).text()));
            //融资净买额(元)
            stockData.put("rzjme", getYuan(td.get(7).text()));
            //融券余量
            stockData.put("rqyl", getYuan(td.get(8).text()));
            //融券卖出量
            stockData.put("rqmcl", getYuan(td.get(9).text()));
            //融券偿还量
            stockData.put("rqchl", getYuan(td.get(10).text()));
            //融资融券余额(元)
            stockData.put("rzrqye", getYuan(td.get(11).text()));
            stockDataList.add(stockData);
        }
        return stockDataList;
    }

    private static double getYuan(String text) {
        if (text.trim().contains("万")) {
            return Double.parseDouble(text.replaceAll("万", "").trim()) * 10000;
        } else if (text.trim().contains("亿")) {
            return Double.parseDouble(text.replaceAll("亿", "").trim()) * 100000000;
        } else {
            return Double.parseDouble(text.trim());
        }
    }

    public static void main(String[] args) {
//        List<StockData> fpya = ReferenceDataProvider.getFPYA("2014");
//        fpya.forEach((StockData stockData) ->
//            System.out.println(stockData.toString() + "\t" +
//                    stockData.get("dividend") + "\t" +
//                    stockData.get("shares")
//            ));

//        List<StockData> margintrade = ReferenceDataProvider.getMarginTrade();
//        margintrade.forEach(stockData ->{
//            System.out.println(stockData.toString());
//            Utils.printMap(stockData);
//        });

        List<StockData> margintrade = ReferenceDataProvider.getTotalMarginTrade();
        margintrade.forEach(stockData -> {
            System.out.println(stockData.toString());
            Utils.printMap(stockData);
        });
    }
}
