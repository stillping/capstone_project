package uk.me.desiderio.shiftt.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * global executor pool
 * as described at CodeLabs' "Build an App with Architecture Components"
 */

public class AppExecutors {


    private static final Object LOCK = new Object();
    private static AppExecutors instance;
    private ExecutorService diskIO;
    private Executor networkIO;
    private ExecutorService executorService;


    private AppExecutors(ExecutorService diskIO, Executor networkIO) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
    }

    // TODO investigate if the synchronized can be provided from Dagger
    public static AppExecutors getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                instance = new AppExecutors(Executors.newSingleThreadExecutor(),
                                            Executors.newFixedThreadPool(3));
            }
        }
        return instance;
    }

    public ExecutorService getDiskIO() {
        return diskIO;
    }

    public Executor getNetworkIO() {
        return networkIO;
    }
}
