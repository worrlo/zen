package zen.util;

import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

public class Log {
    private static final BlockingQueue<Entry> QUEUE = new LinkedTransferQueue<Entry>();

    /**
     * Formatter
     */
    @FunctionalInterface
    public interface Formatter {
        void writeEntry(Entry entry);
    }

    /**
     * Object to describe a Log messge
     * Entry
     */
    public static class Entry 
    {
        private final long epoch;
        private final int line;
        private final String level, thread, logger, method, message;
        private final Throwable throwable;

        public Entry(long epoch, String level, String thread, 
            String logger, String method, int line, Throwable throwable, String message)
        {
            this.epoch = epoch;
            this.line = line;
            this.level = level;
            this.thread = thread;
            this.logger = logger;
            this.method = method;
            this.message = message;
            this.throwable = throwable;
        }

        public long getEpoch() { return epoch; }
        public int getLine() { return line; }
        public String getLevel() { return level; }
        public String getThread() { return thread; }
        public String getLogger() { return logger; }
        public String getMethod() { return method; }
        public String getMessage() { return message; }
        public Throwable getThrowable() { return throwable; }

        public boolean hasDiagnostics() { return this.throwable != null; }

        @Override public String toString() {
            return String.format(
                "%s{logger:%s, level:%s, message:%s, thread:%s, method:%s, line:%d}",
                this.getClass().getSimpleName(),
                logger, level, message, thread, method, line
            );
        }
    }

    private static final class FlushMarker extends Entry 
    {
        final CountDownLatch latch = new CountDownLatch(1);
        FlushMarker() {
            super(0L, "FLUSH", "", "", "", 0, null, "");
        }
    }

    /*
     * Entry Creation & Logging Helpers
     */
    public static Entry buildEntry(String level, int depth, Throwable throwable, String message)
    {
        String logger = "";
        String method = "";
        int line = 0;

        Thread ct = Thread.currentThread();
        StackTraceElement[] stack = ct.getStackTrace();
        if (stack != null && stack.length > depth) {
            StackTraceElement element = stack[depth];
            logger = element.getClassName();
            method = element.getMethodName();
            line = element.getLineNumber();
        }
        return new Entry(
            Instant.now().toEpochMilli(), 
            level, ct.getName(), logger, method, line, throwable, message
        );
    }

    public static void entry(String level, Throwable t, String message)
    {
        entry(level, 4, t, message);
    }
    public static void entry(String level, int depth, Throwable t, String message)
    {
        QUEUE.add(buildEntry(level, depth, t, message));
    }

    /**
     * Background Consumer Service
     * Service
     */
    public static class Service implements Callable<Void> 
    {
        private final ExecutorService exec;
        private final Formatter formatter;

        public Service(ExecutorService exec) {
            this(exec, new DefaultFormatter());
        }

        public Service(ExecutorService exec, Formatter formatter) {
            this.exec = exec;
            this.formatter = (formatter != null) ? formatter : new DefaultFormatter();
        }

        @Override
        public Void call() throws Exception {
            try {
                while(!exec.isShutdown() || !QUEUE.isEmpty()) {
                    Entry entry = QUEUE.poll(100, TimeUnit.MILLISECONDS);
                    if (entry == null) {
                        continue;
                    }
                    if (entry instanceof FlushMarker) {
                        ((FlushMarker) entry).latch.countDown();
                    }
                    else {
                        formatter.writeEntry(entry);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                formatter.writeEntry(
                    buildEntry("WARN", 2, e, "service interrupt detected")
                );
            } finally {
                formatter.writeEntry(
                    buildEntry("INFO", 2, null, String.format("service shutdown [QUEUE.size = %d]", QUEUE.size()))
                );
            }
            return null;
        }
    }

    /*
     * Package-Private Test Hooks
     */
    static boolean flush(long timeout, TimeUnit unit) throws InterruptedException {
        FlushMarker marker = new FlushMarker();
        QUEUE.put(marker);
        return marker.latch.await(timeout, unit);
    }

    static void resetForTesting() {
        QUEUE.clear();
    }

    static int drainToForTesting(Collection<Entry> target) {
        return QUEUE.drainTo(target);
    }

    private static class DefaultFormatter implements Formatter {
        @Override 
        public void writeEntry(Entry entry) {
            String format = "[%tF %<tT] [%s] [%s %s.%s:%d] %s";
            System.out.println(String.format(
                format,
                entry.getEpoch(),
                entry.getLevel(),
                entry.getThread(),
                entry.getLogger(),
                entry.getMethod(),
                entry.getLine(),
                entry.getMessage()
            ));
            if (entry.hasDiagnostics()) {
                System.err.println(String.format(
                    format,
                    entry.getEpoch(),
                    entry.getLevel(),
                    entry.getThread(),
                    entry.getLogger(),
                    entry.getMethod(),
                    entry.getLine(),
                    entry.getMessage()
                ));
                entry.getThrowable().printStackTrace(System.err);
            }
        }
    }

    /**
     * Utility interface to enable easy access to Log functions
     * 
     * Utils
     */
    public static interface Utils
    {
        default void log(String level, String message, Object...objects)
        {
            String msg = null;
            if (message != null)
                msg = String.format(message, objects);
            Log.entry(level, 4, null, msg);
        }
        default void log(String level, Throwable t, String message, Object...objects)
        {
            Log.entry(level, 4, DiagnosticData.on(t, objects), message);
        }
        default void log(String level, DiagnosticData dd, String message)
        {
            Log.entry(level, 4, dd, message);
        }
    }

}
