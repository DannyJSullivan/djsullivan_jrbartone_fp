package com.example.djsullivan_jrbartone_finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
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
    private ActionBar actionBar;

    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private EditText e;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_request);
        sentRequests = findViewById(R.id.sentRequests);
        incomingRequests = findViewById(R.id.incomingRequests);

        actionBar=getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#C2C0C0")));

        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");

        dl = (DrawerLayout)findViewById(R.id.activity_logged_in);
        t = new ActionBarDrawerToggle(this, dl,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dl.addDrawerListener(t);
        t.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nv = (NavigationView)findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch(id)
                {
                    case R.id.home:
                        Toast.makeText(BookRequest.this, "Home",Toast.LENGTH_SHORT).show();break;
                    case R.id.settings:
                        Toast.makeText(BookRequest.this, "Settings",Toast.LENGTH_SHORT).show();break;
                    case R.id.profile:
                        Toast.makeText(BookRequest.this, "Profile",Toast.LENGTH_SHORT).show();break;
                    case R.id.add:
                        addBookNoClick();
                        Toast.makeText(BookRequest.this, "Add",Toast.LENGTH_SHORT).show();break;
                    case R.id.req:
                        requests();
                        Toast.makeText(BookRequest.this, "Trade",Toast.LENGTH_SHORT).show();break;
                    default:
                        return true;
                }
                return true;
            }
        });

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

    public void requests() {
        Intent intent = new Intent(BookRequest.this, BookRequest.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    public void addBookNoClick() {
        Intent intent = new Intent(BookRequest.this, AddBook.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (t.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }
}
