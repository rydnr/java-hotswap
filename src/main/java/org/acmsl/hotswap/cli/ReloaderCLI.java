package org.acmsl.hotswap.cli;

import java.nio.file.Files;
import java.nio.file.Path;

import org.acmsl.hotswap.config.WatcherConfiguration;

/**
 * CLI that loads a watch configuration and starts reloading service.
 */
public final class ReloaderCLI {
    private ReloaderCLI() {}

    /**
     * Entry point for the reloader CLI.
     *
     * @param args ignored
     */
    public static void main(String[] args) throws Exception {
        run();
    }

    /**
     * Executes the CLI logic. Returns {@code true} if startup succeeded.
     */
    static boolean run() throws Exception {
        String configPath = System.getProperty("jhsconfig");
        if (configPath == null || configPath.isBlank()) {
            System.err.println("Missing required system property jhsconfig");
            return false;
        }

        if (!(configPath.endsWith(".yml") || configPath.endsWith(".yaml"))) {
            System.err.println("Configuration file must be .yml or .yaml");
            return false;
        }

        Path path = Path.of(configPath);
        if (!Files.exists(path)) {
            System.err.println("Configuration file " + configPath + " does not exist");
            return false;
        }

        WatcherConfiguration config;
        try {
            config = WatcherConfiguration.load(path);
        } catch (Exception e) {
            System.err.println("Failed to load configuration: " + e.getMessage());
            return false;
        }

        if (config == null || config.getFolders() == null || config.getFolders().isEmpty()) {
            System.err.println("Configuration must define at least one folder to watch");
            return false;
        }

        System.out.println(
                "Loaded configuration for " + config.getFolders().size() + " folder(s)");
        // In a full implementation watchers would be started here
        return true;
    }
}
