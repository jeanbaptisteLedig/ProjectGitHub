package com.example.jeanbaptisteledig.projectgithub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class AddGistActivity extends AppCompatActivity {

    private String username;
    private String password;
    private Boolean access = false;

    private String urlCurrentUser;

    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gist);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            Gson gson = new Gson();
            currentUser = gson.fromJson(extras.getString("currentUser"),User.class);
            username = currentUser.getUsername();
            password = currentUser.getPassword();
            urlCurrentUser = "https://api.github.com/users/"+ username;
        } else {
            Toast.makeText(getApplicationContext(), "Please retry to connect", Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(AddGistActivity.this, LoginActivity.class);
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

    public void addGist(View view) {
        EditText editTextDescription = (EditText) findViewById(R.id.editTextDescription);
        EditText editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        EditText editTextContent = (EditText) findViewById(R.id.editTextContent);
        Switch publicGist = (Switch) findViewById(R.id.switchPublic);
        String description = editTextDescription.getText().toString();
        String title = editTextTitle.getText().toString();
        String content = editTextContent.getText().toString();

        if (publicGist.isChecked() == true) {
            access = true;
        } else {
            access = false;
        }
        JSONObject object = new JSONObject();
        JSONObject objectFiles = new JSONObject();
        JSONObject objectFiless = new JSONObject();
        try {
            objectFiless.put("content", content);
            objectFiles.put(title, objectFiless);
            object.put("description", description);
            object.put("public", access);
            object.put("files", objectFiles);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new PostAPI().execute(username, password, "https://api.github.com/gists", object.toString(), "POST");

        Intent intent = new Intent(AddGistActivity.this, ResultSearchActivity.class);
        Gson gson = new Gson();
        intent.putExtra("currentUser", gson.toJson(currentUser));
        intent.putExtra("urlGistsCurrentUser", urlCurrentUser + "/gists");
        startActivity(intent);
    }
}
