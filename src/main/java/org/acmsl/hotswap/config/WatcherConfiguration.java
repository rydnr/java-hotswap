package org.acmsl.hotswap.config;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.yaml.snakeyaml.Yaml;

/** Parses YAML configuration describing folders to watch. */
public class WatcherConfiguration {
    private int port;
    private List<FolderWatch> folders;

    public List<FolderWatch> getFolders() {
        return folders;
    }

    public void setFolders(List<FolderWatch> folders) {
        this.folders = folders;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    /** Load configuration from a YAML file. */
    public static WatcherConfiguration load(Path path) throws Exception {
        try (InputStream is = Files.newInputStream(path)) {
            Yaml yaml = new Yaml();
            return yaml.loadAs(is, WatcherConfiguration.class);
        }
    }
}
