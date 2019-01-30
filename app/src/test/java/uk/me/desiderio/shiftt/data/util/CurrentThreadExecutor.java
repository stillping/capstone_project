package uk.me.desiderio.shiftt.data.util;

import java.util.concurrent.Executor;

/**
 * Executor to be used in test where execution should be inmidate
 */
public class CurrentThreadExecutor implements Executor {
    @Override
    public void execute(Runnable command) {
        command.run();
    }
}
