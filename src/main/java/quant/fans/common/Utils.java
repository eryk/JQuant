package quant.fans.common;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.yaml.snakeyaml.Yaml;
import quant.fans.model.StockData;
import quant.fans.model.Tick;
import quant.fans.tools.Sleeper;

import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;

import static quant.fans.common.Constants.MINUTE_ROWKEY_DATA_FORMAT;
import static quant.fans.common.Constants.SECOND_ROWKEY_DATA_FORMAT;
import static quant.fans.common.Constants.UTF8;

/**
 * Created by eryk on 2015/7/4.
 */
public class Utils {

    public static Date bytes2Date(byte[] bytes,String pattern){
        DateTimeFormatter format = DateTimeFormat.forPattern(pattern);
        DateTime dateTime = DateTime.parse(toString(bytes),format);
        return dateTime.toDate();
    }

    public static Date double2Date(Double date){
        long time = Double.doubleToLongBits(date);
        DateTime dateTime = new DateTime(time);
        return dateTime.toDate();
    }

    public static Date long2Date(Long date){
        DateTime datetime = new DateTime(date);
        return datetime.toDate();
    }

    public static Date str2Date(String date, String format){
        return DateTimeFormat.forPattern(format).parseDateTime(date).toDate();
    }

    public static String getNow(){
        DateTime dateTime = new DateTime();
        return dateTime.toString(MINUTE_ROWKEY_DATA_FORMAT);
    }

    public static String getNow(String pattern){
        DateTime dateTime = new DateTime();
        return dateTime.toString(pattern);
    }

    /**
     * 计算两个日期之间相差的天数
     * @param smdate 较小的时间
     * @param bdate 较大的时间
     * @return 相差天数
     */
    public static int daysBetween(Date smdate,Date bdate){
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days=(time2-time1)/(1000*3600*24);
        return Integer.parseInt(String.valueOf(between_days));
    }

    //TODO 时间取整
    public static String getTomorrow(){
        DateTime dateTime = new DateTime();
        dateTime = dateTime.plusDays(1);
        return dateTime.toString(MINUTE_ROWKEY_DATA_FORMAT);
    }

    public static String getTomorrow(String pattern){
        DateTime dateTime = new DateTime();
        dateTime = dateTime.plusDays(1);
        return dateTime.toString(pattern);
    }

    //TODO 时间取整
    public static String getYesterday(){
        DateTime dateTime = new DateTime();
        dateTime = dateTime.plusDays(-1);
        return dateTime.toString(MINUTE_ROWKEY_DATA_FORMAT);
    }

    public static String getYesterday(String pattern){
        DateTime dateTime = new DateTime();
        dateTime = dateTime.plusDays(-1);
        return dateTime.toString(pattern);
    }

    public static String getYear(int plusYear){
        DateTime dateTime = new DateTime();
        dateTime = dateTime.plusYears(plusYear);
        return String.valueOf(dateTime.getYear());
    }

    public static String formatDate(Date date){
        DateTime dateTime = new DateTime(date);
        return dateTime.toString("yyyy/MM/dd HH:mm:ss");
    }

    public static String formatDate(Date date,String format){
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(format);
    }

    public static Date getDailyClosingTime(Date date){
        DateTime dateTime = new DateTime(date);
        dateTime = dateTime.plusHours(15);
        return dateTime.toDate();
    }

    public static byte[] getRowkeyWithMd5PrefixAndDaySuffix(StockData stock) {
        byte[] md5 = md5Prefix(stock.symbol,4);
        byte[] symbol = toBytes(stock.symbol);
        byte[] date = toBytes(Utils.formatDate(stock.date, MINUTE_ROWKEY_DATA_FORMAT));
        return add(md5,symbol,date);
    }

    public static byte[] getTickRowkey(String symbolStr,Tick tick){
        byte[] md5 = md5Prefix(symbolStr,4);
        byte[] symbol = toBytes(symbolStr);
        byte[] date = toBytes(Utils.formatDate(tick.date,SECOND_ROWKEY_DATA_FORMAT));
        return add(md5,symbol,date);
    }

    public static byte[] getRowkeyWithMd5PrefixAndDateSuffix(String symbol,String date){
        return add(getRowkeyWithMD5Prefix(symbol.getBytes()), date.getBytes());
    }

    public static byte[] getRowkeyWithMD5Prefix(StockData stock){
        return add(md5Prefix(stock.symbol,4),stock.symbol.getBytes());
    }

    public static byte[] getRowkeyWithMD5Prefix(String symbol){
        return add(md5Prefix(symbol,4),symbol.getBytes());
    }

