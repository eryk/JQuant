package net.jquant.model;


import net.jquant.common.Constants;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-8-9.
 */
public enum PeriodType {
    ONE_MIN("1"),
    FIVE_MIN("5"),
    FIFTEEN_MIN("15"),
    THIRTY_MIN("30"),
    SIXTY_MIN("60"),
    DAY("daily"),
    WEEK("week"),
    MONTH("month"),
    YEAR("year");

    private String type;

    private PeriodType(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public int getIntValue(){
        int val = 0;
        switch (this) {
            case ONE_MIN:
                val =  Constants.MINUTE;
                break;
            case FIVE_MIN:
                val =  Constants.MINUTES_5;
                break;
            case FIFTEEN_MIN:
                val =  Constants.MINUTES_15;
                break;
            case THIRTY_MIN:
                val =  Constants.MINUTES_30;
                break;
            case SIXTY_MIN:
                val =  Constants.MINUTES_60;
                break;
            case DAY:
                val = Constants.DAY;
                break;
            case WEEK:
                val = Constants.WEEK;
                break;
        }
        return val;
    }
}
