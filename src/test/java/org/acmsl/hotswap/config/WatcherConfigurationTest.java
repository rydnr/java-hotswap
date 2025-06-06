package org.acmsl.hotswap.config;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

public class WatcherConfigurationTest {
    @Test
    public void parsesFoldersAndIntervals() throws Exception {
        String yaml = """
        folders:
          - path: /tmp/foo
            interval: 1000
          - path: /tmp/bar
            interval: 2000
        """;
        Path file = Files.createTempFile("watch", ".yml");
        Files.writeString(file, yaml);

        WatcherConfiguration config = WatcherConfiguration.load(file);
        assertNotNull(config);
        List<FolderWatch> folders = config.getFolders();
        assertEquals(2, folders.size());
        assertEquals("/tmp/foo", folders.get(0).getPath());
        assertEquals(1000, folders.get(0).getInterval());
        assertEquals("/tmp/bar", folders.get(1).getPath());
        assertEquals(2000, folders.get(1).getInterval());
    }
}
