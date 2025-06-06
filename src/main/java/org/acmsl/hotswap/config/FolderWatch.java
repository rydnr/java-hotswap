package org.acmsl.hotswap.config;

/** Simple POJO representing a folder to watch and the polling interval in milliseconds. */
public class FolderWatch {
    private String path;
    private long interval;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }
}
