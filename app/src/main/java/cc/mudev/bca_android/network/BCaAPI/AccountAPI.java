package cc.mudev.bca_android.network.BCaAPI;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import cc.mudev.bca_android.dataStorage.SharedPref;
import cc.mudev.bca_android.network.APIException;
import cc.mudev.bca_android.network.APIResponse;
import cc.mudev.bca_android.network.NetworkSupport;
import cc.mudev.bca_android.service.FCMHandlerService;

public class AccountAPI {
    public static CompletableFuture<APIResponse> signup(Context context, String id, String pw, String nick, String email) {
        return CompletableFuture.supplyAsync(() -> {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", id);
                jsonObject.put("nick", nick);
                jsonObject.put("email", email);
                jsonObject.put("pw", pw);

                SharedPref sharedPref = SharedPref.getInstance(context);
                sharedPref.setPref(SharedPref.SharedPrefKeys.ID, id);
                sharedPref.setPref(SharedPref.SharedPrefKeys.PASSWORD, pw);
                sharedPref.setPref(SharedPref.SharedPrefKeys.NICKNAME, nick);
                sharedPref.setPref(SharedPref.SharedPrefKeys.EMAIL, email);
            } catch (JSONException e) {
                e.printStackTrace();
                throw new CompletionException(
                        new APIException(
                                "raised while packing id/pw/nick/email on JSONObject",
                                "회원가입 중 중 문제가 발생했습니다,\n" +
                                        "잠시 후 다시 시도해주세요",
                                -1, null));
            }
            return jsonObject;
        }).thenComposeAsync((jsonObject) -> {
            NetworkSupport api = NetworkSupport.getInstance(context);
            return api.doPost("account/signup", getClientTokenHeader(context), jsonObject, false, false);
        });
    }

    public static CompletableFuture<APIResponse> signin(Context context, String id, String pw) {
        return CompletableFuture.supplyAsync(() -> {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", id);
                jsonObject.put("pw", pw);

                SharedPref sharedPref = SharedPref.getInstance(context);
                sharedPref.setPref(SharedPref.SharedPrefKeys.ID, id);
                sharedPref.setPref(SharedPref.SharedPrefKeys.PASSWORD, pw);
            } catch (JSONException e) {
                e.printStackTrace();
                throw new CompletionException(
                        new APIException(
                                "raised while packing id/pw on JSONObject",
                                "로그인 중 문제가 발생했습니다,\n" +
                                        "잠시 후 다시 시도해주세요",
                                -1, null));
            }
            return jsonObject;
        }).thenComposeAsync((jsonObject) -> {
            NetworkSupport api = NetworkSupport.getInstance(context);
            return api.doPost("account/signin", getClientTokenHeader(context), jsonObject, false, false)
                    .thenApplyAsync((response) -> {
                        SharedPref sharedPref = SharedPref.getInstance(context);
                        try {
                            sharedPref.setPref(SharedPref.SharedPrefKeys.EMAIL, response.body.data.getJSONObject("user").getString("email"));
                            sharedPref.setPref(SharedPref.SharedPrefKeys.NICKNAME, response.body.data.getJSONObject("user").getString("nickname"));
                        } catch (Exception e) {
                        }
                        return response;
                    });
        });
    }

    public static CompletableFuture<APIResponse> signout(Context context) {
        return CompletableFuture.supplyAsync(() -> {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("signout", true);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            SharedPref sharedPref = SharedPref.getInstance(context);
            sharedPref.removePref(SharedPref.SharedPrefKeys.ID);
            sharedPref.removePref(SharedPref.SharedPrefKeys.PASSWORD);
            sharedPref.removePref(SharedPref.SharedPrefKeys.NICKNAME);
            sharedPref.removePref(SharedPref.SharedPrefKeys.EMAIL);
            sharedPref.removePref(SharedPref.SharedPrefKeys.REFRESH_TOKEN);

            return jsonObject;
        }).thenComposeAsync((jsonObject) -> {
            NetworkSupport api = NetworkSupport.getInstance(context);
            return api.doPost("account/signout", null, jsonObject, true, true);
        });
    }

    public static CompletableFuture<APIResponse> sendPasswordResetMail(Context context, String email) {
        return CompletableFuture.supplyAsync(() -> {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", email);
            } catch (JSONException e) {
                e.printStackTrace();
                throw new CompletionException(
                        new APIException(
                                "raised while packing email on JSONObject",
                                "비밀번호를 초기화할 수 있는 메일을 보내는 중 문제가 발생했습니다,\n" +
                                        "잠시 후 다시 시도해주세요.",
                                -1, null));
            }
            return jsonObject;
        }).thenComposeAsync((jsonObject) -> {
            NetworkSupport api = NetworkSupport.getInstance(context);
            return api.doPost("account/reset-password", null, jsonObject, false, false);
        });
    }

    public static CompletableFuture<Boolean> isRefreshSuccess(Context context) {
        NetworkSupport api = NetworkSupport.getInstance(context);
        return api.doPost("account/refresh", getClientTokenHeader(context), null, true, false)
                .thenApplyAsync((response) -> true)
                .exceptionally((e) -> {
                    api.resetAuthData();
                    return false;
                });
    }

    public static Map<String, String> getClientTokenHeader(Context context) {
        HashMap<String, String> headers = new HashMap<>();
        try {
            headers.put("X-Client-Token", FCMHandlerService.getToken(context));
        } catch (Exception e) {
            // Just ignore, but this client won't be able to receive any notifications.
            e.printStackTrace();
        }
        return headers;
    }
}
