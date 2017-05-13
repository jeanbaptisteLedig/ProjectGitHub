package com.example.jeanbaptisteledig.projectgithub;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ResultSearchActivity extends AppCompatActivity {

    private String TAG = ResultSearchActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv;
    ArrayList<HashMap<String, String>> apiList;

    // ------ FOR REPO --------
    private String urlRepo = null;
    private String messageRepo = null;
    // ------ FOR USER --------
    private String urlUser = null;
    private String messageUser = null;
    // ------ FOR REPO FOR ONE USER ------
    private String urlRepoForOneUser = null;
    private String repos = null;
    // ------ FOR ORGS FOR ONE USER ------
    private String urlOrgsForOneUser = null;
    private String orgs = null;
    // ------ FOR GISTS FOR ONE USER ------
    private String urlGistsForOneUser = null;
    private String gists = null;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv = (ListView) findViewById(R.id.list);
        Intent intent = getIntent();
        apiList = new ArrayList<>();

        // ------ FOR REPO --------
        messageRepo = intent.getStringExtra("searchRepo");
        urlRepo = intent.getStringExtra("urlRepo");
        urlRepo = urlRepo + messageRepo;
        // ------ FOR REPO --------
        // ------ FOR USER --------
        messageUser = intent.getStringExtra("searchUser");
        urlUser = intent.getStringExtra("urlUser");
        urlUser = urlUser + messageUser;
        // ------ FOR USER --------
        // ------ FOR REPO FOR ONE USER ------
        repos = intent.getStringExtra("repos");
        urlRepoForOneUser = intent.getStringExtra("urlRepos");
        urlRepoForOneUser = urlRepoForOneUser + repos;
        // ------ FOR REPO FOR ONE USER ------
        // ------ FOR ORGS FOR ONE USER ------
        orgs = intent.getStringExtra("orgs");
        urlOrgsForOneUser = intent.getStringExtra("urlOrgs");
        urlOrgsForOneUser = urlOrgsForOneUser + orgs;
        // ------ FOR ORGS FOR ONE USER ------
        // ------ FOR GISTS FOR ONE USER ------
        gists = intent.getStringExtra("gists");
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
        urlGistsForOneUser = intent.getStringExtra("urlGists");
        urlGistsForOneUser = urlGistsForOneUser + gists;
        Log.e(TAG, "onCreate: " + urlGistsForOneUser );
        Log.e("TAG", "addGist: " + username + password );
        // ------ FOR GISTS FOR ONE USER ------

        if(messageUser == null && repos == null && orgs == null && gists == null) {
            new resultRepositories().execute(); }
        else if (messageRepo == null && repos == null && orgs == null && gists == null) {
            new resultUsers().execute(); }
        else if (messageRepo == null && messageUser == null && orgs == null && gists == null) {
            new resultRepositoriesForOneUser().execute(); }
        else if (messageRepo == null && messageUser == null && repos == null && gists == null) {
            new resultOrgsForOneUser().execute(); }
        else if (messageRepo == null && messageUser == null && repos == null && orgs == null) {
            new resultGistsForOneUser().execute();

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ResultSearchActivity.this, AddGistActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("password", password);
                    startActivity(intent);
                }
            });
        }
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
            String jsonStr = sh.callAPI(urlRepo);

            if (jsonStr != null) {
                try {
                    //JSONstr to JSONobj
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray items = jsonObj.getJSONArray("items");

                    //Looping through all items
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject c = items.getJSONObject(i);

                        String full_name = c.getString("full_name");
                        String description = c.getString("description");
                        String language = c.getString("language");
                        String url = c.getString("url");

                        HashMap<String, String> item = new HashMap<>();

                        item.put("full_name", full_name);
                        item.put("description", description);
                        item.put("language", language);
                        item.put("url", url);

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

            ListAdapter adapter = new SimpleAdapter(
                    ResultSearchActivity.this, apiList,
                    R.layout.list_item, new String[]{"full_name", "description", "language", "url"}, new int[]{R.id.textViewFullName, R.id.textViewDescription, R.id.textViewLanguage, R.id.textViewUrl});

            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String url = ((TextView) view.findViewById(R.id.textViewUrl)).getText().toString();
                    String full_name = ((TextView) view.findViewById(R.id.textViewFullName)).getText().toString();

                    Toast toast = Toast.makeText(getApplicationContext(), "Go to : " + full_name, Toast.LENGTH_SHORT);
                    toast.show();

                    Intent intent = new Intent(ResultSearchActivity.this, RepositoriesActivity.class);
                    intent.putExtra("url", url);
                    intent.putExtra("username", username);
                    intent.putExtra("password", password);
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

            ListAdapter adapter = new SimpleAdapter(
                    ResultSearchActivity.this, apiList,
                    R.layout.list_item_users, new String[]{"login"}, new int[]{R.id.textViewName});
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String login = ((TextView) view.findViewById(R.id.textViewName)).getText().toString();

                    Toast toast = Toast.makeText(getApplicationContext(), "Go to : " + login, Toast.LENGTH_SHORT);
                    toast.show();

                    Intent intent = new Intent(ResultSearchActivity.this, UsersActivity.class);
                    intent.putExtra("login", login);
                    startActivity(intent);
                }
            });
        }
    }

    // ------ RESULTATS REPO FOR THIS USER
    private class resultRepositoriesForOneUser extends AsyncTask<Void, Void, Void> {

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
            String jsonStr = sh.callAPI(urlRepoForOneUser);

            if (jsonStr != null) {
                try {
                    //jsonStr to JsonArray
                    JSONArray items = new JSONArray(jsonStr);

                    //Looping through all items
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject c = items.getJSONObject(i);

                        String full_name = c.getString("full_name");
                        String description = c.getString("description");
                        String language = c.getString("language");
                        String url = c.getString("url");

                        HashMap<String, String> item = new HashMap<>();

                        item.put("full_name", full_name);
                        item.put("description", description);
                        item.put("language", language);
                        item.put("url", url);

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

            ListAdapter adapter = new SimpleAdapter(
                    ResultSearchActivity.this, apiList,
                    R.layout.list_item, new String[]{"full_name", "description", "language", "url"}, new int[]{R.id.textViewFullName, R.id.textViewDescription, R.id.textViewLanguage, R.id.textViewUrl});
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String url = ((TextView) view.findViewById(R.id.textViewUrl)).getText().toString();
                    String full_name = ((TextView) view.findViewById(R.id.textViewFullName)).getText().toString();

                    Toast toast = Toast.makeText(getApplicationContext(), full_name, Toast.LENGTH_SHORT);
                    toast.show();

                    Intent intent = new Intent(ResultSearchActivity.this, RepositoriesActivity.class);
                    intent.putExtra("url", url);
                    startActivity(intent);
                }
            });
        }
    }


    // ------ RESULTATS ORGS FOR THIS USER
    private class resultOrgsForOneUser extends AsyncTask<Void, Void, Void> {

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
            String jsonStr = sh.callAPI(urlOrgsForOneUser);

            if (jsonStr != null) {
                try {
                    //jsonStr to JsonArray
                    JSONArray items = new JSONArray(jsonStr);

                    //Looping through all items
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject c = items.getJSONObject(i);

                        String login = c.getString("login");

                        HashMap<String, String> item = new HashMap<>();

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

            ListAdapter adapter = new SimpleAdapter(
                    ResultSearchActivity.this, apiList,
                    R.layout.list_item_orgs, new String[]{"login"}, new int[]{R.id.textViewLoginOrgs});
            lv.setAdapter(adapter);
        }
    }


    // ------ RESULTATS GISTS FOR THIS USER
    private class resultGistsForOneUser extends AsyncTask<Void, Void, Void> {

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
            String jsonStr = sh.callAPI(urlGistsForOneUser);

            if (jsonStr != null) {
                try {
                    //jsonStr to JsonArray
                    JSONArray items = new JSONArray(jsonStr);

                    //Looping through all items
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject c = items.getJSONObject(i);

                        String description = c.getString("description");
                        String url = c.getString("url");

                        JSONObject filesObj = c.getJSONObject("files");
                        JSONArray filesArray = filesObj.names();
                        String files = filesArray.getString(0);

                        JSONObject thisFileObj = filesObj.getJSONObject(files);
                        String filename = thisFileObj.getString("filename");

                        HashMap<String, String> item = new HashMap<>();

                        item.put("url", url);
                        item.put("filename", filename);
                        item.put("description", description);

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

            ListAdapter adapter = new SimpleAdapter(
                    ResultSearchActivity.this, apiList,
                    R.layout.list_item_orgs, new String[]{"filename", "description", "url"}, new int[]{R.id.textViewLoginOrgs, R.id.textViewDescription, R.id.textViewURL});
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String url = ((TextView) view.findViewById(R.id.textViewURL)).getText().toString();

                    Toast toast = Toast.makeText(getApplicationContext(), "Test", Toast.LENGTH_SHORT);
                    toast.show();

                    Intent intent = new Intent(ResultSearchActivity.this, GistsActivity.class);
                    intent.putExtra("url", url);
                    startActivity(intent);
                }
            });
        }
    }
}
