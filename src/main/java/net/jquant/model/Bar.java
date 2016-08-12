package net.jquant.model;

import java.util.Date;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-11-17.
 */
public class Bar {
    private long id;
    private Date date; //close time
    private double open;
    private double high;
    private double low;
    private double close;
    private int volume;
    private double amount;

    public Bar(long id,Tick tick){
        this.id = id;
        this.date = tick.date;
        this.open = tick.price;
        this.high = tick.price;
        this.low = tick.price;
        this.close = tick.price;
        this.volume = tick.volume;
        this.amount = tick.amount;
    }

    public void addTick(Tick tick){
        this.date = tick.date;
        if(this.high<tick.price){
            this.high = tick.price;
        }
        if(this.low > tick.price){
            this.low = tick.price;
        }
        this.close = tick.price;
        this.volume += tick.volume;
        this.amount += tick.amount;
    }

    public void appendBar(Bar bar){

    }

    @Override
    public String toString() {
        return "Bar{" +
                "id=" + id +
                ", date=" + date +
                ", open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", close=" + close +
                ", volume=" + volume +
                ", amount=" + amount +
                '}';
    }
}
