package com.aios.jamsession.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aios.jamsession.R;
import com.aios.jamsession.adapters.CommentsAdapter;
import com.aios.jamsession.adapters.SliderAdapter;
import com.aios.jamsession.models.Comment;
import com.aios.jamsession.models.FCMBody;
import com.aios.jamsession.models.FCMResponse;
import com.aios.jamsession.models.SliderItem;
import com.aios.jamsession.providers.AuthProvider;
import com.aios.jamsession.providers.CommentProvider;
import com.aios.jamsession.providers.JoinProvider;
import com.aios.jamsession.providers.NotificationProvider;
import com.aios.jamsession.providers.PostProvider;
import com.aios.jamsession.providers.TokenProvider;
import com.aios.jamsession.providers.UserProvider;
import com.aios.jamsession.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailActivity extends AppCompatActivity {

    SliderView mSliderView;
    SliderAdapter mSliderAdapter;
    List<SliderItem> mSliderItems = new ArrayList<>();
    String mExtraPostId;
    String mIdUser = "";

    RecyclerView mRecyclerViewComments;
    CommentsAdapter mCommentsAdapter;
    TextView mTextViewRelativeTime;
    TextView mTextViewJoiners;

    TextView mTextViewTitle;
    TextView mTextViewDescription;
    TextView mTextViewUsername;
    TextView mTextViewGenre;
    ImageView mImageViewGenre;
    CircleImageView mCircleImageViewProfile;
    Button mButtonShowProfile;
    TextView mTextViewDate;
    TextView mTextViewLocation;
    FloatingActionButton mFloatingActionButtonComments;
    Toolbar mToolbar;

    // Allows us to get data from the database
    PostProvider mPostProvider;
    UserProvider mUserProvider;
    CommentProvider mCommentProvider;
    AuthProvider mAuthProvider;
    JoinProvider mJoinProvider;
    NotificationProvider mNotificationProvider;
    TokenProvider mTokenProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        mPostProvider = new PostProvider();
        mUserProvider = new UserProvider();
        mCommentProvider = new CommentProvider();
        mAuthProvider = new AuthProvider();
        mJoinProvider = new JoinProvider();
        mNotificationProvider = new NotificationProvider();
        mTokenProvider = new TokenProvider();

        mSliderView = findViewById(R.id.imageSlider);
        mExtraPostId = getIntent().getStringExtra("id");
        mRecyclerViewComments = findViewById(R.id.recyclerViewComments);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PostDetailActivity.this);
        mRecyclerViewComments.setLayoutManager(linearLayoutManager);

        mTextViewRelativeTime = findViewById(R.id.textViewRelativeTime);
        mTextViewJoiners = findViewById(R.id.textViewJoiners);

        mTextViewTitle = findViewById(R.id.textViewTitle);
        mTextViewUsername = findViewById(R.id.textViewUsername);
        mTextViewDescription = findViewById(R.id.textViewDescription);
        mTextViewGenre = findViewById(R.id.textViewGenre);
        mTextViewDate = findViewById(R.id.textViewDate);
        mTextViewLocation = findViewById(R.id.textViewLocation);
        mImageViewGenre = findViewById(R.id.imageViewGenre);
        mCircleImageViewProfile = findViewById(R.id.circleImageProfile);
        mButtonShowProfile = findViewById(R.id.buttonShowProfile);
        mFloatingActionButtonComments = findViewById(R.id.floatingActionButtonComments);
        mToolbar = findViewById(R.id.toolBar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mButtonShowProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToShowProfile();
            }
        });

        mFloatingActionButtonComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogComment();
            }
        });

        getPost();
        getNumberJoins();
    }

    private void getNumberJoins() {
        mJoinProvider.getJoinsByPost(mExtraPostId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                int numberJoins = queryDocumentSnapshots.size();
                if(numberJoins == 1){
                    mTextViewJoiners.setText(numberJoins + " Participante");
                } else {
                    mTextViewJoiners.setText(numberJoins + " Participantes");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Query query = mCommentProvider.getCommentsByPost(mExtraPostId);
        FirestoreRecyclerOptions<Comment> options =
                new FirestoreRecyclerOptions.Builder<Comment>()
                        .setQuery(query, Comment.class)
                        .build();
        mCommentsAdapter = new CommentsAdapter(options, PostDetailActivity.this);
        mRecyclerViewComments.setAdapter(mCommentsAdapter);
        mCommentsAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCommentsAdapter.stopListening();
    }

    /* Alert dialog for input comment */
    private void showDialogComment() {
        AlertDialog.Builder alert = new AlertDialog.Builder(PostDetailActivity.this);
        alert.setTitle("COMENTARIO");
        alert.setMessage("Ingresa tu comentario");

        EditText editText = new EditText(PostDetailActivity.this);
        editText.setHint("Texto");

        // Dialog layout
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(36,0,36,36);
        editText.setLayoutParams(params);
        RelativeLayout container = new RelativeLayout(PostDetailActivity.this);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        container.setLayoutParams(relativeParams);
        container.addView(editText);
        alert.setView(container);

        // Dialog button OK, gets users text
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = editText.getText().toString();
                if(!value.isEmpty()){
                    createComment(value);
                } else {
                    Toast.makeText(PostDetailActivity.this, "Debe ingresar el comentario", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Dialog button Cancel
        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Nothing
            }
        });

        alert.show();
    }

    private void createComment(final String value) {
        Comment comment = new Comment();
        comment.setComment(value);
        comment.setIdPost(mExtraPostId);
        comment.setIdUser(mAuthProvider.getUserId());
        comment.setTimestamp(new Date().getTime());
        mCommentProvider.create(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    sendNotification(value);
                    Toast.makeText(PostDetailActivity.this, "Comentario publicado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PostDetailActivity.this, "No se pudo ingresar el comentario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendNotification(final String value) {
        if(mIdUser == null){
            return;
        }
        mTokenProvider.getToken(mIdUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("token")){
                        // ERROR
                        String token = documentSnapshot.getString("token");
                        Map<String, String> data = new HashMap<>();
                        data.put("title", "NUEVO COMENTARIO");
                        data.put("body", value);
                        FCMBody body = new FCMBody(token, "high", "4500s", data);
                        mNotificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                            @Override
                            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                if(response.body() != null){
                                    if (response.body().getSuccess() == 1){
                                        Toast.makeText(PostDetailActivity.this, "La notificación se envió correctamente", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(PostDetailActivity.this, "La notificación no pudo enviarse existosamente", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(PostDetailActivity.this, "La notificación no pudo enviarse porque el comentario es nulo", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<FCMResponse> call, Throwable t) {

                            }
                        });
                    }
                } else {
                    Toast.makeText(PostDetailActivity.this, "Token de notificaciones del usuario no existe", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void goToShowProfile() {
        if(!mIdUser.equals("")){
            Intent intent = new Intent(PostDetailActivity.this, UserProfileActivity.class);
            intent.putExtra("iduser", mIdUser);
            startActivity(intent);
        } else {
            Toast.makeText(this, "El id del usuario aún no carga", Toast.LENGTH_SHORT).show();
        }
    }

    private void instanceSlider(){
        // SLIDER
        mSliderAdapter = new SliderAdapter(PostDetailActivity.this, mSliderItems);
        mSliderView.setSliderAdapter(mSliderAdapter);
        mSliderView.setIndicatorAnimation(IndicatorAnimationType.THIN_WORM);
        mSliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        mSliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        mSliderView.setIndicatorSelectedColor(Color.WHITE);
        mSliderView.setIndicatorUnselectedColor(Color.GRAY);
        mSliderView.setScrollTimeInSec(3);
        mSliderView.setAutoCycle(true);
        mSliderView.startAutoCycle();
    }

    private void getPost(){
        mPostProvider.getPostById(mExtraPostId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("image")){
                        String image = documentSnapshot.getString("image");
                        SliderItem item = new SliderItem();
                        item.setImageURL(image);
                        mSliderItems.add(item);
                    }
                    if(documentSnapshot.contains("image2")){
                        String image2 = documentSnapshot.getString("image2");
                        SliderItem item = new SliderItem();
                        item.setImageURL(image2);
                        mSliderItems.add(item);
                    }
                    if(documentSnapshot.contains("title")){
                        String title = documentSnapshot.getString("title");
                        mTextViewTitle.setText(title.toUpperCase());
                    }
                    if(documentSnapshot.contains("description")){
                        String description = documentSnapshot.getString("description");
                        mTextViewDescription.setText(description);
                    }
                    if(documentSnapshot.contains("genre")){
                        String genre = documentSnapshot.getString("genre");
                        mTextViewGenre.setText(genre);

                        if(genre.equals("JAZZ")){
                            mImageViewGenre.setImageResource(R.drawable.genre_jazz);
                        } else if(genre.equals("BLUES")){
                            mImageViewGenre.setImageResource(R.drawable.genre_blues);
                        } else if(genre.equals("ROCK")){
                            mImageViewGenre.setImageResource(R.drawable.genre_rock);
                        } else if(genre.equals("RAP")){
                            mImageViewGenre.setImageResource(R.drawable.genre_rap);
                        }

                    }
                    if(documentSnapshot.contains("date")){
                        String date = documentSnapshot.getString("date");
                        mTextViewDate.setText(date);
                    }
                    if(documentSnapshot.contains("location")){
                        String location = documentSnapshot.getString("location");
                        mTextViewLocation.setText(location);
                    }
                    if(documentSnapshot.contains("iduser")){
                        mIdUser = documentSnapshot.getString("iduser");
                        getUserInfo(mIdUser);
                    }
                    if(documentSnapshot.contains("timestamp")){
                        long timestamp = documentSnapshot.getLong("timestamp");
                        String relativeTime = RelativeTime.getTimeAgo(timestamp, PostDetailActivity.this);
                        mTextViewRelativeTime.setText(relativeTime);
                    }

                    instanceSlider();
                }
            }
        });
    }

    private void getUserInfo(String iduser) {
        mUserProvider.getUser(iduser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("username")){
                       String username =  documentSnapshot.getString("username");
                       mTextViewUsername.setText(username);
                    }
                    if(documentSnapshot.contains("image_profile")){
                        String imageProfile = documentSnapshot.getString("image_profile");
                        Picasso.with(PostDetailActivity.this).load(imageProfile).into(mCircleImageViewProfile);
                    }
                }
            }
        });
    }
}