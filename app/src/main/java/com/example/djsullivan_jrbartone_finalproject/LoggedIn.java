package com.example.djsullivan_jrbartone_finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoggedIn extends AppCompatActivity {

    EditText bookNameField;

    String bookName;

    FirebaseFirestore db = FirebaseFirestore.getInstance();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        setTheme(R.style.GoatBooks);

        Bundle bundle = getIntent().getExtras();
        String user = bundle.getString("username");
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
                                Log.d("YEEEEET", document.getId() + " => " + document.getData());
                            }
                        }
                        else {
                            Log.d("ERROR", "error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
