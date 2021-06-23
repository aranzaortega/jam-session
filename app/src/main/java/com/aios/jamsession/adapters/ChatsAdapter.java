package com.aios.jamsession.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aios.jamsession.R;
import com.aios.jamsession.activities.ChatActivity;
import com.aios.jamsession.models.Chat;
import com.aios.jamsession.providers.AuthProvider;
import com.aios.jamsession.providers.UserProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends FirestoreRecyclerAdapter<Chat, ChatsAdapter.ViewHolder> {
    // Variables
    Context context;
    UserProvider mUserProvider;
    AuthProvider mAuthProvider;

    // Constructor for image
    public ChatsAdapter(FirestoreRecyclerOptions<Chat> options, Context context){
        super(options);
        this.context = context;
        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();
    }

    /*Required methods from the Recycler Adapter*/

    // Set the content to the card view
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Chat chat) {
        // Get comment from database
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String chatId = document.getId();
        if(mAuthProvider.getUserId().equals(chat.getIdUser1())){
            getUserInfo(chat.getIdUser2(), holder);
        } else{
            getUserInfo(chat.getIdUser1(), holder);
        }

        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChatActivity(chatId, chat.getIdUser1(), chat.getIdUser2());
            }
        });
    }

    private void goToChatActivity(String chatId, String idUser1, String idUser2) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("idChat", chatId);
        intent.putExtra("idUser1", idUser1);
        intent.putExtra("idUser2", idUser2);
        context.startActivity(intent);
    }

    private void getUserInfo(String idUser, ViewHolder holder){
        mUserProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("username")){
                        String username = documentSnapshot.getString("username");
                        holder.textViewUsernameChat.setText(username);
                    }
                    if(documentSnapshot.contains("image_profile")){
                        String imageProfile = documentSnapshot.getString("image_profile");
                        if(imageProfile != null){
                            if(!imageProfile.isEmpty()){
                                Picasso.with(context).load(imageProfile).into(holder.circleImageViewChat);
                            }
                        }
                    }
                }
            }
        });
    }

    // Here is the instance of the view that we want to use
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_chat, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewUsernameChat;
        TextView textViewLastMessage;
        CircleImageView circleImageViewChat;
        View viewHolder;

        public ViewHolder(View view){
            super(view);
            textViewUsernameChat = view.findViewById(R.id.textViewUsernameChat);
            textViewLastMessage = view.findViewById(R.id.textViewLastMessageChat);
            circleImageViewChat = view.findViewById(R.id.circleImageChat);
            viewHolder = view;
        }
    }
}
