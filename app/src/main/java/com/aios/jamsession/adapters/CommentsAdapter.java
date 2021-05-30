package com.aios.jamsession.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aios.jamsession.R;
import com.aios.jamsession.activities.PostDetailActivity;
import com.aios.jamsession.models.Comment;
import com.aios.jamsession.models.Post;
import com.aios.jamsession.providers.UserProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends FirestoreRecyclerAdapter<Comment, CommentsAdapter.ViewHolder> {
    // Variables
    Context context;
    UserProvider mUserProvider;

    // Constructor for image
    public CommentsAdapter(FirestoreRecyclerOptions<Comment> options, Context context){
        super(options);
        this.context = context;
        mUserProvider = new UserProvider();
    }

    /*Required methods from the Recycler Adapter*/

    // Set the content to the card view
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Comment comment) {
        // Get comment from database
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String commentId = document.getId();
        String userId = document.getString("idUser");

        holder.textViewComment.setText(comment.getComment());
        getUserInfo(userId, holder);

    }

    private void getUserInfo(String idUser, ViewHolder holder){
        mUserProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("username")){
                        String username = documentSnapshot.getString("username");
                        holder.textViewUsername.setText(username);
                    }
                    if(documentSnapshot.contains("image_profile")){
                        String imageProfile = documentSnapshot.getString("image_profile");
                        if(imageProfile != null){
                            if(!imageProfile.isEmpty()){
                                Picasso.with(context).load(imageProfile).into(holder.circleImageViewComment);
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_comment, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewUsername;
        TextView textViewComment;
        CircleImageView circleImageViewComment;
        View viewHolder;

        public ViewHolder(View view){
            super(view);
            textViewUsername = view.findViewById(R.id.textViewUsername);
            textViewComment = view.findViewById(R.id.textViewComment);
            circleImageViewComment = view.findViewById(R.id.circleImageComment);
            viewHolder = view;
        }
    }
}
