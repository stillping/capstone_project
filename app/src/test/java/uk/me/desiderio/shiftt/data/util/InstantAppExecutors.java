package uk.me.desiderio.shiftt.data.util;

import java.util.concurrent.Executor;

import uk.me.desiderio.shiftt.util.AppExecutors;

/**
 * Global executor pool to be used in test where execution should be inmidate
 */
public class InstantAppExecutors extends AppExecutors {

    protected InstantAppExecutors(Executor diskIO, Executor networkIO, Executor mainThread) {
        super(diskIO, networkIO, mainThread);
    }

    public static AppExecutors getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                instance = new InstantAppExecutors(new CurrentThreadExecutor(),
                                                   new CurrentThreadExecutor(),
                                                   new CurrentThreadExecutor());
            }
        }
        return instance;
    }
}
