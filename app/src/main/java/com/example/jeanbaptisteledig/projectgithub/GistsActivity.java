package com.example.jeanbaptisteledig.projectgithub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class GistsActivity extends AppCompatActivity {

    private String TAG = GistsActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> apiList;
    HashMap<String, String> item = new HashMap<>();
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gists);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //From ResultSearchActivity
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        apiList = new ArrayList<>();

        new resultOneGist().execute();
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

    private class resultOneGist extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(GistsActivity.this);
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

                    String description = jsonObj.getString("description");
                    String comments = jsonObj.getString("comments");
                    String access = jsonObj.getString("public");
                    String created_at = jsonObj.getString("created_at");

                    JSONObject filesObj = jsonObj.getJSONObject("files");
                    JSONArray filesArray = filesObj.names();
                    String files = filesArray.getString(0);

                    JSONObject thisFileObj = filesObj.getJSONObject(files);
                    String filename = thisFileObj.getString("filename");

                    JSONArray history = jsonObj.getJSONArray("history");
                    JSONArray forks = jsonObj.getJSONArray("forks");

                    JSONObject owner = jsonObj.getJSONObject("owner");
                    String login = owner.getString("login");
                    String avatar_url = owner.getString("avatar_url");

                    int revisionsNumber = history.length();
                    String nombreRevisions = String.valueOf(revisionsNumber);
                    int forksNumber = forks.length();
                    String nombreForks = String.valueOf(forksNumber);
                    int filesNumber = files.length();
                    String nombreFiles = String.valueOf(filesNumber);

                    item.put("description", description);
                    item.put("comments", comments);
                    item.put("login", login);
                    item.put("avatar_url", avatar_url);
                    item.put("access", access);
                    item.put("filename", filename);
                    item.put("nombreRevisions", nombreRevisions);
                    item.put("nombreForks", nombreForks);
                    item.put("nombreFiles", nombreFiles);

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
            TextView textViewComments = (TextView) findViewById(R.id.textViewComments);
            TextView textViewLogin = (TextView) findViewById(R.id.textViewLogin);
            TextView textViewAccess = (TextView) findViewById(R.id.textViewAccess);
            TextView textViewRevisions = (TextView) findViewById(R.id.textViewRevisions);
            TextView textViewForks = (TextView) findViewById(R.id.textViewForks);
            TextView textViewFiles = (TextView) findViewById(R.id.textViewFiles);
            TextView textViewDate = (TextView) findViewById(R.id.textViewDate);

            ImageView imageView = (ImageView) findViewById(R.id.imageView);

            String filename = item.get("filename").toString();
            String description = item.get("description").toString();
            String comments = item.get("comments").toString() + " Comments";
            String login = item.get("login").toString();
            String avatar_url = item.get("avatar_url").toString();
            String access = item.get("access").toString();
            String nombreRevisions = item.get("nombreRevisions").toString();
            String nombreForks = item.get("nombreForks").toString();
            String nombreFiles = item.get("nombreFiles").toString();

            textViewName.setText(filename);
            textViewLogin.setText(login);
            textViewComments.setText(comments);
            textViewRevisions.setText(nombreRevisions + " Revisions");
            textViewForks.setText(nombreForks + " Forks");
            textViewFiles.setText(nombreFiles + " Files");
            Picasso.with(GistsActivity.this).load(avatar_url).into(imageView);

            if (description == "null") {
                textViewDescription.setText("N/A");
            }
            else {
                textViewDescription.setText(description);
            }

            if (access == "true") {
                textViewAccess.setText("Public");
            }
            else {
                textViewAccess.setText("Private");
            }
            setTitle(filename);
        }
    }
}
