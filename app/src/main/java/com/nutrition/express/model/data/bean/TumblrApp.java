package com.nutrition.express.model.data.bean;

/**
 * Created by huang on 11/22/16.
 */

public class TumblrApp {
    private String apiKey;
    private String apiSecret;
    private boolean using;

    public TumblrApp(String apiKey, String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public boolean isUsing() {
        return using;
    }

}
