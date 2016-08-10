package quant.fans.provider;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import quant.fans.common.Utils;
import quant.fans.downloader.BasicDownloader;
import quant.fans.downloader.Downloader;
import quant.fans.model.StockData;

import java.util.Date;
import java.util.List;

/**
 * 龙虎榜数据
 * 数据来源
 *      http://data.eastmoney.com/stock/stockstatistic.html
 *      http://vip.stock.finance.sina.com.cn/q/go.php/vInvestConsult/kind/lhb/index.phtml
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-9-16.
 */
public class TopListDataProvider {
    /**
     * 每日龙虎榜详情
     * param1=yyyy-MM-dd
     */
    public static String dailyTopURL = "http://data.eastmoney.com/stock/lhb/%s.html";

    /**
     * 东方财富网    个股龙虎榜统计
     * stat,月份，1，3，6，12或其他
     * st，按列排序：
     *      2=龙虎榜成交金额（万）
     *      3=上榜次数
     *      4=买入额（万）
     *      5=卖出额（万）
     *      6=净额（万）
     * rt=8位随机数，貌似是30秒间隔
     */
    public static String stockHistoryTopURL = "http://datainterface.eastmoney.com/EM_DataCenter/JS.aspx?type=LHB&sty=GGTJ&" +
            "stat=%&st=%s&ps=5000&rt=%s";
    /**
     * 新浪网    个股龙虎榜统计
     * last，天数，5，10，30，60
     * p：页数
     */
    public static String sinaStockHistoryTopURL = "http://vip.stock.finance.sina.com.cn/q/go.php/vLHBData/kind/ggtj/index.phtml?last=%s&p=%s";

    /**
     * 营业部上榜统计
     */
    public static String sinaBusinessHistoryTopURL = "http://vip.stock.finance.sina.com.cn/q/go.php/vLHBData/kind/yytj/index.phtml?last=5&p=1";

    /**
     * 机构席位追踪
     * last:天数，5，10，30，60
     * p:页数
     */
    public static String sinaOrganizationTopURL = "http://vip.stock.finance.sina.com.cn/q/go.php/vLHBData/kind/jgzz/index.phtml?last=%s&p=%s";

    /**
     * 机构席位成交明细
     * ps:最近多少条
     */
    public static String eastMoneyOrganizationDetail = "http://datainterface.eastmoney.com/EM_DataCenter/JS.aspx?type=LHB&sty=JGXWMX&p=1&ps=%s&rt=48090807";

    /**
     * 每日龙虎榜详情
     * @param date yyyyMMdd
     * @return stock data list
     */
    public static List<StockData> getDailyTopList(String date){
        Date day = Utils.str2Date(date,"yyyyMMdd");

        List<StockData> stockDataList = Lists.newArrayListWithExpectedSize(50);
        String url  = String.format(dailyTopURL,Utils.formatDate(day,"yyyy-MM-dd"));
        String htmlSource = Downloader.download(url);
        Elements table = Jsoup.parse(htmlSource).getElementById("dt_1").getElementsByTag("tbody").get(0).select("tr[class^=all]");
        Elements tmpLine = null;
        for(Element tr:table){
            Elements td = tr.getElementsByTag("td");
            if(td.size()==11){
                tmpLine = td;
                StockData stockData = new StockData(td.get(1).text());
                stockData.name = td.get(2).text();
                stockData.date = Utils.str2Date(date, "yyyy-MM-dd");
                stockData.attr("change",td.get(4).text());
                stockData.attr("龙虎榜成交额(万)",td.get(5).text());
                stockData.attr("买入额(万)",td.get(6).text());
                stockData.attr("买入额占总成交比例",td.get(7).text());
                stockData.attr("卖出额(万)",td.get(8).text());
                stockData.attr("卖出额占总成交比例",td.get(9).text());
                stockData.attr("reason",td.get(10).text());
                stockDataList.add(stockData);
            }else if(td.size()==6){
                StockData stockData = new StockData(tmpLine.get(1).text());
                stockData.name = tmpLine.get(2).text();
                stockData.date = Utils.str2Date(date, "yyyy-MM-dd");
                stockData.attr("change",tmpLine.get(4).text());
                stockData.attr("龙虎榜成交额(万)",td.get(5).text());
                stockData.attr("买入额(万)",td.get(0).text());
                stockData.attr("买入额占总成交比例",td.get(1).text());
                stockData.attr("卖出额(万)",td.get(2).text());
                stockData.attr("卖出额占总成交比例",td.get(3).text());
                stockData.attr("reason",td.get(4).text());
                stockDataList.add(stockData);
            }
        }
        return stockDataList;
    }

