package cc.mudev.bca_android.network;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import javax.net.ssl.HttpsURLConnection;

import cc.mudev.bca_android.activity.profile.ProfileDetailActivity;
import cc.mudev.bca_android.dataStorage.SharedPref;

public class NetworkSupport {
    public Context context = null;

    public boolean isOffline = true;

    public static String baseDomain = "https://bca.mudev.cc";
    public static String baseUrl = baseDomain + "/api/dev/";

    // TODO: below values must be a private!!!
    public String refreshToken;
    public String accessToken;
    public String csrfToken;

    public boolean isInitialized = false;

    public APIRole roleList;
    public APIRole.ProfileRole currentProfile;

    private NetworkSupport() {
        this.csrfToken = UUID.randomUUID().toString();
    }

    private static class LazyHolder {
        private static final NetworkSupport INSTANCE = new NetworkSupport();
    }

    public static NetworkSupport getInstance(Context context) {
        if (LazyHolder.INSTANCE.context == null && context == null)
            return null;

        if (context != null)
            LazyHolder.INSTANCE.context = context;

        if (!LazyHolder.INSTANCE.isInitialized) {
            LazyHolder.INSTANCE.isInitialized = true;
            // Try to get refreshToken from shared preferences
            if (LazyHolder.INSTANCE.refreshToken == null) {
                SharedPref sharedPref = SharedPref.getInstance(context);
                LazyHolder.INSTANCE.refreshToken = sharedPref.getString(SharedPref.SharedPrefKeys.REFRESH_TOKEN);
            }
        }
        return LazyHolder.INSTANCE;
    }

    public int getCurrentProfileId() {
        try {
            return LazyHolder.INSTANCE.currentProfile.id;
        } catch (NullPointerException e) {
            return -1;
        }
    }

    public List<Integer> getAllProfileIds() {
        List<Integer> resultIds = new ArrayList<>();
        for (APIRole.APIRoleBase myProfileId : this.roleList.role) {
            if (myProfileId.getClass() == APIRole.ProfileRole.class) {
                resultIds.add(myProfileId.id);
            }
        }
        return resultIds;
    }

