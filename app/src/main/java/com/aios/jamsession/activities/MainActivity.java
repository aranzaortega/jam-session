package com.aios.jamsession.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aios.jamsession.R;
import com.aios.jamsession.models.User;
import com.aios.jamsession.providers.AuthProvider;
import com.aios.jamsession.providers.UserProvider;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    // Members
    TextView mTextViewRegister;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    Button mLoginButton;
    SignInButton mLoginGoogleButton;
    private GoogleSignInClient mGoogleSignInClient;
    private final int REQUEST_CODE_GOOGLE = 1;
    AlertDialog mDialog;

    // Providers
    AuthProvider mAuthProvider;
    UserProvider mUserProvider;

    //Methods

    // Activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find and assign the variables that are in the xml script
        mTextViewRegister = findViewById(R.id.textViewRegister);
        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputPassword = findViewById(R.id.textInputPassword);
        mLoginButton = findViewById(R.id.loginButton);
        mLoginGoogleButton = findViewById(R.id.loginGoogleButton);

        // General instances
        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false)
                .build();
        mAuthProvider = new AuthProvider();
        mUserProvider = new UserProvider();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Events
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        mLoginGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();
            }
        });

        mTextViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    // To login in app
    private void login(){
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();

        // Show waiting message
        mDialog.show();

        // When the task is over
        mAuthProvider.login(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // Dismiss waiting message
                mDialog.dismiss();
                if (task.isSuccessful()){
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    // To clear the activity record
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    // Join to home activity
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Las credenciales no son correctas", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // To call the Google account sign in
    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE);
    }

    // Waits till the user makes an action (select their account)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQUEST_CODE_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("ERROR", "Google sign in failed", e);
            }
        }
    }

    // Validate the sign in with Google credentials
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        // Show waiting message
        mDialog.show();

        mAuthProvider.googleLogin(account)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String id = mAuthProvider.getUserId();
                        checkUserExist(id);
                    } else {
                        // Dismiss waiting message
                        mDialog.dismiss();

                        // If sign in fails, display a message to the user.
                        Log.w("ERROR", "signInWithCredential:failure", task.getException());
                        Toast.makeText(MainActivity.this, "No se pudo iniciar sesi??n con Google", Toast.LENGTH_LONG).show();
                    }
                }
            });
    }

    // Check if google user exists in firestore database
    private void checkUserExist(final String id) {
        mUserProvider.getUser(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    // Dismiss dialog message
                    mDialog.dismiss();

                    // Sign in success, update UI with the signed-in user's information
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else {
                    String email = mAuthProvider.getEmail();
                    User user = new User();
                    user.setEmail(email);
                    user.setId(id);

                    // If the task was successful (the data was stored in the database)
                    mUserProvider.create(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               // Dismiss dialog message
                               mDialog.dismiss();

                               if (task.isSuccessful()){
                                    // Sign in success, update UI with the signed-in user's information
                                    Intent intent = new Intent(MainActivity.this, CompleteProfileActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(MainActivity.this, "No se pudo almacenar la informaci??n del usuario", Toast.LENGTH_LONG).show();
                                }
                           }
                       }
                    );
                }
            }
        });
    }

    // Maintain user's session opened
    @Override
    protected void onStart() {
        super.onStart();
        if(mAuthProvider.getUserSession() != null){
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}