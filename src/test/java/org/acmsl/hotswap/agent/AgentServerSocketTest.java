package org.acmsl.hotswap.agent;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class AgentServerSocketTest {
    private Process process;

    @AfterEach
    public void tearDown() throws Exception {
        if (process != null) {
            process.destroy();
            process.waitFor(5, TimeUnit.SECONDS);
        }
    }

    @Test
    public void failsWhenPropertyMissing() throws Exception {
        startProcess(null);
        boolean exited = process.waitFor(5, TimeUnit.SECONDS);
        assertTrue(exited, "process should exit when hsconfig missing");
        assertNotEquals(0, process.exitValue());
    }

    @Test
    public void failsWithInvalidYaml() throws Exception {
        Path file = Files.createTempFile("config", ".yml");
        Files.writeString(file, "invalid: [");
        startProcess(file);
        boolean exited = process.waitFor(5, TimeUnit.SECONDS);
        assertTrue(exited, "process should exit on invalid yaml");
        assertNotEquals(0, process.exitValue());
    }

    @Test
    public void agentUsesConfiguredPort() throws Exception {
        int port = 62346;
        Path file = Files.createTempFile("config", ".yml");
        Files.writeString(file, "port: " + port + "\nfolders: []\n");
        startProcess(file);
        assertTrue(waitForPort(port), "server did not start on custom port");
        try (Socket s = new Socket("localhost", port);
             PrintWriter out = new PrintWriter(s.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
            out.println("refresh /tmp");
            String resp = in.readLine();
            assertEquals("OK", resp);
        }
    }

    @Test
    public void agentDefaultsPortWhenMissing() throws Exception {
        Path file = Files.createTempFile("config", ".yml");
        Files.writeString(file, "folders: []\n");
        startProcess(file);
        int port = 62345;
        assertTrue(waitForPort(port), "server did not start on default port");
    }

    private void startProcess(Path config) throws Exception {
        String java = System.getProperty("java.home") + "/bin/java";
        String classpath = System.getProperty("java.class.path");
        ProcessBuilder pb;
        if (config != null) {
            pb = new ProcessBuilder(java, "-cp", classpath,
                    "-Dhsconfig=" + config.toString(),
                    "org.acmsl.hotswap.test.AgentRunner");
        } else {
            pb = new ProcessBuilder(java, "-cp", classpath,
                    "org.acmsl.hotswap.test.AgentRunner");
        }
        pb.redirectErrorStream(true);
        process = pb.start();
    }

    private boolean waitForPort(int port) throws Exception {
        long deadline = System.currentTimeMillis() + 10000;
        while (System.currentTimeMillis() < deadline) {
            try (Socket s = new Socket()) {
                s.connect(new InetSocketAddress("localhost", port), 100);
                return true;
            } catch (Exception e) {
                Thread.sleep(100);
            }
        }
        return false;
    }
}
