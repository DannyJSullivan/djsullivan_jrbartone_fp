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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddBook extends AppCompatActivity {

    private ActionBar actionBar;

    EditText mAuthor;
    EditText mTitle;
    EditText mISBN;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;

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

        dl = (DrawerLayout)findViewById(R.id.activity_add_book);
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
                        Toast.makeText(AddBook.this, "Home",Toast.LENGTH_SHORT).show();break;
                    case R.id.settings:
                        Toast.makeText(AddBook.this, "Settings",Toast.LENGTH_SHORT).show();break;
                    case R.id.profile:
                        Toast.makeText(AddBook.this, "Profile",Toast.LENGTH_SHORT).show();break;
                    case R.id.add:
                        Toast.makeText(AddBook.this, "Add",Toast.LENGTH_SHORT).show();break;
                    case R.id.req:
                        requests();
                        Toast.makeText(AddBook.this, "Trade",Toast.LENGTH_SHORT).show();break;
                    default:
                        return true;
                }


                return true;

            }
        });

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
                        clearPage();
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
                        clearPage();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error! Book not added!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result != null){
            if (result.getContents() == null){
                Toast.makeText(this,"Book Not Found! Please enter manually.", Toast.LENGTH_SHORT).show();
            }
            else{
                String isbn = result.getContents();
                Log.d("ISBN", isbn);
                getBookInfo(isbn);
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    String queryResult = "";
    String title = "";
    String author = "";

    public void getBookInfo(String isbn) {
        Thread thread = new Thread(new Runnable() {
           @Override
           public void run() {
               String query = "https://www.googleapis.com/books/v1/volumes?q=isbn" + isbn;
               System.out.println("QUERY!!!!!! --> " + query);
               StringBuilder result = new StringBuilder();

               try {
                   URL url = new URL(query);
                   HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                   connection.setRequestMethod("GET");
                   BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                   String line;
                   while ((line = reader.readLine()) != null) {
                       result.append(line);
                   }
                   reader.close();
                   Log.d("GOOGLE BOOKS RESULT", result.toString());

                   queryResult = result.toString();

                   try {
                       JSONObject obj = new JSONObject(queryResult);
                       JSONArray items = obj.getJSONArray("items");

                       JSONObject itemsObj = new JSONObject(items.get(0).toString());

                       JSONObject volumeInfo = new JSONObject(itemsObj.get("volumeInfo").toString());

                       title = volumeInfo.getString("title");

                       JSONArray authors = volumeInfo.getJSONArray("authors");
                       System.out.println("THESE ARE THE AUTHROS!!! --> " + authors);
                       if(authors.length() > 1) {
                           for(int i = 0; i < authors.length(); i++) {
                               if(i == authors.length() - 1) {
                                   author += authors.get(i).toString();
                               }
                               else {
                                   author += authors.get(i).toString();
                                   author += ", ";
                               }
                           }
                       }
                       else if(authors.length() == 1) {
                           author = authors.get(0).toString();
                       }
                       else {
                           author = "No author found!";
                       }
                   }
                   catch (JSONException e) {
                       System.out.println("JSON ERROR!!!!");
                       e.printStackTrace();
                   }
               }
               catch (MalformedURLException e) {
                   e.printStackTrace();
               }
               catch (IOException e) {
                   e.printStackTrace();
               }
           }
        });

        // start looking up book, and wait until search is finished
        thread.start();
        try {
            thread.join();
        }
        catch(InterruptedException e) {
            e.printStackTrace();
        }

        String[] result = new String[2];
        result[0] = title;
        result[1] = author;

        mTitle.setText(title);
        mAuthor.setText(author);
        mISBN.setText(isbn);
    }

    public void scaNow(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(Portrait.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan your book's barcode");
        integrator.initiateScan();
    }

    // TODO: functionality to add book/pdf
    // test functionality with camera to scan book, automatically add, add ability to confirm infromation is correct first
    // possibly save with docId of ISBN
    public void scanBarcode(View view) {
        scaNow();
    }

    public void clearPage() {
        mTitle.setText("");
        mAuthor.setText("");
        mISBN.setText("");
    }

    public void goHome() {
        Intent intent = new Intent(AddBook.this, LoggedIn.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }


    public void requests() {
        Intent intent = new Intent(AddBook.this, BookRequest.class);
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
