package org.acmsl.hotswap.reloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

/**
 * Core class responsible for applying reloads using the Instrumentation API.
 * This is highly simplified and will later incorporate byte-code enhancements
 * and resource updates.
 */
public class Reloader {
    private static final int DEFAULT_PORT = 62345;

    private final Instrumentation instrumentation;
    private final int port;

    public Reloader(Instrumentation instrumentation) {
        this(instrumentation, DEFAULT_PORT);
    }

    public Reloader(Instrumentation instrumentation, int port) {
        this.instrumentation = Objects.requireNonNull(instrumentation);
        this.port = port <= 0 ? DEFAULT_PORT : port;
    }

    /** Starts the reloader and opens a server socket for commands. */
    public void start() {
        Thread thread = new Thread(this::runServer, "hotswap-server");
        thread.setDaemon(true);
        thread.start();
    }

    private void runServer() {
        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                try (Socket socket = server.accept();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
                    String line = reader.readLine();
                    if (line != null && line.startsWith("refresh ")) {
                        writer.println("OK");
                    } else {
                        writer.println("ERROR");
                    }
                } catch (IOException ignored) {
                    // continue accepting new connections
                }
            }
        } catch (IOException e) {
            // Fail fast if the server cannot start
            throw new RuntimeException("Failed to start server", e);
        }
    }

    /**
     * Reload a single class with new bytecode.
     *
     * @param type     the class to redefine
     * @param bytecode the new class file bytes
     */
    public void reloadClass(Class<?> type, byte[] bytecode) throws Exception {
        ClassDefinition def = new ClassDefinition(type, bytecode);
        instrumentation.redefineClasses(def);
    }
}
