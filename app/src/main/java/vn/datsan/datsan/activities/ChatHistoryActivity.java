package vn.datsan.datsan.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.datsan.datsan.R;
import vn.datsan.datsan.models.User;
import vn.datsan.datsan.models.chat.Chat;
import vn.datsan.datsan.serverdata.CallBack;
import vn.datsan.datsan.serverdata.UserManager;
import vn.datsan.datsan.serverdata.chat.ChatService;
import vn.datsan.datsan.ui.adapters.DividerItemDecoration;
import vn.datsan.datsan.ui.adapters.FlexListAdapter;
import vn.datsan.datsan.ui.adapters.RecyclerTouchListener;

/**
 * Created by yennguyen on 8/17/16.
 */
public class ChatHistoryActivity extends SimpleActivity {

    FlexListAdapter adapter;
    List<Chat> chatHistory;

    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);
        ButterKnife.bind(this);

        super.initToolBar();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FlexListAdapter();
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (chatHistory == null || chatHistory.isEmpty()) {
                    return;
                }

                // Starting chat
                Intent intent = new Intent(view.getContext(), ChatActivity.class);
                intent.putExtra("chat", chatHistory.get(position));
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        FloatingActionButton createChatBtn = (FloatingActionButton) findViewById(R.id.create);
        createChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create chat
                User currentUser = UserManager.getInstance().getCurrentUser();
                User dummyBuddy = new User();
                dummyBuddy.setId("zdyU0yszf1XRxhx1FyYxWM0ykpl1");
                dummyBuddy.setName("Dummy Buddy");

                Chat chat = ChatService.getInstance().createOneToOneChat(currentUser, dummyBuddy);
                // Starting chat
                Intent intent = new Intent(view.getContext(), ChatActivity.class);
                intent.putExtra("chat", chat);
                startActivity(intent);
            }
        });

        // Load chat history
        populateData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ChatService.getInstance().removeDatabaseRefListeners();
    }


    private void populateData() {
        ChatService.getInstance().loadChatHistory(new CallBack.OnResultReceivedListener() {
            @Override
            public void onResultReceived(Object result) {
                chatHistory = (List<Chat>) result;
                if (chatHistory == null || chatHistory.size() == 0) {
                    return; // No data found
                }

                List<FlexListAdapter.FlexItem> list = new ArrayList<>();
                // Get history in order of latest on top of the list
                for (Chat chat : chatHistory) {
                    String title = chat.getTitle();
                    String content = chat.getLastMessage() == null ? "" : chat.getLastMessage();
                    FlexListAdapter.FlexItem item = adapter.createItem(null, title, content, null);
                    list.add(item);
                }

                adapter.update(list);
                adapter.notifyDataSetChanged();
            }
        });
    }

}
