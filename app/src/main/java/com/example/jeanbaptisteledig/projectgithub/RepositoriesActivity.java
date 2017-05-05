package com.example.jeanbaptisteledig.projectgithub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RepositoriesActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repositories);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String full_name = intent.getStringExtra("full_name");
        String bio = intent.getStringExtra("bio");
        String avatar_url = intent.getStringExtra("avatar_url");

        TextView textView = (TextView) findViewById(R.id.textViewName);
        TextView textViewFullName = (TextView) findViewById(R.id.textViewFullName);
        TextView textViewDescription = (TextView) findViewById(R.id.textViewDescription);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Picasso.with(this).load(avatar_url).into(imageView);

        textView.setText(name);
        textViewFullName.setText(full_name);
        textViewDescription.setText(bio);
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
