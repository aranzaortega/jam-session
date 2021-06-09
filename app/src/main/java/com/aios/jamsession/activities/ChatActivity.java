package com.aios.jamsession.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.aios.jamsession.R;
import com.aios.jamsession.models.Chat;
import com.aios.jamsession.providers.ChatProvider;

import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    String mExtraIdUser1;
    String mExtraIdUser2;

    ChatProvider mChatProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mExtraIdUser1 = getIntent().getStringExtra("idUser1");
        mExtraIdUser2 = getIntent().getStringExtra("idUser2");

        mChatProvider = new ChatProvider();

        createChat();
    }

    private void createChat() {
        Chat chat = new Chat();
        chat.setIdUser1(mExtraIdUser1);
        chat.setIdUser2(mExtraIdUser2);
        chat.setWritting(false);
        chat.setTimestamp(new Date().getTime());
        mChatProvider.create(chat);
    }
}