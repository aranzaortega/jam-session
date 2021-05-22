package com.aios.jamsession.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aios.jamsession.R;
import com.aios.jamsession.models.Post;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class PostsAdapter extends FirestoreRecyclerAdapter<Post, PostsAdapter.ViewHolder> {
    // Variables
    Context context;

    // Constructor for image
    public PostsAdapter(FirestoreRecyclerOptions<Post> options, Context context){
        super(options);
        this.context = context;
    }

    /*Required methods from the Recycler Adapter*/

    // Set the content to the card view
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Post post) {
        holder.textViewTitle.setText(post.getTitle());
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
            Toast.makeText(context, "No image", Toast.LENGTH_SHORT).show();
        }
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
        ImageView imageViewPost;

        public ViewHolder(View view){
            super(view);
            textViewTitle = view.findViewById(R.id.textViewTitlePostCard);
            textViewDescription = view.findViewById(R.id.textViewDescriptionPostCard);
            textViewLocation = view.findViewById(R.id.textViewLocationPostCard);
            textViewDate= view.findViewById(R.id.textViewDatePostCard);
            imageViewPost = view.findViewById(R.id.imageViewPostCard);
        }
    }
}
