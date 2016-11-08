package com.fantasysmash.SportsJoust;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.fantasysmash.SportsJoust.R;
import com.parse.ParseInstallation;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class Home extends ActionBarActivity {

    TextView setEdit;
    TextView findMatches;
    TextView potential;
    TextView myContests;

    String userName;
    String bal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        setEdit = (TextView)findViewById(R.id.setEdit);
        findMatches = (TextView)findViewById(R.id.findMatches);
        potential = (TextView)findViewById(R.id.potential);
        myContests = (TextView)findViewById(R.id.myContests);

        SharedPreferences prefs = getSharedPreferences("SportsJoust", MODE_PRIVATE);
        String userText = prefs.getString("userName", null);
        userName = userText;

        //get request to get balance
        String url = "http://52.24.226.232/mgetBal?userName="+userText;
        try {
            String balResp = new GetBal().execute(url).get();
            bal = balResp;
        }
        catch (Exception e){
            //
        }


        setEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setIntent = new Intent(Home.this, ChooseContest.class);
                startActivity(setIntent);
            }
        });

        findMatches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setIntent = new Intent(Home.this, ChooseContestMatch.class);
                startActivity(setIntent);
            }
        });

        potential.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, Potential.class);
                startActivity(i);
            }
        });

        myContests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(Home.this, com.fantasysmash.SportsJoust.myContests.class);
                startActivity(homeIntent);
            }
        });

    }

    class GetBal extends AsyncTask<String, String, String> {
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
        setTitle(userName+"  $"+bal);
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
                Intent i = new Intent(Home.this, Home.class);
                startActivity(i);
                return true;
            case R.id.logOut:
                //parse
                ParseInstallation.getCurrentInstallation().put("device_id", " ");

                // location found
                SharedPreferences prefs = getSharedPreferences("SportsJoust", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.commit();
                finish();

                Intent logOutI = new Intent(Home.this, MainActivity.class);
                startActivity(logOutI);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
