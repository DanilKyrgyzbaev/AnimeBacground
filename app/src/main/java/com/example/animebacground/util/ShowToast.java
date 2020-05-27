package com.example.animebacground.util;

import android.widget.Toast;


public class ShowToast {
    public static void me(String message){
        Toast.makeText(App.instance, message, Toast.LENGTH_SHORT).show();
    }
}
