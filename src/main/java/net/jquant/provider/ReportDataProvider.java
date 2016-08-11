package net.jquant.provider;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import net.jquant.common.Utils;
import net.jquant.downloader.Downloader;
import net.jquant.model.StockData;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-9-22.
 * 分析师研究报告数据，数据来源
 *  http://data.eastmoney.com/report/
 */
public class ReportDataProvider {

    /**
     * 个股研究报告地址
     * p=页数，一页200条
     */
    private static String STOCK_REPORT_URL = "http://datainterface.eastmoney.com//EM_DataCenter/js.aspx?type=SR&sty=GGSR&ps=200&p=%s&mkt=0&stat=0&cmd=2&rt=48097671";

    /**
     * 盈利预期数据 http://data.eastmoney.com/report/ylyc.html
     * param1=版块，全部=_A
     */
    private static String EXPECT_EARNINGS_URL = "http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?type=CT&cmd=C._A&sty=GEMCPF&st=(AllNum)&sr=-1&p=2&ps=5000&cb=&token=3a965a43f705cf1d9ad7e1a3e429d622&rt=48090871";

    private static String EXPECT_EARNINGS_PAGE_URL = "http://data.eastmoney.com/report/ylyc.html";


    /**
     * 个股研究报告
     * @param startDate 报告起始时间，格式:yyyyMMdd
     * @return stock data list
     */
    public static List<StockData> getStockReportData(String startDate){
        Date start = Utils.str2Date(startDate,"yyyyMMdd");

        List<StockData> stockDataList = Lists.newLinkedList();
        int pageCount = 1;
        while(pageCount<2){
            String url = String.format(STOCK_REPORT_URL, pageCount);
            String data = Downloader.download(url);
            Gson gson = new Gson();
            List<Map<String,Object>> list = gson.fromJson(data.substring(1, data.length() - 1), List.class);
            for(Map<String,Object> record:list){
                Date date = Utils.str2Date(String.valueOf(record.get("datetime")).replaceAll("T", " "), "yyyy-MM-dd HH:mm:ss");
                if(date.getTime() < start.getTime()){
                    break;
                }
                StockData stockData = new StockData(String.valueOf(record.get("secuFullCode")).substring(0,6));
                stockData.name = String.valueOf(record.get("secuName"));
                stockData.date = date;
                stockData.attr("上次评级",String.valueOf(record.get("sratingName")));
                stockData.attr("评级",String.valueOf(record.get("rate")));
                stockData.attr("评级变动",String.valueOf(record.get("change")));
                stockData.attr("title",String.valueOf(record.get("title")));
                stockData.attr("机构名称",String.valueOf(record.get("insName")));
                stockData.attr("机构评级",String.valueOf(record.get("insStar")));   //1-5，5是最好
                stockData.attr("author",String.valueOf(record.get("author")));

                fillRecord(record, stockData ,"jlrs","净利润");
                fillRecord(record, stockData ,"sys","每股收益");
                fillRecord(record, stockData ,"syls","市盈率");

                stockDataList.add(stockData);
            }
            pageCount++;
        }
        return stockDataList;
    }

    private static void fillRecord(Map<String, Object> record, StockData stockData,String recordKeyName,String stockAttrName) {
        List<String> list = (List<String>) record.get(recordKeyName);
        for(int i =0;i<list.size();i++){
            if(!Strings.isNullOrEmpty(list.get(i))){
                String year = Utils.getYear(i);
                stockData.attr(year + stockAttrName,list.get(i));
            }
        }
    }

    /**
     * 获取盈利预期数据，全部版块=_A
     * @return stock data list
     */
    public static List<StockData> getExpectEarnings(){
        List<StockData> stockDataList = Lists.newLinkedList();

        String data = Downloader.download(EXPECT_EARNINGS_URL);

        Gson gson = new Gson();
        List<String> records = gson.fromJson(data.substring(1, data.length() - 1), List.class);
        for(String record:records){
            String[] fields = record.split(",",21);
            double yanbaoCount = Double.parseDouble(fields[5]);
            if(yanbaoCount<=0){
                break;
            }
            StockData stockData = new StockData(fields[1]);
            stockData.name = fields[2];
            stockData.put("close",checkValueIsDouble(fields[3]));
            stockData.put("change",checkValueIsDouble(fields[4].replace("%","")));
            stockData.put("研报数",yanbaoCount);
            stockData.put("机构投资评级(近六个月)_买入",checkValueIsDouble(fields[6]));
            stockData.put("机构投资评级(近六个月)_增持",checkValueIsDouble(fields[7]));
            stockData.put("机构投资评级(近六个月)_中性",checkValueIsDouble(fields[8]));
            stockData.put("机构投资评级(近六个月)_减持",checkValueIsDouble(fields[9]));
            stockData.put("机构投资评级(近六个月)_卖出",checkValueIsDouble(fields[10]));
            stockData.put(Utils.getYear(-1) + "实际_收益",checkValueIsDouble(fields[11]));
            stockData.put(Utils.getYear(0) + "预测_收益",checkValueIsDouble(fields[12]));
            stockData.put(Utils.getYear(0) + "预测_市盈率",checkValueIsDouble(fields[13]));
            stockData.put(Utils.getYear(1) + "预测_收益",checkValueIsDouble(fields[14]));
            stockData.put(Utils.getYear(1) + "预测_市盈率",checkValueIsDouble(fields[15]));
            stockData.put(Utils.getYear(2) + "预测_收益",checkValueIsDouble(fields[16]));
            stockData.put(Utils.getYear(2) + "预测_市盈率",checkValueIsDouble(fields[17]));
            stockDataList.add(stockData);
        }
        return stockDataList;
    }


    private static Double checkValueIsDouble(String field){
        if(Utils.isDouble(field)){
            return Double.parseDouble(field);
        }
        return Double.MIN_VALUE;
    }
}
