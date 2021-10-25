package cc.mudev.bca_android.activity.debug;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import cc.mudev.bca_android.R;
import cc.mudev.bca_android.dataStorage.SharedPref;
import cc.mudev.bca_android.database.ProfileDatabase;
import cc.mudev.bca_android.network.BCaAPI.AccountAPI;
import cc.mudev.bca_android.network.BCaAPI.ProfileDBSyncAPI;
import cc.mudev.bca_android.network.NetworkSupport;
import cc.mudev.bca_android.service.FCMHandlerService;

public class DebugNetworkActivity extends AppCompatActivity {
    String id, pw, nick, email;

    Button networkPingBtn;
    Button setClearAccountDataBtn, setSignInDataBtn, setSignUpDataBtn;
    Button signUpBtn, signInBtn, signOutBtn, tokenRefreshBtn;
    Button dbGetHashNetworkBtn, dbGetHashLocalBtn, dbSyncBtn, dbLocalResetBtn, dbNetworkResetBtn;
    Button refreshTextBtn;

    TextView ipText, idText, pwText, emailText, nickText;
    TextView refreshTokenText, accessTokenText, csrfTokenText;
    TextView dbLocalHashText, dbOriginHashText;
    TextView fcmTokenText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_network);

        ProfileDatabase.getInstance(DebugNetworkActivity.this);
        NetworkSupport.getInstance(DebugNetworkActivity.this);

        Toolbar toolbar = findViewById(R.id.ac_debugNetwork_toolbar);
        toolbar.setNavigationOnClickListener((view) -> finish());

        ipText = findViewById(R.id.ac_debugNetwork_ipText);
        idText = findViewById(R.id.ac_debugNetwork_idText);
        pwText = findViewById(R.id.ac_debugNetwork_pwText);
        emailText = findViewById(R.id.ac_debugNetwork_emailText);
        nickText = findViewById(R.id.ac_debugNetwork_nickText);

        refreshTokenText = findViewById(R.id.ac_debugNetwork_refreshTokenText);
        accessTokenText = findViewById(R.id.ac_debugNetwork_accessTokenText);
        csrfTokenText = findViewById(R.id.ac_debugNetwork_csrfTokenText);

        dbLocalHashText = findViewById(R.id.ac_debugNetwork_dbLocalHashText);
        dbOriginHashText = findViewById(R.id.ac_debugNetwork_dbOriginHashText);

        fcmTokenText = findViewById(R.id.ac_debugNetwork_fcmTokenText);

        networkPingBtn = findViewById(R.id.ac_debugNetwork_pingBtn);
        signUpBtn = findViewById(R.id.ac_debugNetwork_signUpBtn);
        signInBtn = findViewById(R.id.ac_debugNetwork_signInBtn);
        signOutBtn = findViewById(R.id.ac_debugNetwork_signOutBtn);
        tokenRefreshBtn = findViewById(R.id.ac_debugNetwork_tokenRefreshBtn);

        setClearAccountDataBtn = findViewById(R.id.ac_debugNetwork_setClearAccountDataBtn);
        setSignInDataBtn = findViewById(R.id.ac_debugNetwork_setSignInDataBtn);
        setSignUpDataBtn = findViewById(R.id.ac_debugNetwork_setSignUpDataBtn);

        dbGetHashNetworkBtn = findViewById(R.id.ac_debugNetwork_dbGetHashNetworkBtn);
        dbGetHashLocalBtn = findViewById(R.id.ac_debugNetwork_dbGetHashLocalBtn);
        dbSyncBtn = findViewById(R.id.ac_debugNetwork_dbSyncBtn);
        dbLocalResetBtn = findViewById(R.id.ac_debugNetwork_dbLocalResetBtn);
        dbNetworkResetBtn = findViewById(R.id.ac_debugNetwork_dbNetworkResetBtn);

        refreshTextBtn = findViewById(R.id.ac_debugNetwork_refreshTextBtn);

        this.setOnClickListener(DebugNetworkActivity.this);
        this.updateText(DebugNetworkActivity.this);
    }

    public void setOnClickListener(Context context) {
        refreshTextBtn.setOnClickListener((v) -> updateText(context));

        networkPingBtn.setOnClickListener((v) -> {
            try {
                NetworkSupport networkSupport = NetworkSupport.getInstance(DebugNetworkActivity.this);
                networkSupport.ping()
                        .thenAcceptAsync((response) -> (new Handler(Looper.getMainLooper())).postDelayed(() -> {
                            Toast.makeText(context, "PING 성공", Toast.LENGTH_LONG).show();
                            updateText(context);
                        }, 0))
                        .exceptionally(e -> {
                            e.printStackTrace();
                            (new Handler(Looper.getMainLooper())).postDelayed(() -> {
                                Toast.makeText(context, "PING 실패", Toast.LENGTH_LONG).show();
                                updateText(context);
                            }, 0);
                            return null;
                        });
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "PING 실패 - out", Toast.LENGTH_LONG).show();
                updateText(context);
            }
        });
        signUpBtn.setOnClickListener((v) -> {
            try {
                NetworkSupport networkSupport = NetworkSupport.getInstance(DebugNetworkActivity.this);
                AccountAPI.signup(context, id, pw, nick, email)
                        .thenAcceptAsync((response) -> (new Handler(Looper.getMainLooper())).postDelayed(() -> {
                            Toast.makeText(context, "회원가입 성공", Toast.LENGTH_LONG).show();
                            updateText(context);
                        }, 0))
                        .exceptionally(e -> {
                            e.printStackTrace();
                            (new Handler(Looper.getMainLooper())).postDelayed(() -> {
                                Toast.makeText(context, "회원가입 실패", Toast.LENGTH_LONG).show();
                                updateText(context);
                            }, 0);
                            return null;
                        });
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "회원가입 실패 - out", Toast.LENGTH_LONG).show();
                updateText(context);

            }
        });
        signInBtn.setOnClickListener((v) -> {
            try {
                NetworkSupport networkSupport = NetworkSupport.getInstance(DebugNetworkActivity.this);
                AccountAPI.signin(context, id, pw)
                        .thenAcceptAsync((response) -> (new Handler(Looper.getMainLooper())).postDelayed(() -> {
                            Toast.makeText(context, "로그인 성공", Toast.LENGTH_LONG).show();
                            updateText(context);
                        }, 0))
                        .exceptionally(e -> {
                            e.printStackTrace();
                            (new Handler(Looper.getMainLooper())).postDelayed(() -> {
                                Toast.makeText(context, "로그인 실패", Toast.LENGTH_LONG).show();
                                updateText(context);
                            }, 0);
                            return null;
                        });
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "로그인 실패 - out", Toast.LENGTH_LONG).show();
                updateText(context);
            }
        });
        signOutBtn.setOnClickListener((v) -> {
            try {
                AccountAPI.signout(context)
                        .thenAcceptAsync((response) -> (new Handler(Looper.getMainLooper())).postDelayed(() -> {
                            Toast.makeText(context, "로그아웃 성공", Toast.LENGTH_LONG).show();
                            updateText(context);
                        }, 0))
                        .exceptionally(e -> {
                            e.printStackTrace();
                            (new Handler(Looper.getMainLooper())).postDelayed(() -> {
                                Toast.makeText(context, "로그아웃 실패", Toast.LENGTH_LONG).show();
                                updateText(context);
                            }, 0);
                            return null;
                        });
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "로그아웃 실패 - out", Toast.LENGTH_LONG).show();
                updateText(context);
            }
        });
        tokenRefreshBtn.setOnClickListener((v) -> {
            try {
                AccountAPI.isRefreshSuccess(context)
                        .thenAcceptAsync((success) ->
                                (new Handler(Looper.getMainLooper())).postDelayed(() -> {
                                    Toast.makeText(context, success ? "Access 토큰 갱신 성공" : "Access 토큰 갱신 실패", Toast.LENGTH_LONG).show();
                                    updateText(context);
                                }, 0))
                        .exceptionally(e -> {
                            e.printStackTrace();
                            (new Handler(Looper.getMainLooper())).postDelayed(() -> {
                                Toast.makeText(context, "Access 토큰 갱신 실패", Toast.LENGTH_LONG).show();
                                updateText(context);
                            }, 0);
                            return null;
                        });
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Access 토큰 갱신 실패 - out", Toast.LENGTH_LONG).show();
                updateText(context);
            }
        });

        setClearAccountDataBtn.setOnClickListener((v) -> {
            id = pw = nick = email = null;
            updateText(context);
        });
        setSignInDataBtn.setOnClickListener((v) -> {
            id = "musoftware";
            pw = "qwerty!0";
            nick = null;
            email = null;

            updateText(context);
        });
        setSignUpDataBtn.setOnClickListener((v) -> {
            id = "musoftware";
            pw = "qwerty!0";
            nick = "MUsoftware";
            email = "musoftware@daum.net";

            updateText(context);
        });

        dbGetHashNetworkBtn.setOnClickListener((v) -> updateDBText(context));
        dbGetHashLocalBtn.setOnClickListener((v) -> updateDBText(context));
        dbSyncBtn.setOnClickListener((v) -> {
            try {
                ProfileDBSyncAPI.updateDB(context)
                        .thenAcceptAsync((response) -> (new Handler(Looper.getMainLooper())).postDelayed(() -> {
                            Toast.makeText(context, "DB 갱신 성공", Toast.LENGTH_LONG).show();
                            updateDBText(context);
                        }, 0))
                        .exceptionally(e -> {
                            e.printStackTrace();
                            (new Handler(Looper.getMainLooper())).postDelayed(() -> Toast.makeText(context, "DB 갱신 실패", Toast.LENGTH_LONG).show(), 0);
                            return null;
                        });
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "DB 갱신 실패 - out", Toast.LENGTH_LONG).show();
            }
        });
        dbLocalResetBtn.setOnClickListener((v) -> {
            // Temporary copy db file from asset
            File dbFile = new File(context.getFilesDir(), "user_db.sqlite");
            dbFile.delete();
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

            updateDBText(context);
        });
        dbNetworkResetBtn.setOnClickListener((v) -> {
            try {
                ProfileDBSyncAPI.recreateDBOnServer(DebugNetworkActivity.this)
                        .thenAcceptAsync((response) -> (new Handler(Looper.getMainLooper())).postDelayed(() -> {
                            Toast.makeText(context, "서버 DB 초기화 성공", Toast.LENGTH_LONG).show();
                            updateDBText(context);
                        }, 0))
                        .exceptionally(e -> {
                            e.printStackTrace();
                            (new Handler(Looper.getMainLooper())).postDelayed(() -> Toast.makeText(context, "서버 DB 초기화 실패", Toast.LENGTH_LONG).show(), 0);
                            return null;
                        });
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "서버 DB 초기화 실패 - out", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void updateText(Context context) {
        SharedPref sharedPref = SharedPref.getInstance(context);
        NetworkSupport networkSupport = NetworkSupport.getInstance(DebugNetworkActivity.this);

        String sharedPrefID = sharedPref.getString(SharedPref.SharedPrefKeys.ID);
        String sharedPrefPW = sharedPref.getString(SharedPref.SharedPrefKeys.PASSWORD);
        String sharedPrefEMAIL = sharedPref.getString(SharedPref.SharedPrefKeys.EMAIL);
        String sharedPrefNICK = sharedPref.getString(SharedPref.SharedPrefKeys.NICKNAME);

        String refreshToken = sharedPref.getString(SharedPref.SharedPrefKeys.REFRESH_TOKEN);
        String accessToken = networkSupport.accessToken;
        String csrfToken = networkSupport.csrfToken;

        ipText.setText("IP : " + getLocalIpAddress());
        idText.setText("MEM_ID : " + (id != null ? id : "") + "  |  SHAREDPREF_ID : " + (sharedPrefID != null ? sharedPrefID : ""));
        pwText.setText("MEM_PW : " + (pw != null ? pw : "") + "  |  SHAREDPREF_PW : " + (sharedPrefPW != null ? sharedPrefPW : ""));
        emailText.setText("MEM_EMAIL : " + (email != null ? email : "") + "  |  SHAREDPREF_EMAIL : " + (sharedPrefEMAIL != null ? sharedPrefEMAIL : ""));
        nickText.setText("MEM_NICK : " + (nick != null ? nick : "") + "  |  SHAREDPREF_NICK : " + (sharedPrefNICK != null ? sharedPrefNICK : ""));

        refreshTokenText.setText("REFRESH_TOKEN : " + (refreshToken != null ? refreshToken : ""));
        accessTokenText.setText("ACCESS_TOKEN : " + (accessToken != null ? accessToken : ""));
        csrfTokenText.setText("CSRF_TOKEN : " + (csrfToken != null ? csrfToken : ""));

        fcmTokenText.setText("FCM_TOKEN : " + FCMHandlerService.getToken(context));
    }

    public void updateDBText(Context context) {
        try {
            ProfileDBSyncAPI.getServerDBETag(DebugNetworkActivity.this)
                    .thenAcceptAsync((hash) -> (new Handler(Looper.getMainLooper())).postDelayed(() -> dbOriginHashText.setText("SERVER_DB_HASH : " + hash), 0))
                    .exceptionally(e -> {
                        e.printStackTrace();
                        (new Handler(Looper.getMainLooper())).postDelayed(() -> dbOriginHashText.setText("SERVER_DB_HASH : ERROR WHILE GET"), 0);
                        return null;
                    });
            ProfileDBSyncAPI.getLocalDBETag(context)
                    .thenAcceptAsync((hash) -> (new Handler(Looper.getMainLooper())).postDelayed(() -> dbLocalHashText.setText("LOCAL_DB_HASH : " + hash), 0))
                    .exceptionally(e -> {
                        e.printStackTrace();
                        (new Handler(Looper.getMainLooper())).postDelayed(() -> dbLocalHashText.setText("LOCAL_DB_HASH : ERROR WHILE GET"), 0);
                        return null;
                    });
            updateText(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "OFFLINE";
    }
}
