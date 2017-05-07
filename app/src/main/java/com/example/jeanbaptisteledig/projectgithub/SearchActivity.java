package com.example.jeanbaptisteledig.projectgithub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SearchActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.projectgithub.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void sendRequestRepositories (View view) {
        Intent intent = new Intent(this, ResultSearchActivity.class);
        EditText editText = (EditText) findViewById(R.id.editTextRepo);
        String search = editText.getText().toString();
        intent.putExtra("searchRepo", search);
        intent.putExtra("urlRepo", "https://api.github.com/search/repositories?q=");
        startActivity(intent);
    }

    public void sendRequestUsers (View view) {
        Intent intent = new Intent(this, ResultSearchActivity.class);
        EditText editTextUser = (EditText) findViewById(R.id.editTextUser);
        String searchUser = editTextUser.getText().toString();
        intent.putExtra("searchUser", searchUser);
        intent.putExtra("urlUser", "https://api.github.com/search/users?q=");
        startActivity(intent);
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
}