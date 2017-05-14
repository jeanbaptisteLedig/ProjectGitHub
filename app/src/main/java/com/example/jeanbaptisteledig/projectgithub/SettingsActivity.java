package com.example.jeanbaptisteledig.projectgithub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingsActivity extends AppCompatActivity {

    User currentUser;

    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            Gson gson = new Gson();
            currentUser = gson.fromJson(extras.getString("currentUser"),User.class);
            username = currentUser.getUsername();
            password = currentUser.getPassword();
        } else {
            Toast.makeText(getApplicationContext(), "Please retry to connect", Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(SettingsActivity.this, LoginActivity.class);
            startActivity(myIntent);
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

    public void authorGit(View v) {
        Intent intent = new Intent(SettingsActivity.this, UsersActivity.class);
        Gson gson = new Gson();
        intent.putExtra("currentUser", gson.toJson(currentUser));
        intent.putExtra("login", "jeanbaptisteLedig");
        startActivity(intent);
    }

    public void projectGit(View v) {
        Intent intent = new Intent(SettingsActivity.this, RepositoriesActivity.class);
        Gson gson = new Gson();
        intent.putExtra("currentUser", gson.toJson(currentUser));
        intent.putExtra("url", "https://api.github.com/repos/jeanbaptisteLedig/ProjectGitHub");
        startActivity(intent);
    }

    public void easterEgg(View v) {
        watchProject();
        starProject();
        TextView textViewMERCI = (TextView) findViewById(R.id.textViewMERCI);
        textViewMERCI.setText("Merci de mettre mon projet en avant... ;)");
    }

    public void watchProject() {
        JSONObject object = new JSONObject();
        try {
            object.put("subscribed", true);
            object.put("ignored", false);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new PostAPI().execute(username, password, "https://api.github.com/repos/jeanbaptisteLedig/ProjectGitHub/subscription", object.toString(), "PUT");
    }

    public void starProject() {
        JSONObject object = new JSONObject();
        try {
            object.put("subscribed", true);
            object.put("ignored", false);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new PostAPI().execute(username, password, "https://api.github.com/user/starred/jeanbaptisteLedig/ProjectGitHub", object.toString(), "PUT");
    }
}
