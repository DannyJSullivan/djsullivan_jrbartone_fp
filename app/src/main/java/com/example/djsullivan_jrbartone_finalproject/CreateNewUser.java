package com.example.djsullivan_jrbartone_finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateNewUser extends AppCompatActivity {
    EditText mUsername;
    EditText mPassword;
    EditText mPassword2;

    private ActionBar actionBar;

    //TODO: secureley store & compare passwords

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_user);
        actionBar=getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#C2C0C0")));
    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    boolean userExists = false;

    public void addUser(String username, String password) {
        Map<String, Object> user = new HashMap<>();
        user.put("username", username.toLowerCase());
        user.put("password", password);

        // first check to see if user exists
        db.collection("users").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        userExists = true;
                        Toast.makeText(getApplicationContext(), "Username already exists! Could not make user!", Toast.LENGTH_SHORT).show();
                        clearPage();
                    }
                    else {
                        db.collection("users")
                                .document(username)
                                .set(user);
                        Toast.makeText(getApplicationContext(), "Account created!", Toast.LENGTH_SHORT).show();
                        toLoginPage();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error! User not added!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void clearPage() {
        mUsername.setText("");
        mPassword.setText("");
        mPassword2.setText("");
    }


    public void makeAccount(View view) {
        mUsername = findViewById(R.id.createAccount_username);
        mPassword = findViewById(R.id.createAccount_password);
        mPassword2 = findViewById(R.id.createAccount_password2);

        String username = mUsername.getText().toString().toLowerCase();
        String password = mPassword.getText().toString();
        String password2 = mPassword2.getText().toString();

        if(password.equals(password2)) {
            addUser(username, password);
        }
        else {
            Toast.makeText(getApplicationContext(), "Passwords do not match! Try again!", Toast.LENGTH_SHORT).show();
        }
    }

    public void toLoginPage() {
        Intent intent = new Intent(CreateNewUser.this, MainActivity.class);
        startActivity(intent);
    }

    public void toLoginPage(View view) {
        Intent intent = new Intent(CreateNewUser.this, MainActivity.class);
        startActivity(intent);
    }
}
