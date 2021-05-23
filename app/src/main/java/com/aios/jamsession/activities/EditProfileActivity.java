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
import android.widget.Toast;

import com.aios.jamsession.R;
import com.aios.jamsession.models.Post;
import com.aios.jamsession.models.User;
import com.aios.jamsession.providers.AuthProvider;
import com.aios.jamsession.providers.ImageProvider;
import com.aios.jamsession.providers.PostProvider;
import com.aios.jamsession.providers.UserProvider;
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

public class EditProfileActivity extends AppCompatActivity {

    CircleImageView mCircleImageViewBack;
    CircleImageView mCircleImageViewProfile;
    ImageView mImageViewCover;
    TextInputEditText mTextInputUsername;
    Button mButtonEditProfile;
    AlertDialog mDialog;
    File mImageFile;
    File mImageFile2;
    private final int GALLERY_REQUEST_CODE_PROFILE = 1;
    private final int GALLERY_REQUEST_CODE_COVER = 2;

    String mUsername = "";

    ImageProvider mImageProvider;
    UserProvider mUserProvider;
    AuthProvider mAuthProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Instances
        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false)
                .build();

        mImageProvider = new ImageProvider();
        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();

        mCircleImageViewBack = findViewById(R.id.circleImageBack);
        mCircleImageViewProfile = findViewById(R.id.circleImageProfile);
        mImageViewCover = findViewById(R.id.imageViewCover);
        mTextInputUsername = findViewById(R.id.textInputUsername);
        mButtonEditProfile = findViewById(R.id.editProfileButton);

        mCircleImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mCircleImageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(GALLERY_REQUEST_CODE_PROFILE);
            }
        });

        mImageViewCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(GALLERY_REQUEST_CODE_COVER);
            }
        });

        mButtonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickEditProfile();
            }
        });

    }

    private void clickEditProfile() {
        mUsername = mTextInputUsername.getText().toString();
        if(!mUsername.isEmpty()){
            if (mImageFile != null){
                saveImage();
            } else {
                Toast.makeText(this, "Por favor, selecciona al menos una imagen", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No deben haber campos vacíos", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImage() {
        mDialog.show();
        // IMAGE 1
            mImageProvider.save(EditProfileActivity.this, mImageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();

                            // IMAGE 2
                            mImageProvider.save(EditProfileActivity.this, mImageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage2) {
                                    if (taskImage2.isSuccessful()){
                                        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri2) {
                                                String url2= uri2.toString();
                                                User user = new User();
                                                user.setUsername(mUsername);
                                                user.setImageProfile(url);
                                                user.setImageCover(url2);
                                                user.setId(mAuthProvider.getUserId());
                                                mUserProvider.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        mDialog.dismiss();
                                                        if(task.isSuccessful()) {
                                                            Toast.makeText(EditProfileActivity.this, "La información se actualizó correctamente", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(EditProfileActivity.this, "La información no se pudo actualizar", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    } else {
                                        mDialog.dismiss();
                                        Toast.makeText(EditProfileActivity.this, "Error al almacenar la imagen n2", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                } else {
                    mDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Error al almacenar la imagen n1", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

        if(requestCode == GALLERY_REQUEST_CODE_PROFILE && resultCode == RESULT_OK){
            try {
                // Select the image from gallery
                mImageFile = FileUtil.from(this, data.getData());
                mCircleImageViewProfile.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));

            } catch (Exception e){
                Log.d("ERROR", "Se produjo un error con la imagen 1" + e.getMessage());
                Toast.makeText(this, "Se produjo un error con la imagen 1" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        if(requestCode == GALLERY_REQUEST_CODE_COVER && resultCode == RESULT_OK){
            try {
                // Select the image from gallery
                mImageFile2 = FileUtil.from(this, data.getData());
                mImageViewCover.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));

            } catch (Exception e){
                Log.d("ERROR", "Se produjo un error con la imagen 2" + e.getMessage());
                Toast.makeText(this, "Se produjo un error con la imagen 2" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}