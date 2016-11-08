package com.fantasysmash.SportsJoust;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.fantasysmash.SportsJoust.R;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class ChooseContest extends ActionBarActivity {

    TextView ncaa;
    TextView nfl;
    String ncaaContest;
    String nflContest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose);

        ncaa = (TextView)findViewById(R.id.ncaa);
        nfl = (TextView)findViewById(R.id.nfl);

        //create rank map for nfl
        nfl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChooseContest.this, QbsDSLV.class);
                i.putExtra("contest", nflContest);
                startActivity(i);
            }
        });
        ncaa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChooseContest.this, QbsDSLV.class);
                i.putExtra("contest", ncaaContest);
                startActivity(i);
            }
        });

        String url = "http://52.24.226.232/mgetContests";
        try {

            String res = new GetContests().execute(url).get();
            //Log.i("contests",res);
            //Log.i("please", stuff);

            JSONObject json = new JSONObject(res);
            String ncaaStr = json.getString("ncaa");
            String nflStr = json.getString("nfl");
            ncaa.setText(ncaaStr);
            nfl.setText(nflStr);


            //turn json object to array with function
            //JSONArray jsonContests = json.getJSONArray("contests");
            ncaaContest = json.getString("ncaaCode");
            nflContest = json.getString("nflCode");

        }
        catch (Exception e) {
            //Log.d("stuff", "nope");
        }
    }

    class GetContests extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(uri[0]));
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();
                    out.close();
                } else{
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                //TODO Handle problems..
            } catch (IOException e) {
                //TODO Handle problems..
            }
            // Log.i("async", responseString);
            return responseString;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        setTitle("Set/Edit Rankings");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_settings:
                // search action
                return true;
            case R.id.home:
                // location found
                Intent i = new Intent(ChooseContest.this, Home.class);
                startActivity(i);
                return true;
            case R.id.logOut:
                // location found
                SharedPreferences prefs = getSharedPreferences("SportsJoust", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.commit();
                finish();

                Intent logOutI = new Intent(ChooseContest.this, MainActivity.class);
                startActivity(logOutI);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
