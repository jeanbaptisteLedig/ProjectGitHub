package com.example.jeanbaptisteledig.projectgithub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import org.json.JSONException;
import org.json.JSONObject;

public class AddGistActivity extends AppCompatActivity {

    private String username;
    private String password;
    private Boolean access = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gist);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");

        Log.e("TAG", "addGist: " + username + password );
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
        System.out.println(object);

        Log.e("TAG", "addGist: " + username + password );
        new PostAPI().execute(username, password, "https://api.github.com/gists", object.toString(), "POST");

        Intent intent = new Intent(AddGistActivity.this, MainActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        startActivity(intent);
    }
}
