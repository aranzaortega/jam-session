package com.aios.jamsession.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aios.jamsession.R;
import com.aios.jamsession.models.User;
import com.aios.jamsession.providers.AuthProvider;
import com.aios.jamsession.providers.UserProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {

    // Variables
    TextView mTextViewLogin;
    TextInputEditText mTextInputUsername;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    TextInputEditText mTextInputConfirmPassword;
    Button mRegisterButton;
    AlertDialog mDialog;

    // Providers
    AuthProvider mAuthProvider;
    UserProvider mUserProvider;

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

        // General instances
        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false)
                .build();

        mAuthProvider = new AuthProvider();
        mUserProvider = new UserProvider();

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
        // Show waiting message
        mDialog.show();

        mAuthProvider.register(email, password).addOnCompleteListener(
            new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        // Get de current user ID
                        String id = mAuthProvider.getUserId();

                        User user =  new User();
                        user.setId(id);
                        user.setEmail(email);
                        user.setUsername(username);

                        // Create a document (User) with the current User ID in the Users Collection
                        mUserProvider.create(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            // Validate if the task is successful
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // Dismiss waiting message
                                mDialog.dismiss();

                                if (task.isSuccessful()){
                                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                    // To clear the activity record
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    // Join to home activity
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(RegisterActivity.this, "El usuario se no almacenó correctamente en la base de datos", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        Toast.makeText(RegisterActivity.this, "Se registró correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        // Dismiss waiting message
                        mDialog.dismiss();

                        Toast.makeText(RegisterActivity.this, "No se pudo registrar correctamente", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        );
    }
}