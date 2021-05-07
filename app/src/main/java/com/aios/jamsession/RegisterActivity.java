package com.aios.jamsession;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class RegisterActivity extends AppCompatActivity {

    // Variables
    TextView mTextViewLogin;
    TextInputEditText mTextInputUsername;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    TextInputEditText mTextInputConfirmPassword;
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
        setContentView(R.layout.activity_register);

        // Instances

        /*
         * Find and assign the variables that are in the xml script
         */
        mTextViewLogin = findViewById(R.id.textViewLogin);
        mTextInputUsername = findViewById(R.id.textInputUsername);
        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputPassword = findViewById(R.id.textInputPassword);
        mTextInputConfirmPassword = findViewById(R.id.textInputConfirmPassword);
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

        mTextViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    /*
     * To register in app
     */
    private void register(){
        String username = mTextInputUsername.getText().toString();
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();
        String confirmPassword = mTextInputConfirmPassword.getText().toString();

        // Validation
        if(!username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()){
            if (isEmailValid(email)) {
                if(password.equals(confirmPassword)){
                    if(password.length() >= 6){
                        createUser(username, email, password);
                    } else {
                        Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Haz insertado todos los campos", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Debes rellenar todos los campos para continuar", Toast.LENGTH_LONG).show();
        }
    }

    /*
     * Validate if it's a valid email with regular expressions
     */
    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /*
     * Connection with database
     */
    private void createUser(String username, String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
            new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        // Get de current user ID
                        String id = mAuth.getCurrentUser().getUid();

                        // Create the document structure and assign value
                        Map<String, Object> map = new HashMap<>();
                        map.put("username", username);
                        map.put("email", email);

                        // Create a document (User) with the current User ID in the Users Collection
                        mFirestore.collection("Users").document(id).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            // Validate if the task is successful
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(RegisterActivity.this, "El usuario se almacenó correctamente en la base de datos", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "El usuario se no almacenó correctamente en la base de datos", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        Toast.makeText(RegisterActivity.this, "Se registró correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "No se pudo registrar correctamente", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        );
    }
}