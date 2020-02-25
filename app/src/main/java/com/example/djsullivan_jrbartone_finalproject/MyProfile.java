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
    boolean isOnline = false;
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
                        Toast.makeText(MyProfile.this, "Home",Toast.LENGTH_SHORT).show();break;
                    case R.id.settings:
                        Toast.makeText(MyProfile.this, "Settings",Toast.LENGTH_SHORT).show();break;
                    case R.id.profile:
                        Toast.makeText(MyProfile.this, "Profile",Toast.LENGTH_SHORT).show();break;
                    case R.id.add:
                        addBookNoClick();
                        Toast.makeText(MyProfile.this, "Add",Toast.LENGTH_SHORT).show();break;
                    case R.id.req:
                        requests();
                        Toast.makeText(MyProfile.this, "Trade",Toast.LENGTH_SHORT).show();break;
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
                                if(document.get("owner").toString().contains(username)) { // add filter here to exclude books owned by you
                                    if(document.get("isPdf") != null && document.get("isOnline") != null && document.get("url") != null) {
                                        System.out.println("NOT NULL FOR --> " + document.get("title").toString());
                                        if(document.get("isPdf").toString().equals("true")) {
                                            isPdf = true;
                                            url = document.get("url").toString();
                                        }
                                        if(document.get("isOnline").toString().equals("true")) {
                                            isOnline = true;
                                            url = document.get("url").toString();
                                        }
                                    }

                                    Log.d("SUCCESS", document.getId() + " => " + document.getData());
                                    TableRow tbrow = new TableRow(context);
                                    TableRow.LayoutParams trlp = new TableRow.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    tbrow.setLayoutParams(trlp);
                                    TextView tv = new TextView(context);
                                    tv.setText(padAndTrim(document.getString("title")));
                                    tv.setTextSize(20);
                                    tv.setLayoutParams(new TableRow.LayoutParams(
                                            TableRow.LayoutParams.MATCH_PARENT,
                                            TableRow.LayoutParams.MATCH_PARENT, 0.9f));
                                    tbrow.addView(tv);

                                    //TODO: only have link appear for results with links
                                    ImageButton link = new ImageButton(context);
                                    link.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_share));
                                    link.setBackground(new ColorDrawable(0x00000000));
                                    link.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            Intent intent = new Intent();
                                            intent.setAction(Intent.ACTION_VIEW);
                                            intent.addCategory(Intent.CATEGORY_BROWSABLE);
                                            intent.setData(Uri.parse(url));
                                            startActivity(intent);
                                        }
                                    });
                                    tbrow.addView(link);
                                    ((TableRow.MarginLayoutParams) link.getLayoutParams()).rightMargin = 16;
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
