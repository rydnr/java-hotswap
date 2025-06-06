package org.acmsl.hotswap.config;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

public class FolderWatchTest {
    @Test
    public void detectsChangesOnSeparateThread() throws Exception {
        Path dir = Files.createTempDirectory("fw");
        long interval = 1000 + (long)(Math.random() * 500);
        FolderWatch watch = new FolderWatch();
        watch.setPath(dir.toString());
        watch.setInterval(interval);

        AtomicBoolean detected = new AtomicBoolean(false);
        AtomicReference<String> callbackThread = new AtomicReference<>();

        watch.watch(() -> {
            detected.set(true);
            callbackThread.set(Thread.currentThread().getName());
        });

        // create file after watcher is running
        Files.writeString(dir.resolve("test.txt"), "data");

        Thread.sleep(interval + 1000);
        watch.stop();

        assertTrue(detected.get(), "change should have been detected");
        assertNotEquals(Thread.currentThread().getName(), callbackThread.get(), "callback should run on separate thread");
    }
}
