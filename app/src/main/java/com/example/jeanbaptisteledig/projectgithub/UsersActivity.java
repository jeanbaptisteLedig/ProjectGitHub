package com.example.jeanbaptisteledig.projectgithub;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class UsersActivity extends AppCompatActivity {

    private String TAG = UsersActivity.class.getSimpleName();
    ArrayList<HashMap<String, String>> apiList;
    HashMap<String, String> item = new HashMap<>();
    private String url = "https://api.github.com/users/";
    private String loginUser;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        //For Return button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            Gson gson = new Gson();
            currentUser = gson.fromJson(extras.getString("currentUser"),User.class);
            loginUser = intent.getStringExtra("login");
        } else {
            Toast.makeText(getApplicationContext(), "Please retry to connect", Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(UsersActivity.this, LoginActivity.class);
            startActivity(myIntent);
        }

        //For ResultOneUser
        url = url + loginUser;
        apiList = new ArrayList<>();

        new resultOneUser().execute();
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
                    String name = jsonObj.getString("name");
                    String followers = jsonObj.getString("followers");
                    String following = jsonObj.getString("following");
                    String nbRepos = jsonObj.getString("public_repos");
                    String nbGists = jsonObj.getString("public_gists");

                    item.put("login", login);
                    item.put("avatar_url", avatar_url);
                    item.put("bio", bio);
                    item.put("name", name);
                    item.put("followers", followers);
                    item.put("following", following);
                    item.put("nbRepos", nbRepos);
                    item.put("nbGists", nbGists);

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

            TextView textView = (TextView) findViewById(R.id.textViewName);
            TextView textViewDescription = (TextView) findViewById(R.id.textViewDescription);
            TextView textViewName = (TextView) findViewById(R.id.textViewName);
            TextView textViewFollowers = (TextView) findViewById(R.id.textViewFollowers);
            TextView textViewFollowing = (TextView) findViewById(R.id.textViewFollowing);
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            Button buttonRepo = (Button) findViewById(R.id.buttonRepos);
            Button buttonGist = (Button) findViewById(R.id.buttonGists);

            String login = item.get("login").toString();
            String bio = item.get("bio").toString();
            String avatar_url = item.get("avatar_url").toString();
            String name = item.get("name").toString();
            String followers = item.get("followers").toString() + " Followers";
            String following = item.get("following").toString() + " Following";
            String nbRepos = item.get("nbRepos").toString() + " Repositories";
            String nbGists = item.get("nbGists").toString() + " Gists";

            textView.setText(login);
            textViewDescription.setText(bio);
            textViewName.setText(name);
            textViewFollowers.setText(followers);
            textViewFollowing.setText(following);
            buttonRepo.setText(nbRepos);
            buttonGist.setText(nbGists);
            Picasso.with(UsersActivity.this).load(avatar_url).into(imageView);

            setTitle(login);
        }
    }

    public void searchRepositoriesForOneUser (View view) {
        Intent intent = new Intent(this, ResultSearchActivity.class);
        Gson gson = new Gson();
        intent.putExtra("currentUser", gson.toJson(currentUser));
        intent.putExtra("urlReposCurrentUser", url + "/repos");
        startActivity(intent);
    }

    public void searchTeamsForOneUser (View view) {
        Intent intent = new Intent(this, ResultSearchActivity.class);
        Gson gson = new Gson();
        intent.putExtra("currentUser", gson.toJson(currentUser));
        intent.putExtra("urlOrgs", url + "/orgs");
        startActivity(intent);
    }

    public void searchGistsForOneUser (View view) {
        Intent intent = new Intent(this, ResultSearchActivity.class);
        Gson gson = new Gson();
        intent.putExtra("currentUser", gson.toJson(currentUser));
        intent.putExtra("urlGistsCurrentUser", url + "/gists");
        startActivity(intent);
    }
}
