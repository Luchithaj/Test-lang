package ast;

import java.util.*;

public class ConfigBlock {
    public String baseUrl;
    public Map<String, String> headers;
    
    public ConfigBlock() {
        this.baseUrl = null;
        this.headers = new HashMap<>();
    }
    
    public ConfigBlock(String baseUrl, Map<String, String> headers) {
        this.baseUrl = baseUrl;
        this.headers = headers != null ? headers : new HashMap<>();
    }
}
