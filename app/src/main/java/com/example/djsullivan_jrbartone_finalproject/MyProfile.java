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
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseAppLifecycleListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MyProfile extends AppCompatActivity {

    EditText bookNameField;
    String bookName;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ActionBar actionBar;
    String username;
    String url;
    String email;
    String isbn;
    boolean isPdf = false;
    boolean reqAccepted = false;

    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private EditText e;
    private TableLayout results;
    private HashMap<String, Boolean> highlightedItems = new HashMap<String, Boolean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        actionBar=getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#C2C0C0")));
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        results = findViewById(R.id.myBookTable);
        dl = (DrawerLayout)findViewById(R.id.activity_my_profile);
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
                    case R.id.logout:
                        logout();
                    case R.id.profile:
                        break;
                    case R.id.add:
                        addBookNoClick();
                    case R.id.req:
                        requests();
                    default:
                        return true;
                }
                return true;
            }
        });
        initTable(this);
    }

    public void goHome() {
        Intent intent = new Intent(MyProfile.this, LoggedIn.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    public void initTable(Context context) {
        db.collection("books")
                .orderBy("title")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document: task.getResult()) {
                                isPdf = false;
                                if(document.get("owner").toString().contains(username)) { // add filter here to exclude books owned by you
                                    if(document.get("isPdf") != null && document.get("url") != null) {
                                        if(document.get("isPdf").toString().equals("true")) {
                                            isPdf = true;
                                            url = document.get("url").toString();
                                        }
                                    }

                                    Log.d("SUCCESS", document.getId() + " => " + document.getData());
                                    TableRow tbrow = new TableRow(context);
                                    TableRow.LayoutParams trlp = new TableRow.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    tbrow.setLayoutParams(trlp);
                                    TextView tv = new TextView(context);
                                    tv.setTextColor(0xFFFFFFFF);
                                    tv.setText(padAndTrim(document.getString("title")));
                                    tv.setTextSize(20);
                                    tv.setLayoutParams(new TableRow.LayoutParams(
                                            TableRow.LayoutParams.MATCH_PARENT,
                                            TableRow.LayoutParams.MATCH_PARENT, 0.9f));
                                    tbrow.addView(tv);

                                    if(isPdf) {
                                        ImageButton link = new ImageButton(context);
                                        link.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_share));
                                        link.setBackground(new ColorDrawable(0x00000000));
                                        link.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View v) {
                                                url = document.get("url").toString();
                                                Intent intent = new Intent();
                                                intent.setAction(Intent.ACTION_VIEW);
                                                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                                                intent.setData(Uri.parse(url));
                                                startActivity(intent);
                                            }
                                        });
                                        tbrow.addView(link);
                                        ((TableRow.MarginLayoutParams) link.getLayoutParams()).rightMargin = 16;
                                    }

                                    else{
                                        isbn = document.get("isbn").toString();
                                        System.out.println("ISBN!!!!!!! --> " + isbn);
                                        ImageButton link = new ImageButton(context);
                                        link.setImageDrawable(getResources().getDrawable(R.drawable.ic_email_black_24dp));
                                        link.setBackground(new ColorDrawable(0x00000000));
                                        link.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View v) {
                                                // TODO: if reqAccepted, link to email
                                                //          else send toast not
                                                db.collection("acceptedRequests")
                                                        .document(username + "&" + isbn)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if(task.isSuccessful()) {
                                                                    isbn = document.getId();
                                                                    System.out.println(isbn);
                                                                    System.out.println("REQUESTED!!!!!!!! --> " + username + "&" + isbn);

                                                                    System.out.println("DIS DE RESULT!!! --> " + task.getResult());
                                                                    if(task.getResult().get("requestedBy") == null) {
                                                                        System.out.println("IT's NULLLLLLLLLLLL!!!");
                                                                        Toast.makeText(getApplicationContext(), "Request has not been accepted for this book!", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    else {
                                                                        String requestedBy = task.getResult().get("requestedBy").toString();

                                                                        // get email from user that requested book and open email instance
                                                                        db.collection("users")
                                                                                .document(requestedBy)
                                                                                .get()
                                                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                        email = document.get("email").toString();
                                                                                        System.out.println("ON CLICK SET FOR --> " + email);
                                                                                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                                                                                "mailto",email, null));
                                                                                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Goat Books Exchange");
                                                                                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Looking to exchange " + isbn + " with you!");
                                                                                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                                else {
                                                                    Toast.makeText(getApplicationContext(), "Request has not been accepted for this book!", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
//                                                if(reqAccepted) {
//
//
//                                                    url = document.get("url").toString();
//                                                    System.out.println("ON CLICK SET FOR --> " + url);
//                                                    Intent intent = new Intent();
//                                                    intent.setAction(Intent.ACTION_VIEW);
//                                                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
//                                                    intent.setData(Uri.parse(url));
//                                                    startActivity(intent);
//                                                }
//                                                else {
//                                                    Toast.makeText(getApplicationContext(), "Request has not been accepted for this book!", Toast.LENGTH_SHORT).show();
//                                                }
                                            }
                                        });
                                        tbrow.addView(link);
                                    }
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
        int len = 30;
        s = "  " + s;
        if(s.length() > len){
            s = s.substring(0,len - 3) + "...";
        }
        return s;
    }

    public void requests() {
        Intent intent = new Intent(MyProfile.this, BookRequest.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    public void addBookNoClick() {
        Intent intent = new Intent(MyProfile.this, AddBook.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
    public void logout() {
        Intent intent = new Intent(MyProfile.this, MainActivity.class);
        startActivity(intent);

    }

    private void cleanTable(TableLayout table) {
        int childCount = table.getChildCount();
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

    public void goToLink(String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}