    public static byte[] getRowkeyWithMD5Prefix(byte[] symbol){
        return add(md5Prefix(toString(symbol),4),symbol);
    }

    public static byte[] md5Prefix(String rowkey,int length){
        return head(Hashing.md5().hashString(rowkey,UTF8).toString().getBytes(),length);
    }

    /**
     * 从rowkey的bytes中获取symbol和date信息
     * @param rowkey
     * @return
     */
    public static String getStockSymbol(byte[] rowkey){
        if(rowkey.length == 22){ //4byte_md5_prefix + symbol + yyyyMMddHHmm
            return toString(rowkey).substring(4,10);
        }else if(rowkey.length == 24){  //4byte_md5_prefix + symbol + yyyyMMddHHmmss
            return toString(rowkey).substring(4,10);
        }
        return "";
    }

    public static Date getStockDate(byte[] rowkey){
        return Utils.bytes2Date(tail(rowkey,12), MINUTE_ROWKEY_DATA_FORMAT);
    }

    public static Date getTickDate(byte[] rowkey){
        return Utils.bytes2Date(tail(rowkey,14), SECOND_ROWKEY_DATA_FORMAT);
    }

    public static List<URL> findResources(String name) throws IOException {
        List<URL> urls = new ArrayList<URL>();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> res = cl.getResources(name);
        while (res.hasMoreElements())
            urls.add(res.nextElement());
        return urls;
    }

    public static Integer getInt(Object o) {
        if (o == null)
            return 0;
        else if (o instanceof String)
            return Integer.parseInt((String) o);
        else if (o instanceof Long)
            return ((Long) o).intValue();
        else if (o instanceof Integer)
            return (Integer) o;
        else if (o instanceof Short)
            return ((Short) o).intValue();
        else
            throw new IllegalArgumentException("Don't know how to convert " + o
                    + " + to integer");
    }

    public static Long getLong(Object o) {
        if (o == null)
            return 0l;
        else if (o instanceof String)
            return Long.parseLong((String)o);
        else if (o instanceof Long)
            return (Long) o;
        else if (o instanceof Integer)
            return ((Integer) o).longValue();
        else if (o instanceof Short)
            return ((Short) o).longValue();
        else
            throw new IllegalArgumentException("Don't know how to convert " + o
                    + " + to long integer");
    }

    public static Double getDouble(Object o) {
        if (o == null)
            return 0.0;
        else if (o instanceof String)
            return Double.parseDouble((String)o);
        else if (o instanceof Long)
            return ((Long) o).doubleValue();
        else if (o instanceof Integer)
            return ((Integer) o).doubleValue();
        else if (o instanceof Short)
            return ((Short) o).doubleValue();
        else if (o instanceof Float)
            return ((Float) o).doubleValue();
        else if (o instanceof Double)
            return (Double) o;
        throw new IllegalArgumentException("Don't know how to convert " + o
                + " to double");
    }

    public static Map readYamlConf(String name, boolean asResource)
            throws IOException {
        InputStream input = null;
        try {
            if (asResource) {
                List<URL> urls = findResources(name);
                if (urls.isEmpty())
                    throw new IOException("Resource `" + name + "' not found");
                else if (urls.size() > 1)
                    throw new IOException("Multiple resources `" + name
                            + "' found");
                else
                    input = urls.get(0).openStream();
            } else
                input = new FileInputStream(name);
            return readYamlConf(input);
        } finally {
            if (input != null)
                input.close();
        }
    }

    public static Map readYamlConf(InputStream input) throws IOException {
        InputStreamReader reader = new InputStreamReader(input, UTF8);
        Yaml yaml = new Yaml();
        Map conf = (Map) yaml.load(reader);
        return conf == null ? new HashMap() : conf;
    }


    public static Map loadConf(String file) throws IOException {
        Yaml yaml = new Yaml();
        Map conf = (Map) yaml.load(new FileInputStream(file));
        return conf;
    }

    public static String toStr(Object obj){
        if(obj !=null){
            return String.valueOf(obj);
        }
        return "";
    }

    public static String getStrOrEmpty(Map conf,String key){
        Preconditions.checkNotNull(conf);
        Preconditions.checkNotNull(key);
        return toStr(conf.get(key));
    }

    public static double str2Double(String numStr){
        if(Strings.isNullOrEmpty(numStr)){
            return 0d;
        }else{
            return Double.parseDouble(numStr);
        }
    }

    public static long str2Long(String numStr){
        if(Strings.isNullOrEmpty(numStr)){
            return 0l;
        }else{
            return  Long.parseLong(numStr);
        }
    }

