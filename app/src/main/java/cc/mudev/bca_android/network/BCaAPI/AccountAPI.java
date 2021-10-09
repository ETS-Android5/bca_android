package cc.mudev.bca_android.network.BCaAPI;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import cc.mudev.bca_android.dataStorage.SharedPref;
import cc.mudev.bca_android.network.APIException;
import cc.mudev.bca_android.network.APIResponse;
import cc.mudev.bca_android.network.NetworkSupport;

public class AccountAPI {
    public static CompletableFuture<APIResponse> signup(Context context, String id, String pw, String nick, String email) {
        return CompletableFuture.supplyAsync(() -> {
            if (id == null || id.isEmpty()) {
                throw new CompletionException(
                        new APIException(
                                "id is null or empty",
                                "아이디를 입력해주세요!",
                                -1, null));
            } else if (pw == null || pw.isEmpty()) {
                throw new CompletionException(
                        new APIException(
                                "pw is null or empty",
                                "비밀번호를 입력해주세요!",
                                -1, null));
            } else if (nick == null || nick.isEmpty()) {
                throw new CompletionException(
                        new APIException(
                                "nick is null or empty",
                                "성함을 입력해주세요!",
                                -1, null));
            } else if (email == null || email.isEmpty()) {
                throw new CompletionException(
                        new APIException(
                                "email is null or empty",
                                "이메일을 입력해주세요!",
                                -1, null));
            }

            SharedPref sharedPref = SharedPref.getInstance(context);
            sharedPref.setPref(SharedPref.SharedPrefKeys.ID, id);
            sharedPref.setPref(SharedPref.SharedPrefKeys.PASSWORD, pw);
            sharedPref.setPref(SharedPref.SharedPrefKeys.NICKNAME, nick);
            sharedPref.setPref(SharedPref.SharedPrefKeys.EMAIL, email);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", id);
                jsonObject.put("nick", nick);
                jsonObject.put("email", email);
                jsonObject.put("pw", pw);
            } catch (JSONException e) {
                e.printStackTrace();
                throw new CompletionException(
                        new APIException(
                                "raised while packing id/pw/nick/email on JSONObject",
                                "회원가입 중 중 문제가 발생했습니다,\n" +
                                        "잠시 후 다시 시도해주세요.",
                                -1, null));
            }
            return jsonObject;
        }).thenComposeAsync((jsonObject) -> {
            NetworkSupport api = NetworkSupport.getInstance();
            return api.doPost("account/signup", null, jsonObject, false, false);
        });
    }

    public static CompletableFuture<APIResponse> signin(Context context, String id, String pw) throws APIException {
        return CompletableFuture.supplyAsync(() -> {
            if (id == null || id.isEmpty()) {
                throw new CompletionException(
                        new APIException(
                                "id is null or empty",
                                "아이디나 이메일을 입력해주세요!",
                                -1, null));
            } else if (pw == null || pw.isEmpty()) {
                throw new CompletionException(
                        new APIException(
                                "pw is null or empty",
                                "비밀번호를 입력해주세요!",
                                -1, null));
            }

            SharedPref sharedPref = SharedPref.getInstance(context);
            sharedPref.setPref(SharedPref.SharedPrefKeys.ID, id);
            sharedPref.setPref(SharedPref.SharedPrefKeys.PASSWORD, pw);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", id);
                jsonObject.put("pw", pw);
            } catch (JSONException e) {
                e.printStackTrace();
                throw new CompletionException(
                        new APIException(
                                "raised while packing id/pw on JSONObject",
                                "로그인 중 문제가 발생했습니다,\n" +
                                        "잠시 후 다시 시도해주세요.",
                                -1, null));
            }
            return jsonObject;
        }).thenComposeAsync((jsonObject) -> {
            NetworkSupport api = NetworkSupport.getInstance();
            return api.doPost("account/signin", null, jsonObject, false, false).thenApplyAsync((response) -> {
                        SharedPref sharedPref = SharedPref.getInstance(context);
                        try {
                            sharedPref.setPref(SharedPref.SharedPrefKeys.EMAIL, response.body.data.getJSONObject("user").getString("email"));
                        } catch (Exception e) {
                        }
                        try {
                            sharedPref.setPref(SharedPref.SharedPrefKeys.NICKNAME, response.body.data.getJSONObject("user").getString("nickname"));
                        } catch (Exception e) {
                        }
                        return response;
                    }
            );
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
            NetworkSupport api = NetworkSupport.getInstance();
            return api.doPost("account/signout", null, jsonObject, false, false);
        });
    }

    public static CompletableFuture<Boolean> isRefreshSuccess() {
        NetworkSupport api = NetworkSupport.getInstance();
        return api.doPost("account/refresh", null, null, true, false)
                .thenApplyAsync((response) -> true)
                .exceptionally((e) -> {
                    api.resetAuthData();
                    return false;
                });
    }
}
