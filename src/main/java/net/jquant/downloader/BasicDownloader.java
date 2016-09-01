package net.jquant.downloader;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-8-15.
 */
public class BasicDownloader {
    private static final Logger LOG = LoggerFactory.getLogger(BasicDownloader.class);

    private static int RETRY_COUNT = 1;

    public static String download(String url,Map header){
        int retry = 0;
        while(retry<=RETRY_COUNT){
            try {
                HttpResponse<String> response = null;
                if(header.size() > 0){
                    response = Unirest.get(url).headers(header).asString();
                }else{
                    response = Unirest.get(url).asString();
                }
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
                    LOG.error("fail to download from" + url);
                }
            }
        }
        return "";
    }

    public static String download(String url) {
        return download(url, Maps.newHashMap());
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

}
