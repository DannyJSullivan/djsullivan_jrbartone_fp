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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddBook extends AppCompatActivity {

    private ActionBar actionBar;

    EditText mAuthor;
    EditText mTitle;
    EditText mISBN;

    String username;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        // checks if book exists
        // if book exists, adds user as an owner
        // if book does not exist, adds the book
        // TODO: possibly add book to users in booksOwned field?
        db.collection("books").document(isbn).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        Log.d("SUCCESS", "FOUND THE DOCUMENT!!!!");
                        db.collection("books")
                                .document(isbn)
                                .update("owner", FieldValue.arrayUnion(username));

                        Toast.makeText(getApplicationContext(), "Book added!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Log.d("FAILURE", "COULD NOT FIND THE DOCUMENT!!!");
                        List<String> user = new ArrayList<String>();
                        user.add(username);
                        Map<String, Object> book = new HashMap<>();
                        book.put("author", author);
                        book.put("owner", user);
                        book.put("title", title);
                        book.put("isbn", isbn);

                        db.collection("books")
                                .document(isbn)
                                .set(book);

                        Toast.makeText(getApplicationContext(), "Book added!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error! Book not added!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // TODO: functionality to add book/pdf
    // test functionality with camera to scan book, automatically add, add ability to confirm infromation is correct first
    // possibly save with docId of ISBN
    public void scanBarcode(View view) {
        
    }
}
