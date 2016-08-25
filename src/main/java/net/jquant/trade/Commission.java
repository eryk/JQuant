package net.jquant.trade;

//交易手续费
public class Commission {
    private final double buyCost; //买入时手续费
    private final double sellCost;    //卖出时手续费
    private final double minCost;    //卖出时手续费

    //每笔交易时的手续费是, 买入时万分之三，卖出时万分之三加千分之一印花税, 每笔交易最低扣5块钱
    public Commission(){
        buyCost = 0.0003;
        sellCost = 0.0013;
        minCost = 5;
    }

    public Commission(double buyCost,double sellCost,double minCost){
        this.buyCost = buyCost;
        this.sellCost = sellCost;
        this.minCost = minCost;
    }

    public double getBuyCost() {
        return buyCost;
    }

    public double getSellCost() {
        return sellCost;
    }

    public double getMinCost() {
        return minCost;
    }
}
