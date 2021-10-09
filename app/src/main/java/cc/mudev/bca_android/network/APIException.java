package cc.mudev.bca_android.network;

public class APIException extends Exception {
    public String debugMsg = "default error message";
    public String displayMsg = "서버와 통신 중 문제가 발생했습니다ㅠㅜ";
    public int statusCode = 500;
    public APIResponseBody body;

    public APIException(String debugMsg, String displayMsg, int statusCode, APIResponseBody body) {
        super(debugMsg);
        this.debugMsg = debugMsg;
        this.displayMsg = displayMsg;
        this.statusCode = statusCode;
        this.body = body;
    }
}
