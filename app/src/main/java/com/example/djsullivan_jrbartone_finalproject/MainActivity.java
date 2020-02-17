package com.example.djsullivan_jrbartone_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private EditText usernameField;
    private EditText passwordField;
    private String username;
    private String password;
    private TextView addNewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addNewUser = findViewById(R.id.createAccountText);
    }

    public void loginAttempt(View view) {
        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        username = usernameField.getText().toString();
        password = passwordField.getText().toString();

//        clearDb();

        userAuth(username.toLowerCase(), password);
    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void userAuth(String username, String password) {
        HashMap<String, Object> users = new HashMap<>();

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
                                if(document.getData().toString().contains("username=" + username) && document.getData().toString().contains("password=" + password)) {
                                    userExists = true;
                                }
                            }
                        } else {
                            Log.w("ERROR! Users not got!", "Error getting documents.", task.getException());
                        }

                        if(userExists) {
                            Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(MainActivity.this, LoggedIn.class);
                            intent.putExtra("username", username);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Login failed!", Toast.LENGTH_LONG).show();
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
