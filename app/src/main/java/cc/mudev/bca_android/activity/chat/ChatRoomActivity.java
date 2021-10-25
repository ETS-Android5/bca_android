package cc.mudev.bca_android.activity.chat;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cc.mudev.bca_android.R;
import cc.mudev.bca_android.adapter.ChatRoomMessageData;
import cc.mudev.bca_android.adapter.ChatRoomMessageGroupAdapter;
import cc.mudev.bca_android.adapter.ChatRoomMessageGroupData;
import cc.mudev.bca_android.database.ChatDatabase;
import cc.mudev.bca_android.database.TB_CHAT_EVENT;
import cc.mudev.bca_android.network.NetworkSupport;
import cc.mudev.bca_android.util.SwipeDismissBaseActivity;

public class ChatRoomActivity extends SwipeDismissBaseActivity {
    public ArrayList<ChatRoomMessageGroupData> messageBubbleData;
    public ChatRoomMessageGroupAdapter chatRoomMessageGroupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        Toolbar toolbar = findViewById(R.id.ac_chatRoom_toolbar);
        toolbar.setNavigationOnClickListener((view) -> finish());

        messageBubbleData = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.ac_chatRoom_chatRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatRoomActivity.this));
        chatRoomMessageGroupAdapter = new ChatRoomMessageGroupAdapter(ChatRoomActivity.this, this.messageBubbleData);
        recyclerView.setAdapter(chatRoomMessageGroupAdapter);
        recyclerView.scrollToPosition(chatRoomMessageGroupAdapter.getItemCount() - 1);

        ArrayList<TB_CHAT_EVENT> dummy = createDummyEvents();
        ImageButton messageSendBtn = findViewById(R.id.ac_chatRoom_messageSendBtn);
        messageSendBtn.setOnClickListener((v) -> {
            if (!dummy.isEmpty()) {
                addChatBubble(dummy.remove(0));
            }
            recyclerView.scrollToPosition(messageBubbleData.size() - 1);
        });
    }

    // Dummy test lines
    private static int TC_VAR = 0;
    private static final ZoneId seoulId = ZoneId.of("Asia/Seoul");
    public static TB_CHAT_EVENT TC(String eventType, String message, int causedByProfileId, int m, int d, int h, int min) {
        TC_VAR++;
        int targetTime = Long.valueOf(ZonedDateTime.of(2020, m, d, h, min, 0, 0, seoulId).toEpochSecond()).intValue();
        return new TB_CHAT_EVENT(TC_VAR, TC_VAR, eventType, -1, message, causedByProfileId, causedByProfileId, "", targetTime, targetTime, 0);
    }
    public ArrayList<TB_CHAT_EVENT> createDummyEvents() {
        final int c = NetworkSupport.getInstance(ChatRoomActivity.this).getCurrentProfileId();
        final int o = 12345;
        final int r = -1;
        final String PIN = "PARTICIPANT_IN",
                POUT = "PARTICIPANT_OUT",
                PKICK = "PARTICIPANT_KICKED",
                MP = "MESSAGE_POSTED",
                MPI = "MESSAGE_POSTED_IMAGE",
                MPD = "MESSAGE_POSTED_DELETED";

        TB_CHAT_EVENT dummy[] = {
                TC(PIN, "김하나 님이 들어왔습니다.", c, 7, 7, 0, 0),
                TC(PIN, "장서희 님이 들어왔습니다.", o, 7, 7, 0, 0),
                TC(MP, "안녕하세요. 디자인 의뢰 신청하려고 합니다.", c, 7, 7, 10, 21),
                TC(MP, "신청 방법은 제 인스타에 나와있으니 확인하시면 됩니다.", o, 7, 7, 10, 21),
                TC(MP, "알겠습니다. 그럼 잠시뒤에 다시 연락드리겠습니다.", c, 7, 7, 10, 22),
                TC(MP, "디자인은 지금 막 메일로 보냈습니다, 그리고 배경은 A-2로 하겠습니다.", c, 7, 7, 10, 30),
                TC(MP, "그리고 문구는 'GoldPecker'입니다.", c, 7, 7, 10, 30),
                TC(MP, "커스텀 디자인이라 추가 비용이 들어가 OO원 위 ㅁㅁ계좌로 입금하시면 됩니다.", o, 7, 7, 10, 32),
                TC(MP, "그리고 서비스 기간이라 위 사진중 하나 무료로 선택하실 수 있습니다.", o, 7, 7, 10, 32),
                TC(MP, "돈 보내실때 계좌명에 추가해서 보내주시면 됩니다.", o, 7, 7, 10, 33),
                TC(MP, "네 알겠습니다.", c, 7, 7, 10, 33),
                TC(MP, "돈 보냈습니다.", c, 7, 7, 10, 38),
                TC(MP, "확인했습니다. 이틀 뒤 결과물 확인 하실 수 있고 1번만 무료 수정 가능합니다.", o, 7, 7, 10, 40),
                TC(MP, "그 후 수정은 추가 비용이 청구됩니다.", o, 7, 7, 10, 40),
                TC(MP, "알겠습니다. 잘 부탁드립니다.", c, 7, 7, 10, 40),
                TC(MP, "메일로 사진을 보내드렸습니다.", o, 7, 9, 9, 20),
                TC(MP, "한번 보시고 수정사항 있으면 말씀해 주시면 감사하겠습니다.", o, 7, 9, 9, 20),
                TC(MP, "없으시면 주소 보내주시면 바로 발송해드리겠습니다.", o, 7, 9, 9, 21),
                TC(MP, "만족스럽게 잘 나왔습니다. 감사합니다.", c, 7, 9, 9, 40),
                TC(MP, "주소는 경기 오산시 한신대길 137 입니다.", c, 7, 9, 9, 40),
                TC(MP, "보냈습니다. 즐거운 하루 되시길 바랍니다. 감사합니다.", o, 7, 9, 9, 41),
        };
        return new ArrayList<>(Arrays.asList(dummy));
    }

    public void loadChatEventsFromDB(int roomId) {
        List<TB_CHAT_EVENT> chatEvents = ChatDatabase.getInstance(ChatRoomActivity.this).eventDao().getAllRoomEvents(roomId);
        for (TB_CHAT_EVENT chatEvent: chatEvents) {
            addChatBubble(chatEvent);
        }
    }

    public void addChatBubble(TB_CHAT_EVENT newChatEvent) {
        int currentProfileId = NetworkSupport.getInstance(ChatRoomActivity.this).getCurrentProfileId();
        if (currentProfileId == -1) {
            // Cannot load current profile. DO NOT OPERATE ANYTHING
            return;
        }

        ZonedDateTime newChatEventTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(newChatEvent.created_at), ZoneOffset.UTC);
        boolean isMyMessage = newChatEvent.caused_by_profile_id == currentProfileId;
        String profileName = null;

        if (this.messageBubbleData.isEmpty()) {
            // ChatRoom data is empty, then we need to add date indicator.
            this.messageBubbleData.add(new ChatRoomMessageGroupData(ChatRoomActivity.this, newChatEventTime));
            chatRoomMessageGroupAdapter.notifyItemInserted(this.messageBubbleData.size() - 1);
        }

        // If event is PARTICIPANT_(IN|OUT|KICKED)
        if (newChatEvent.getEventTypeAsEnum().informationEvent) {
            this.messageBubbleData.add(new ChatRoomMessageGroupData(ChatRoomActivity.this, newChatEvent.message));
            chatRoomMessageGroupAdapter.notifyItemInserted(this.messageBubbleData.size() - 1);
            return;
        } else {
            // TODO: set profile name as this chat event is not a informative event.
            final int c = NetworkSupport.getInstance(ChatRoomActivity.this).getCurrentProfileId();
            final int o = 12345;
            if (newChatEvent.caused_by_profile_id == c)
                profileName = "김하나";
            else
                profileName = "장서희";
        }

        // OK, newChatEvent is message-related thing.
        ChatRoomMessageGroupData lastMessageGroup = this.messageBubbleData.get(this.messageBubbleData.size() - 1);
        ChatRoomMessageData lastMessage = lastMessageGroup.getLastMessage();

        // Add date indicator if last message is not null and last message's day is not equals to new event date.
        if (lastMessage != null && !isSameDay(lastMessage.messageTime, newChatEventTime)) {
            ChatRoomMessageGroupData newTimeIndicator = new ChatRoomMessageGroupData(ChatRoomActivity.this, newChatEventTime);
            this.messageBubbleData.add(newTimeIndicator);
            chatRoomMessageGroupAdapter.notifyItemInserted(this.messageBubbleData.size() - 1);
            lastMessageGroup = newTimeIndicator;
            lastMessage = null;
        }

        // If
        //   - last message is not available,
        //   - last message and new chat event's time differences are not acceptable,
        //   - last message group is information bubble,
        //   - last message group's profile id is not same with new event's profile id,
        // then we need to add new message group.
        if (lastMessage == null
                || !isTimeDifferenceAcceptable(lastMessage.messageTime, newChatEventTime)
                || lastMessageGroup.messageTime != null
                || lastMessageGroup.infoMessage != null
                || lastMessageGroup.profileId != newChatEvent.caused_by_profile_id) {
            ChatRoomMessageGroupData newMessageGroup = new ChatRoomMessageGroupData(
                    ChatRoomActivity.this, isMyMessage, newChatEvent.caused_by_profile_id, profileName, new ArrayList<>());
            this.messageBubbleData.add(newMessageGroup);
            chatRoomMessageGroupAdapter.notifyItemInserted(this.messageBubbleData.size() - 1);
            lastMessageGroup = newMessageGroup;
            lastMessage = null;
        }

        lastMessageGroup.chatRoomChildData.add(new ChatRoomMessageData(isMyMessage, newChatEventTime, newChatEvent.message));
        chatRoomMessageGroupAdapter.notifyItemChanged(this.messageBubbleData.size() - 1);
    }

    public static boolean isSameDay(ZonedDateTime date1, ZonedDateTime date2) {
        return date1.truncatedTo(ChronoUnit.DAYS).equals(date2.truncatedTo(ChronoUnit.DAYS));
    }

    public static boolean isTimeDifferenceAcceptable(ZonedDateTime date1, ZonedDateTime date2) {
        // This static method returns true when date1 and date2's day is same,
        // and both time's differences are on acceptable range.

        // First, check if those times are on same day.
        if (!isSameDay(date1, date2))
            return false;

        // Second, check if those time's differences are on acceptable range.
        final Duration acceptableTimeDifference = Duration.ofMinutes(5);
        Duration timeDifference = Duration.between(date1, date2);
        return timeDifference.compareTo(acceptableTimeDifference) <= 0;
    }
}
