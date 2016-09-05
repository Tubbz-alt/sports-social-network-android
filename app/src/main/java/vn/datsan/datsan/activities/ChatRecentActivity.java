package vn.datsan.datsan.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

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
public class ChatRecentActivity extends SimpleActivity {

    FlexListAdapter adapter;
    List<Chat> chatHistory;

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.delete_chat_button) Button deleteChatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_recent);
        ButterKnife.bind(this);

        super.initToolBar();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FlexListAdapter(ChatRecentActivity.this) {

            @Override
            public void setImage(Context context, ImageView imageView, String url) {

            }
        };
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

                final Chat chat = chatHistory.get(position);

                // Show menu pop-up
                PopupMenu popupMenu = new PopupMenu(view.getContext(), deleteChatButton);

                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.menu_context_recent_chat, popupMenu.getMenu());

                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        ChatService.getInstance().deleteChat(chat.getId());
                        return false;
                    }
                });
            }
        }));

        FloatingActionButton createChatBtn = (FloatingActionButton) findViewById(R.id.create);
        createChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create chat
                Intent intent = new Intent(view.getContext(), CreateChatActivity.class);
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

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            UserManager.getInstance().getCurrentUserInfo(new CallBack.OnResultReceivedListener() {
                @Override
                public void onResultReceived(Object result) {
                    UserManager.getInstance().setCurrentUser((User) result);
                }
            });
        } else {
            UserManager.getInstance().setCurrentUser(null);
        }
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
                    String title = chat.getDynamicChatTitle();
                    String content = chat.getLastMessage() == null ? "" : chat.getLastMessage();
                    String date = null;
                    FlexListAdapter.FlexItem item = adapter.createItem(null, title, content, date);
                    list.add(item);
                }

                adapter.update(list);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Back to Home
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}
