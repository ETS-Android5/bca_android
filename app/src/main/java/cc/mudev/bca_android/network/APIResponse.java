package cc.mudev.bca_android.network;

import java.util.List;
import java.util.Map;

public class APIResponse {
    public int status;
    public Map<String, List<String>> headers;
    public APIResponseBody body;

    public APIResponse(int status, Map<String, List<String>> headers, APIResponseBody body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }
}
