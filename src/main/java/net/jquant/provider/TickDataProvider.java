package net.jquant.provider;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import net.jquant.common.Utils;
import net.jquant.downloader.BasicDownloader;
import net.jquant.model.Symbol;
import net.jquant.model.Tick;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-9-3.
 * 逐笔数据
 */
public class TickDataProvider {
    private static final Logger LOG = LoggerFactory.getLogger(TickDataProvider.class);
    //http://vip.stock.finance.sina.com.cn/quotes_service/view/vMS_tradehistory.php?symbol=sh600199&date=2015-11-16
    private static String tickHisDataURL = "http://market.finance.sina.com.cn/downxls.php?date=%s&symbol=%s";

    private static String tickRTDataURL = "http://vip.stock.finance.sina.com.cn/quotes_service/view/CN_TransListV2.php?num=10000&symbol=%s&rn=%s";

    /**
     *
     * @param symbol stock symbol
     * @param date yyyy-MM-dd
     * @return tick data list
     */
    public static List<Tick> get(String symbol, String date){
        String url = String.format(tickHisDataURL, date, Symbol.getSymbol(symbol, tickHisDataURL));
        InputStream input = BasicDownloader.downloadStream(url);

        List<Tick> ticks = Lists.newLinkedList();
        try {
            List<String> lines = CharStreams.readLines(new InputStreamReader(input, "gbk"));
            for(int i = 1;i<lines.size();i++){
                String[] fields = lines.get(i).split("\t", 6);
                Tick tick = new Tick();
                tick.date = Utils.str2Date(date + fields[0],"yyyy-MM-ddHH:mm:ss");
                tick.price = Utils.getDouble(fields[1]);
                tick.volume = Utils.getInt(fields[3]);
                tick.amount = Utils.getDouble(fields[4]);
                tick.type = getTickType(fields[5]);

                ticks.add(tick);
            }
        } catch (Exception e) {
            LOG.error("fail to get tick list data",e);
        }
        //按照从早晨到下午顺序存储
        return Lists.reverse(ticks);
    }

    public static List<Tick> get(String symbol){
        String url = String.format(tickRTDataURL,Symbol.getSymbol(symbol,tickRTDataURL),new Date().getTime());
        String data = BasicDownloader.download(url);
        Pattern pattern = Pattern.compile("(\\(.*\\))");
        Matcher matcher = pattern.matcher(data);
        List<Tick> ticks = Lists.newLinkedList();
        while(matcher.find()){
            String line = matcher.group().replaceAll("[(|)|'| ]","");
            if(!Strings.isNullOrEmpty(line)){
                String[] fields = line.split(",",4);
                Tick tick = new Tick();
                tick.date =  Utils.str2Date(Utils.getNow("yyyyMMdd") + fields[0],"yyyyMMddHH:mm:ss");
                tick.volume = Utils.getInt(fields[1])/100;
                tick.price = Utils.getDouble(fields[2]);
                tick.amount = Utils.formatDouble(tick.volume * tick.price * 100,"#.##");
                tick.type = getTickType(fields[3]);
                ticks.add(tick);
            }
        }
        return ticks;
    }

    private static Tick.Type getTickType(String type){
        if(type.equals("买盘") || type.equals("UP")){
            return Tick.Type.BUY;
        }else if(type.equals("卖盘") || type.equals("DOWN")){
            return Tick.Type.SELL;
        }else{
            return Tick.Type.MID;
        }
    }

    public static void main(String[] args) throws IOException {
        List<Tick> ticks = get("600376","2015-11-16");
        for(Tick tick:ticks){
            System.out.println(tick);
        }



//        String date = "2015-09-02";
//
//        List<String> stockList = StockMap.getStockListWithConditions();
//        for(String symbol : stockList){
//            StockData stockData = RealTimeDataProvider.get(symbol);
//            if(stockData.get("change")< -9.5){
//                continue;
//            }
//
//            List<Tick> ticks = TickDataProvider.get(symbol, date);
//
//            double avgVolume = 0;
//            for(Tick tick:ticks){
//                avgVolume += tick.volume;
//            }
//            avgVolume = avgVolume/ticks.size();
//
//            int count = 0;
//            for(int i = 1 ;i<ticks.size();i++){
//                Tick tick = ticks.get(i);
//
//                if(tick.type == Tick.Type.BUY && tick.volume > avgVolume * 30 && !tick.date.contains("15:00:") && !tick.date.contains("09:30:")){
////                    bufferedWriter.write(ticks.get(i) + "\n");
//                    System.out.println(ticks.get(i));
//                    count++;
//                }
//            }
//            if(count>1){
////                bufferedWriter.write(symbol + ",平均每笔成交量:" + Utils.formatDouble(avgVolume)+ "\n");
//                System.out.println(symbol + ",平均每笔成交量:" + Utils.formatDouble(avgVolume));
//            }
//        }
////        bufferedWriter.close();
    }
}
