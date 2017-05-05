package com.example.jeanbaptisteledig.projectgithub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ResultSearchActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv;
    ArrayList<HashMap<String, String>> apiList;

    // ------ FOR REPO --------
    private String url = "https://api.github.com/search/repositories?q=";
    // ------ FOR USER --------
    private String urlUser = "https://api.github.com/search/users?q=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_search);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv = (ListView) findViewById(R.id.list);
        Intent intent = getIntent();
        apiList = new ArrayList<>();

        // ------ FOR REPO --------
        String message = intent.getStringExtra("SearchRepo");
        url = url + message;
        // ------ FOR REPO --------
        // ------ FOR USER --------
        String messageUser = intent.getStringExtra("SearchUser");
        urlUser = urlUser + messageUser;
        // ------ FOR USER --------

        if(message == null) {
            new resultUsers().execute(); }
        else if (messageUser == null) {
            new resultRepositories().execute(); }
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


    // -------- RESULTS REPO ----------
    private class resultRepositories extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ResultSearchActivity.this);
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

                    // Getting JSON Array node
                    JSONArray items = jsonObj.getJSONArray("items");

                    //Looping through all items
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject c = items.getJSONObject(i);

                        String id = c.getString("id");
                        String name = c.getString("name");
                        String full_name = c.getString("full_name");
                        String description = c.getString("description");

                        JSONObject owner = c.getJSONObject("owner");
                        String login = owner.getString("login");
                        String avatar_url = owner.getString("avatar_url");

                        HashMap<String, String> item = new HashMap<>();

                        item.put("id", id);
                        item.put("name", name);
                        item.put("full_name", full_name);
                        item.put("login", login);
                        item.put("description", description);
                        item.put("avatar_url", avatar_url);

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
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    ResultSearchActivity.this, apiList,
                    R.layout.list_item, new String[]{"login", "name", "full_name", "description", "avatar_url"}, new int[]{R.id.login, R.id.name, R.id.full_name, R.id.description, R.id.avatar_url});

            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String name = ((TextView) view.findViewById(R.id.name)).getText().toString();
                    String full_name = ((TextView) view.findViewById(R.id.full_name)).getText().toString();
                    String description = ((TextView) view.findViewById(R.id.description)).getText().toString();
                    String avatar_url = ((TextView) view.findViewById(R.id.avatar_url)).getText().toString();

                    Toast toast = Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_SHORT);
                    toast.show();

                    Intent intent = new Intent(ResultSearchActivity.this, RepositoriesActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("full_name", full_name);
                    intent.putExtra("description", description);
                    intent.putExtra("avatar_url", avatar_url);
                    startActivity(intent);
                }
            });
        }
    }



    // -------- RESULTS USERS ----------
    private class resultUsers extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ResultSearchActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            ClientHTTP sh = new ClientHTTP();

            // Making a request to url and getting response
            String jsonStr = sh.callAPI(urlUser);

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
                        String login = c.getString("login");
                        String avatar_url = c.getString("avatar_url");
                        String url = c.getString("url");

                        HashMap<String, String> item = new HashMap<>();

                        item.put("id", id);
                        item.put("login", login);
                        item.put("url", url);
                        item.put("avatar_url", avatar_url);

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
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    ResultSearchActivity.this, apiList,
                    R.layout.list_item, new String[]{"login", "url", "avatar_url"}, new int[]{R.id.login, R.id.full_name, R.id.avatar_url});
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String login = ((TextView) view.findViewById(R.id.login)).getText().toString();

                    Toast toast = Toast.makeText(getApplicationContext(), "Go to : " + login, Toast.LENGTH_SHORT);
                    toast.show();

                    Intent intent = new Intent(ResultSearchActivity.this, UsersActivity.class);
                    intent.putExtra("login", login);
                    startActivity(intent);
                }
            });
        }
    }
}