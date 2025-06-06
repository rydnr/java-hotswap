package org.acmsl.hotswap.agent;

import java.lang.instrument.Instrumentation;

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
        System.out.println("[Java-Hotswap] Agent initialized");
        Reloader reloader = new Reloader(inst);
        reloader.start();
    }

    /**
     * Called when the agent is attached to a running JVM.
     */
    public static void agentmain(String agentArgs, Instrumentation inst) {
        premain(agentArgs, inst);
    }
}
