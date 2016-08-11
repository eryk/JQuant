package net.jquant.common;

import java.nio.charset.Charset;

/**
 * Created by eryk on 2015/7/20.
 */
public class Constants {
    public static final Charset UTF8 = Charset.forName("UTF-8");

    public static final String DATABASE_POOL_SIZE = "database.pool.size";
    public static final String SCHEDULE_POOL_SIZE = "schedule.pool.size";
    public static final String THREAD_POOL_SIZE = "thread.pool.size";

    public static final String BROKER_POOL_SIZE = "broker.pool.size";
    public static final String BROKER_CHECK_INTERVAL = "broker.check.interval";

    public static final String TRADER_ACCOUNT_ID = "trader.account.id";
    public static final String TRADER_TRADING_DAY_COUNT = "trader.trading.day.count";
    public static final String TRADER_POOL_SIZE = "trader.pool.size";
    public static final String TRADER_EXCEL_BASE_DIR = "trader.execl.base.dir";

    public static String NETEASE_DATE_STYLE = "yyyy-MM-dd";

    public static String IFENG_DATE_STYLE = "yyyy-MM-dd";

    public static String BISNESS_DATA_FORMAT = "yyyyMMdd";

    public static final String MINUTE_ROWKEY_DATA_FORMAT = "yyyyMMddHHmm";

    public static final String SECOND_ROWKEY_DATA_FORMAT = "yyyyMMddHHmmss";

    public static final String MARKET_START_DATE = "19901219";

    public static final String SCHEDULER_JOBS = "jobs";

    public static final String SCHEDULER_JOB_CLASSNAME = "classname";

    public static final String SCHEDULER_JOB_CRON = "cron";

    public static int SECOND = 1 * 1000;
    public static int MINUTE = 60 * SECOND;
    public static int MINUTES_5 = 5 * MINUTE;
    public static int MINUTES_15  = 15 * MINUTE;
    public static int MINUTES_30 = 30 * MINUTE;
    public static int MINUTES_60 = 60 * MINUTE;
    public static int DAY = 1440 * MINUTE;
    public static int WEEK = 7 * 1440 * MINUTE;

    /**
     * table definition
     */
    public static final String TABLE_STOCK_5_MINUTES = "stocks_data_5mins";

    public static final String TABLE_STOCK_15_MINUTES = "stocks_data_15mins";

    public static final String TABLE_STOCK_30_MINUTES = "stocks_data_30mins";

    public static final String TABLE_STOCK_60_MINUTES = "stocks_data_60mins";

    public static final String TABLE_STOCK_DAILY = "stocks_data_daily";

    public static final String TABLE_STOCK_WEEK = "stocks_data_week";

    public static final String TABLE_STOCK_MONTH = "stocks_data_month";

    public static final String TABLE_STOCK_TICK = "stocks_tick_data";

    public static final byte[] TABLE_CF_DATA = "d".getBytes();

    public static final String TABLE_STOCK_INFO = "stocks_info";

    public static final byte[] TABLE_CF_INFO = "i".getBytes();

    public static final String TABLE_ARTICLE = "stocks_article";

    public static final byte[] TABLE_CF_ARTICLE = "a".getBytes();

    public static final String[] ACCOUNT_HEADER = {"交易时间","起始资产","期末资产","交易盈亏","收益率","夏普比率","索提诺比率","操作正确率","最大单笔盈利","最大单笔亏损","平均每笔盈利","基准收益额","基准收益率","最大资产","最小资产","最大回撤","交易次数","盈利次数","亏损次数","持仓总时间","平均持仓时间"};

    public static final String[] ACCOUNT_COLUMN = {"date","start","end","pnl","pnlRate","sharpe","sortino","accuracy","maxEarnPerOp","maxLossPerOp","meanEarnPerOp","benchmarkBenfit","benchmarkBenfitPercent","max","min","drawdown","totalOperate","earnOperate","lossOperate","totalPositionDays","avgPositionDays"};

    public static final String[] POSITION_HEADER = {"代码","买入日期","卖出日期","成交量","成交额","买入价","卖出价","盈亏额"};

    public static final String[] POSITION_COLUMN = {"symbol","buyDate","sellDate","volume","amount","price","sellPrice","pnl"};

    /**
     * stock column
     */
    public static final byte[] CLOSE = "close".getBytes();
    public static final byte[] HIGH = "high".getBytes();
    public static final byte[] LOW = "low".getBytes();
    public static final byte[] OPEN = "open".getBytes();
    public static final byte[] LAST_CLOSE = "lastClose".getBytes();
    public static final byte[] CHANGE_AMOUNT = "changeAmount".getBytes();
    public static final byte[] CHANGE = "change".getBytes();
    public static final byte[] TURNOVER_RATE = "turnoverRate".getBytes();
    public static final byte[] VOLUME = "volume".getBytes();
    public static final byte[] AMOUNT = "amount".getBytes();
    public static final byte[] TOTAL_VALUE = "totalValue".getBytes();
    public static final byte[] MARKET_VALUE = "marketValue".getBytes();
    public static final byte[] AMPLITUDE = "amplitude".getBytes();
    public static final byte[] NAME = "name".getBytes();
    public static final byte[] STATUS = "status".getBytes();

    public static final byte[] AVG_COST = "avgCost".getBytes();
}
