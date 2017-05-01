package com.example.jeanbaptisteledig.projectgithub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class SearchActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.projectgithub.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    public void sendRequest (View view) {
        Intent intent = new Intent(this, ResultSearchActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String search = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, search);
        startActivity(intent);
    }
}