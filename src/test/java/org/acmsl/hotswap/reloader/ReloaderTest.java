package org.acmsl.hotswap.reloader;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import net.bytebuddy.agent.ByteBuddyAgent;
import org.junit.jupiter.api.Test;

public class ReloaderTest {
    @Test
    public void reloadsClassBytecode() throws Exception {
        Path oldDir = Paths.get("/tmp/old");
        Path newDir = Paths.get("/tmp/new");
        Files.createDirectories(oldDir);
        Files.createDirectories(newDir);

        compile(oldDir, "Hello world!");
        compile(newDir, "Hi! I'm different!");

        try (URLClassLoader loader = new URLClassLoader(new URL[] { oldDir.toUri().toURL() })) {
            Class<?> cls = loader.loadClass("TestMe");
            Object instance = cls.getDeclaredConstructor().newInstance();
            Method method = cls.getMethod("testMe");
            String result = (String) method.invoke(instance);
            assertEquals("Hello world!", result);

            Instrumentation inst = ByteBuddyAgent.install();
            Reloader reloader = new Reloader(inst);
            byte[] newBytes = Files.readAllBytes(newDir.resolve("TestMe.class"));
            reloader.reloadClass(cls, newBytes);

            Object instance2 = cls.getDeclaredConstructor().newInstance();
            String newResult = (String) method.invoke(instance2);
            assertEquals("Hi! I'm different!", newResult);
        }
    }

    private void compile(Path dir, String message) throws IOException {
        String source = "public class TestMe { public String testMe() { return \"" + message + "\"; } }";
        Path javaFile = dir.resolve("TestMe.java");
        Files.writeString(javaFile, source);
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int result = compiler.run(null, null, null, javaFile.toString());
        if (result != 0) {
            throw new IOException("Compilation failed for " + javaFile);
        }
    }
}
