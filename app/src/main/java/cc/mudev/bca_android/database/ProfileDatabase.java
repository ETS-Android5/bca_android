package cc.mudev.bca_android.database;

import android.content.Context;
import android.content.res.AssetManager;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
                TB_PROFILE.class,
                TB_PROFILE_RELATION.class,
                TB_CARD.class,
                TB_CARD_SUBSCRIPTION.class, },
        version = 1,
        exportSchema = false)
public abstract class ProfileDatabase extends RoomDatabase {
    public static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static final String DATABASE_NAME = "user_db.sqlite";

    public abstract ProfileDao profileDao();
    public abstract ProfileRelationDao profileRelationDao();
    public abstract ProfileCardDao cardDao();
    public abstract ProfileCardSubscriptionDao cardSubscriptionDao();

    private static ProfileDatabase INSTANCE;

    public static ProfileDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (ProfileDatabase.class) {
                if (INSTANCE == null) {
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
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ProfileDatabase.class, DATABASE_NAME).allowMainThreadQueries() //TODO 꼭 제거해줄것
                            .createFromFile(dbFile)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // TODO DB 업데이트 싱글톤 제거. 테스트할것
    public static void updateDB(){
        INSTANCE = null;
    }

}
