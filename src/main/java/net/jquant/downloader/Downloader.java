package net.jquant.downloader;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-8-15.
 */
public class Downloader {

    public static String download(String url){
        return BasicDownloader.download(url);
    }

    public static String download(String url,String encoding){
        return BasicDownloader.download(url,encoding);
    }

    public static String downloadAjaxData(String url){
        return AjaxDownloader.download(url);
    }
}
