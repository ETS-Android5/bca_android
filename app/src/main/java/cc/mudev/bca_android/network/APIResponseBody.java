package cc.mudev.bca_android.network;

import org.json.JSONObject;

public class APIResponseBody {
    public int code;
    public String subCode;
    public String message;
    public JSONObject data;

    public APIResponseBody(int code, String subCode, String message, JSONObject data) {
        this.code = code;
        this.subCode = subCode;
        this.message = message;
        this.data = data;
    }
}
