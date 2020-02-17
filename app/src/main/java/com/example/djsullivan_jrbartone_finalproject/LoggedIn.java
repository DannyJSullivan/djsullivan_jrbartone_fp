package com.example.djsullivan_jrbartone_finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class LoggedIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        TextView welcome = findViewById(R.id.loggedInWelcome);

        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("username");

        welcome.setText("Welcome " + message + "!");
    }
}
