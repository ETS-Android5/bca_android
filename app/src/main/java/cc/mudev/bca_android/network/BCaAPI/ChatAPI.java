package cc.mudev.bca_android.network.BCaAPI;

import android.content.Context;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import cc.mudev.bca_android.activity.chat.ChatCreateActivity;
import cc.mudev.bca_android.activity.chat.ChatRoomActivity;
import cc.mudev.bca_android.database.ChatDatabase;
import cc.mudev.bca_android.database.ChatEventDao;
import cc.mudev.bca_android.database.ChatParticipantDao;
import cc.mudev.bca_android.database.ChatRoomDao;
import cc.mudev.bca_android.database.TB_CHAT_EVENT;
import cc.mudev.bca_android.database.TB_CHAT_PARTICIPANT;
import cc.mudev.bca_android.database.TB_CHAT_ROOM;
import cc.mudev.bca_android.network.APIResponse;
import cc.mudev.bca_android.network.NetworkSupport;

public class ChatAPI {
    public static CompletableFuture<APIResponse> updateChatRoom(Context context) {
        NetworkSupport api = NetworkSupport.getInstance(context);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("X-Profile-Id", Integer.toString(api.currentProfile.id));
        return api.doGet("chats", headers, false, true)
                .thenApplyAsync((response) -> {
                    try {
                        JSONArray roomDataArray = response.body.data.getJSONArray("chat_rooms");
                        ChatDatabase chatDB = ChatDatabase.getInstance(context);
                        ChatRoomDao chatRoomDao = chatDB.roomDao();
                        ChatParticipantDao chatParticipantDao = chatDB.participantDao();
                        ChatEventDao chatEventDao = chatDB.eventDao();
                        for (int i = 0; i < roomDataArray.length(); i++) {
                            JSONObject roomData = roomDataArray.getJSONObject(i);
                            int roomId = roomData.getInt("uuid");

                            chatRoomDao.insertRoom(
                                    new TB_CHAT_ROOM(
                                            roomId,
                                            roomData.getString("name"),
                                            null,
                                            roomData.getInt("created_by_profile_id"),
                                            roomData.getString("commit_id"),
                                            roomData.getInt("created_at_int"),
                                            roomData.getInt("modified_at_int"),
                                            null,
                                            0,
                                            0
                                    ));

                            JSONArray participantArray = roomData.getJSONArray("participants");
                            if (participantArray.length() > 0) {
                                for (int p = 0; p < participantArray.length(); p++) {
                                    JSONObject participantData = participantArray.getJSONObject(p);

                                    chatParticipantDao.insertParticipant(
                                            new TB_CHAT_PARTICIPANT(
                                                    participantData.getInt("uuid"),
                                                    roomId,
                                                    participantData.getString("room_name"),
                                                    participantData.getInt("profile_id"),
                                                    participantData.getString("profile_name"),
                                                    participantData.getString("commit_id"),
                                                    participantData.getInt("created_at_int"),
                                                    participantData.getInt("modified_at_int")
                                            ));
                                }
                            }

                            JSONArray eventArray = roomData.getJSONArray("events");
                            if (eventArray.length() > 0) {
                                for (int e = 0; e < eventArray.length(); e++) {
                                    JSONObject eventData = eventArray.getJSONObject(e);

                                    chatEventDao.insertEvent(
                                            new TB_CHAT_EVENT(
                                                    eventData.getInt("uuid"),
                                                    eventData.getInt("event_index"),
                                                    eventData.getString("event_type"),
                                                    roomId,
                                                    eventData.getString("message"),
                                                    eventData.getInt("caused_by_profile_id"),
                                                    eventData.getInt("caused_by_participant_id"),
                                                    eventData.getString("commit_id"),
                                                    eventData.getInt("created_at_int"),
                                                    eventData.getInt("modified_at_int"),
                                                    eventData.getBoolean("encrypted") ? 1 : 0
                                            ));
                                }
                            }
                        }
                    } catch (Exception e) {
                        throw new CompletionException(e);
                    }
                    return response;
                });
    }

    public static CompletableFuture<APIResponse> createChatRoom(Context context, String invitingProfiles) {
        NetworkSupport api = NetworkSupport.getInstance(context);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("X-Profile-Id", Integer.toString(api.currentProfile.id));
        JSONObject data = new JSONObject();
        try {
            data.put("inviting_profiles", invitingProfiles);
        } catch (Exception e) {
        }

        // Hack to get multiple returns (chat room creation result and chat room list update result)
        CompletableFuture<APIResponse> chatRoomCreationStage = api.doPost("chats", headers, data, false, true);
        CompletableFuture<APIResponse> chatRoomListUpdateStage = chatRoomCreationStage.thenComposeAsync((response1) -> ChatAPI.updateChatRoom(context));
        return chatRoomListUpdateStage.thenCombineAsync(chatRoomListUpdateStage, (response1, response2) -> response1);
    }


    public static CompletableFuture<APIResponse> sendMessage(Context context, int roomId, String message) {
        NetworkSupport api = NetworkSupport.getInstance(context);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("X-Profile-Id", Integer.toString(api.currentProfile.id));
        JSONObject data = new JSONObject();
        try {
            data.put("message", message);
        } catch (Exception e) {
        }
        return api.doPut("chats/" + roomId + "/events/", headers, data, false, true);
    }


}
