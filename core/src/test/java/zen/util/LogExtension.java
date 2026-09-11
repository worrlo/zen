package zen.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class LogExtension implements BeforeEachCallback, AfterEachCallback {

    private ExecutorService executor;
    private final List<Log.Entry> logs = Collections.synchronizedList(new ArrayList<Log.Entry>());
    
    @Override
    public void beforeEach(ExtensionContext ctx) {
        logs.clear();
        Log.resetForTesting();

        executor = Executors.newSingleThreadExecutor();
        // Pass the method reference directly to the Log.Service constructor
        Log.Service service = new Log.Service(executor, logs::add);
        executor.submit(service);
    }

    @Override 
    public void afterEach(ExtensionContext ctx) throws Exception {
        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.SECONDS);
        Log.resetForTesting();
    }

    /**
     * Flushes queued log events and returns a thread-safe snapshot of captured entries.
     */
    public List<Log.Entry> getCapturedLogs() {
        try {
            boolean flushed = Log.flush(2, TimeUnit.SECONDS);
            if(!flushed) {
                throw new IllegalStateException("Log flush timed out before assertions.");
            }
            return Collections.unmodifiableList(new ArrayList<Log.Entry>(logs));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
