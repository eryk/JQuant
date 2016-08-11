package net.jquant.provider;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.joda.time.DateTime;
import net.jquant.common.DateRange;
import net.jquant.common.Utils;
import net.jquant.downloader.Downloader;
import net.jquant.model.StockData;

import java.util.List;
import java.util.Map;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-8-30.
 * 财务报表数据
 */
public class FinanceDataProvider {

    //主要财务指标
    private static String mainFinanceReport = "http://quotes.money.163.com/service/zycwzb_%s.html?type=report";
    //盈利能力
    private static String profitReport = "http://quotes.money.163.com/service/zycwzb_%s.html?type=report&part=ylnl";
    //偿还能力
    private static String debtReport = "http://quotes.money.163.com/service/zycwzb_%s.html?type=report&part=chnl";
    //成长能力
    private static String growReport = "http://quotes.money.163.com/service/zycwzb_%s.html?type=report&part=cznl";
    //营运能力
    private static String operateReport = "http://quotes.money.163.com/service/zycwzb_%s.html?type=report&part=yynl";
    //财务报表摘要
    private static String abstractFinanceReport = "http://quotes.money.163.com/service/cwbbzy_%s.html";

    public static List<StockData> get(String symbol, String startDate, String stopDate) {
        Map<String, Map<String, String>> reports = collect(symbol);
        List<StockData> puts = Lists.newLinkedList();
        for(Map.Entry<String,Map<String,String>> entry:reports.entrySet()){
            StockData stockData = new StockData(symbol);
            stockData.date = Utils.str2Date(entry.getKey(), "yyyy-MM-dd");

            if(Utils.isInRange(stockData.date,startDate,stopDate)){
                for(Map.Entry<String,String> kv:entry.getValue().entrySet()){
                    if(Utils.isDouble(kv.getValue())){
                        stockData.put(kv.getKey(),Double.parseDouble(kv.getValue()));
                    }else{
                        stockData.put(kv.getKey(),Double.MIN_VALUE);
                    }
                }
                puts.add(stockData);
            }
        }
        return puts;
    }

    public static List<StockData> getYear(String symbol){
        DateRange range = DateRange.getRange(365*20);
        List<StockData> stockDataList = get(symbol,range.start(),range.stop());
        List<StockData> stockDataYearList = Lists.newArrayList();
        for(StockData stockData:stockDataList){
            DateTime time = new DateTime(stockData.date);
            if(time.getMonthOfYear()==12){
                stockDataYearList.add(stockData);
            }
        }
        return stockDataYearList;
    }

    private static Map<String,Map<String,String>> collect(String symbol) {
        Map<String,Map<String,String>> report = Maps.newTreeMap();

        String[] lines = Downloader.download(String.format(mainFinanceReport, symbol)).split("\n");
        toReport(lines, report);
        lines = Downloader.download(String.format(profitReport, symbol)).split("\n");
        toReport(lines, report);
        lines = Downloader.download(String.format(debtReport, symbol)).split("\n");
        toReport(lines, report);
        lines = Downloader.download(String.format(growReport, symbol)).split("\n");
        toReport(lines, report);
        lines = Downloader.download(String.format(operateReport, symbol)).split("\n");
        toReport(lines, report);
        lines = Downloader.download(String.format(abstractFinanceReport, symbol)).split("\n");
        toReport(lines, report);

        return report;
    }

    private static void toReport(String[] lines, Map<String, Map<String, String>> report) {
        if(lines.length<=1){
            return;
        }
        List<String[]> columns = Lists.newArrayList();
        for(int i=0;i<lines.length;i++){
            if(!Strings.isNullOrEmpty(lines[i].trim())){
                columns.add(lines[i].split(","));
            }
        }

        //列
        for(int i=1;i<columns.get(0).length;i++){
            Map<String,String> maps = Maps.newTreeMap();
            //行
            for(int j=1;j<columns.size();j++){
                if(i<columns.get(j).length){
                    maps.put(columns.get(j)[0],columns.get(j)[i]);
                }
            }
            if(report.get(columns.get(0)[i])!=null&&!Strings.isNullOrEmpty(columns.get(0)[i])){
                report.get(columns.get(0)[i]).putAll(maps);
            }else if(!Strings.isNullOrEmpty(columns.get(0)[i].trim())){
                report.put(columns.get(0)[i],maps);
            }
        }
    }

}
