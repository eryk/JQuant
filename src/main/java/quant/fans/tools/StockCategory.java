package quant.fans.tools;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quant.fans.common.Pair;
import quant.fans.common.Utils;
import quant.fans.downloader.Downloader;
import quant.fans.model.StockBlock;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-8-16.
 */
public class StockCategory {
    private static final Logger LOG = LoggerFactory.getLogger(StockCategory.class);

    private static String tagsURL = "http://quote.eastmoney.com/center/BKList.html#notion_0_0?sortRule=0";

    private static String blockBaseURL = "http://quote.eastmoney.com/center/";

    private static String blockStockListURL = "http://hqdigi2.eastmoney.com/EM_Quote2010NumericApplication/index.aspx?type=s&sortType=C&sortRule=-1&pageSize=500&page=1&style=%s&token=44c9d251add88e27b65ed86506f6e5da";

    private static int RETRY_TIMES = 3;
    private static int SLEEP_INTERVAL_MS = 3000;

    private static ExecutorService threadPool  = Executors.newFixedThreadPool(30);

    private static Map<String,List<StockBlock>> category = Maps.newConcurrentMap();

    @PostConstruct
    public void init(){
        category = getCategory();
    }

    //获取概念版块、行业版块、地域版块分类
    //key:版块分类：概念，行业，地域
    //value：List<StockBlock>具体版块信息
    public static Map<String,List<StockBlock>> getCategory() {
        if(category.size()!=0){
            return category;
        }
        final Map<String,List<StockBlock>> blockMap = Maps.newConcurrentMap();

        int retryTimes = 0;
        while (retryTimes < RETRY_TIMES) {

            try {
                List<Pair<String,Element>> taskList = Lists.newLinkedList();

                Document document = Jsoup.connect(tagsURL).get();
                Elements elements = document.select("li[class=node-sub-sub]");
                for (Element element : elements) {
                    String html = new String(element.html().getBytes(Charset.forName("utf8")));
                    Elements items = element.select("ul li");

                    final String type;
                    if (html.contains("概念板块")) {
                        type = "概念";
                    } else if (html.contains("行业板块")) {
                        type = "行业";
                    } else if (html.contains("地域板块")) {
                        type = "地域";
                    } else {
                        type = "";
                    }
                    for (final Element item : items) {
                        taskList.add(new Pair(type,item));
                    }
                }

                blockMap.put("概念",new LinkedList<>());
                blockMap.put("行业",new LinkedList<>());
                blockMap.put("地域",new LinkedList<>());

                final CountDownLatch countDownLatch = new CountDownLatch(taskList.size());
                for(final Pair<String,Element> block:taskList){
                    threadPool.execute(() -> {
                        StockBlock stockBlock = new StockBlock();
                        stockBlock.name = new String(block.getVal().select("span[class=text]").text().getBytes(Charset.forName("utf8")));
                        stockBlock.url = blockBaseURL+block.getVal().select("a").attr("href");
                        String _id = CharMatcher.DIGIT.retainFrom(stockBlock.url);
                        stockBlock.id = _id.substring(0,_id.length()-2);
                        stockBlock.symbolList.addAll(getBlockStockList(stockBlock.id));
                        stockBlock.type = block.getKey();
                        blockMap.get(block.getKey()).add(stockBlock);
                        countDownLatch.countDown();
                    });
                }
                countDownLatch.await();
                Utils.closeThreadPool(threadPool);
                LOG.info("概念:" + blockMap.get("概念").size() + ",行业:" + blockMap.get("行业").size() + ",地域:" + blockMap.get("地域").size());
                return blockMap;
            } catch (IOException e) {
                LOG.error("fail to get stock list",e);
                retryTimes++;
                try {
                    Thread.sleep(SLEEP_INTERVAL_MS);
                } catch (InterruptedException e1) {
                    LOG.error("fail to sleep "+ SLEEP_INTERVAL_MS + "ms");
                }
            } catch (InterruptedException e) {
                LOG.error("fail to stop thread pool",e);
            }
        }
        category.putAll(blockMap);
        return blockMap;
    }

    /**
     * 获取股票某个大分类下的具体分类:概念，行业，地域
     * @param type type
     * @return stock category
     */
    public static Map<String,Set<String>> getStockCategory(String type){
        List<StockBlock> stockBlocks = category.get(type);
        Map<String,Set<String>> symbolMap = Maps.newTreeMap();
        if(stockBlocks!=null){
            for(StockBlock stockBlock :stockBlocks){
                for(String symbol:stockBlock.symbolList){
                    Set<String> category = symbolMap.get(symbol);
                    if(category == null){
                        category = Sets.newHashSet();
                    }
                    category.add(stockBlock.name);
                    symbolMap.put(symbol, category);
                }
            }
        }else{
            LOG.warn("get stock category failed");
        }
        return symbolMap;
    }

    /**
     * 获取股票的全部类别，包含概念，行业，地域
     * @return stock category
     */
    public static Map<String,List<String>> getStockCategory(){
        Map<String, List<StockBlock>> stockCategory = category;
        Map<String,List<String>> symbolMap = Maps.newTreeMap();
        for(List<StockBlock> stockBlocks:stockCategory.values()){
            for(StockBlock stockBlock :stockBlocks){
                for(String symbol:stockBlock.symbolList){
                    List<String> category = symbolMap.get(symbol);
                    if(category == null){
                        category = Lists.newLinkedList();
                    }
                    category.add(stockBlock.name);
                    symbolMap.put(symbol, category);
                }
            }
        }
        return symbolMap;
    }

    /**
     * 获取某个板块下的全部股票
     */
    private static List<String> getBlockStockList(String id) {
        String url = String.format(blockStockListURL,id);
        String data = Downloader.download(url);
        Pattern pattern = Pattern.compile("(\\[.*\\])");
        Matcher matcher = pattern.matcher(data);
        if(matcher.find()){
            List<String> blockStockList = Lists.newLinkedList();
            String group = matcher.group();
            List<String> lines = new Gson().fromJson(group,List.class);
            for(String line:lines){
                String[] fields = line.split(",");
                blockStockList.add(fields[1]);
            }
            return blockStockList;
        }
        return Lists.newLinkedList();
    }
}
