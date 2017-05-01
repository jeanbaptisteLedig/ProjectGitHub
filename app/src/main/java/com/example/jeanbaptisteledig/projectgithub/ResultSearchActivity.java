package com.example.jeanbaptisteledig.projectgithub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ResultSearchActivity extends AppCompatActivity {

    // ------ FOR REPO --------
    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv;
    private String url = "https://api.github.com/search/repositories?q=";
    ArrayList<HashMap<String, String>> apiList;
    // ------ FOR REPO --------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_search);

        // ------ FOR REPO --------
        Intent intent = getIntent();
        String message = intent.getStringExtra(SearchActivity.EXTRA_MESSAGE);
        apiList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);
        url = url + message;
        // ------ FOR REPO --------
        new resultRepositories().execute();
    }

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

            Log.e(TAG, "Response from url: " + jsonStr);

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
                        String url = c.getString("url");

                        JSONObject owner = c.getJSONObject("owner");
                        String login = owner.getString("login");

                        HashMap<String, String> item = new HashMap<>();

                        item.put("id", id);
                        item.put("name", name);
                        item.put("full_name", full_name);
                        item.put("login", login);

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
                    R.layout.list_item, new String[]{"login", "name", "full_name"}, new int[]{R.id.login, R.id.name, R.id.full_name});

            lv.setAdapter(adapter);
        }

    }
}