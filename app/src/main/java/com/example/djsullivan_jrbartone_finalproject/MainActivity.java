package com.example.djsullivan_jrbartone_finalproject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    EditText usernameField;
    EditText passwordField;

    String username;
    String password;

    TextView addNewUser;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        actionBar=getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#C2C0C0")));

        addNewUser = findViewById(R.id.createAccountText);

        passwordField.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            loginAttemptNoClick();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    public void loginAttempt(View view) {
        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        username = usernameField.getText().toString();
        password = passwordField.getText().toString();

//        clearDb();
        if(username.equals("") || password.equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter username and password!", Toast.LENGTH_SHORT).show();
        }
        else {
            userAuth(username.toLowerCase(), password);
        }
    }

    public void loginAttemptNoClick() {
        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        username = usernameField.getText().toString();
        password = passwordField.getText().toString();

//        clearDb();
        if(username.equals("") || password.equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter username and password!", Toast.LENGTH_SHORT).show();
        }
        else {
            userAuth(username.toLowerCase(), password);
        }
    }

    public void userAuth(String username, String password) {
        HashMap<String, Object> users = new HashMap<>();
        // TODO: fix login logic to actually compare elements
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        boolean userExists = false;
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //                                Log.d("Success!", document.getId() + " => " + document.getData());
                                users.put(document.getId(), document.getData());
                                if (document.getData().toString().contains("username=" + username) && document.getData().toString().contains("password=" + password)) {
                                    userExists = true;
                                }
                            }
                        } else {
                            Log.w("ERROR! Users not got!", "Error getting documents.", task.getException());
                        }

                        if (userExists) {
                            Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, LoggedIn.class);
                            intent.putExtra("username", username);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Login failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void createNewAccount(View view) {
        Intent intent = new Intent(MainActivity.this, CreateNewUser.class);
        startActivity(intent);
    }

    public void clearDb() {
        db.clearPersistence();
    }
}
