package quant.fans.tools;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.util.List;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-10-21.
 */
public class SharpeRatio {
    /**
     * 夏普比率 = 实际回报率 / 回报率的标准差
     * Computes the Sharpe ratio for a list of returns.
     *
     * @param returns The returns
     * @param rf The risk free average return
     *
     * @return The Sharpe ratio
     */
    public static double value(List<Double> returns, double rf) {
        SummaryStatistics ss = new SummaryStatistics();
        returns.forEach((xx) -> ss.addValue(xx - rf));

        return ss.getMean() / ss.getStandardDeviation();
    }

    public static double value(List<Double> returns) {
        return value(returns, 0);
    }
}
