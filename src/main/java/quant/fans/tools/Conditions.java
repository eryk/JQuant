package quant.fans.tools;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import quant.fans.model.StockData;

import java.util.Set;

public class Conditions {
    public enum Operation{
        GT,LT,EQ,NGT,NLT
    }

    //指标名称:操作类型(大于,小于,等于):比较值
    private Table<String,Operation,Double> conditions;

    public Conditions(){
        conditions = HashBasedTable.create();
    }

    public void addCondition(String name,Operation op,Double value){
        conditions.put(name,op,value);
    }

    public boolean check(StockData stockData){
        Set<Table.Cell<String, Operation, Double>> cells = conditions.cellSet();
        for(Table.Cell<String,Operation,Double> cell:cells){
            Double val = stockData.get(cell.getRowKey());
            if(val!=null){

                switch (cell.getColumnKey()){
                    case GT:
                        if (val<=cell.getValue().doubleValue()){
                            return false;
                        }
                        break;
                    case LT:
                        if(val >= cell.getValue().doubleValue()){
                            return false;
                        }
                        break;
                    case EQ:
                        if(val!=cell.getValue().doubleValue()){
                            return false;
                        }
                        break;
                    case NGT:
                        if(val>cell.getValue().doubleValue()){
                            return false;
                        }
                        break;
                    case NLT:
                        if(val<cell.getValue().doubleValue()){
                            return false;
                        }
                        break;
                }
            }else{
                return false;
            }

        }
        return true;
    }
}
