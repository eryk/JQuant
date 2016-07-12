package quant.fans.strategy;

import com.google.common.base.Preconditions;
import quant.fans.common.Utils;

/**
 * 公式为: f* = ( bp – q ) / b
    p = 胜率
    q = 败率 = 1 – p
    b = 平均获利 / 平均损失

 这个定义的公式经过移项后可以换成比较容易记忆的: 胜率 – (败率 / 盈亏比)
 KELLY = p – ( q / b )

 */
public class KellyFormula {

    private double p = 0;
    private double q = 0;
    private double b = 0;
    private double f = 0;

    /**
     * @param p 胜率
     * @param b 赔率
     */
    public KellyFormula(double p, double b) {
        Preconditions.checkArgument(p > 0 && p < 1);
        Preconditions.checkArgument(b > 1);
        this.p = p;
        this.q = 1 - p;
        this.b = b;
        f = Utils.formatDouble(compute(p, b));
    }

    /**
     *
     * @param p 胜率
     * @param profit
     * @param loss  盈亏比,赔率,盈亏比平均赢的金额/平均输的金额
     */
    public KellyFormula(double p, double profit, double loss) {
        Preconditions.checkArgument(p > 0 && p < 1);
        Preconditions.checkArgument(profit > 0 && loss > 0 && profit > loss);
        this.p = p;
        this.q = 1 - p;
        this.b = profit / loss;
        this.f = Utils.formatDouble(p - q / b);
    }

    //胜率 – (败率 / 盈亏比)
    public static double compute(double p, double b) {
        return p - (1 - p) / b;
    }

    public static double compute(double p, double profit, double loss) {
        return p - (1 - p) / (profit / loss);
    }

}
