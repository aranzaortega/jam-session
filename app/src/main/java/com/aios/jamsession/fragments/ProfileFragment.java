package com.aios.jamsession.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aios.jamsession.R;
import com.aios.jamsession.activities.EditProfileActivity;
import com.aios.jamsession.adapters.MyPostsAdapter;
import com.aios.jamsession.adapters.PostsAdapter;
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

public class ProfileFragment extends Fragment {

    MyPostsAdapter mPostAdapter;

    RecyclerView mRecyclerView;
    View mView;
    LinearLayout mLinearLayoutEditProfile;
    TextView mTextViewUsername;
    TextView mTextViewEmail;
    TextView mTextViewPostsNumber;
    ImageView mImageViewCover;
    CircleImageView mCircleImageProfile;
    TextView mTextViewPostsExist;

    UserProvider mUserProvider;
    AuthProvider mAuthProvider;
    PostProvider mPostProvider;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_profile, container, false);

        mLinearLayoutEditProfile = mView.findViewById(R.id.linearLayoutEditProfile);
        mTextViewUsername = mView.findViewById(R.id.textViewUsername);
        mTextViewEmail = mView.findViewById(R.id.textViewEmail);
        mTextViewPostsNumber = mView.findViewById(R.id.textViewPostsNumber);
        mImageViewCover = mView.findViewById(R.id.imageViewCover);
        mCircleImageProfile = mView.findViewById(R.id.circleImageProfile);
        mTextViewPostsExist = mView.findViewById(R.id.textViewPostsExist);

        mRecyclerView = mView.findViewById(R.id.recyclerViewMyPosts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);



        mLinearLayoutEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEditProfile();
            }
        });

        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();

        getUser();
        checkIfExistPosts();
        
        return mView;
    }

    private void checkIfExistPosts() {
        mPostProvider.getPostsByUser(mAuthProvider.getUserId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
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

        Query query = mPostProvider.getPostsByUser(mAuthProvider.getUserId());
        FirestoreRecyclerOptions<Post> options =
                new FirestoreRecyclerOptions.Builder<Post>()
                        .setQuery(query, Post.class)
                        .build();
        mPostAdapter = new MyPostsAdapter(options, getContext());
        mRecyclerView.setAdapter(mPostAdapter);
        mPostAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPostAdapter.stopListening();
    }

    private void goToEditProfile() {
        Intent intent = new Intent(getContext(), EditProfileActivity.class);
        startActivity(intent);
    }

    private void getUser(){
        mUserProvider.getUser(mAuthProvider.getUserId()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                                Picasso.with(getContext()).load(imageProfile).into(mCircleImageProfile);
                            }
                        }
                    }
                    if(documentSnapshot.contains("image_cover")){
                        String imageCover = documentSnapshot.getString("image_cover");
                        if(imageCover != null){
                            if(!imageCover.isEmpty()){
                                Picasso.with(getContext()).load(imageCover).into(mImageViewCover);
                            }
                        }
                    }
                }
            }
        });
    }
}