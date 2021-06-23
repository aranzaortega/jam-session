package com.aios.jamsession.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aios.jamsession.R;
import com.aios.jamsession.adapters.MessagesAdapter;
import com.aios.jamsession.adapters.MyPostsAdapter;
import com.aios.jamsession.models.Chat;
import com.aios.jamsession.models.Message;
import com.aios.jamsession.models.Post;
import com.aios.jamsession.providers.AuthProvider;
import com.aios.jamsession.providers.ChatProvider;
import com.aios.jamsession.providers.MessageProvider;
import com.aios.jamsession.providers.UserProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String mExtraIdUser1;
    String mExtraIdUser2;
    String mExtraIdChat;

    View mActionBarView;
    EditText mEditTextMessage;
    CircleImageView mCircleImageViewSendMessage;
    CircleImageView mCircleImageViewProfile;
    TextView mTextViewUsername;
    TextView mTextViewRelativeTime;
    ImageView mImageViewBack;
    RecyclerView mRecyclerViewMessage;
    LinearLayoutManager mLinearLayoutManager;

    MessagesAdapter mAdapter;

    ChatProvider mChatProvider;
    MessageProvider mMessageProvider;
    AuthProvider mAuthProvider;
    UserProvider mUserProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatProvider = new ChatProvider();
        mMessageProvider = new MessageProvider();
        mAuthProvider = new AuthProvider();
        mUserProvider = new UserProvider();

        mExtraIdUser1 = getIntent().getStringExtra("idUser1");
        mExtraIdUser2 = getIntent().getStringExtra("idUser2");
        mExtraIdChat = getIntent().getStringExtra("idChat");

        mEditTextMessage = findViewById(R.id.editTextMessage);
        mCircleImageViewSendMessage = findViewById(R.id.circleImageSendMessage);
        mRecyclerViewMessage = findViewById(R.id.recyclerViewMessage);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        // For the messages to order from the bottom to the top
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerViewMessage.setLayoutManager(mLinearLayoutManager);

        showCustomToolbar(R.layout.custom_chat_toolbar);

        mCircleImageViewSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        checkIfChatExists();
    }

    private void sendMessage() {
        String textMessage = mEditTextMessage.getText().toString();
        if(!textMessage.isEmpty()){
            Message message = new Message();
            message.setIdChat(mExtraIdChat);
            if(mAuthProvider.getUserId().equals(mExtraIdUser1)){
                message.setIdSender(mExtraIdUser1);
                message.setIdReceiver(mExtraIdUser2);
            } else {
                message.setIdSender(mExtraIdUser2);
                message.setIdReceiver(mExtraIdUser1);
            }
            message.setTimestamp(new Date().getTime());
            message.setViewed(false);
            message.setMessage(textMessage);

            mMessageProvider.create(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        mEditTextMessage.setText("");
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ChatActivity.this, "El mensaje no se pudo crear", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showCustomToolbar(int resource) {
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        // Layout casting
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mActionBarView = inflater.inflate(resource, null);
        actionBar.setCustomView(mActionBarView);

        mCircleImageViewProfile = mActionBarView.findViewById(R.id.circleImageProfile);
        mTextViewUsername = mActionBarView.findViewById(R.id.textViewUsername);
        mTextViewRelativeTime = mActionBarView.findViewById(R.id.textViewRelativeTime);
        mImageViewBack = mActionBarView.findViewById(R.id.imageViewBack);

        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getUserInfo();
    }

    private void getUserInfo() {
         String idUserInfo = "";
         if(mAuthProvider.getUserId().equals(mExtraIdUser1)){
            idUserInfo = mExtraIdUser2;
         } else {
             idUserInfo = mExtraIdUser1;
         }
         mUserProvider.getUser(idUserInfo).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
             @Override
             public void onSuccess(DocumentSnapshot documentSnapshot) {
                 if(documentSnapshot.exists()){
                     if(documentSnapshot.contains("username")){
                         String username = documentSnapshot.getString("username");
                         mTextViewUsername.setText(username);
                     }
                     if(documentSnapshot.contains("image_profile")){
                         String imageProfile = documentSnapshot.getString("image_profile");
                         if(imageProfile != null){
                             if(!imageProfile.equals("")){
                                 Picasso.with(ChatActivity.this).load(imageProfile).into(mCircleImageViewProfile);
                             }
                         }
                     }
                 }
             }
         });
    }

    private void checkIfChatExists(){
        mChatProvider.getChatByUser1AndUser2(mExtraIdUser1, mExtraIdUser2).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.size();
                if(size == 0){
                    createChat();
                } else {
                    mExtraIdChat = queryDocumentSnapshots.getDocuments().get(0).getId();
                    getMessagesChat();
                }
            }
        });
    }

    private void createChat() {
        Chat chat = new Chat();
        chat.setIdUser1(mExtraIdUser1);
        chat.setIdUser2(mExtraIdUser2);
        chat.setWriting(false);
        chat.setTimestamp(new Date().getTime());
        chat.setId(mExtraIdUser1 + mExtraIdUser2);
        ArrayList<String> ids = new ArrayList<>();
        ids.add(mExtraIdUser1);
        ids.add(mExtraIdUser2);
        chat.setIds(ids);
        mChatProvider.create(chat);
    }

    private void getMessagesChat(){
        Query query = mMessageProvider.getMessagesByChat(mExtraIdChat);
        FirestoreRecyclerOptions<Message> options =
                new FirestoreRecyclerOptions.Builder<Message>()
                        .setQuery(query, Message.class)
                        .build();
        mAdapter = new MessagesAdapter(options, ChatActivity.this);
        mRecyclerViewMessage.setAdapter(mAdapter);
        mAdapter.startListening(); /*
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int numberMessages = mAdapter.getItemCount();
                int lastMessagePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if(lastMessagePosition == -1 || (positionStart >= (numberMessages - 1 ) && lastMessagePosition == (positionStart - 1))){
                    mRecyclerViewMessage.scrollToPosition(positionStart);
                }
            }
        });*/
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}