    public boolean isInternetOffline() {
        this.isOffline = true;
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection)
                    (new URL("https://clients3.google.com/generate_204")
                            .openConnection());
            httpURLConnection.setRequestProperty("User-Agent", "Android");
            httpURLConnection.setRequestProperty("Connection", "close");
            httpURLConnection.setConnectTimeout(1500);
            httpURLConnection.connect();
            this.isOffline = !(httpURLConnection.getResponseCode() == 204 && httpURLConnection.getContentLength() == 0);
            return this.isOffline;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error checking internet connection", e);
        }
        return this.isOffline;
    }

    public void resetAuthData() {

        this.refreshToken = null;
        this.accessToken = null;
        this.csrfToken = UUID.randomUUID().toString();
        this.roleList = null;

        SharedPref sharedPref = SharedPref.getInstance(this.context);
        sharedPref.removePref(SharedPref.SharedPrefKeys.ID);
        sharedPref.removePref(SharedPref.SharedPrefKeys.PASSWORD);
        sharedPref.removePref(SharedPref.SharedPrefKeys.NICKNAME);
        sharedPref.removePref(SharedPref.SharedPrefKeys.EMAIL);
        sharedPref.removePref(SharedPref.SharedPrefKeys.ROLE_STRING);
        sharedPref.removePref(SharedPref.SharedPrefKeys.REFRESH_TOKEN);
    }

    public void setRoleFromString(String roleString) {
        try {
            if (!TextUtils.isEmpty(roleString)) {
                SharedPref.minstance.setPref(SharedPref.SharedPrefKeys.ROLE_STRING, roleString);

                ArrayList<String> roleStringList = new ArrayList<>();
                JSONArray jArray = new JSONArray(roleString);
                for (int i = 0; i < jArray.length(); i++)
                    roleStringList.add(jArray.getString(i));

                this.roleList = new APIRole(roleStringList);
                if (this.currentProfile == null) {
                    for (APIRole.APIRoleBase role : this.roleList.role) {
                        if (role.getClass() == APIRole.ProfileRole.class) {
                            this.currentProfile = (APIRole.ProfileRole) role;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setRoleFromSharedPref() {
        String roleString = SharedPref.getInstance(this.context).getString(SharedPref.SharedPrefKeys.ROLE_STRING);
        setRoleFromString(roleString);
    }

    public HashMap<String, String> getAuthenticatedHeader(boolean doRefreshTokenAuth, boolean doAccessTokenAuth) throws APIException {
        HashMap<String, String> resultHeader = new HashMap<>();

        // Check access token and refresh token when those are used
        if (doAccessTokenAuth) {
            if (accessToken == null || accessToken.isEmpty()) {
                if (refreshToken == null || refreshToken.isEmpty()) {
                    throw new APIException(
                            "Access token is null or empty, but tried accessTokenAuth",
                            "로그인 정보가 없습니다, 다시 로그인을 해 주세요.",
                            401, null);
                }
            }
            resultHeader.put("Authorization", "Bearer " + this.accessToken);
        }
        if (doRefreshTokenAuth) {
            if (refreshToken == null || refreshToken.isEmpty()) {
                this.resetAuthData();
                throw new APIException(
                        "Refresh token is null or empty, but tried refreshTokenAuth",
                        "로그인 정보가 없습니다, 다시 로그인을 해 주세요.",
                        401, null);
            }
            resultHeader.put("Cookie", "refresh_token=" + this.refreshToken);
        }
        return resultHeader;
    }

    private APIResponse sendRequest(
            String method, String route,
            Map<String, String> headers, JSONObject data,
            boolean doRefreshTokenAuth, boolean doAccessTokenAuth, boolean isRetry)
            throws APIException {

        String networkErrorMsg = "서버와 통신 중 예상하지 못한 문제가 발생했습니다!\n";
        String networkErrorRetryAfter10MinMsg = networkErrorMsg + "10분 후에 다시 시도해주세요.";

        try {
            if (!this.isInitialized)
                throw new APIException(
                        "NetworkSupport not initialized",
                        networkErrorMsg + "(앱 초기화 중 문제가 발생했습니다.)",
                        -1, null);

            if (this.isInternetOffline())
                throw new APIException(
                        "no internet connection",
                        networkErrorMsg + "(인터넷 연결을 확인해주세요.)",
                        -1, null);

            URL url = new URL(baseUrl + route);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod(method.toUpperCase());
            conn.setRequestProperty("User-Agent", System.getProperty("http.agent"));
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");

            // Set additional headers when headers are set
            if (headers != null) {
                Set<String> keys = headers.keySet();
                for (String key : keys) {
                    conn.setRequestProperty(key, headers.get(key));
                }
            }

            // Always set X-Csrf-Token header
            if (csrfToken == null) {
                csrfToken = UUID.randomUUID().toString();
            }
            conn.setRequestProperty("X-Csrf-Token", csrfToken);

            // Check access token and refresh token when those are used
            if (doAccessTokenAuth) {
                if (accessToken == null || accessToken.isEmpty()) {
                    if (refreshToken == null || refreshToken.isEmpty()) {
                        throw new APIException(
                                "Access token is null or empty, but tried accessTokenAuth",
                                "로그인 정보가 없습니다, 다시 로그인을 해 주세요.",
                                401, null);
                    }
                    if (isRetry) {
                        this.resetAuthData();
                        throw new APIException(
                                "request after refreshing access token failed",
                                "로그인 정보에 문제가 있습니다, 다시 로그인을 해 주세요!",
                                401, null);
                    }
                    // Refresh access token and try again
                    this.sendRequest("post", "account/refresh", null, null, true, false, true);
                    return this.sendRequest(method, route, headers, data, doRefreshTokenAuth, true, true);
                }
                conn.setRequestProperty("Authorization", "Bearer " + this.accessToken);
            }
            if (doRefreshTokenAuth) {
                if (refreshToken == null || refreshToken.isEmpty()) {
                    this.resetAuthData();
                    throw new APIException(
                            "Refresh token is null or empty, but tried refreshTokenAuth",
                            "로그인 정보가 없습니다, 다시 로그인을 해 주세요.",
                            401, null);
                }
                conn.setRequestProperty("Cookie", "refresh_token=" + this.refreshToken);
            }

            // We want to set request body except for get|head|delete|options|connect|trace method
            if (!method.matches("get|head|delete|options|connect|trace") && data != null) {
                // Those method must not send request body
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = data.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            } else {
                conn.setDoOutput(false);
            }

            // Do request and get status code
            int status = conn.getResponseCode();
            Map<String, List<String>> responseHeaders = conn.getHeaderFields();

            // Try to parse response from server
            JSONObject responseBodyJson;
            APIResponseBody responseBody = null;
            InputStream tmpResponse = (200 <= status && status <= 399) ? conn.getInputStream() : conn.getErrorStream();

            if (tmpResponse != null) {
                try {
                    BufferedReader br = new BufferedReader((new InputStreamReader(tmpResponse)));
                    StringBuffer responseStringBuffer = new StringBuffer();

                    String inputLine;
                    while ((inputLine = br.readLine()) != null) {
                        responseStringBuffer.append(inputLine);
                    }
                    br.close();

                    responseBodyJson = new JSONObject(responseStringBuffer.toString());
                    responseBody = new APIResponseBody(
                            responseBodyJson.getInt("code"),
                            responseBodyJson.getString("sub_code"),
                            responseBodyJson.getString("message"),
                            responseBodyJson.getJSONObject("data"));
                } catch (Exception e1) {
                    if (!method.matches("head|trace")) {
                        e1.printStackTrace();
                        System.out.println("Exception raised " + e1.getClass().getName() + " - " + e1.getMessage());
                    }
                }
            }

            if (method.matches("head|trace")) {
                return new APIResponse(status, responseHeaders, responseBody);
            } else if (responseBody == null) {
                throw new APIException(
                        "response data is null",
                        networkErrorRetryAfter10MinMsg + "\n(서버에서 받은 응답이 없거나 해석하는데 문제가 있음)",
                        status, null);
            }

            // If response contains access token, then set this variable on this class
            if (route.matches("account/signin|account/signup|account/refresh")) {
                if (200 <= status && status <= 299) {
                    this.accessToken = responseBody.data.getJSONObject("user").getJSONObject("access_token").getString("token");

                    String tokenDataBase64 = this.accessToken.split("\\.")[1];
                    String tokenDataString = new String(Base64.decode(tokenDataBase64, Base64.DEFAULT), StandardCharsets.UTF_8);
                    JSONObject tokenData = new JSONObject(tokenDataString);
                    String roleString = (tokenData.has("role")) ? tokenData.getString("role") : null;
                    setRoleFromString(roleString);

                    if (route.matches("account/signin|account/signup")) {
                        // Set refresh token, too
                        // We need to find refresh token cookie
                        for (String cookieVal : responseHeaders.get("Set-Cookie")) {
                            if (cookieVal.startsWith("refresh_token")) {
                                String[] cookieStr = cookieVal.split("=");
                                String[] refreshTokenStr = cookieStr[1].split(";");
                                this.refreshToken = refreshTokenStr[0].trim();

                                SharedPref.minstance.setPref(SharedPref.SharedPrefKeys.REFRESH_TOKEN, this.refreshToken);
                            }
                        }
                    }
                } else {
                    // TODO: Set proper message when login fails
                    throw new APIException(
                            responseBody.subCode,
                            "로그인에 실패했습니다\n(" + responseBody.subCode + ")",
                            status, responseBody);
                }
            } else if (route.equals("account/signout")) {
                if (200 <= status && status <= 299) {
                    this.resetAuthData();
                    return new APIResponse(status, responseHeaders, responseBody);
                }
            }

            if (status >= 500) {
                throw new APIException(
                        "server error",
                        networkErrorRetryAfter10MinMsg + "\n(서버 오류)",
                        status, responseBody);
            } else if (status >= 400) {
                switch (status) {
                    case 400:
                        throw new APIException(
                                "server cannot understand our request (Bad request)",
                                networkErrorRetryAfter10MinMsg + "\n(앱 요청 구문 오류)",
                                status, responseBody);

                    case 401:  // this "possibly" returns response
                        // token not given / token expired / token invalid
                        // wrong password / account locked / account deactivated
                        // We need to try refreshing access token and retry this.
                        // If access token refresh fails, then raise errors.
                        if (isRetry) {
                            this.resetAuthData();
                            throw new APIException(
                                    "request after refreshing access token failed",
                                    "로그인 정보에 문제가 있습니다, 다시 로그인을 해 주세요!",
                                    status, responseBody);
                        }
                        if (refreshToken == null || refreshToken.isEmpty()) {
                            throw new APIException(
                                    "refreshToken is null or empty",
                                    "로그인이 필요합니다!",
                                    status, responseBody);
                        }

                        // Refresh access token and try again
                        this.sendRequest("post", "account/refresh", null, null, true, false, true);
                        return this.sendRequest(method, route, headers, data, doRefreshTokenAuth, doAccessTokenAuth, true);

                    case 403:
                        throw new APIException(
                                "requested action was forbidden",
                                "권한이 없습니다.",
                                status, responseBody);

                    case 404:
                        throw new APIException(
                                "resource not found",
                                "요청하신 정보를 찾을 수 없습니다.",
                                status, responseBody);

                    case 405:
                        throw new APIException(
                                "method not permitted",
                                networkErrorRetryAfter10MinMsg + "\n(잘못된 요청입니다)",
                                status, responseBody);

                    case 409: // this "possibly" returns response
                        // already used / information mismatch, conflict
                        break;
                    case 410: // this "possibly" returns response
                        // resource gone
                        break;

                    case 412: // this "possibly" returns response
                        // resource prediction failed
                        break;

                    case 415:
                        throw new APIException(
                                "requested response content-type not supported",
                                networkErrorRetryAfter10MinMsg,
                                status, responseBody);

                    case 422: // this "possibly" returns response
                        // request.body.bad_semantics - email address validation failure, etc.
                        break;

                    default:
                        break;
                }
            }
            return new APIResponse(status, responseHeaders, responseBody);
        } catch (APIException e) {
            throw e;
        } catch (java.net.SocketTimeoutException e) {
            e.printStackTrace();
            throw new APIException(
                    "SocketTimeoutException",
                    networkErrorRetryAfter10MinMsg + "\n(서버와 연결하는데 너무 오래 걸립니다.)",
                    -1, null);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new APIException(
                    "UnknownHostException raised",
                    networkErrorMsg + "인터넷 연결을 확인해주시고,\n잠시 후 다시 시도해주세요.",
                    -1, null);
        } catch (IOException e) {
            e.printStackTrace();
            throw new APIException(
                    "IOException raised",
                    networkErrorRetryAfter10MinMsg + "\n(서버에서 받은 응답을 읽는 중 문제가 발생했습니다.)",
                    -1, null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new APIException(
                    "uncaught exception - " + e.getClass().getName(),
                    networkErrorRetryAfter10MinMsg + "(예상하지 못한 오류가 발생했습니다.)",
                    -1, null);
        }
    }

    public CompletableFuture<APIResponse> doHead(
            String route, Map<String, String> headers,
            boolean doRefreshToken, boolean doAccessToken) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.sendRequest("head", route, headers, null, doRefreshToken, doAccessToken, false);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<APIResponse> doGet(
            String route, Map<String, String> headers,
            boolean doRefreshToken, boolean doAccessToken) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.sendRequest("get", route, headers, null, doRefreshToken, doAccessToken, false);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<APIResponse> doPost(
            String route, Map<String, String> headers, JSONObject data,
            boolean doRefreshToken, boolean doAccessToken) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.sendRequest("post", route, headers, data, doRefreshToken, doAccessToken, false);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<APIResponse> doPatch(
            String route, Map<String, String> headers, JSONObject data,
            boolean doRefreshToken, boolean doAccessToken) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.sendRequest("patch", route, headers, data, doRefreshToken, doAccessToken, false);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<APIResponse> doPut(
            String route, Map<String, String> headers, JSONObject data,
            boolean doRefreshToken, boolean doAccessToken) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.sendRequest("put", route, headers, data, doRefreshToken, doAccessToken, false);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<APIResponse> doDelete(
            String route, Map<String, String> headers, JSONObject data,
            boolean doRefreshToken, boolean doAccessToken) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.sendRequest("delete", route, headers, data, doRefreshToken, doAccessToken, false);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    public CompletableFuture<APIResponse> ping() {
        return this.doGet("ping", null, false, false);
    }

    public CompletableFuture<APIResponse> uploadFile(String filepath, String filefield, String fileMimeType, Map<String, String> params) {
        return CompletableFuture.supplyAsync(() -> {
            String networkErrorMsg = "서버와 통신 중 예상하지 못한 문제가 발생했습니다!\n";
            String networkErrorRetryAfter10MinMsg = networkErrorMsg + "10분 후에 다시 시도해주세요.";

            DataOutputStream outputStream;

            String twoHyphens = "--";
            String boundary = "*****" + System.currentTimeMillis() + "*****";
            String lineEnd = "\r\n";

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;

            String[] q = filepath.split("/");
            int idx = q.length - 1;

            try {
                if (!this.isInitialized)
                    throw new APIException(
                            "NetworkSupport not initialized",
                            networkErrorMsg + "(앱 초기화 중 문제가 발생했습니다.)",
                            -1, null);

                if (this.isInternetOffline())
                    throw new APIException(
                            "no internet connection",
                            networkErrorMsg + "(인터넷 연결을 확인해주세요.)",
                            -1, null);

                File file = new File(filepath);
                FileInputStream fileInputStream = new FileInputStream(file);

                URL url = new URL(baseUrl + "uploads");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("User-Agent", System.getProperty("http.agent"));
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Connection", "Keep-Alive");

                // Always set X-Csrf-Token header
                if (csrfToken == null) {
                    csrfToken = UUID.randomUUID().toString();
                }
                conn.setRequestProperty("X-Csrf-Token", csrfToken);

                // Check access token
                if (accessToken == null || accessToken.isEmpty()) {
                    throw new APIException(
                            "Access token is null or empty, but tried accessTokenAuth",
                            "로그인 정보가 없습니다, 다시 로그인을 해 주세요.",
                            401, null);
                }
                conn.setRequestProperty("Authorization", "Bearer " + this.accessToken);

                outputStream = new DataOutputStream(conn.getOutputStream());
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + filefield + "\"; filename=\"" + q[idx] + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
                outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
                outputStream.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    outputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                outputStream.writeBytes(lineEnd);

                // Upload POST Data
                if (params != null) {
                    for (String key : params.keySet()) {
                        String value = params.get(key);

                        outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                        outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                        outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                        outputStream.writeBytes(lineEnd);
                        outputStream.writeBytes(value);
                        outputStream.writeBytes(lineEnd);
                    }
                }

                outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Do request and get status code
                int status = conn.getResponseCode();
                Map<String, List<String>> responseHeaders = conn.getHeaderFields();

                // Try to parse response from server
                JSONObject responseBodyJson = null;
                APIResponseBody responseBody = null;
                InputStream tmpResponse = (200 <= status && status <= 399) ? conn.getInputStream() : conn.getErrorStream();

                if (tmpResponse != null) {
                    try {
                        BufferedReader br = new BufferedReader((new InputStreamReader(tmpResponse)));
                        StringBuffer responseStringBuffer = new StringBuffer();

                        String inputLine;
                        while ((inputLine = br.readLine()) != null) {
                            responseStringBuffer.append(inputLine);
                        }
                        br.close();

                        responseBodyJson = new JSONObject(responseStringBuffer.toString());
                        responseBody = new APIResponseBody(
                                responseBodyJson.getInt("code"),
                                responseBodyJson.getString("sub_code"),
                                responseBodyJson.getString("message"),
                                responseBodyJson.getJSONObject("data"));
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        System.out.println("Exception raised " + e1.getClass().getName() + " - " + e1.getMessage());
                    }
                }

                if (responseBody == null) {
                    throw new APIException(
                            "response data is null",
                            networkErrorRetryAfter10MinMsg + "\n(서버에서 받은 응답이 없거나 해석하는데 문제가 있음)",
                            status, null);
                }

                return new APIResponse(status, responseHeaders, responseBody);

            } catch (Exception e) {
                e.printStackTrace();
                throw new CompletionException(
                        new APIException(
                                "error",
                                networkErrorRetryAfter10MinMsg + "\n(서버와 연결하는데 너무 오래 걸립니다.)",
                                -1, null));
            }
        });
    }
}