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
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class LoggedIn extends AppCompatActivity {

    EditText bookNameField;

    String bookName;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ActionBar actionBar;

    String username;

    boolean isPdf = false;

    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private EditText e;
    private TableLayout results;
    private HashMap<String, Boolean> highlightedItems = new HashMap<String, Boolean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        actionBar=getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#C2C0C0")));
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        results = findViewById(R.id.tableLayout);


        e = (EditText) findViewById(R.id.bookSearch_plainText);
        e.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            for (Map.Entry entry: highlightedItems.entrySet()){
                                if(entry.getValue().equals(true)){
                                    return true;
                                }
                            }
                            searchBookNoClick();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

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
                        break;
                    case R.id.profile:
                        myProfile();
                        break;
                    case R.id.add:
                        addBookNoClick();
                        break;
                    case R.id.req:
                        requests();
                        break;
                    case R.id.logout:
                        logout();
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });
    }

    public void searchBook(View view) {
        bookNameField = findViewById(R.id.bookSearch_plainText);
        bookName = bookNameField.getText().toString();
        bookQuery(bookName, this);
    }

    public void searchBookNoClick() {
        bookNameField = findViewById(R.id.bookSearch_plainText);
        bookName = bookNameField.getText().toString();
        cleanTable(results);
        bookQuery(bookName, this);
    }

    public void bookQuery(String bookName, Context context) {
        findViewById(R.id.send).setVisibility(View.INVISIBLE);

        db.collection("books")
                .orderBy("title")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document: task.getResult()) {
                                if(document.getString("title").toLowerCase().contains(bookName.toLowerCase()) && !document.get("owner").toString().contains(username)) { // add filter here to exclude books owned by you

                                    Log.d("SUCCESS", document.getId() + " => " + document.getData());
                                    TableRow tbrow = new TableRow(context);
                                    TextView tv = new TextView(context);
                                    tv.setText(padAndTrim(document.getString("title")));
                                    tv.setTextSize(20);
                                    tv.setTextColor(0xFFFFFFFF);
                                    tbrow.addView(tv);
                                    tbrow.setClickable(true);
                                    int currColor = tv.getCurrentTextColor();
                                    tbrow.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            if(tv.getCurrentTextColor()==0xFFEF233C){
                                                tbrow.setBackground(new ColorDrawable(0xEF233C));
                                                tv.setTextColor(currColor);
                                                highlightedItems.put(document.getId(),false);
                                                for (Map.Entry entry: highlightedItems.entrySet()){
                                                    if(entry.getValue().equals(true)){
                                                        return;
                                                    }
                                                }
                                                findViewById(R.id.send).setVisibility(View.INVISIBLE);
                                                Log.d("HILI", "UNHILI");


                                            }
                                            else {
                                                tbrow.setBackground(new ColorDrawable(0xFFFFFFFF));
                                                tv.setTextColor(0xFFEF233C);
                                                findViewById(R.id.send).setVisibility(View.VISIBLE);
                                                highlightedItems.put(document.getId(),true);
                                                Log.d("HILI", "HIHI");
                                            }
                                        }
                                    });
                                    results.addView(tbrow);


                                }

                            }
                        }
                        else {
                            Log.d("ERROR", "error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public String padAndTrim(String s){
        int len = 41;
        s = "  " + s;
        if(s.length() > len){
            s = s.substring(0,len - 3) + "...";
        }
        return s;
    }

    public void myProfile() {
        Intent intent = new Intent(LoggedIn.this, MyProfile.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    public void addBook(View view) {
        Intent intent = new Intent(LoggedIn.this, AddBook.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    public void requests() {
        Intent intent = new Intent(LoggedIn.this, BookRequest.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    public void addBookNoClick() {
        Intent intent = new Intent(LoggedIn.this, AddBook.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    public void logout() {
        Intent intent = new Intent(LoggedIn.this, MainActivity.class);
        startActivity(intent);
    }

    private void cleanTable(TableLayout table) {
        int childCount = table.getChildCount();
        if (childCount > 1) {
            table.removeViews(1, childCount - 1);
        }
    }

    public void sendBookRequest(String[] ID){
        Toast.makeText(this,"Book Req ID" + ID, Toast.LENGTH_SHORT).show();
        //TODO: Contact DB with a request (message) for owners of the book
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

    // TODO: check and see transaction between users already exists, if it does, append a new ending to it (i.e. _1 or &1) upwards until doc doesn't exist and can be added
    public void createRequest(View view) {
        Map<String, Object> obj = new HashMap<>();
        for(String isbn: highlightedItems.keySet()) {
            if(!highlightedItems.get(isbn)){
                continue;
            }
            db.collection("books").document(isbn).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    if(task.isSuccessful()) {
                        String strOwners = document.get("owner").toString();
                        String[] cleaned = strOwners.replace("[", "").replace("]", "").replace(" ", "").split(",");
                        obj.put("isbn", isbn);

                        if(document.get("isPdf") != null) {
                            if(document.get("isPdf").toString().equals("true")) {
                                isPdf = true;
                            }
                        }

                        // if it's a pdf, add current user as owner
                        if(isPdf) {
                            db.collection("books")
                                    .document(isbn)
                                    .update("owner", FieldValue.arrayUnion(username));
                            Toast.makeText(getApplicationContext(), "Book is either online or a PDF. You now have access!", Toast.LENGTH_SHORT).show();
                        }
                        // otherwise, send out a request for the book to all owners
                        else {
                            for(int i = 0; i < cleaned.length; i++) {
                                db.collection("requests")
                                        .document(username + "_" + cleaned[i] + "&" + isbn)
                                        .set(obj);

                                System.out.println("ISBN IN OBJ!!!! --> " + obj.get("isbn"));
                                System.out.println("ISBN IN REQUEST!!!! --> " + isbn);
                            }
                            highlightedItems.put(isbn,false);
                            Toast.makeText(getApplicationContext(), "Request(s) sent!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
        searchBookNoClick();
    }
}
