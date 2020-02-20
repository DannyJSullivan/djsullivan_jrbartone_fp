package com.example.djsullivan_jrbartone_finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.firestore.local.QueryResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

//                try {
//                    Log.d("GETS HERE", "TRYING TO GET JSON!!!!!");
//                    JSONObject json = new JSONObject(bookInfo);
//                    JSONArray items = json.getJSONArray("items");
////                    JSONArray authors = json.getJSONArray("authors");
//
//                    for(int i = 0; i < items.length(); i++) {
//                        final JSONObject object = items.getJSONObject(i);
//
//                    }
//
//                    Log.d("TITLE ------>", items.toString());
//
//                }
//                catch (JSONException e) {
//                    Log.d("ERROR", "JSON DOESN'T WORK!!!");
//                    e.printStackTrace();
//                }
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    String queryResult = "";

    public void getBookInfo(String isbn) {
        Thread thread = new Thread(new Runnable() {
           @Override
           public void run() {
               String query = "https://www.googleapis.com/books/v1/volumes?q=isbn" + isbn;
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
                   String title = "";
                   String author = "";

                   try {
                       JSONObject obj = new JSONObject(queryResult);
                       JSONArray items = obj.getJSONArray("items");
//                       JSONObject itemsObj = items.toJSONObject();

                       System.out.println("WHAT HERE?????? --> " + items.get(0));

//                       items.

//                       JSONArray vol = items.getJSONArray(0);

//                       title = obj["items"][0]["title"];

//                       JSONObject items = obj.getJSONObject("items");
                       System.out.println("ITEMS?????? --> " + items);

//                       JSONArray vol = items.getJSONArray(0).getJSONArray(4);

//                       System.out.println("volume???? " + vol.toString());

//                       System.out.println(vol);
//                       System.out.println(items);
//                       System.out.println("JSON OBJECT!!! --> " + obj);
//                       System.out.println("ITEMS!!! --> " + obj.get("items"));
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

        thread.start();

        String title = "";
        String author = "";

//        try {
//            JSONObject json = new JSONObject(queryResult);
//            String items = (String) json.get("items");
//            Log.d("JSON RETURNED!!!", items);
//        }
//        catch (JSONException e) {
//            e.printStackTrace();
//            Log.d("JSON EXCEPTION!", e.toString());
//        }

        mTitle.setText(title);
        mAuthor.setText(author);
        mISBN.setText(isbn);
//        return queryResult;
    }

    public void scaNow(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(Portrait.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan Your Barcode");
        integrator.initiateScan();
    }

    // TODO: functionality to add book/pdf
    // test functionality with camera to scan book, automatically add, add ability to confirm infromation is correct first
    // possibly save with docId of ISBN
    public void scanBarcode(View view) {
        scaNow();
    }
}