    public static double formatDouble(double num){
        return formatDouble(num,"#.####");
    }

    public static double formatDouble(double num,String format){
        DecimalFormat df=new DecimalFormat(format);
        return Double.parseDouble(df.format(num));
    }

    public static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Class getClass(String className){
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isNotNullorZero(Double val){
        if(val!=null || val>0){
            return true;
        }else{
            return false;
        }
    }

    public static void printMap(Map<String,Double> map){
        for(Map.Entry<String,Double> entry:map.entrySet()){
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
    }

    public static String map2Json(Map map){
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    public static void printMapStr(Map<String,String> map){
        for(Map.Entry<String,String> entry:map.entrySet()){
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
    }

    public static double getAmount(String amount) {
        double val = Double.parseDouble(amount.replaceAll("[亿|千万|百万|十万|万]", ""));
        if(amount.contains("亿")){
            return val * 100000000;
        }
        if(amount.contains("千万")){
            return val * 10000000;
        }
        if(amount.contains("百万")){
            return val * 1000000;
        }
        if(amount.contains("十万")){
            return val * 100000;
        }
        if(amount.contains("万")){
            return val * 10000;
        }
        return val;
    }

    public static boolean isInRange(Date date,String startDate,String stopDate){
        if(date.getTime() >= str2Date(startDate,"yyyyMMdd").getTime() && date.getTime() <= str2Date(stopDate,"yyyyMMdd").getTime()){
            return true;
        }else{
            return false;
        }
    }

    public static void closeThreadPool(ExecutorService threadPool){
        if(threadPool==null){
            return;
        }
        threadPool.shutdown();
        while(!threadPool.isTerminated()){
            threadPool.shutdownNow();
            Sleeper.sleep(1000);
        }
    }

    public static List tailList(List list,int tail){
        int size = list.size();
        return Lists.newArrayList(list.subList(size - tail, size));
    }

    public static List headList(List list,int head){
        return Lists.newArrayList(list.subList(0,head));
    }

    public static double[] tailArray(double[] array,int tail){
        return Arrays.copyOfRange(array, array.length - tail, array.length);
    }

    public static double[] headArray(double[] array,int head){
        return Arrays.copyOfRange(array,0,head);
    }

    public static double[] getArrayFrom(List<StockData> stockDataList, String columnName) {
        double[] closes = new double[stockDataList.size()];
        for (int i = 0; i < stockDataList.size(); i++) {
            closes[i] = stockDataList.get(i).get(columnName);
        }
        return closes;
    }

    public static String[] toArray(List stockList){
        String[] array = new String[stockList.size()];
        stockList.toArray(array);
        return array;
    }

    /**
     * 判断当前时间是否是交易时间段
     * 周一到周五
     * 上午：09:30-11:30
     * 下午：13:00-15:00
     * @return
     */
    public static boolean isTradingTime(){
        DateTime dateTime = new DateTime();
        if(dateTime.dayOfWeek().get()>=1 && dateTime.getDayOfWeek()<=5){
            LocalTime now = dateTime.toLocalTime();
            LocalTime start = new LocalTime(9,30,0,0);
            LocalTime mid1 = new LocalTime(11,30,0,0);
            LocalTime mid2 = new LocalTime(13,0,0,0);
            LocalTime end = new LocalTime(15,0,0,0);
            if((now.isAfter(start) && now.isBefore(mid1))||(now.isAfter(mid2) && now.isBefore(end))){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前时间是否是交易时间段
     * 周一到周五
     * 上午：09:30-11:30
     * 下午：13:00-14:45
     * @return
     */
    public static boolean isNearTradingTime(){
        DateTime dateTime = new DateTime();
        if(dateTime.dayOfWeek().get()>=1 && dateTime.getDayOfWeek()<=5){
            LocalTime now = dateTime.toLocalTime();
            LocalTime start = new LocalTime(9,30,0,0);
            LocalTime mid1 = new LocalTime(11,30,0,0);
            LocalTime mid2 = new LocalTime(13,0,0,0);
            LocalTime end = new LocalTime(14,45,0,0);
            if((now.isAfter(start) && now.isBefore(mid1))||(now.isAfter(mid2) && now.isBefore(end))){
                return true;
            }
        }
        return false;
    }

    /**
     * 获取最新的交易日期
     * @return
     */
    public static String getRecentWorkingDay(Date date,String format){
        DateTime dt = new DateTime(date);
        while(dt.getDayOfWeek()>= DateTimeConstants.SATURDAY){
            dt = dt.plusDays(-1);
        }
        return dt.toString(format);
    }

    public static boolean isBetween(double value,double start,double stop){
        return Range.open(start, stop).contains(value);
    }

    /**
     * 获取数据复权数据
     * @param stockData
     * @param sg
     * @param zz
     * @param px
     * @return
     */
    public static StockData fuquan(StockData stockData,double sg,double zz,double px){
        double percent = (stockData.get("close") - px/10) / ((zz+10)/10+sg/10) / stockData.get("close");
        stockData.put("close",stockData.get("close") * percent);
        stockData.put("high",stockData.get("high") * percent);
        stockData.put("low",stockData.get("low") * percent);
        stockData.put("open",stockData.get("open") * percent);
        stockData.put("lastClose",stockData.get("lastClose") * percent);
        return stockData;
    }

    public static List<String> getYearBetween(String startDate, String stopDate) {
        List<String> years = Lists.newLinkedList();
        DateTime start = new DateTime(Utils.str2Date(startDate,"yyyyMMdd"));
        DateTime stop = new DateTime(Utils.str2Date(stopDate,"yyyyMMdd"));
        while(start.getYear() <= stop.getYear()){
            years.add(String.valueOf(start.getYear()));
            start = start.plusYears(1);
        }
        return years;
    }

    public static Map<String,String> toMap(StockData stockData){
        Map<String,String> result = Maps.newHashMap();
        result.putAll(stockData.attribute);
        for(Map.Entry<String,Double> entry:stockData.entrySet()){
            result.put(entry.getKey(),String.valueOf(entry.getValue()));
        }
        result.put("symbol",stockData.symbol);
        if(!Strings.isNullOrEmpty(stockData.name)){
            result.put("name",stockData.name);
        }
        result.put("date",Utils.formatDate(stockData.date,"yyyyMMdd"));
        return result;
    }

    /**
     * @param b Presumed UTF-8 encoded byte array.
     * @return String made from <code>b</code>
     */
    public static String toString(final byte [] b) {
        if (b == null) {
            return null;
        }
        return toString(b, 0, b.length);
    }

    /**
     * Joins two byte arrays together using a separator.
     * @param b1 The first byte array.
     * @param sep The separator to use.
     * @param b2 The second byte array.
     */
    public static String toString(final byte [] b1,
                                  String sep,
                                  final byte [] b2) {
        return toString(b1, 0, b1.length) + sep + toString(b2, 0, b2.length);
    }

    /**
     * This method will convert utf8 encoded bytes into a string. If
     * an UnsupportedEncodingException occurs, this method will eat it
     * and return null instead.
     *
     * @param b Presumed UTF-8 encoded byte array.
     * @param off offset into array
     * @param len length of utf-8 sequence
     * @return String made from <code>b</code> or null
     */
    public static String toString(final byte [] b, int off, int len) {
        if (b == null) {
            return null;
        }
        if (len == 0) {
            return "";
        }
        try {
            return new String(b, off, len, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * @param a lower half
     * @param b upper half
     * @return New array that has a in lower half and b in upper half.
     */
    public static byte [] add(final byte [] a, final byte [] b) {
        return add(a, b, new byte [0]);
    }

    /**
     * @param a first third
     * @param b second third
     * @param c third third
     * @return New array made from a, b and c
     */
    public static byte [] add(final byte [] a, final byte [] b, final byte [] c) {
        byte [] result = new byte[a.length + b.length + c.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        System.arraycopy(c, 0, result, a.length + b.length, c.length);
        return result;
    }

    /**
     * @param a array
     * @param length amount of bytes to grab
     * @return First <code>length</code> bytes from <code>a</code>
     */
    public static byte [] head(final byte [] a, final int length) {
        if (a.length < length) {
            return null;
        }
        byte [] result = new byte[length];
        System.arraycopy(a, 0, result, 0, length);
        return result;
    }

    /**
     * @param a array
     * @param length amount of bytes to snarf
     * @return Last <code>length</code> bytes from <code>a</code>
     */
    public static byte [] tail(final byte [] a, final int length) {
        if (a.length < length) {
            return null;
        }
        byte [] result = new byte[length];
        System.arraycopy(a, a.length - length, result, 0, length);
        return result;
    }

    /**
     * Converts a string to a UTF-8 byte array.
     * @param s string
     * @return the byte array
     */
    public static byte[] toBytes(String s) {
        return s.getBytes(UTF8);
    }

    /**
     * Convert an int value to a byte array
     * @param val value
     * @return the byte array
     */
    public static byte[] toBytes(int val) {
        byte [] b = new byte[4];
        for(int i = 3; i > 0; i--) {
            b[i] = (byte) val;
            val >>>= 8;
        }
        b[0] = (byte) val;
        return b;
    }
}
