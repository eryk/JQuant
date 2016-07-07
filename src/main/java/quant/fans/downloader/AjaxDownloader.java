package quant.fans.downloader;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-8-31.
 */
public class AjaxDownloader{

    public static String download(String url) {
        WebDriver driver = new FirefoxDriver();
        driver.get(url);
        String source = driver.getPageSource();
        driver.close();
        return source;
    }

}
