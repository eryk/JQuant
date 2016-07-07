package quant.fans.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quant.fans.common.Utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * author: eryk
 * mail: xuqi86@gmail.com
 * date: 15-11-21.
 */
public class ThreadPool {
    public static final Logger LOG = LoggerFactory.getLogger(ThreadPool.class);

    public static ExecutorService executorService;

    public static synchronized void init(int poolSize){
        if(executorService==null){
            executorService = Executors.newFixedThreadPool(poolSize);
            LOG.info(ThreadPool.class.getSimpleName() + " is init with poolsize="+poolSize);
        }
    }

    public static void close(){
        Utils.closeThreadPool(executorService);
    }

    public static void execute(Runnable runnable){
        executorService.execute(runnable);
    }

    public static Future submit(Callable callable){
        return executorService.submit(callable);
    }
}
