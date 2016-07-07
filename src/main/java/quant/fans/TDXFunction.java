package quant.fans;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-8-12.
 */
public class TDXFunction {
    Core core;

    public TDXFunction() {
        core = new Core();
    }

    /**
     * 计算一段时间周期内的最大值
     *
     * @param prices
     * @param period
     * @return
     */
    public double[] hhv(double[] prices, int period) {
        double[] output = new double[prices.length];
        double[] tempOutPut = new double[prices.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        begin.value = -1;
        length.value = -1;

        RetCode max = core.max(0, prices.length - 1, prices, period, begin, length, tempOutPut);

        for (int i = 0; i < period; i++) {
            output[i] = 0;
        }
        for (int i = period; 0 < i && i < (prices.length); i++) {
            output[i] = tempOutPut[i - period];
        }
        return output;
    }

    public double[] llv(double[] prices, int period) {
        double[] output = new double[prices.length];
        double[] tempOutPut = new double[prices.length];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();
        begin.value = -1;
        length.value = -1;

        RetCode max = core.min(0, prices.length - 1, prices, period, begin, length, tempOutPut);

        for (int i = 0; i < period; i++) {
            output[i] = 0;
        }
        for (int i = period; 0 < i && i < (prices.length); i++) {
            output[i] = tempOutPut[i - period];
        }
        return output;
    }

    /**
     * 计算A和B两条线的金叉和死叉
     * 当A在B线以下，返回-1
     * 当A向上穿过B（金叉），返回0
     * 当A在B线以上，返回1
     * 当A向下穿过B（死叉），返回0
     *
     * @param lineA
     * @param lineB
     * @return
     */
    public double[] crossPoint(double[] lineA, double[] lineB) {
        if (lineA != null && lineB != null & lineA.length != lineB.length && lineA.length != 0) {
            return new double[0];
        }
        int length = lineA.length;
        double[] output = new double[length];
        for (int i = 0; i < length; i++) {
            if (lineA[i] < lineB[i]) {
                output[i] = -1;
            } else if (lineA[i] > lineB[i]) {
                output[i] = 1;
            } else {
                output[i] = 0;
            }
        }
        return output;
    }

    /**
     * 判断a和b是否在最后一个周期内相交
     *
     * @param lineA
     * @param lineB
     * @return
     */
    public boolean cross(double[] lineA, double[] lineB) {
        if (lineA != null && lineB != null & lineA.length != lineB.length && lineA.length != 0) {
            return false;
        }
        if (lineA[lineA.length - 2] < lineB[lineB.length - 2] && lineA[lineA.length - 1] > lineB[lineB.length - 1]) {
            return true;
        }
        return false;
    }

    public boolean crossBetween(double[] lineA, double[] lineB, int period) {
        if (lineA != null && lineB != null & lineA.length != lineB.length && lineA.length != 0) {
            return false;
        }
        int length = lineA.length;
        for (int i = length - period; i < length; i++) {
            if (lineA[i - 1] < lineB[i - 1] && lineA[i] > lineB[i]) {
                return true;
            }
        }
        return false;
    }

    public double max(double... values) {
        double tmp = Double.MIN_VALUE;
        for (double value : values) {
            if (value > tmp) {
                tmp = value;
            }
        }
        return tmp;
    }

    public double min(double... values) {
        double tmp = Double.MAX_VALUE;
        for (double value : values) {
            if (value < tmp && value >0) {
                tmp = value;
            }
        }
        return tmp;
    }
}
