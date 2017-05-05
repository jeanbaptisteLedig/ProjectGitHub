package com.example.jeanbaptisteledig.projectgithub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class UsersActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> apiList;
    HashMap<String, String> item = new HashMap<>();
    private String url = "https://api.github.com/users/";
    private String urlRepo;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        //For Return button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //From ResultSearchActivity
        Intent intent = getIntent();
        String login = intent.getStringExtra("login");
        //For ResultOneUser
        url = url + login;
        urlRepo = url + "/repos";
        apiList = new ArrayList<>();

        //For ResultRepositoriesForOneUser
        lv = (ListView) findViewById(R.id.list);

        new resultOneUser().execute();
        new resultRepositoriesForOneUser().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class resultOneUser extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(UsersActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            ClientHTTP sh = new ClientHTTP();

            // Making a request to url and getting response
            String jsonStr = sh.callAPI(url);

            if (jsonStr != null) {
                try {
                    //JSONstr to JSONobj
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    String login = jsonObj.getString("login");
                    String avatar_url = jsonObj.getString("avatar_url");
                    String bio = jsonObj.getString("bio");

                    item.put("login", login);
                    item.put("avatar_url", avatar_url);
                    item.put("bio", bio);

                    apiList.add(item);

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            TextView textView = (TextView) findViewById(R.id.textViewName);
            TextView textViewDescription = (TextView) findViewById(R.id.textViewDescription);
            ImageView imageView = (ImageView) findViewById(R.id.imageView);

            String login = item.get("login").toString();
            String bio = item.get("bio").toString();
            String avatar_url = item.get("avatar_url").toString();

            textView.setText(login);
            textViewDescription.setText(bio);
            Picasso.with(UsersActivity.this).load(avatar_url).into(imageView);
        }
    }



    // ------ RESULTATS REPO FOR THIS USER
    private class resultRepositoriesForOneUser extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(UsersActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            ClientHTTP sh = new ClientHTTP();

            // Making a request to url and getting response
            String jsonStr = sh.callAPI(urlRepo);
            Log.e(TAG, "doInBackground: " + jsonStr );

            if (jsonStr != null) {
                try {
                    //JSONstr to JSONobj
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray items = jsonObj.getJSONArray("items");

                    //Looping through all items
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject c = items.getJSONObject(i);

                        String id = c.getString("id");
                        String name = c.getString("name");
                        String full_name = c.getString("full_name");

                        HashMap<String, String> item = new HashMap<>();

                        item.put("id", id);
                        item.put("name", name);
                        item.put("full_name", full_name);

                        apiList.add(item);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }
}