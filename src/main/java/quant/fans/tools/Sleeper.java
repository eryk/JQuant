package quant.fans.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-10-29.
 */
public class Sleeper {
    private static Logger LOG = LoggerFactory.getLogger(Sleeper.class);

    public static void sleep(int ms){
        try {
            TimeUnit.MILLISECONDS.sleep(ms);
        } catch (InterruptedException e) {
            LOG.error("fail to sleep");
        }
    }
}
