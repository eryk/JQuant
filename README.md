# JQuant

![微信订阅号](https://github.com/eryk/JQuant/blob/test/qrcode_for_gh_188ebb8e2c0a_258.jpg "微信订阅号")

# [changelog](https://github.com/eryk/JQuant/wiki#changelog)

# Maven依赖

```xml
<dependencies>
    <dependency>
        <groupId>net.jquant</groupId>
        <artifactId>JQuant</artifactId>
        <version>0.1</version>
    </dependency>
</dependencies>

<repositories>
    <repository>
        <id>oss</id>
        <url>https://oss.sonatype.org/content/groups/public/</url>
    </repository>
</repositories>
```

# 示例

```java
package quant.fans;

import quant.fans.model.StockData;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        Quants quants = new Quants();
        //获取股票列表
        List<String> list = quants.data.stockList();

        for (String stock : list) {
            //StockData代表一个时间片的数据，例如日线级别，每个StockData为一天收盘后的股票数据
            List<StockData> stockDatas = quants.data.dailyData(stock);
            //剔除交易数据小于60天的股票
            if (stockDatas == null || stockDatas.size() < 60) {
                continue;
            }
            //indicator包含常用指标的计算
            quants.indicator.macd(stockDatas);
            //获取5、10、20、30、40、60均线，也可以通过sma(stockDatas,ma)获取指定时间间隔的均线
            quants.indicator.sma(stockDatas);
            quants.indicator.boll(stockDatas);
            quants.indicator.kdj(stockDatas);
            //strategy包含简单的策略计算，例如macd金叉
            quants.strategy.macdCross(stockDatas);
            quants.strategy.kdjCross(stockDatas);
            quants.strategy.goldenSpider(stockDatas);
            quants.strategy.bollThroat(stockDatas);
            for (StockData stockData : stockDatas) {
                System.out.println(stockData);
            }
        }
    }
}
```

# API

## 初始化

> Quants quants = new Quants();

`StockData`表示股票的一个时间片的数据，继承自LinkedHashMap<String,Double>，存储属性名称和double类型数值。

```java
StockData stockData = Provider.realtimeData("000001");
System.out.println("股票名称:" + stockData.name);
System.out.println("股票代码:" + stockData.symbol);
for(Map.Entry<String,Double> data : stockData.entrySet()){
    System.out.println(data.getKey() + "=" + data.getValue());
}
```

不同Provider返回的StockData的数据项不同，具体信息查询[字段说明](https://github.com/eryk/JQuant/wiki/StockData%E5%AD%97%E6%AE%B5%E8%AF%B4%E6%98%8E)

## 股票数据 quants.data

net.jquant.provider包提供了股票相关的数据获取类

### Provider列表如下：

* DailyDataProvider：日线级别数据
* MinuteDataProvider：分钟级别股票数据，可获得5、15、30、60分钟级别股票数据
* RealTimeDataProvider：实时股票数据
* StockIndexDataProvider：指数实时行情数据
* TickDataProvider：股票逐笔数据
* TopListDataProvider：龙虎榜数据
* ReportDataProvider：研报数据
* ReferenceDataProvider：分红数据
* MoneyFlowDataProvider：股票资金流数据
* FinanceDataProvider：个股财务报表数据

> net.jquant.provider.Provider类里的static方法汇总了全部provider方法，一般情况，使用Provider类就可以满足数据查询需求。

## 股票列表 quants.stocks

`StockList` 用于获取股票列表，并提供一些过滤和处理接口对股票进行筛选。

## 指标计算 quants.indicator

`net.jquant。Indicators` 是对[Ta-lib](http://ta-lib.org/function.html)库的封装，提供常用指标计算，返回StockData list对象。同时也提供了一些Ta-lib没有的指标计算。

支持的指标包括：

* sma：简单移动平均线
* ema：指数移动平均线
* dma：平均线差
* macd：指数平滑异同平均线
* boll：布林线
* kdj：随机指标
* rsi：强弱指标
* sar：抛物线指标或停损转向操作点指标
* adx：平均趋向指数
* adxr：趋向指标
* cci：顺势指标
* mfi：资金流量指标
* obv：能量潮又称为平衡交易量
* roc：变动率指标
* rocP：Rate of change Percentage: (price-prevPrice)/prevPrice
* trix：三重指数平滑平均线
* willR：威廉指标
* ad：收集派发摆荡指标
* aroon：阿隆指标
* aroonOsc：Aroon Oscillator
* bop：均势指标
* kama：适应性移动平均线
* trima：三角移动平均线

## 策略计算 quants.strategy

* `StragegyUtils` 常用策略
* `TDXFunction` 通达信常用指标
