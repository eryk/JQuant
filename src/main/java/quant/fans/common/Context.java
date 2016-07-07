//package quant.fans.common;
//
//import com.google.common.base.Strings;
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.hbase.HConstants;
//import org.apache.hadoop.hbase.client.HTableInterface;
//import org.apache.hadoop.hbase.client.HTablePool;
//import org.apache.hadoop.hbase.util.Bytes;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by eryk on 15-7-26.
// */
//public class Context {
//    private static final Logger LOG = LoggerFactory.getLogger(Context.class);
//
//    private Map conf;
//    private Configuration hbaseConf;
//    private HTablePool pool;
//
//    public Context() {
//        this("conf.yaml");
//    }
//
//    public Context(String conf){
//        try {
//            this.conf = Utils.readYamlConf(conf, true);
//        } catch (IOException e) {
//            LOG.error("fail to load config yaml",e);
//        }
//        pool = new HTablePool(getHBaseConf(),getInt(Constants.DATABASE_POOL_SIZE,1));
//    }
//
//    public void close(){
//        try {
//            pool.close();
//        } catch (IOException e) {
//            LOG.error("can't close table pool",e);
//        }
//    }
//
//    public Map<String,Object> getMap(String key){
//        return (Map<String, Object>) conf.get(key);
//    }
//
//    public HTableInterface getTable(String tableName){
//        return pool.getTable(tableName);
//    }
//
//    public void closeTable(HTableInterface table){
//        try {
//            table.close();
//        } catch (IOException e) {
//            LOG.error("can't close table " + Bytes.toString(table.getTableName()),e);
//        }
//    }
//
//
//
//    public void put(String key, Object value) {
//        conf.put(key,value);
//    }
//
//    public String getStr(String key){
//        return Utils.getStrOrEmpty(conf,key);
//    }
//
//    public String getStr(String key,String defaultVal){
//        String val = getStr(key);
//        if(Strings.isNullOrEmpty(val)){
//            return defaultVal;
//        }
//        return val;
//    }
//
//    public Integer getInt(String key,Integer defaultVal){
//        if(Strings.isNullOrEmpty(getStr(key))){
//            return defaultVal;
//        }else{
//            return Integer.parseInt(getStr(key));
//        }
//    }
//
//    public List<String> getList(String key){
//        List<String> list = (List<String>) conf.get(key);
//        return list;
//    }
//
//    public Configuration getHBaseConf(){
//        if(hbaseConf == null){
//            hbaseConf = new Configuration();
//            hbaseConf.set(HConstants.ZOOKEEPER_QUORUM,getStr(HConstants.ZOOKEEPER_QUORUM));
//            hbaseConf.set(HConstants.ZOOKEEPER_ZNODE_PARENT,getStr(HConstants.ZOOKEEPER_ZNODE_PARENT));
//        }
//        return hbaseConf;
//    }
//}
