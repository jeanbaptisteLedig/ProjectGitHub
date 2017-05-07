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

            if (jsonStr != null) {
                try {
                    //JSONstr to JSONobj
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    String name = jsonObj.getString("name");
                    String description = jsonObj.getString("description");
                    String language = jsonObj.getString("language");
                    String stargazers = jsonObj.getString("stargazers_count");
                    String watchers = jsonObj.getString("subscribers_count");
                    String forks = jsonObj.getString("forks");
                    String issues = jsonObj.getString("open_issues_count");
                    String access = jsonObj.getString("private");

                    JSONObject owner = jsonObj.getJSONObject("owner");
                    String login = owner.getString("login");
                    String avatar_url = owner.getString("avatar_url");

                    item.put("name", name);
                    item.put("description", description);
                    item.put("language", language);
                    item.put("login", login);
                    item.put("avatar_url", avatar_url);
                    item.put("stargazers", stargazers);
                    item.put("watchers", watchers);
                    item.put("forks", forks);
                    item.put("issues", issues);
                    item.put("access", access);

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
            TextView textViewStargazers = (TextView) findViewById(R.id.textViewStargazers);
            TextView textViewWatchers = (TextView) findViewById(R.id.textViewWatchers);
            TextView textViewForks = (TextView) findViewById(R.id.textViewForks);
            TextView textViewIssues = (TextView) findViewById(R.id.textViewIssues);
            TextView textViewAccess = (TextView) findViewById(R.id.textViewAccess);
            ImageView imageView = (ImageView) findViewById(R.id.imageView);

            String name = item.get("name").toString();
            String description = item.get("description").toString();
            String language = item.get("language").toString();
            String login = item.get("login").toString();
            String avatar_url = item.get("avatar_url").toString();
            String stargazers = item.get("stargazers").toString() + " Stargazers";
            String watchers = item.get("watchers").toString() + " Watchers";
            String forks = item.get("forks").toString() + " Forks";
            String issues = item.get("issues").toString() + " Issues";
            String access = item.get("access").toString();

            textViewName.setText(name);
            textViewDescription.setText(description);
            textViewLanguage.setText(language);
            textViewLogin.setText(login);
            textViewStargazers.setText(stargazers);
            textViewWatchers.setText(watchers);
            textViewForks.setText(forks);
            textViewIssues.setText(issues);
            Picasso.with(RepositoriesActivity.this).load(avatar_url).into(imageView);

            if (access == "true") {
                textViewAccess.setText("Private");
            }
            else if (access == "false") {
                textViewAccess.setText("Public");
            }
        }
    }
}
