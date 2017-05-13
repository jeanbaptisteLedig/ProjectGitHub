package com.example.jeanbaptisteledig.projectgithub;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jeanbaptiste.ledig on 13/05/2017.
 */

public class PostAPI extends AsyncTask<String, Void, Void> {
    private static final String TAG = ClientHTTP.class.getSimpleName();

    public PostAPI() {
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            String urlAddGist = params[2];
            URL url = new URL(urlAddGist);
            String username = params[0];
            String password = params[1];
            String credentials = username + ":" + password;
            String basicAuth = "Basic " + new String(Base64.encode(credentials.getBytes(), Base64.DEFAULT));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(500000);
            connection.setRequestMethod(params[4]);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            if (username != null){
                connection.setRequestProperty("Authorization", basicAuth);
            }

            String jsonStr = params[3];
            JSONObject jsonObj = new JSONObject(jsonStr);
            OutputStream os = connection.getOutputStream();
            os.write(jsonObj.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

            connection.connect();

            InputStream inputStream = connection.getInputStream();

        } catch (IOException IOEx){
            Log.e("HTTP", "HTTP failed to fetch data "+IOEx.getMessage());
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
