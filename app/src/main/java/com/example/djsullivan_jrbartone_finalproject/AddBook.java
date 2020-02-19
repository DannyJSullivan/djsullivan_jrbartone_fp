package com.example.djsullivan_jrbartone_finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddBook extends AppCompatActivity {

    private ActionBar actionBar;

    EditText mAuthor;
    EditText mTitle;
    EditText mISBN;

    String username;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    boolean bookExists = false;

    String users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        actionBar=getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#C2C0C0")));
        mAuthor = findViewById(R.id.bookAuthor_editText);
        mTitle = findViewById(R.id.bookTitle_editText);
        mISBN = findViewById(R.id.bookISBN_editText);

        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
    }

    public void addBook(View view) {
        String author = mAuthor.getText().toString();
        String title = mTitle.getText().toString();
        String isbn = mISBN.getText().toString();

        db.collection("books").document(isbn).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        Log.d("SUCCESS", "Document exists!");
                        users = document.toString();
                        bookExists = true;
                    }
                    else {
                        Log.d("FAILURE", "Document does not exist!");
                        bookExists = false;
                    }
                }
            }
        });

        // TODO: add users to array
        // TODO: if need to, get all current owners, make an array, then upload that array
        if(bookExists) {
            db.collection("books")
                    .document(isbn)
                    .update("owner", FieldValue.arrayUnion(username));
        }
        else {
            String[] user = new String[1];
            user[0] = username;
            Map<String, Object> book = new HashMap<>();
            book.put("author", author);
            book.put("owner", username);
            book.put("title", title);
            book.put("isbn", isbn);

            db.collection("books")
                    .document(isbn)
                    .set(book);

            Toast.makeText(this, "Book added!", Toast.LENGTH_SHORT);
        }
    }

    // TODO: functionality to add book/pdf
    // test functionality with camera to scan book, automatically add, add ability to confirm infromation is correct first
    // possibly save with docId of ISBN
    public void scanBarcode(View view) {
        
    }
}
