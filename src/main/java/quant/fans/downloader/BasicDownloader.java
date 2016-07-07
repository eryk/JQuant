package quant.fans.downloader;

import com.google.common.base.Joiner;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-8-15.
 */
public class BasicDownloader {
    private static final Logger LOG = LoggerFactory.getLogger(BasicDownloader.class);

    private static int RETRY_COUNT = 1;

    public static String download(String url) {
        int retry = 0;
        while(retry<=RETRY_COUNT){
            try {
                HttpResponse<String> response = Unirest.get(url).asString();
                if(response.getStatus()==200){
                    return response.getBody();
                }else{
                    LOG.warn("status="+response.getStatus()+",url="+url);
                }
                retry++;
            } catch (UnirestException e) {
                retry++;
                LOG.error("retry="+retry+",fail to download from " + url);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }

        return "";
    }

    public static InputStream downloadStream(String url) {
        int retry = 0;
        while(retry<=RETRY_COUNT){
            try {
                HttpResponse<String> response = Unirest.get(url).asString();
                if(response.getStatus()==200){
                    return response.getRawBody();
                }else{
                    LOG.warn("status="+response.getStatus()+",url="+url);
                }
                retry++;
            } catch (UnirestException e) {
                retry++;
                LOG.error("fail to download from " + url);
            }
        }
        return null;
    }

    public static String download(String url,String encoding){
        InputStream inputStream = downloadStream(url);
        if(inputStream!=null){
            try {
                List<String> strings = IOUtils.readLines(inputStream, encoding);
                return Joiner.on("\n").join(strings);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static void main(String[] args) {
        BasicDownloader downloader = new BasicDownloader();
        String download = downloader.download("http://nuff.eastmoney.com/EM_Finance2015TradeInterface/JS.ashx?id=3002462&token=beb0a0047196124721f56b0f0ff5a27c&cb=callback031451186537742615&callback=callback031451186537742615&_=1439558792487");
        System.out.println(download);
    }
}
