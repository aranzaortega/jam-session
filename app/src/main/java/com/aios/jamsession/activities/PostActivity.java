package com.aios.jamsession.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aios.jamsession.R;
import com.aios.jamsession.models.Post;
import com.aios.jamsession.providers.AuthProvider;
import com.aios.jamsession.providers.ImageProvider;
import com.aios.jamsession.providers.PostProvider;
import com.aios.jamsession.utils.FileUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class PostActivity extends AppCompatActivity {

    ImageView mImageViewPost1;
    ImageView mImageViewPost2;
    File mImageFile;
    File mImageFile2;
    Button mPostButton;
    TextInputEditText mTextInputTitle;
    TextInputEditText mTextInputDescription;
    TextInputEditText mTextInputLocation;
    TextInputEditText mTextInputDate;
    TextView mTextViewGenre;
    ImageView mImageViewJazz;
    ImageView mImageViewBlues;
    ImageView mImageViewRock;
    ImageView mImageViewRap;
    AlertDialog mDialog;
    CircleImageView mCircleImageBack;

    private final int GALLERY_REQUEST_CODE = 1;
    private final int GALLERY_REQUEST_CODE_2 = 2;

    ImageProvider mImageProvider;
    PostProvider mPostProvider;
    AuthProvider mAuthProvider;

    String mTitle = "";
    String mDescription = "";
    String mLocation = "";
    String mDate = "";
    String mGenre = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // Instances
        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false)
                .build();

        mImageProvider = new ImageProvider();
        mPostProvider = new PostProvider();
        mAuthProvider = new AuthProvider();

        mImageViewPost1 = findViewById(R.id.imageViewPost1);
        mImageViewPost2 = findViewById(R.id.imageViewPost2);
        mPostButton = findViewById(R.id.postButton);
        mTextInputTitle = findViewById(R.id.textInputTitle);
        mTextInputDescription = findViewById(R.id.textInputDescription);
        mTextInputLocation = findViewById(R.id.textInputLocation);
        mTextInputDate = findViewById(R.id.textInputDate);
        mTextViewGenre = findViewById(R.id.textViewGenre);
        mImageViewJazz = findViewById(R.id.imageViewJazz);
        mImageViewBlues = findViewById(R.id.imageViewBlues);
        mImageViewRock = findViewById(R.id.imageViewRock);
        mImageViewRap = findViewById(R.id.imageViewRap);
        mCircleImageBack = findViewById(R.id.circleImageBack);

        // Events
        mCircleImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPost();
            }
        });
        
        mImageViewPost1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(GALLERY_REQUEST_CODE);
            }
        });

        mImageViewPost2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(GALLERY_REQUEST_CODE_2);
            }
        });

        mImageViewJazz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGenre = "Jazz";
                mTextViewGenre.setText(mGenre);
            }
        });

        mImageViewBlues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGenre = "Blues";
                mTextViewGenre.setText(mGenre);
            }
        });

        mImageViewRock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGenre = "Rock";
                mTextViewGenre.setText(mGenre);
            }
        });

        mImageViewRap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGenre = "Rap";
                mTextViewGenre.setText(mGenre);
            }
        });
    }

    private void clickPost() {
        mTitle = mTextInputTitle.getText().toString();
        mDescription = mTextInputDescription.getText().toString();
        mLocation = mTextInputLocation.getText().toString();
        mDate = mTextInputDate.getText().toString();

        // Validation
        if(!mTitle.isEmpty() && !mDescription.isEmpty() && !mLocation.isEmpty() && !mDate.isEmpty() && !mGenre.isEmpty()){
            if (mImageFile != null){
                saveImage();
            } else {
                Toast.makeText(this, "Por favor, selecciona al menos una imagen", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImage() {
        mDialog.show();
        // IMAGE 1
        mImageProvider.save(PostActivity.this, mImageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();

                            // IMAGE 2
                            mImageProvider.save(PostActivity.this, mImageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage2) {
                                    if (taskImage2.isSuccessful()){
                                        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri2) {
                                                String url2= uri2.toString();
                                                Post post = new Post();
                                                post.setImage(url);
                                                post.setImage2(url2);
                                                post.setTitle(mTitle);
                                                post.setDescription(mDescription);
                                                post.setLocation(mLocation);
                                                post.setDate(mDate);
                                                post.setGenre(mGenre);
                                                post.setIduser(mAuthProvider.getUserId());

                                                mPostProvider.save(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> taskSave) {
                                                        mDialog.dismiss();
                                                        if (taskSave.isSuccessful()){
                                                            clearForm();
                                                            Toast.makeText(PostActivity.this, "La información se almacenó correctamente", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(PostActivity.this, "No se pudo almacenar la información", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    } else {
                                    mDialog.dismiss();
                                    Toast.makeText(PostActivity.this, "Error al almacenar la imagen n2", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                } else {
                    mDialog.dismiss();
                    Toast.makeText(PostActivity.this, "Error al almacenar la imagen n1", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void clearForm() {
        mTextInputTitle.setText("");
        mTextInputDescription.setText("");
        mTextInputLocation.setText("");
        mTextInputDate.setText("");
        mTextViewGenre.setText("GÉNERO MUSICAL");
        mImageViewPost1.setImageResource(R.drawable.ic_image_search);
        mImageViewPost2.setImageResource(R.drawable.ic_image_search);
        mTitle = "";
        mDescription = "";
        mLocation= "";
        mDate = "";
        mGenre = "";
        mImageFile = null;
        mImageFile2 = null;
    }

    private void openGallery(int requestCode) {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        // Opens the gallery
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            try {
                // Select the image from gallery
                mImageFile = FileUtil.from(this, data.getData());
                mImageViewPost1.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));

            } catch (Exception e){
                Log.d("ERROR", "Se produjo un error con la imagen 1" + e.getMessage());
                Toast.makeText(this, "Se produjo un error con la imagen 1" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        if(requestCode == GALLERY_REQUEST_CODE_2 && resultCode == RESULT_OK){
            try {
                // Select the image from gallery
                mImageFile2 = FileUtil.from(this, data.getData());
                mImageViewPost2.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));

            } catch (Exception e){
                Log.d("ERROR", "Se produjo un error con la imagen 2" + e.getMessage());
                Toast.makeText(this, "Se produjo un error con la imagen 2" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}