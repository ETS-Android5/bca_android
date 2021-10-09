package cc.mudev.bca_alter.activity.chat;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import cc.mudev.bca_alter.R;
import cc.mudev.bca_alter.adapter.ChatRoomMessageData;
import cc.mudev.bca_alter.adapter.ChatRoomMessageGroupAdapter;
import cc.mudev.bca_alter.adapter.ChatRoomMessageGroupData;
import cc.mudev.bca_alter.util.SwipeDismissBaseActivity;

public class ChatRoomActivity extends SwipeDismissBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        Toolbar toolbar = findViewById(R.id.ac_chatRoom_toolbar);
        toolbar.setNavigationOnClickListener(new Toolbar.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ArrayList<ChatRoomMessageGroupData> messageData = new ArrayList<>();
        messageData.add(new ChatRoomMessageGroupData(
                getApplicationContext(),
                ZonedDateTime.of(2020, 7, 7, 0, 0, 0, 0, ZoneId.of("Asia/Seoul"))));

        messageData.add(new ChatRoomMessageGroupData(
                getApplicationContext(),
                true,
                "김하나",
                new ArrayList<ChatRoomMessageData>(Arrays.asList(
                        new ChatRoomMessageData(
                                true,
                                ZonedDateTime.of(2020, 7, 7, 10, 21, 0, 0, ZoneId.of("Asia/Seoul")),
                                "안녕하세요. 디자인 의뢰 신청하려고 합니다.")))));
        messageData.add(new ChatRoomMessageGroupData(
                getApplicationContext(),
                false,
                "장서희",
                new ArrayList<ChatRoomMessageData>(Arrays.asList(
                        new ChatRoomMessageData(
                                false,
                                ZonedDateTime.of(2020, 7, 7, 10, 21, 0, 0, ZoneId.of("Asia/Seoul")),
                                "신청 방법은 제 인스타에 나와있으니 확인하시면 됩니다.")))));
        messageData.add(new ChatRoomMessageGroupData(
                getApplicationContext(),
                true,
                "김하나",
                new ArrayList<ChatRoomMessageData>(Arrays.asList(
                        new ChatRoomMessageData(
                                true,
                                ZonedDateTime.of(2020, 7, 7, 10, 22, 0, 0, ZoneId.of("Asia/Seoul")),
                                "알겠습니다. 그럼 잠시뒤에 다시 연락드리겠습니다."),
                        new ChatRoomMessageData(
                                true,
                                ZonedDateTime.of(2020, 7, 7, 10, 30, 0, 0, ZoneId.of("Asia/Seoul")),
                                "디자인은 지금 막 메일로 보냈습니다, 그리고 배경은 A-2로 하겠습니다."),
                        new ChatRoomMessageData(
                                true,
                                ZonedDateTime.of(2020, 7, 7, 10, 30, 0, 0, ZoneId.of("Asia/Seoul")),
                                "그리고 문구는 'goldpecker'입니다.")
                        ))));
        messageData.add(new ChatRoomMessageGroupData(
                getApplicationContext(),
                false,
                "장서희",
                new ArrayList<ChatRoomMessageData>(Arrays.asList(
                        new ChatRoomMessageData(
                                false,
                                ZonedDateTime.of(2020, 7, 7, 10, 32, 0, 0, ZoneId.of("Asia/Seoul")),
                                "커스텀 디자인이라 추가 비용이 들어가 OO원 위 ㅁㅁ계좌로 입금하시면 됩니다."),
                        new ChatRoomMessageData(
                                false,
                                ZonedDateTime.of(2020, 7, 7, 10, 32, 0, 0, ZoneId.of("Asia/Seoul")),
                                "그리고 서비스 기간이라 위 사진중 하나 무료로 선택하실 수 있습니다."),
                        new ChatRoomMessageData(
                                false,
                                ZonedDateTime.of(2020, 7, 7, 10, 33, 0, 0, ZoneId.of("Asia/Seoul")),
                                "돈 보내실때 계좌명에 추가해서 보내주시면 됩니다.")
                        ))));
        messageData.add(new ChatRoomMessageGroupData(
                getApplicationContext(),
                true,
                "김하나",
                new ArrayList<ChatRoomMessageData>(Arrays.asList(
                        new ChatRoomMessageData(
                                true,
                                ZonedDateTime.of(2020, 7, 7, 10, 33, 0, 0, ZoneId.of("Asia/Seoul")),
                                "네 알겠습니다."),
                        new ChatRoomMessageData(
                                true,
                                ZonedDateTime.of(2020, 7, 7, 10, 38, 0, 0, ZoneId.of("Asia/Seoul")),
                                "돈 보냈습니다.")
                ))));
        messageData.add(new ChatRoomMessageGroupData(
                getApplicationContext(),
                false,
                "장서희",
                new ArrayList<ChatRoomMessageData>(Arrays.asList(
                        new ChatRoomMessageData(
                                false,
                                ZonedDateTime.of(2020, 7, 7, 10, 40, 0, 0, ZoneId.of("Asia/Seoul")),
                                "확인했습니다. 이틀 뒤 결과물 확인 하실 수 있고 1번만 무료 수정 가능합니다."),
                        new ChatRoomMessageData(
                                false,
                                ZonedDateTime.of(2020, 7, 7, 10, 40, 0, 0, ZoneId.of("Asia/Seoul")),
                                "그 후 수정은 추가 비용이 청구됩니다.")
                ))));
        messageData.add(new ChatRoomMessageGroupData(
                getApplicationContext(),
                true,
                "김하나",
                new ArrayList<ChatRoomMessageData>(Arrays.asList(
                        new ChatRoomMessageData(
                                true,
                                ZonedDateTime.of(2020, 7, 7, 10, 40, 0, 0, ZoneId.of("Asia/Seoul")),
                                "알겠습니다. 잘 부탁드립니다.")))));

        messageData.add(new ChatRoomMessageGroupData(
                getApplicationContext(),
                ZonedDateTime.of(2020, 7, 9, 0, 0, 0, 0, ZoneId.of("Asia/Seoul"))));

        messageData.add(new ChatRoomMessageGroupData(
                getApplicationContext(),
                false,
                "장서희",
                new ArrayList<ChatRoomMessageData>(Arrays.asList(
                        new ChatRoomMessageData(
                                false,
                                ZonedDateTime.of(2020, 7, 9, 9, 20, 0, 0, ZoneId.of("Asia/Seoul")),
                                "메일로 사진을 보내드렸습니다."),
                        new ChatRoomMessageData(
                                false,
                                ZonedDateTime.of(2020, 7, 9, 9, 20, 0, 0, ZoneId.of("Asia/Seoul")),
                                "한번 보시고 수정사항 있으면 말씀해 주시면 감사하겠습니다."),
                        new ChatRoomMessageData(
                                false,
                                ZonedDateTime.of(2020, 7, 9, 9, 20, 0, 0, ZoneId.of("Asia/Seoul")),
                                "없으시면 주소 보내주시면 바로 발송해드리겠습니다.")
                ))));
        messageData.add(new ChatRoomMessageGroupData(
                getApplicationContext(),
                true,
                "김하나",
                new ArrayList<ChatRoomMessageData>(Arrays.asList(
                        new ChatRoomMessageData(
                                true,
                                ZonedDateTime.of(2020, 7, 9, 9, 40, 0, 0, ZoneId.of("Asia/Seoul")),
                                "만족스럽게 잘 나왔습니다. 감사합니다."),
                        new ChatRoomMessageData(
                                true,
                                ZonedDateTime.of(2020, 7, 9, 9, 40, 0, 0, ZoneId.of("Asia/Seoul")),
                                "주소는 경기 오산시 한신대길 137 입니다.")
                        ))));
        messageData.add(new ChatRoomMessageGroupData(
                getApplicationContext(),
                false,
                "장서희",
                new ArrayList<ChatRoomMessageData>(Arrays.asList(
                        new ChatRoomMessageData(
                                false,
                                ZonedDateTime.of(2020, 7, 9, 9, 41, 0, 0, ZoneId.of("Asia/Seoul")),
                                "보냈습니다. 즐거운 하루 되시길 바랍니다. 감사합니다.")))));

        RecyclerView recyclerView = findViewById(R.id.ac_chatRoom_chatRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ChatRoomMessageGroupAdapter adapter = new ChatRoomMessageGroupAdapter(getApplicationContext(), messageData);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
    }
}
