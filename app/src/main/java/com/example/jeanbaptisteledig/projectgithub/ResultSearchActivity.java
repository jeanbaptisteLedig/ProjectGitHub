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

import com.google.gson.Gson;

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

    User currentUser;

    // ------ FOR REPO --------
    private String urlRepo = null;
    private String messageRepo = null;
    // ------ FOR USER --------
    private String urlUser = null;
    private String messageUser = null;
    // ------ FOR REPO FOR ONE USER ------
    private String urlRepoForOneUser = null;
    // ------ FOR ORGS FOR ONE USER ------
    private String urlOrgsForOneUser = null;
    private String orgs = null;
    // ------ FOR GISTS FOR ONE USER ------
    private String urlGistsForOneUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv = (ListView) findViewById(R.id.list);
        apiList = new ArrayList<>();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            Gson gson = new Gson();
            currentUser = gson.fromJson(extras.getString("currentUser"),User.class);

            // ------ FOR REPO --------
            messageRepo = intent.getStringExtra("searchRepo");
            urlRepo = intent.getStringExtra("urlRepo");
            urlRepo = urlRepo + messageRepo;
            // ------ FOR USER --------
            messageUser = intent.getStringExtra("searchUser");
            urlUser = intent.getStringExtra("urlUser");
            urlUser = urlUser + messageUser;
            // ------ FOR REPO FOR ONE USER ------
            urlRepoForOneUser = intent.getStringExtra("urlReposCurrentUser");
            // ------ FOR ORGS FOR ONE USER ------
            urlOrgsForOneUser = intent.getStringExtra("urlOrgs");
            // ------ FOR GISTS FOR ONE USER ------
            urlGistsForOneUser = intent.getStringExtra("urlGistsCurrentUser");

        } else {
            Toast.makeText(getApplicationContext(), "Please retry to connect", Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(ResultSearchActivity.this, LoginActivity.class);
            startActivity(myIntent);
        }

        if(messageUser == null && urlRepoForOneUser == null && urlOrgsForOneUser == null && urlGistsForOneUser == null) {
            new resultRepositories().execute(); }
        else if (messageRepo == null && urlRepoForOneUser == null && urlOrgsForOneUser == null && urlGistsForOneUser == null) {
            new resultUsers().execute(); }
        else if (messageRepo == null && messageUser == null && urlOrgsForOneUser == null && urlGistsForOneUser == null) {
            new resultRepositoriesForOneUser().execute(); }
        else if (messageRepo == null && messageUser == null && urlRepoForOneUser == null && urlGistsForOneUser == null) {
            new resultOrgsForOneUser().execute(); }
        else if (messageRepo == null && messageUser == null && urlRepoForOneUser == null && urlOrgsForOneUser == null) {
            new resultGistsForOneUser().execute();

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ResultSearchActivity.this, AddGistActivity.class);
                    Gson gson = new Gson();
                    intent.putExtra("currentUser", gson.toJson(currentUser));
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

            setTitle("Repository result for : " + messageRepo);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String url = ((TextView) view.findViewById(R.id.textViewUrl)).getText().toString();
                    String full_name = ((TextView) view.findViewById(R.id.textViewFullName)).getText().toString();

                    Toast toast = Toast.makeText(getApplicationContext(), full_name, Toast.LENGTH_SHORT);
                    toast.show();

                    Intent intent = new Intent(ResultSearchActivity.this, RepositoriesActivity.class);
                    Gson gson = new Gson();
                    intent.putExtra("currentUser", gson.toJson(currentUser));
                    intent.putExtra("urlPart2", full_name);
                    intent.putExtra("url", url);
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

            setTitle("Repository result for : " + messageUser);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String login = ((TextView) view.findViewById(R.id.textViewName)).getText().toString();

                    Toast toast = Toast.makeText(getApplicationContext(), login, Toast.LENGTH_SHORT);
                    toast.show();

                    Intent intent = new Intent(ResultSearchActivity.this, UsersActivity.class);
                    Gson gson = new Gson();
                    intent.putExtra("currentUser", gson.toJson(currentUser));
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

            setTitle("Repository result");

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String url = ((TextView) view.findViewById(R.id.textViewUrl)).getText().toString();
                    String full_name = ((TextView) view.findViewById(R.id.textViewFullName)).getText().toString();

                    Toast toast = Toast.makeText(getApplicationContext(), full_name, Toast.LENGTH_SHORT);
                    toast.show();

                    Intent intent = new Intent(ResultSearchActivity.this, RepositoriesActivity.class);
                    Gson gson = new Gson();
                    intent.putExtra("currentUser", gson.toJson(currentUser));
                    intent.putExtra("urlPart2", full_name);
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

            setTitle("Organisations result");
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

            setTitle("Gists result");

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String url = ((TextView) view.findViewById(R.id.textViewURL)).getText().toString();
                    String filename = ((TextView) view.findViewById(R.id.textViewLoginOrgs)).getText().toString();

                    Toast toast = Toast.makeText(getApplicationContext(), filename, Toast.LENGTH_SHORT);
                    toast.show();

                    Intent intent = new Intent(ResultSearchActivity.this, GistsActivity.class);
                    Gson gson = new Gson();
                    intent.putExtra("currentUser", gson.toJson(currentUser));
                    intent.putExtra("url", url);
                    startActivity(intent);
                }
            });
        }
    }
}
