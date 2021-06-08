package com.aios.jamsession.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.aios.jamsession.R;
import com.aios.jamsession.adapters.PostsAdapter;
import com.aios.jamsession.models.Post;
import com.aios.jamsession.providers.AuthProvider;
import com.aios.jamsession.providers.PostProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class SearchActivity extends AppCompatActivity {

    String mExtraGenre;
    Toolbar mToolbar;
    TextView mTextViewSearchNumber;

    AuthProvider mAuthProvider;
    RecyclerView mRecyclerView;
    PostProvider mPostProvider;
    PostsAdapter mPostAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Instance linear layout for posts
        mRecyclerView = findViewById(R.id.recyclerViewSearch);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SearchActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mTextViewSearchNumber = findViewById(R.id.textViewSearchNumber);

        mExtraGenre = getIntent().getStringExtra("genre");

        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();

    }


    // Get data from database when the activity starts
    @Override
    public void onStart() {
        super.onStart();
        Query query = mPostProvider.getPostsByGenreAndTimestamp(mExtraGenre);
        FirestoreRecyclerOptions<Post> options =
                new FirestoreRecyclerOptions.Builder<Post>()
                        .setQuery(query, Post.class)
                        .build();
        mPostAdapter = new PostsAdapter(options, SearchActivity.this, mTextViewSearchNumber);
        mRecyclerView.setAdapter(mPostAdapter);
        mPostAdapter.startListening();
    }

    // Stop getting data from database
    @Override
    public void onStop() {
        super.onStop();
        mPostAdapter.stopListening();
    }

    /*For the user's id "mExtraIdUser doesn't return null in the back activity function"*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }
}