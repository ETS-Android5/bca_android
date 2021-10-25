package cc.mudev.bca_android.network.BCaAPI;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import cc.mudev.bca_android.network.APIException;
import cc.mudev.bca_android.network.APIResponse;
import cc.mudev.bca_android.network.NetworkSupport;

public class ProfileAPI {
    public static CompletableFuture<Boolean> create(Context context, String name, String description, String data) {
        return CompletableFuture.supplyAsync(() -> {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", name);
                jsonObject.put("data", data);
                if (description != null && !"".equals(description)) {
                    jsonObject.put("description", description);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                throw new CompletionException(
                        new APIException(
                                "raised while packing items on JSONObject",
                                "프로필을 만드는 중 문제가 발생했습니다,\n" +
                                        "잠시 후 다시 시도해주세요.",
                                -1, null));
            }
            return jsonObject;
        }).thenComposeAsync((jsonObject) -> {
            NetworkSupport api = NetworkSupport.getInstance(context);
            return api.doPost("profiles", null, jsonObject, false, true)
                    .thenComposeAsync((response) -> AccountAPI.isRefreshSuccess(context));
        });
    }

    public static CompletableFuture<APIResponse> load(Context context, int targetProfileId) {
        NetworkSupport api = NetworkSupport.getInstance(context);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("X-Profile-Id", Integer.toString(api.getCurrentProfileId()));

        return api.doPost("profiles/" + targetProfileId, headers, null, false, true);
    }
}
