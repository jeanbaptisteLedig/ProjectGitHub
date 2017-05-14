package com.example.jeanbaptisteledig.projectgithub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.BoolRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static com.example.jeanbaptisteledig.projectgithub.R.id.login;
import static com.example.jeanbaptisteledig.projectgithub.R.id.navigation_header_container;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> apiList;
    private String urlEventsCurrentUSer;
    private String urlCurrentUser;
    private ListView lv;
    private String username;

    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            Gson gson = new Gson();
            currentUser = gson.fromJson(extras.getString("currentUser"),User.class);
            username = currentUser.getUsername();
        } else {
            Toast.makeText(getApplicationContext(), "Please retry to connect", Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(myIntent);
        }

        urlEventsCurrentUSer = "https://api.github.com/users/" + username + "/received_events/public";
        urlCurrentUser = "https://api.github.com/users/"+ username;

        lv = (ListView) findViewById(R.id.list);
        apiList = new ArrayList<>();

        if (username.length() != 0) {
            new resultEventsCurrentUser().execute();
        } else {
            Toast.makeText(getApplicationContext(), "Please retry to connect", Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(myIntent);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            Gson gson = new Gson();
            intent.putExtra("currentUser", gson.toJson(currentUser));
            startActivity(intent);
        } else if (id == R.id.action_logout) {
            currentUser = null;
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_search) {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            Gson gson = new Gson();
            intent.putExtra("currentUser", gson.toJson(currentUser));
            startActivity(intent);
        } else if (id == R.id.nav_profil) {
            Intent intent = new Intent(MainActivity.this, UsersActivity.class);
            Gson gson = new Gson();
            intent.putExtra("currentUser", gson.toJson(currentUser));
            intent.putExtra("login", currentUser.getUsername());
            startActivity(intent);
        } else if (id == R.id.nav_news) {
            //TO DO
        } else if (id == R.id.nav_own_repo) {
            Intent intent = new Intent(MainActivity.this, ResultSearchActivity.class);
            Gson gson = new Gson();
            intent.putExtra("currentUser", gson.toJson(currentUser));
            intent.putExtra("urlReposCurrentUser", urlCurrentUser + "/repos");
            startActivity(intent);
        } else if (id == R.id.nav_star_repo) {
            Intent intent = new Intent(MainActivity.this, ResultSearchActivity.class);
            Gson gson = new Gson();
            intent.putExtra("currentUser", gson.toJson(currentUser));
            intent.putExtra("urlReposCurrentUser", urlCurrentUser + "/starred");
            startActivity(intent);
        } else if (id == R.id.nav_own_gists) {
            Intent intent = new Intent(MainActivity.this, ResultSearchActivity.class);
            Gson gson = new Gson();
            intent.putExtra("currentUser", gson.toJson(currentUser));
            intent.putExtra("urlGistsCurrentUser", urlCurrentUser + "/gists");
            startActivity(intent);
        } else if (id == R.id.nav_star_gist) {
            //TO DO
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class resultEventsCurrentUser extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            ClientHTTP sh = new ClientHTTP();

            // Making a request to url and getting response
            String jsonStr = sh.callAPI(urlEventsCurrentUSer);

            if (jsonStr != null) {
                try {
                    //jsonStr to JsonArray
                    JSONArray items = new JSONArray(jsonStr);

                    //Looping through all items
                    for (int i = 0; i < items.length(); i++) {
                        //JSONstr to JSONobj
                        JSONObject c = items.getJSONObject(i);

                        String type = c.getString("type");
                        JSONObject actor = c.getJSONObject("actor");
                        String login = actor.getString("login");

                        HashMap<String, String> item = new HashMap<>();

                        item.put("type", type);
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
                    MainActivity.this, apiList,
                    R.layout.list_item_events, new String[]{"type", "login"}, new int[]{R.id.textViewType, R.id.textViewLogin});
            lv.setAdapter(adapter);
        }
    }
}
