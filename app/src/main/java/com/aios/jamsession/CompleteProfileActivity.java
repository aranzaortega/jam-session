package com.aios.jamsession;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompleteProfileActivity extends AppCompatActivity {

    // Variables
    TextInputEditText mTextInputUsername;
    Button mRegisterButton;
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;

    // Methods

    /*
     * When activity is created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        // Instances

        /*
         * Find and assign the variables that are in the xml script
         */
        mTextInputUsername = findViewById(R.id.textInputUsername);
        mRegisterButton = findViewById(R.id.registerButton);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        // Events
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    /*
     * To register in app
     */
    private void register(){
        String username = mTextInputUsername.getText().toString();

        // Validation
        if(!username.isEmpty() ){
            updateUser(username);
        } else {
            Toast.makeText(this, "Debes rellenar todos los campos para continuar", Toast.LENGTH_LONG).show();
        }
    }


    /*
     * Connection with database
     */
    private void updateUser(final String username){
        // Get de current user ID
        String id = mAuth.getCurrentUser().getUid();

        // Create the document structure and assign value
        Map<String, Object> map = new HashMap<>();
        map.put("username", username);

        // Create a document (User) with the current User ID in the Users Collection
        mFirestore.collection("Users").document(id).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            // Validate if the task is successful
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(CompleteProfileActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(CompleteProfileActivity.this, "El usuario se no almacenó correctamente en la base de datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}