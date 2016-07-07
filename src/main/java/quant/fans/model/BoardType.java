package quant.fans.model;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-8-12.
 */
public enum BoardType {
    MAIN, //主板
    SME, //中小板
    GEM, //创业板
    UNKNOW;

    public static BoardType getType(String symbol){
        if(symbol.startsWith("6") || symbol.startsWith("000")){
            return MAIN;
        }else if(symbol.startsWith("002")){
            return SME;
        }else if(symbol.startsWith("3")){
            return GEM;
        }else{
            return UNKNOW;
        }
    }

    public boolean isMatchType(String symbol){
        if(this.equals(MAIN)){
            return symbol.startsWith("6") || symbol.startsWith("000");
        }else if(this.equals(SME)){
            return symbol.startsWith("002");
        }else if(this.equals(GEM)){
            return symbol.startsWith("3");
        }
        return false;
    }
}
