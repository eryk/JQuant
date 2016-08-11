package net.jquant.model;

import java.util.Date;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-9-3.
 */
public class Tick {

    public Date date;     //时间，格式是HH:mm:ss
    public double price;    //成交价格  单位：元
    public int volume;   //成交量   单位：手
    public double amount;   //成交额
    public Type type;

    public enum Type{
        BUY(1),    //买盘
        MID(0),   //卖盘
        SELL(-1);     //中性盘

        int type;

        Type(int type){
            this.type = type;
        }

        public int getType(){
            return type;
        }

        @Override
        public String toString(){
            if(this == BUY){
                return "买盘";
            }else if(this == SELL){
                return "卖盘";
            }else{
                return "中性盘";
            }
        }
    }

    @Override
    public String toString() {
        return "Tick{" +
                "时间='" + date + '\'' +
                ", 价格=" + price +
                ", 成交量=" + volume +
                ", 成交额=" + amount +
                ", 类型=" + type +
                '}';
    }
}
