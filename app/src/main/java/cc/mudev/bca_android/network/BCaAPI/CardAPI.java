package cc.mudev.bca_android.network.BCaAPI;

import android.content.Context;
import android.net.Uri;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import cc.mudev.bca_android.network.APIResponse;
import cc.mudev.bca_android.network.NetworkSupport;
import cc.mudev.bca_android.util.UriTools;

public class CardAPI {
    public static CompletableFuture<APIResponse> create(Context context, int profileId, Uri cardImageUri, String cardName) {
        String filePath = UriTools.getRealPathFromURI(context, cardImageUri);
        String fileMimeType = context.getContentResolver().getType(cardImageUri);

        NetworkSupport api = NetworkSupport.getInstance(context);
        return api.uploadFile(filePath, "file", fileMimeType, null)
                .thenComposeAsync((apiResponse) -> {
                    try {
                        String imageUrl = apiResponse.body.data.getJSONObject("file").getString("url");
                        String dataString = "{\"image\":\"" + imageUrl + "\"}";

                        Map<String, String> headers = new HashMap<>();
                        headers.put("X-Profile-Id", Integer.toString(profileId));

                        Map<String, String> data = new HashMap<>();
                        data.put("name", cardName);
                        data.put("data", dataString);

                        return api.doPost("/cards", headers, new JSONObject(data), false, true);
                    } catch (Exception e) {
                        throw new CompletionException((e));
                    }
                });
    }

    public static CompletableFuture<APIResponse> delete(Context context, int cardId) {
        NetworkSupport api = NetworkSupport.getInstance(context);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("X-Profile-Id", Integer.toString(api.getCurrentProfileId()));
        return api.doDelete("cards/" + cardId, headers, null, false, true);
    }

    public static CompletableFuture<APIResponse> getAllCardsOfProfile(Context context, int targetProfileId) {
        NetworkSupport api = NetworkSupport.getInstance(context);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("X-Profile-Id", Integer.toString(api.getCurrentProfileId()));

        return api.doPost("profiles/" + targetProfileId + "/cards", headers, null, false, true);
    }
}
