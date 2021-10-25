package cc.mudev.bca_android.network.BCaAPI;

import android.content.Context;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import cc.mudev.bca_android.network.APIResponse;
import cc.mudev.bca_android.network.NetworkSupport;

public class ChatAPI {
    public static CompletableFuture<APIResponse> updateChatRoom(Context context) {
        NetworkSupport api = NetworkSupport.getInstance(context);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("X-Profile-Id", Integer.toString(api.currentProfile.id));
        return api.doGet("chats", headers, false, true);
    }

    public static CompletableFuture<APIResponse> createChatRoom(Context context, String invitingProfiles) {
        NetworkSupport api = NetworkSupport.getInstance(context);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("X-Profile-Id", Integer.toString(api.currentProfile.id));
        JSONObject data = new JSONObject();
        try {
            data.put("inviting_profiles", invitingProfiles);
        } catch (Exception e) {}
        return api.doPost("chats", headers, data, false, true);
    }


    public static CompletableFuture<APIResponse> sendMessage(Context context, int roomId, String message) {
        NetworkSupport api = NetworkSupport.getInstance(context);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("X-Profile-Id", Integer.toString(api.currentProfile.id));
        JSONObject data = new JSONObject();
        try {
            data.put("message", message);
        } catch (Exception e) {}
        return api.doPut("chats/" + roomId + "/events/", headers, data, false, true);
    }


}
