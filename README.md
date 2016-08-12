# JQuant

![微信订阅号](https://github.com/eryk/JQuant/blob/test/qrcode_for_gh_188ebb8e2c0a_258.jpg "微信订阅号")

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

## StockData

表示股票的一个时间片的数据，继承自LinkedHashMap<String,Double>，存储属性名称和double类型数值。

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

## 指标计算 quants.indicator

## 策略计算 quants.strategy
