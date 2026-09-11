package zen.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

public class Log {
    private static final BlockingQueue<Entry> QUEUE = new LinkedTransferQueue<Entry>();

    @FunctionalInterface
    public static interface Formatter {
        void writeEntry(Entry entry);
    }

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
                "%s{epoch: %d, level: %s, thread: %s, logger: %s, method: %s, message: %s}",
                this.getClass().getSimpleName(),
                epoch, level, thread, logger, method, message
            );
        }
    }

    static final class FlushMarker extends Entry 
    {
        
        FlushMarker() {
            super(0L, "FLUSH", "", "", "", 0, null, "");
        }
    }

    public static class Service 
    {

    }



}
