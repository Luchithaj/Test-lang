package ast;

import java.util.Map;
import java.util.HashMap;

public class Config {
    private String baseUrl;
    private Map<String, String> defaultHeaders;

    public Config() {
        this.defaultHeaders = new HashMap<>();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Map<String, String> getDefaultHeaders() {
        return defaultHeaders;
    }

    public void addHeader(String key, String value) {
        this.defaultHeaders.put(key, value);
    }

    @Override
    public String toString() {
        return "Config{baseUrl='" + baseUrl + "', headers=" + defaultHeaders + "}";
    }
}
