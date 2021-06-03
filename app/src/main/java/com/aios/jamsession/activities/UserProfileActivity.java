package com.aios.jamsession.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aios.jamsession.R;
import com.aios.jamsession.adapters.MyPostsAdapter;
import com.aios.jamsession.models.Post;
import com.aios.jamsession.providers.AuthProvider;
import com.aios.jamsession.providers.PostProvider;
import com.aios.jamsession.providers.UserProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    MyPostsAdapter mPostAdapter;

    RecyclerView mRecyclerView;
    LinearLayout mLinearLayoutEditProfile;
    TextView mTextViewUsername;
    TextView mTextViewEmail;
    TextView mTextViewPostsNumber;
    ImageView mImageViewCover;
    CircleImageView mCircleImageProfile;
    TextView mTextViewPostsExist;
    Toolbar mToolbar;

    UserProvider mUserProvider;
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;
    String mExtraIdUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mLinearLayoutEditProfile = findViewById(R.id.linearLayoutEditProfile);
        mTextViewUsername = findViewById(R.id.textViewUsername);
        mTextViewEmail = findViewById(R.id.textViewEmail);
        mTextViewPostsNumber = findViewById(R.id.textViewPostsNumber);
        mImageViewCover = findViewById(R.id.imageViewCover);
        mCircleImageProfile = findViewById(R.id.circleImageProfile);
        mTextViewPostsExist = findViewById(R.id.textViewPostsExist);
        mToolbar = findViewById(R.id.toolBar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = findViewById(R.id.recyclerViewMyPosts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UserProfileActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        
        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();

        mExtraIdUser = getIntent().getStringExtra("iduser");

        getUser();
        getPostsNumber();
        checkIfExistPosts();

    }

    private void checkIfExistPosts() {
        mPostProvider.getPostsByUser(mExtraIdUser).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                int numberPosts = value.size();
                if(numberPosts > 0) {
                    mTextViewPostsExist.setText("Publicaciones");
                    mTextViewPostsExist.setTextColor(Color.BLACK);
                } else {
                    mTextViewPostsExist.setText("No hay publicaciones");
                    mTextViewPostsExist.setTextColor(Color.GRAY);
                }
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();

        Query query = mPostProvider.getPostsByUser(mExtraIdUser);
        FirestoreRecyclerOptions<Post> options =
                new FirestoreRecyclerOptions.Builder<Post>()
                        .setQuery(query, Post.class)
                        .build();
        mPostAdapter = new MyPostsAdapter(options, UserProfileActivity.this);
        mRecyclerView.setAdapter(mPostAdapter);
        mPostAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPostAdapter.stopListening();
    }


    private void getPostsNumber(){
        mPostProvider.getPostsByUser(mExtraIdUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberPosts = queryDocumentSnapshots.size();
                // numberPosts += 1;
                mTextViewPostsNumber.setText(String.valueOf(numberPosts));
            }
        });
    }

    private void getUser(){
        mUserProvider.getUser(mExtraIdUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("email")){
                        String email = documentSnapshot.getString("email");
                        mTextViewEmail.setText(email);
                    }
                    if(documentSnapshot.contains("username")){
                        String username = documentSnapshot.getString("username");
                        mTextViewUsername.setText(username);
                    }
                    if(documentSnapshot.contains("image_profile")){
                        String imageProfile = documentSnapshot.getString("image_profile");
                        if(imageProfile != null){
                            if(!imageProfile.isEmpty()){
                                Picasso.with(UserProfileActivity.this).load(imageProfile).into(mCircleImageProfile);
                            }
                        }
                    }
                    if(documentSnapshot.contains("image_cover")){
                        String imageCover = documentSnapshot.getString("image_cover");
                        if(imageCover != null){
                            if(!imageCover.isEmpty()){
                                Picasso.with(UserProfileActivity.this).load(imageCover).into(mImageViewCover);
                            }
                        }
                    }
                }
            }
        });
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