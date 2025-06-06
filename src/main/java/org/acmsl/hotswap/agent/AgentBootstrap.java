package org.acmsl.hotswap.agent;

import java.lang.instrument.Instrumentation;

import java.nio.file.Path;

import org.acmsl.hotswap.config.WatcherConfiguration;
import org.acmsl.hotswap.reloader.Reloader;

/**
 * Entry point for the Java agent. This is intentionally minimal and will
 * grow functionality over time.
 */
public final class AgentBootstrap {
    private AgentBootstrap() {}

    /**
     * Called when the agent is attached at JVM startup.
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        String configPath = System.getProperty("hsconfig");
        if (configPath == null || configPath.isBlank()) {
            throw new IllegalStateException("Missing required system property hsconfig");
        }

        WatcherConfiguration config;
        try {
            config = WatcherConfiguration.load(Path.of(configPath));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load configuration", e);
        }

        int port = config.getPort() > 0 ? config.getPort() : 62345;

        System.out.println("[Java-Hotswap] Agent initialized on port " + port);
        Reloader reloader = new Reloader(inst, port);
        reloader.start();
    }

    /**
     * Called when the agent is attached to a running JVM.
     */
    public static void agentmain(String agentArgs, Instrumentation inst) {
        premain(agentArgs, inst);
    }
}
