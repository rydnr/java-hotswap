package org.acmsl.hotswap.cli;

import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import net.bytebuddy.agent.ByteBuddyAgent;
import org.acmsl.hotswap.reloader.Reloader;

/**
 * Simple command-line interface for manually triggering reloads.
 */
public final class ReloadCLI {
    private ReloadCLI() {}

    /**
     * Starts the CLI loop and listens for commands.
     *
     * Supported commands:
     * <ul>
     *   <li>{@code reload &lt;class-name&gt; &lt;class-file&gt;}</li>
     *   <li>{@code quit}</li>
     * </ul>
     */
    public static void main(String[] args) throws Exception {
        System.out.println("Java-Hotswap CLI");

        // Obtain an Instrumentation instance by attaching a Byte Buddy agent
        Instrumentation inst = ByteBuddyAgent.install();
        Reloader reloader = new Reloader(inst);
        reloader.start();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split("\\s+");
            String command = parts[0].toLowerCase();
            switch (command) {
                case "reload" -> {
                    if (parts.length < 3) {
                        System.out.println("Usage: reload <class-name> <class-file>");
                        continue;
                    }

                    String className = parts[1];
                    String file = parts[2];
                    try {
                        byte[] bytes = Files.readAllBytes(Paths.get(file));
                        Class<?> type = Class.forName(className);
                        reloader.reloadClass(type, bytes);
                        System.out.println("Reloaded " + className);
                    } catch (Exception e) {
                        System.err.println("Failed to reload " + className + ": " + e.getMessage());
                    }
                }
                case "quit" -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Unknown command: " + command);
            }
        }
    }
}
