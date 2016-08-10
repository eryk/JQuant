package quant.fans.model;

import com.google.common.base.Strings;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-8-10.
 */
public class Symbol {

    public String code;

    public String site;

    public StockMarketType stockMarketType;

    public BoardType boardType;

    public static String getSymbol(String code,String url){
        if(url.contains("sina.com")){
            return sinaSymbol(code);
        }
        if(url.contains("163.com")){
            return netEaseSymbol(code);
        }
        if(url.contains("quote.eastmoney.com")){
            if(code.equals("000001")){
                return "zs000001";
            }else if(code.equals("399001")){
                return "zs399001";
            }else{
                return sinaSymbol(code);
            }
        }
        if(url.contains("ifeng.com")){
            return sinaSymbol(code);
        }
        if(url.contains("nuff.eastmoney.com")){
            return eastMoneyRealTimeSymbol(code);
        }
        if(url.contains("f10.eastmoney.com")){
            return sinaSymbol(code);
        }
        return "";
    }

    /**
     * 雅虎股票接口需要将股票代码后面加上.ss或者.sz
     * @param symbol stock symbol
     * @return yahoo symbol
     */
    public static String yahooSymbol(String symbol){
        if(Strings.isNullOrEmpty(symbol) && symbol.length() != 6){
            return "";
        }
        if(symbol.startsWith("0") || symbol.startsWith("3")){
            return symbol + ".sz";
        }
        if(symbol.startsWith("6")){
            return symbol + ".ss";
        }
        return "";
    }

    public static String sinaSymbol(String symbol){
        if(Strings.isNullOrEmpty(symbol) && symbol.length() != 6){
            return "";
        }
        if(symbol.startsWith("0") || symbol.startsWith("3")){
            return "sz" + symbol;
        }
        if(symbol.startsWith("6")){
            return "sh" + symbol;
        }
        return "";
    }

    public static String eastMoneyRealTimeSymbol(String symbol){
        if(Strings.isNullOrEmpty(symbol) && symbol.length() != 6){
            return "";
        }
        if(symbol.startsWith("6")){
            return symbol + "1";
        }
        if(symbol.startsWith("0") || symbol.startsWith("3")){
            return symbol + "2";
        }
        return "";
    }

    public static String netEaseSymbol(String symbol){
        if(Strings.isNullOrEmpty(symbol) && symbol.length() != 6){
            return "";
        }
        if(symbol.startsWith("6")){
            return "0" + symbol;
        }
        if(symbol.startsWith("0") || symbol.startsWith("3")){
            return "1" + symbol;
        }
        return "";
    }

    public static StockMarketType getStockMarketType(String symbol){
        return StockMarketType.getType(symbol);
    }

    public static BoardType getBoardType(String symbol){
        return BoardType.getType(symbol);
    }
}
