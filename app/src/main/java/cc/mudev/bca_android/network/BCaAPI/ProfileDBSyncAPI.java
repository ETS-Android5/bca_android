package cc.mudev.bca_android.network.BCaAPI;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import cc.mudev.bca_android.database.ProfileDatabase;
import cc.mudev.bca_android.network.APIException;
import cc.mudev.bca_android.network.APIResponse;
import cc.mudev.bca_android.network.NetworkSupport;

public class ProfileDBSyncAPI {
    public static CompletableFuture<String> getServerDBETag(Context context) {
        NetworkSupport api = NetworkSupport.getInstance(context);

        return api.doHead("sync", null, false, true).thenApplyAsync(
                (APIResponse resp) -> {
                    return resp.headers.get("ETag").get(0);
                }
        );
    }

    public static CompletableFuture<String> getLocalDBETag(Context context) {
        return CompletableFuture.supplyAsync(() -> {
            byte[] dbFileContent = null;
            try {
                // TODO: Change this to real DB file
                File dbFile = new File(context.getFilesDir(), "user_db.sqlite");
                if (!dbFile.exists()) {
                    // Temporary copy db file from asset
                    AssetManager assetManager = context.getAssets();
                    String[] files = null;
                    try {
                        InputStream is = assetManager.open("databases/blank.sqlite");
                        OutputStream out = new FileOutputStream(dbFile);
                        byte[] buffer = new byte[1024];
                        int read = is.read(buffer);

                        while (read != -1) {
                            out.write(buffer, 0, read);
                            read = is.read(buffer);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                FileInputStream dbFileFis = new FileInputStream(dbFile);
                dbFileContent = new byte[(int) dbFileFis.available()];
                dbFileFis.read(dbFileContent);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new CompletionException(
                        new APIException(
                                "db file not found",
                                "내려받아 저장해놓은 DB 파일이 존재하지 않습니다.",
                                -1, null)
                );
            } catch (IOException e) {
                e.printStackTrace();
                throw new CompletionException(
                        new APIException(
                                "exception raised while reading db file",
                                "내려받아 저장해놓은 DB 파일을 읽지 못했습니다.",
                                -1, null)
                );
            }

            MessageDigest mdEnc = null;
            try {
                mdEnc = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                System.out.println("Exception while encrypting to md5");
                e.printStackTrace();
                throw new CompletionException(
                        new APIException(
                                "exception raised while calculating MD5 of db file",
                                "내려받아 저장해놓은 DB 파일의 무결성을 검사하기 위해 파일을 계산하던 중 문제가 발생했습니다.",
                                -1, null)
                );
            } // Encryption algorithm
            mdEnc.update(dbFileContent, 0, dbFileContent.length);
            String md5 = new BigInteger(1, mdEnc.digest()).toString(16);
            while (md5.length() < 32) {
                md5 = "0" + md5;
            }
            return md5;
        });
    }

    public static CompletableFuture<Boolean> isDBLatest(Context context) {
        return getServerDBETag(context).thenCombineAsync(
                getLocalDBETag(context),
                (serverEtag, localEtag) -> {
                    try {
                        return serverEtag.equals(localEtag);
                    } catch (Exception e) {
                        return false;
                    }
                }).exceptionally(e -> false);
    }

    public static CompletableFuture<Void> updateDB(Context context) {
        return getServerDBETag(context).thenCombineAsync(getLocalDBETag(context), (serverEtag, localEtag) -> {
            if (!serverEtag.equals(localEtag)) { // Do this only when db file is outdated
                NetworkSupport api = NetworkSupport.getInstance(context);
                Map<String, String> headers = new HashMap<String, String>();

                try {
                    headers.put("If-Match", localEtag);
                } catch (Exception e) {
                }

                api.doGet("sync", headers, false, true).thenAcceptAsync((response) -> {
                    // If db file is latest, then abort this
                    if (response.body.subCode.equals("sync.latest")) return;

                    String newDbFileTemporaryName = "newTempFile_" + UUID.randomUUID().toString().substring(0, 8) + ".sqlite";
                    File newDbFile = new File(context.getFilesDir(), newDbFileTemporaryName);
                    try {
                        newDbFile.createNewFile();
                        // Extract DB file from response
                        String newDbFileBase64 = response.body.data.getString("db");
                        byte[] newDbFileData = Base64.decode(newDbFileBase64, Base64.DEFAULT);
                        FileOutputStream newDbFileFis = new FileOutputStream(newDbFile);
                        newDbFileFis.write(newDbFileData);
                        newDbFileFis.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new CompletionException(
                                new APIException(
                                        "exception raised while extracting db from response",
                                        "DB 파일을 갱신하는 중 문제가 발생했습니다.",
                                        -1, null));
                    }

                    File previousDbFile = new File(context.getFilesDir(), "user_db.sqlite");
                    if (previousDbFile.exists()) {
                        previousDbFile.delete();
                    }
                    newDbFile.renameTo(previousDbFile);

                    // Unload DB, Replace db file and reload DB
                    ProfileDatabase.updateDB(); //TODO 테스트 할 것
                });
            }
            return null;
        });
    }

    public static CompletableFuture<APIResponse> recreateDBOnServer(Context context) throws APIException {
        NetworkSupport api = NetworkSupport.getInstance(context);
        return api.doDelete("sync", null, null, false, true);
    }
}
