package com.aios.jamsession.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aios.jamsession.R;
import com.aios.jamsession.adapters.SliderAdapter;
import com.aios.jamsession.models.SliderItem;
import com.aios.jamsession.providers.PostProvider;
import com.aios.jamsession.providers.UserProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetailActivity extends AppCompatActivity {

    SliderView mSliderView;
    SliderAdapter mSliderAdapter;
    List<SliderItem> mSliderItems = new ArrayList<>();
    String mExtraPostId;

    TextView mTextViewTitle;
    TextView mTextViewDescription;
    TextView mTextViewUsername;
    TextView mTextViewGenre;
    ImageView mImageViewGenre;
    CircleImageView mCircleImageViewProfile;
    Button mButtonShowProfile;
    TextView mTextViewDate;
    TextView mTextViewLocation;

    // Allows us to get data from the database
    PostProvider mPostProvider;
    UserProvider mUserProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        mPostProvider = new PostProvider();
        mUserProvider = new UserProvider();

        mSliderView = findViewById(R.id.imageSlider);
        mExtraPostId = getIntent().getStringExtra("id");

        mTextViewTitle = findViewById(R.id.textViewTitle);
        mTextViewUsername = findViewById(R.id.textViewUsername);
        mTextViewDescription = findViewById(R.id.textViewDescription);
        mTextViewGenre = findViewById(R.id.textViewGenre);
        mTextViewDate = findViewById(R.id.textViewDate);
        mTextViewLocation = findViewById(R.id.textViewLocation);
        mImageViewGenre = findViewById(R.id.imageViewGenre);
        mCircleImageViewProfile = findViewById(R.id.circleImageProfile);
        mButtonShowProfile = findViewById(R.id.buttonShowProfile);

        getPost();
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
                        String iduser = documentSnapshot.getString("iduser");
                        getUserInfo(iduser);
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