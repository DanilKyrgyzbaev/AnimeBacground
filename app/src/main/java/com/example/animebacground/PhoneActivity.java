package com.example.animebacground;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.OnClick;

public class PhoneActivity extends AppCompatActivity {
    @BindView(R.id.phone_number)
     TextInputLayout phonenumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, PhoneActivity.class));
    }


    @OnClick(R.id.buttonRegistration)
    void sendNumber(View v) {
        getUserPhoneNumber();
    }

    private void getUserPhoneNumber() {
        String phoneNumber = phonenumber.getEditText().getText().toString().trim();
        if (phoneNumber.isEmpty() || phoneNumber.length() < 13) {
            phonenumber.setError("Номер должен содержать не менее 13-ти символов");
            return;
        }
        VerifyCodeActivity.start(this, phoneNumber);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}


