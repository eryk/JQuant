package quant.fans.common;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;

/**
 * start:今天往前的n个交易日，步长为天
 * stop：明天，因为hbase的stoprow是开区间
 */
public class DateRange {

    private int dayCount;

    private DateRange() {
    }

    private DateRange(int dayCount) {
        this.dayCount = dayCount;
    }

    public static DateRange getRange(int dayCount) {
        return new DateRange(dayCount);
    }

    public static DateRange getRange(){
        return new DateRange(0);
    }

    public String start() {
        return start("yyyyMMdd");
    }

    public String start(String format) {
        DateTime dt = new DateTime();
        dt = addDays(dt, dayCount);  //[)
        return dt.toString(format);
    }

    public Date startDate(String format){
        return Utils.str2Date(start(),format);
    }

    public Date startDate(){
        return startDate("yyyyMMdd");
    }

    private DateTime addDays(DateTime dateTime, int days) {
        DateTime dt = dateTime;
        for (int i = 0; i < days; i++) {
            dt = dt.plusDays(-1);
            if (dt.getDayOfWeek() == 7) {
                dt = dt.plusDays(-2);
            }
        }
        return dt;
    }

    public String stop() {
        return stop("yyyyMMdd");
    }

    public String stop(String format) {
        DateTime dt = new DateTime();
        dt = dt.plusDays(1);
        return dt.toString(format);
    }

    public Date stopDate(String format){
        return Utils.str2Date(stop(),format);
    }

    public Date stopDate(){
        return stopDate("yyyyMMdd");
    }

    /**
     * 获取从开始时间到结束时间的交易日期
     * @param format
     * @return
     */
    public List<String> getDateList(String format){
        List<String> dateList = Lists.newArrayListWithCapacity(dayCount+1);
        DateTime tmpDate = new DateTime(startDate());
        DateTime stop = new DateTime(stopDate());
        while(tmpDate.toDate().getTime() <=stop.toDate().getTime()){
            //只添加周一到周五
            if(tmpDate.getDayOfWeek()>=1 && tmpDate.getDayOfWeek()<=5){
                dateList.add(tmpDate.toString(format));
            }
            tmpDate = tmpDate.plusDays(1);
        }
        return dateList;
    }

    public List<String> getDateList(){
        return getDateList("yyyyMMdd");
    }
}