    /**
     * 个股龙虎榜统计
     * @param dayCount 取值:5,10,30,60
     * @return stock data list
     */
    public static List<StockData> getStockRanking(int dayCount){
        int pageCount = getPageCount(sinaStockHistoryTopURL,dayCount);
        String url;
        String data;
        Document doc;
        List<StockData> stockDataList = Lists.newLinkedList();
        for(int i=1;i<=pageCount;i++){
            url =String.format(sinaStockHistoryTopURL,dayCount,i);
            data = BasicDownloader.download(url,"gb2312");
            doc = Jsoup.parse(data);

            Elements trList = doc.getElementById("dataTable").getElementsByTag("tbody").get(0).getElementsByTag("tr");
            for(Element tr:trList){
                Elements tdList = tr.getElementsByTag("td");
                StockData stockData = new StockData(tdList.get(0).text());
                stockData.name = tdList.get(1).text();
                stockData.put("上榜次数",Double.parseDouble(tdList.get(2).text()));
                stockData.put("累积购买额(万)",Double.parseDouble(tdList.get(3).text()));
                stockData.put("累积卖出额(万)",Double.parseDouble(tdList.get(4).text()));
                stockData.put("净额(万)",Double.parseDouble(tdList.get(5).text()));
                stockData.put("买入席位数",Double.parseDouble(tdList.get(6).text()));
                stockData.put("卖出席位数",Double.parseDouble(tdList.get(7).text()));
                stockDataList.add(stockData);
            }
        }
        return stockDataList;
    }

    public static List<StockData> getOrganizationRanking(int dayCount){
        int pageCount = getPageCount(sinaOrganizationTopURL,dayCount);
        String url;
        String data;
        Document doc;
        List<StockData> stockDataList = Lists.newLinkedList();
        for(int i=1;i<=pageCount;i++){
            url =String.format(sinaOrganizationTopURL,dayCount,i);
            data = BasicDownloader.download(url,"gb2312");
            doc = Jsoup.parse(data);

            Elements trList = doc.getElementById("dataTable").getElementsByTag("tbody").get(0).getElementsByTag("tr");
            for(Element tr:trList){
                Elements tdList = tr.getElementsByTag("td");
                System.out.println(tdList.html());
                StockData stockData = new StockData(tdList.get(0).text());
                stockData.name = tdList.get(1).text();
//                stockData.put("close",Double.parseDouble(tdList.get(2).text()));
//                stockData.put("change",Double.parseDouble(tdList.get(3).text()));
                stockData.put("累积买入额(万)",Double.parseDouble(tdList.get(4).text()));
                stockData.put("买入次数",Double.parseDouble(tdList.get(5).text()));
                stockData.put("累积卖出额(万)",Double.parseDouble(tdList.get(6).text()));
                stockData.put("卖出次数",Double.parseDouble(tdList.get(7).text()));
                stockData.put("净额(万)",Double.parseDouble(tdList.get(8).text()));
                stockDataList.add(stockData);
            }
        }
        return stockDataList;
    }

    /**
     * 机构席位成交明细
     * @param count count
     * @return stock data list
     */
    public static List<StockData> getOrganizationDetailRanking(int count){
        List<StockData> stockDataList = Lists.newLinkedList();
        String url = String.format(eastMoneyOrganizationDetail, count);
        String data = Downloader.download(url);
        Gson gson = new Gson();
        List<String> records = gson.fromJson(data.substring(1, data.length() - 1), List.class);
        for(String record:records){
            String[] fields = record.split(",",6);
            StockData stockData= new StockData(fields[2].substring(0,6));
            stockData.name = fields[4];
            stockData.date = Utils.str2Date(fields[5],"yyyy-MM-dd");

            stockData.attr("reason",fields[0]);
            stockData.put("机构席位卖出",Double.parseDouble(fields[1]));
            stockData.put("机构席位买入",Double.parseDouble(fields[3]));
            stockDataList.add(stockData);
        }
        return stockDataList;
    }

    private static int getPageCount(String baseURL,int dayCount) {
        String url =String.format(baseURL,dayCount,1);
        String data = BasicDownloader.download(url, "gb2312");
        Document doc = Jsoup.parse(data);
        return doc.select("div[class=pages]").get(0).getElementsByTag("a").size()-2;
    }

    public static void main(String[] args) {
        List<StockData> stockDataList = TopListDataProvider.getDailyTopList("2015-09-17");
        for(StockData stockData:stockDataList){
            System.out.println(stockData.toString());
            Utils.printMapStr(stockData.attribute);
        }
    }
}
