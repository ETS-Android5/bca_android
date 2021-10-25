package cc.mudev.bca_android.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
                TB_CHAT_ROOM.class,
                TB_CHAT_PARTICIPANT.class,
                TB_CHAT_EVENT.class, },
        version = 1,
        exportSchema = false)
public abstract class ChatDatabase extends RoomDatabase {
    public static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static final String DATABASE_NAME = "user_chat_record_db.sqlite";
    public abstract ChatRoomDao roomDao();
    public abstract ChatParticipantDao participantDao();
    public abstract ChatEventDao eventDao();

    private static volatile ChatDatabase INSTANCE;
    public static ChatDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (ChatDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ChatDatabase.class, DATABASE_NAME).allowMainThreadQueries() //TODO 꼭 제거해줄것
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
