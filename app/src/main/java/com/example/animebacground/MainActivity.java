package com.example.animebacground;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main Activity";
    private static final int RC_SIGN_IN = 0;
    private TextView textLogin;
    private TextView name;
    private TextView last_name;
    private ImageView imageView;
    private Button logout;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseDatabase firebaseDatabase;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions mGoogleSignInOptions;
    private TextView textId;


    public static void start(Context context){
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         firebaseAuth = FirebaseAuth.getInstance();
         firebaseFirestore = FirebaseFirestore.getInstance();
         firebaseDatabase = FirebaseDatabase.getInstance();

        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions);


        initAndBuildViews();
        logoutLoginActivity();
        googlegetProfile();
        email();


    }

    private void email(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
                firebaseFirestore.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot d : queryDocumentSnapshots){
                            if (d.getString("email").equals(user.getEmail())){
                                textLogin.setText(d.getString("email"));
                                name.setText(d.getString("fName"));
                                last_name.setText(d.getString("lName"));
                                textId.setText(d.getString("phone"));
                                Glide.with(MainActivity.this).clear(imageView);

                            }
                        }
                    }
                });
        }
    }

    private void googlegetProfile(){
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            name.setText(personName);
            textLogin.setText(personEmail);
            last_name.setText(personFamilyName);
            textId.setText(personId);
            Glide.with(this).load(String.valueOf(personPhoto)).into(imageView);
        }

    }


    private void initAndBuildViews(){
        textLogin = findViewById(R.id.text);
        name = findViewById(R.id.name);
        last_name = findViewById(R.id.last_name);
        logout = findViewById(R.id.logout);
        imageView = findViewById(R.id.image);
        textId = findViewById(R.id.textId);
    }

    public void logoutLoginActivity() {
        if (firebaseAuth != null){
            logout.setOnClickListener(v ->{
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            });
        }
        else {
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        // ...
                        case R.id.logout:
                            signOut();
                            break;
                        // ...
                    }
                }
            });
        }

    }
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this,"Signet out!", Toast.LENGTH_SHORT).show();
                        finish();
                        // ...
                    }
                });
    }
}



