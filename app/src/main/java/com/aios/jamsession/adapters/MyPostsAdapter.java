package com.aios.jamsession.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.aios.jamsession.R;
import com.aios.jamsession.activities.PostDetailActivity;
import com.aios.jamsession.models.Join;
import com.aios.jamsession.models.Post;
import com.aios.jamsession.providers.AuthProvider;
import com.aios.jamsession.providers.JoinProvider;
import com.aios.jamsession.providers.PostProvider;
import com.aios.jamsession.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPostsAdapter extends FirestoreRecyclerAdapter<Post, MyPostsAdapter.ViewHolder> {
    // Variables
    Context context;
    JoinProvider mJoinProvider;
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;

    // Constructor for image
    public MyPostsAdapter(FirestoreRecyclerOptions<Post> options, Context context){
        super(options);
        this.context = context;
        mJoinProvider = new JoinProvider();
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();
    }

    /*Required methods from the Recycler Adapter*/

    // Set the content to the card view
    // Controls the insertion of data
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Post post) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String postId = document.getId();
        String relativeTime = RelativeTime.getTimeAgo(post.getTimestamp(), context);

        holder.textViewRelativeTimeMyPost.setText(relativeTime);
        holder.textViewTitleMyPost.setText(post.getTitle().toUpperCase());

        // Icon delete visibility for user's posts
        if(post.getIduser().equals(mAuthProvider.getUserId())){
            holder.imageViewDelete.setVisibility(View.VISIBLE);
        }else{
            holder.imageViewDelete.setVisibility(View.GONE);
        }

        // Validation for empty image
        try{
        if(post.getImage() != null){
            if (!post.getImage().isEmpty()){
                Picasso.with(context).load(post.getImage()).into(holder.circleImageViewMyPost);
            }
        }
        } catch (Exception e){
            Toast.makeText(context, "No second image", Toast.LENGTH_SHORT).show();
        }

        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("id", postId);
                context.startActivity(intent);
            }
        });

        holder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDelete(postId);
            }
        });
    }

    /* Confirm delete post alert configuration */
    private void showConfirmDelete(String postId) {
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Eliminar publicación")
                .setMessage("¿Estás seguro de realizar esta acción?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePost(postId);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deletePost(String postId) {
        mPostProvider.delete(postId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context, "El post se eliminó correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "El post no se eliminó correctamente", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Here is the instance of the view that we want to use
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_my_post, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewTitleMyPost;
        TextView textViewRelativeTimeMyPost;
        CircleImageView circleImageViewMyPost;
        ImageView imageViewDelete;
        View viewHolder;

        public ViewHolder(View view){
            super(view);
            textViewTitleMyPost = view.findViewById(R.id.textViewTitleMyPost);
            textViewRelativeTimeMyPost = view.findViewById(R.id.textViewRelativeTimeMyPost);
            circleImageViewMyPost = view.findViewById(R.id.circleImageMyPost);
            imageViewDelete= view.findViewById(R.id.imageViewDeleteMyPost);
            viewHolder = view;
        }
    }

}
