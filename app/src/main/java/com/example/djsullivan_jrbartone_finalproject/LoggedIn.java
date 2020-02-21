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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoggedIn extends AppCompatActivity {

    EditText bookNameField;

    String bookName;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ActionBar actionBar;

    String username;

    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private EditText e;
    private TableLayout results;

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
                        Toast.makeText(LoggedIn.this, "Home",Toast.LENGTH_SHORT).show();break;
                    case R.id.settings:
                        Toast.makeText(LoggedIn.this, "Settings",Toast.LENGTH_SHORT).show();break;
                    case R.id.profile:
                        Toast.makeText(LoggedIn.this, "Profile",Toast.LENGTH_SHORT).show();break;
                    case R.id.add:
                        addBookNoClick();
                        Toast.makeText(LoggedIn.this, "Add",Toast.LENGTH_SHORT).show();break;
                    case R.id.req:
                        Toast.makeText(LoggedIn.this, "Trade",Toast.LENGTH_SHORT).show();break;
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
        Toast.makeText(LoggedIn.this, "search",Toast.LENGTH_SHORT).show();
        bookNameField = findViewById(R.id.bookSearch_plainText);
        bookName = bookNameField.getText().toString();
        cleanTable(results);
        bookQuery(bookName, this);
    }

    public void bookQuery(String bookName, Context context) {
        db.collection("books")
                .orderBy("title")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document: task.getResult()) {
                                if(document.getString("title").toLowerCase().contains(bookName.toLowerCase())) {
                                    Log.d("SUCCESS", document.getId() + " => " + document.getData());
                                    TableRow tbrow = new TableRow(context);
                                    TextView tv = new TextView(context);
                                    tv.setText(document.getString("title"));
                                    tbrow.addView(tv);
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

    public void addBook(View view) {
        Intent intent = new Intent(LoggedIn.this, AddBook.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    public void addBookNoClick() {
        Intent intent = new Intent(LoggedIn.this, AddBook.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
    private void cleanTable(TableLayout table) {

        int childCount = table.getChildCount();

        // Remove all rows except the first one
        if (childCount > 1) {
            table.removeViews(1, childCount - 1);
        }
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
