package net.jquant.trade;

import com.google.common.collect.Maps;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

public class Portfolio {
    public static final String DEFAULT_NAME = "JQuant";
    private String name;

    private Record status = new Record(LocalDateTime.MIN);
    //需要在初始化时赋值
    private RiskAnalysis riskAnalysis = new RiskAnalysis();
    //记录各个时间点账户状态
    private TreeMap<LocalDateTime,Record> records = new TreeMap<LocalDateTime,Record>();
    //最后更新record的时间点
    private LocalDateTime ts;
    //记录账户当前持仓情况
    private Map<String,Position> positions = Maps.newConcurrentMap();

    public Portfolio(String name, LocalDateTime startDate){
        this.name = name;
        this.ts = startDate;
        records.put(ts, new Record(ts));
    }

    public Portfolio(){
        this(DEFAULT_NAME, LocalDateTime.MIN);
    }

    public Portfolio(String name){
        this(name, LocalDateTime.MIN);
    }

    public Record getStatus() {
        return status;
    }

    public void setStatus(Record status) {
        this.status = status;
    }

    public Map<String, Position> getPositions() {
        return positions;
    }

    public void setPositions(Map<String,Position> positions){
        this.positions = positions;
    }

}
