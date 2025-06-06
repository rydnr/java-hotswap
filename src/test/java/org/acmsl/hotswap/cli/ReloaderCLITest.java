package org.acmsl.hotswap.cli;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class ReloaderCLITest {
    @AfterEach
    public void clearProperty() {
        System.clearProperty("jhsconfig");
    }

    @Test
    public void exitsWhenPropertyMissing() throws Exception {
        System.clearProperty("jhsconfig");
        assertFalse(ReloaderCLI.run());
    }

    @Test
    public void exitsWhenFileNotYaml() throws Exception {
        Path file = Files.createTempFile("config", ".txt");
        System.setProperty("jhsconfig", file.toString());
        assertFalse(ReloaderCLI.run());
    }

    @Test
    public void exitsWhenNoFoldersConfigured() throws Exception {
        String yaml = "folders: []";
        Path file = Files.createTempFile("config", ".yml");
        Files.writeString(file, yaml);
        System.setProperty("jhsconfig", file.toString());
        assertFalse(ReloaderCLI.run());
    }
}
