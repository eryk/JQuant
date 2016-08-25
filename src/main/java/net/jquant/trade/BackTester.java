package net.jquant.trade;

import net.jquant.common.Context;
import org.joda.time.DateTime;

public class BackTester {
    private Context context;

    private DateTime start;
    private DateTime end;
    private String frequency;   //day,minute,tick

    public void init(Context context){
        this.context = context;
    }



    public void handleDate(){

    }

}
