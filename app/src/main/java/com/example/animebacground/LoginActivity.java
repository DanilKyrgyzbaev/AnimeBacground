package com.example.animebacground;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;


public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 0;
    private static final String TAG = "GoogleActivity";
    private TextInputLayout email_login;
    private TextInputLayout password_login;
    private Button login_Button;
    private TextView registration;
    private ImageButton image_btn_google;
    private LoginButton image_btn_facebook;
    private ImageButton image_btn_call;
    private ImageButton image_btn_twitter;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInOptions mGoogleSignInOptions;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount mGoogleSignInAccount;
    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initAndBuildViews();
        onClicButton();

        // Configure Google Sign In
        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions);


    }

    private void initAndBuildViews() {
        email_login = findViewById(R.id.email_login);
        password_login = findViewById(R.id.password_login);
        login_Button = findViewById(R.id.login_Button);
        registration = findViewById(R.id.registration);
        image_btn_google = findViewById(R.id.image_btn_google);
        image_btn_call = findViewById(R.id.image_btn_call);
        image_btn_twitter = findViewById(R.id.image_btn_twitter);
        image_btn_facebook = findViewById(R.id.image_btn_facebook);
        callbackManager = CallbackManager.Factory.create();
        firebaseAuth = FirebaseAuth.getInstance();
    }



    private void onClicButton() {
        registration.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            finish();
        });

        image_btn_call.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, PhoneActivity.class)));

        login_Button.setOnClickListener(v -> {
            String emailLogin = Objects.requireNonNull(email_login.getEditText()).getText().toString().trim();
            String passwordLogin = Objects.requireNonNull(password_login.getEditText()).getText().toString().trim();

            if (TextUtils.isEmpty(emailLogin)) {
                email_login.setError("Email is Required.");
                return;
            }

            if (TextUtils.isEmpty(passwordLogin)) {
                password_login.setError("Password is Required.");
                return;
            }

            if (passwordLogin.length() < 6) {
                password_login.setError("Password Must be >= 6 Characters");
                return;
            }

            firebaseAuth.signInWithEmailAndPassword(emailLogin, passwordLogin).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this, "Error ! " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
                finish();
            });



        });

        image_btn_google.setOnClickListener(v -> {
            switch (v.getId()) {
                case R.id.image_btn_google:
                    signIn();
                    break;
                // ...
            }
        });

        image_btn_facebook.setReadPermissions("email","public_profile");
        image_btn_facebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);



        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }


    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }

                        // ...
                    }
                });
    }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));

                        }

                        // ...
                    }
                });
    }


    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}
