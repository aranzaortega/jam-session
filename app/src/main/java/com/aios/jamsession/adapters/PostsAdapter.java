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
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.aios.jamsession.R;
import com.aios.jamsession.activities.PostDetailActivity;
import com.aios.jamsession.models.Join;
import com.aios.jamsession.models.Post;
import com.aios.jamsession.providers.AuthProvider;
import com.aios.jamsession.providers.JoinProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class PostsAdapter extends FirestoreRecyclerAdapter<Post, PostsAdapter.ViewHolder> {
    // Variables
    Context context;
    JoinProvider mJoinProvider;
    AuthProvider mAuthProvider;
    TextView mTextViewSearchNumber;

    // Constructor for image
    public PostsAdapter(FirestoreRecyclerOptions<Post> options, Context context, TextView textView){
        super(options);
        this.context = context;
        mJoinProvider = new JoinProvider();
        mAuthProvider = new AuthProvider();
        mTextViewSearchNumber = textView;
    }

    /*Required methods from the Recycler Adapter*/

    // Set the content to the card view
    // Controls the insertion of data
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Post post) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String postId = document.getId();

        if(mTextViewSearchNumber != null){
            int numberSearch = getSnapshots().size();
            mTextViewSearchNumber.setText(String.valueOf(numberSearch));
        }

        holder.textViewTitle.setText(post.getTitle().toUpperCase());
        holder.textViewDescription.setText(post.getDescription());
        holder.textViewLocation.setText(post.getLocation());
        holder.textViewDate.setText(post.getDate());

        // Validation for empty image
        try{
        if(post.getImage() != null){
            if (!post.getImage().isEmpty()){
                Picasso.with(context).load(post.getImage()).into(holder.imageViewPost);
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
        holder.imageViewJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Join join = new Join();
                join.setIdUser(mAuthProvider.getUserId());
                join.setIdPost(postId);
                join.setTimestamp(new Date().getTime());
                join(join, holder);
            }
        });

        getNumberJoinsByPost(postId, holder);
        checkIfExistsJoin(postId, mAuthProvider.getUserId(), holder);
    }

    private void getNumberJoinsByPost(String idPost, ViewHolder holder){
        // To get in realtime from database
        mJoinProvider.getJoinsByPost(idPost).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                int numberJoins = value.size();
                holder.textViewJoiners.setText(String.valueOf((numberJoins) + " Participantes"));
            }
        });
    }

    private void join(Join join, ViewHolder holder) {
        mJoinProvider.getJoinByPostAndUser(join.getIdPost(), mAuthProvider.getUserId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberDocuments = queryDocumentSnapshots.size();
                if(numberDocuments > 0){
                    String idJoin = queryDocumentSnapshots.getDocuments().get(0).getId();
                    // If the user doesn't want to join the event anymore
                    holder.imageViewJoin.setImageResource(R.drawable.ic_check_circle_uncheck);
                    mJoinProvider.delete(idJoin);
                } else {
                    // If the user wants to join the event
                    holder.imageViewJoin.setImageResource(R.drawable.ic_check_circle_check);
                    mJoinProvider.create(join);
                }
            }
        });
    }

    private void checkIfExistsJoin(String idUser, String idPost, ViewHolder holder) {
        mJoinProvider.getJoinByPostAndUser(idPost, idUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberDocuments = queryDocumentSnapshots.size();
                // Change color
                if(numberDocuments > 0){
                    holder.imageViewJoin.setImageResource(R.drawable.ic_check_circle_check);
                } else {
                    holder.imageViewJoin.setImageResource(R.drawable.ic_check_circle_uncheck);
                }
            }
        });
    }

    // Here is the instance of the view that we want to use
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_post, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewTitle;
        TextView textViewDescription;
        TextView textViewLocation;
        TextView textViewDate;
        TextView textViewJoin;
        TextView textViewJoiners;
        ImageView imageViewPost;
        ImageView imageViewJoin;
        View viewHolder;

        public ViewHolder(View view){
            super(view);
            textViewTitle = view.findViewById(R.id.textViewTitlePostCard);
            textViewDescription = view.findViewById(R.id.textViewDescriptionPostCard);
            textViewLocation = view.findViewById(R.id.textViewLocationPostCard);
            textViewDate= view.findViewById(R.id.textViewDatePostCard);
            textViewJoin = view.findViewById(R.id.textViewJoin);
            textViewJoiners = view.findViewById(R.id.textViewJoiners);
            imageViewPost = view.findViewById(R.id.imageViewPostCard);
            imageViewJoin = view.findViewById(R.id.imageViewJoin);
            viewHolder = view;
        }
    }

}
