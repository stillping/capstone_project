package uk.me.desiderio.shiftt.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;

/**
 * Global executor pool as described at
 * CodeLabs' "Build an App with Architecture Components"
 */

public class AppExecutors {


    protected static final Object LOCK = new Object();
    protected static AppExecutors instance;
    private Executor diskIO;
    private Executor networkIO;
    private Executor mainThread;


    protected AppExecutors(Executor diskIO, Executor networkIO, Executor mainThread) {
        this.mainThread = mainThread;
        this.diskIO = diskIO;
        this.networkIO = networkIO;
    }

    public static AppExecutors getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                instance = new AppExecutors(Executors.newSingleThreadExecutor(),
                                            Executors.newFixedThreadPool(3),
                                            new MainThreadExecutor());
            }
        }
        return instance;
    }

    public Executor getDiskIO() {
        return diskIO;
    }

    public Executor getNetworkIO() {
        return networkIO;
    }

    public Executor getMainThread() {
        return mainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
