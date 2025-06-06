package org.acmsl.hotswap.agent;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class AgentServerSocketTest {
    private Process process;

    @AfterEach
    public void tearDown() {
        if (process != null) {
            process.destroy();
            try {
                process.waitFor();
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Test
    public void agentOpensServerSocket() throws Exception {
        String java = System.getProperty("java.home") + "/bin/java";
        String classpath = System.getProperty("java.class.path");
        ProcessBuilder pb = new ProcessBuilder(java, "-cp", classpath,
                "org.acmsl.hotswap.test.AgentRunner");
        pb.redirectErrorStream(true);
        process = pb.start();

        long deadline = System.currentTimeMillis() + 10000;
        boolean connected = false;
        while (!connected && System.currentTimeMillis() < deadline) {
            try (Socket s = new Socket()) {
                s.connect(new InetSocketAddress("localhost", 62345), 100);
                connected = true;
            } catch (Exception e) {
                Thread.sleep(100);
            }
        }
        assertTrue(connected, "server did not start");

        try (Socket s = new Socket("localhost", 62345);
             PrintWriter out = new PrintWriter(s.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
            out.println("refresh /tmp");
            String resp = in.readLine();
            assertEquals("OK", resp);
        }
    }
}
