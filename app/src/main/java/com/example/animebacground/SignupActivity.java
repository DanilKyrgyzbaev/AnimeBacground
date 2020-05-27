package com.example.animebacground;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity  {
    public static final String TAG = "TAG";
    private TextInputLayout first_name;
    private TextInputLayout last_name;
    private TextInputLayout email;
    private TextInputLayout phone_number;
    private TextInputLayout password;
    private TextInputLayout confirm_Password;
    private Button btn_signup;
    private TextView link_login;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseDatabase db;
    String userID;


    public static void start(Context context){
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        db = FirebaseDatabase.getInstance();
        initAndBuildViews();
        onClicListner();

        if(firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
    }

    private void initAndBuildViews(){
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        email = findViewById(R.id.email);
        phone_number = findViewById(R.id.phone_number);
        password = findViewById(R.id.password);
        confirm_Password = findViewById(R.id.confirm_Password);
        btn_signup = findViewById(R.id.btn_signup);
        link_login = findViewById(R.id.link_login);
    }

    private void onClicListner(){

        link_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this,LoginActivity.class));
                finish();
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String firstName = first_name.getEditText().getText().toString().trim();
                final String lastName = last_name.getEditText().getText().toString().trim();
                final String mEmail = email.getEditText().getText().toString().trim();
                final String phoneNumber = phone_number.getEditText().getText().toString().trim();
                String mPassword = password.getEditText().getText().toString().trim();
                String confirmPassword = confirm_Password.getEditText().getText().toString().trim();

                if (firstName.length() < 3 ){
                    first_name.setError("Fill in the name");
                    return;
                }
                if (lastName.length() < 4){
                    last_name.setError("Fill in the last name");
                    return;
                }
                if (TextUtils.isEmpty(mEmail)){
                    email.setError("Email is Required");
                    return;
                }

                if (phoneNumber.length()< 8){
                    phone_number.setError("Fill in the Phone number");
                    return;
                }

                if (TextUtils.isEmpty(mPassword)){
                    password.setError("Fill in the Password");
                    return;
                }

                if (TextUtils.isEmpty(confirmPassword)){
                    confirm_Password.setError("Fill in the Confirm Password");
                    return;
                }


                firebaseAuth.createUserWithEmailAndPassword(mEmail,mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SignupActivity.this, "User Created.", Toast.LENGTH_SHORT).show();
                            userID = firebaseAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("fName",firstName);
                            user.put("lName",lastName);
                            user.put("email",mEmail);
                            user.put("phone",phoneNumber);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));

                        }else {
                            Toast.makeText(SignupActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

    }

}

