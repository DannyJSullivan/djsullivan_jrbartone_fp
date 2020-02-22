package com.example.djsullivan_jrbartone_finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TableLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import java.util.LinkedList;
import java.util.List;

public class BookRequest extends AppCompatActivity {

    TableLayout sentRequests, incomingRequests;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_request);
        sentRequests = findViewById(R.id.sentRequests);
        incomingRequests = findViewById(R.id.incomingRequests);

        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");

        getRequests();
    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // docId in form of usernameFrom_usernameTo
    public void getRequests() {
        LinkedList<String> requestsFrom = new LinkedList<>();
        LinkedList<String> requestsTo = new LinkedList<>();

        db.collection("requests")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d("Success!", document.getId() + " => " + document.getData());
                                if (document.getId().contains(username + "_")) {
                                    requestsFrom.add(document.get("isbn").toString());
                                    System.out.println("REQUESTS SENT!!! --> " + document.getId());
                                } else if (document.getId().contains("_" + username)) {
                                    requestsTo.add(document.get("isbn").toString());
                                    System.out.println("REQUEST TO!!! --> " + document.getId());
                                }
                            }
                        }
                    }
                });
    }
}
