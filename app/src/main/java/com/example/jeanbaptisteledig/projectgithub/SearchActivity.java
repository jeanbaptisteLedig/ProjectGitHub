package com.example.jeanbaptisteledig.projectgithub;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

public class SearchActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.projectgithub.MESSAGE";
    private String TAG = SearchActivity.class.getSimpleName();

    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            Gson gson = new Gson();
            currentUser = gson.fromJson(extras.getString("currentUser"),User.class);
        } else {
            Toast.makeText(getApplicationContext(), "Please retry to connect", Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(SearchActivity.this, LoginActivity.class);
            startActivity(myIntent);
        }
        setTitle("Search");
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

    public void sendRequestRepositories (View view) {
        Intent intent = new Intent(this, ResultSearchActivity.class);
        EditText editText = (EditText) findViewById(R.id.editTextRepo);
        String search = editText.getText().toString();
        Gson gson = new Gson();
        intent.putExtra("currentUser", gson.toJson(currentUser));
        intent.putExtra("searchRepo", search);
        intent.putExtra("urlRepo", "https://api.github.com/search/repositories?q=");
        startActivity(intent);
    }

    public void sendRequestUsers (View view) {
        Intent intent = new Intent(this, ResultSearchActivity.class);
        EditText editTextUser = (EditText) findViewById(R.id.editTextUser);
        String searchUser = editTextUser.getText().toString();
        Gson gson = new Gson();
        intent.putExtra("currentUser", gson.toJson(currentUser));
        intent.putExtra("searchUser", searchUser);
        intent.putExtra("urlUser", "https://api.github.com/search/users?q=");
        startActivity(intent);
    }
}