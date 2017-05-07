package com.example.jeanbaptisteledig.projectgithub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class RepositoriesActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> apiList;
    HashMap<String, String> item = new HashMap<>();
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repositories);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //From ResultSearchActivity
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        apiList = new ArrayList<>();

        Log.e(TAG, "onCreate: " + url );
        new resultOneRepositorie().execute();
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

    private class resultOneRepositorie extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(RepositoriesActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            ClientHTTP sh = new ClientHTTP();

            // Making a request to url and getting response
            String jsonStr = sh.callAPI(url);
            Log.e(TAG, "doInBackground: " + jsonStr );

            if (jsonStr != null) {
                try {
                    //JSONstr to JSONobj
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    String name = jsonObj.getString("name");
                    String description = jsonObj.getString("description");
                    String language = jsonObj.getString("language");

                    JSONObject owner = jsonObj.getJSONObject("owner");
                    String login = owner.getString("login");
                    String avatar_url = owner.getString("avatar_url");

                    item.put("name", name);
                    item.put("description", description);
                    item.put("language", language);
                    item.put("login", login);
                    item.put("avatar_url", avatar_url);

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

            TextView textViewName = (TextView) findViewById(R.id.textViewName);
            TextView textViewDescription = (TextView) findViewById(R.id.textViewDescription);
            TextView textViewLanguage = (TextView) findViewById(R.id.textViewLanguage);
            TextView textViewLogin = (TextView) findViewById(R.id.textViewLogin);
            ImageView imageView = (ImageView) findViewById(R.id.imageView);

            String name = item.get("name").toString();
            String description = item.get("description").toString();
            String language = item.get("language").toString();
            String login = item.get("login").toString();
            String avatar_url = item.get("avatar_url").toString();

            textViewName.setText(name);
            textViewDescription.setText(description);
            textViewLanguage.setText(language);
            textViewLogin.setText(login);
            Picasso.with(RepositoriesActivity.this).load(avatar_url).into(imageView);
        }
    }
}
