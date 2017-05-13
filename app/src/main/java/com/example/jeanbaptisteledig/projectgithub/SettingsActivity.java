package com.example.jeanbaptisteledig.projectgithub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        intent.putExtra("login", "jeanbaptisteLedig");
        startActivity(intent);
    }

    public void projectGit(View v) {
        Intent intent = new Intent(SettingsActivity.this, RepositoriesActivity.class);
        intent.putExtra("url", "https://api.github.com/repos/jeanbaptisteLedig/ProjectGitHub");
        startActivity(intent);
    }
}
