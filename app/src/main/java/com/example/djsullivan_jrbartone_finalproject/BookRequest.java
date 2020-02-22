package com.example.djsullivan_jrbartone_finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import java.security.spec.ECField;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BookRequest extends AppCompatActivity {

    TableLayout sentRequests, incomingRequests;
    String username;
    private ActionBar actionBar;

    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private EditText e;
    String title = "";

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
                        goHome();
                        Toast.makeText(BookRequest.this, "Home",Toast.LENGTH_SHORT).show();break;
                    case R.id.settings:
                        Toast.makeText(BookRequest.this, "Settings",Toast.LENGTH_SHORT).show();break;
                    case R.id.profile:
                        myProfile();
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

        getRequests(this);
    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // docId in form of usernameFrom_usernameTo
    public void getRequests(Context context) {
        LinkedList<String> requestsFrom = new LinkedList<>();
        LinkedList<String> requestsTo = new LinkedList<>();
        TableLayout sent = findViewById(R.id.sentRequests);
        TableLayout incoming = findViewById(R.id.incomingRequests);

        db.collection("requests")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d("Success!", document.getId() + " => " + document.getData());
                                if (document.getId().contains(username + "_")) {
                                    isbnToTitle(document.get("isbn").toString());
                                    requestsFrom.add(document.get("isbn").toString());
                                    System.out.println("REQUESTS SENT!!! --> " + document.getId());
                                    TableRow tbrow = new TableRow(context);
                                    TextView tv = new TextView(context);
                                    tv.setText("Asking " +
                                            document.getId().toString().substring(document.getId().toString().indexOf("_") + 1,document.getId().toString().length() - 1)
                                            + " for "
                                            +  padAndTrim(title));
                                    tv.setTextSize(20);
                                    tbrow.addView(tv);
                                    tbrow.setClickable(true);
                                    sent.addView(tbrow);

                                } else if (document.getId().contains("_" + username)) {
                                    isbnToTitle(document.get("isbn").toString());
                                    requestsTo.add(document.get("isbn").toString());
                                    System.out.println("REQUEST TO!!! --> " + document.getId());
                                    requestsFrom.add(document.get("isbn").toString());
                                    System.out.println("REQUESTS SENT!!! --> " + document.getId());
                                    TableRow tbrow = new TableRow(context);
                                    TextView tv = new TextView(context);
                                    //TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT);
                                    //tbrow.setLayoutParams(tableRowParams);
                                    ImageButton accept = new ImageButton(context);
                                    ImageButton deny = new ImageButton(context);
                                    accept.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_black_24dp));
                                    deny.setImageDrawable(getResources().getDrawable(R.drawable.ic_close_black_24dp));
                                    accept.setBackground(new ColorDrawable(0x00000000));
                                    deny.setBackground(new ColorDrawable(0x00000000));
                                    accept.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            Toast.makeText(BookRequest.this, "Req Accepted",Toast.LENGTH_SHORT).show();
                                            View row = (View) v.getParent();
                                            ViewGroup container = ((ViewGroup)row.getParent());
                                            container.removeView(row);
                                            container.invalidate();
                                        }
                                    });
                                    deny.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            Toast.makeText(BookRequest.this, "Req Denied",Toast.LENGTH_SHORT).show();
                                            View row = (View) v.getParent();
                                            ViewGroup container = ((ViewGroup)row.getParent());
                                            container.removeView(row);
                                            container.invalidate();
                                        }
                                    });
                                    tv.setText(
                                            document.getId().toString().substring(0,document.getId().toString().indexOf("_"))
                                            + " wants "
                                            + padAndTrim(title) + "      ");
                                    tv.setTextSize(20);
                                    tv.setLayoutParams(new TableRow.LayoutParams(
                                            TableRow.LayoutParams.MATCH_PARENT,
                                            TableRow.LayoutParams.MATCH_PARENT, 0.8f));
                                    tbrow.addView(tv);
                                    tbrow.addView(accept);
                                    tbrow.addView(deny);
                                    ((TableRow.MarginLayoutParams) accept.getLayoutParams()).rightMargin = 16;

                                    ((TableRow.MarginLayoutParams) deny.getLayoutParams()).rightMargin = 16;

                                    tbrow.setClickable(true);
                                    incoming.addView(tbrow);
                                }
                            }
                        }
                    }
                });
    }

    public String padAndTrim(String s){
        int len = 30;
        if(s.length() > len){
            s = s.substring(0,len - 3) + "...";
        }
        return s;
    }


    public void myProfile() {
        Intent intent = new Intent(BookRequest.this, MyProfile.class);
        intent.putExtra("username", username);
        startActivity(intent);
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

    public void goHome() {
        Intent intent = new Intent(BookRequest.this, LoggedIn.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }


    public void isbnToTitle(String isbn){
        db.collection("books")
                .whereEqualTo("isbn", isbn)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                title = document.get("title").toString();
                            }
                        } else {
                            Log.d("FUCK", "Error getting documents: ", task.getException());
                        }
                    }
                });
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
