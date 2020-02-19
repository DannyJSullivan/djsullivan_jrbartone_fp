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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoggedIn extends AppCompatActivity {

    EditText bookNameField;

    String bookName;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ActionBar actionBar;

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        actionBar=getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#C2C0C0")));

        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
    }

    public void searchBook(View view) {
        bookNameField = findViewById(R.id.bookSearch_plainText);
        bookName = bookNameField.getText().toString();

        bookQuery(bookName);
    }

    public void bookQuery(String bookName) {
        db.collection("books")
                .whereEqualTo("bookName", bookName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document: task.getResult()) {
                                Log.d("SUCCESS", document.getId() + " => " + document.getData());
                            }
                        }
                        else {
                            Log.d("ERROR", "error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void addBook(View view) {
        Intent intent = new Intent(LoggedIn.this, AddBook.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
}
