package org.acmsl.hotswap.config;

/**
 * Represents a folder to watch and the polling interval in milliseconds.
 * <p>
 * Besides being a simple configuration holder, this class can actively watch a
 * directory for changes. When {@link #watch(Runnable)} is invoked a dedicated
 * daemon thread is started which checks the folder contents every
 * {@code interval} milliseconds.  Whenever a change is detected the provided
 * callback is executed.
 */
public class FolderWatch implements Runnable {
    private String path;
    private long interval;

    private volatile boolean running;
    private Thread thread;
    private Runnable callback;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    /** Starts watching the configured folder on a separate daemon thread. */
    public void watch(Runnable callback) {
        this.callback = callback;
        running = true;
        thread = new Thread(this, "FolderWatch-" + path);
        thread.setDaemon(true);
        thread.start();
    }

    /** Stops watching the folder. */
    public void stop() {
        running = false;
        if (thread != null) {
            thread.interrupt();
        }
    }

    @Override
    public void run() {
        try {
            java.nio.file.Path dir = java.nio.file.Path.of(path);
            java.util.Set<java.nio.file.Path> snapshot = java.nio.file.Files.list(dir).collect(java.util.stream.Collectors.toSet());
            while (running) {
                Thread.sleep(interval);
                java.util.Set<java.nio.file.Path> current = java.nio.file.Files.list(dir).collect(java.util.stream.Collectors.toSet());
                if (!current.equals(snapshot)) {
                    snapshot = current;
                    if (callback != null) {
                        callback.run();
                    }
                }
            }
        } catch (Exception e) {
            // swallow exceptions for now - this is a simple utility used in tests
        }
    }
}
