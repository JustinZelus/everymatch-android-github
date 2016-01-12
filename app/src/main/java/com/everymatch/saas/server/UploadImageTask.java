package com.everymatch.saas.server;

import java.io.Serializable;

/**
 * Created by dors on 3/16/15.
 */
public class UploadImageTask implements Serializable {

    private String filePath;
    private String url;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
