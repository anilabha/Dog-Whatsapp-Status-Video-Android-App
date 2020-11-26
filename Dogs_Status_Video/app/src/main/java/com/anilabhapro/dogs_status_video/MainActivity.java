package com.anilabhapro.dogs_status_video;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.potyvideo.library.AndExoPlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {
    String shareVideo;
    AndExoPlayerView andExoPlayerView;
    int pagenumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fetchVideo();
    }


    public void showNextVideo(View view) {
        fetchVideo();
    }

    public void shareVideo(View view) {
        Intent myintent = new Intent(Intent.ACTION_SEND);
        myintent.setType("text/plan");

        String shereBoday = "Your Apps are Here";

        String shereSub = shareVideo;
        myintent.putExtra(Intent.EXTRA_SUBJECT, shereBoday);
        myintent.putExtra(Intent.EXTRA_TEXT, shereSub);
        startActivity(Intent.createChooser(myintent, "Share Using"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.download) {

            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(shareVideo);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            downloadManager.enqueue(request);
            Toast.makeText(this, "Downloading Start", Toast.LENGTH_SHORT).show();

            return true;
        }
        return super.onOptionsItemSelected(item);

    }


    public void fetchVideo() {
        ProgressDialog pro = new ProgressDialog(this);
        pro.setMessage("Loding....");
        pro.show();
        String url = "https://api.pexels.com/videos/search?query=dog&per_page=1" + pagenumber;
        //RequestQueue queue = Volley.newRequestQueue(this);
        andExoPlayerView = findViewById(R.id.memeImageView);
        RequestQueue queue = MySingleton.getInstance(this.getApplicationContext()).
                getRequestQueue();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(JSONObject response) {


                        try {


                            JSONArray jsonArray = response.getJSONArray("videos");

                            int length = jsonArray.length();

                            JSONObject obj = jsonArray.getJSONObject(rand(length));
                            JSONArray arr = obj.getJSONArray("video_files");
                            JSONObject object = arr.getJSONObject(3);


                            int id = object.getInt("id");


                            String videolink = object.getString("link");
                            String quality = object.getString("quality");
                            shareVideo = videolink;

                            andExoPlayerView.setSource(videolink);
                            pagenumber++;


                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            pro.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Loding Failed!!", Toast.LENGTH_SHORT).show();
                        pro.dismiss();


                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "563492ad6f91700001000001b5ee10d1e8994d0ab970ba1fc0fc0156");

                return params;
            }
        };
        ;
//        queue.add(jsonObjectRequest);

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }
    public List<Integer> keys = new ArrayList<Integer>();

    public int rand(int m) {
        int n = (int) (Math.random() * m );
        if (!keys.contains(n)) {
            keys.add(n);
            return n;
        } else {
            return rand(m);
        }
    }


}