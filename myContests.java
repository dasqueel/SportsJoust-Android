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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fantasysmash.SportsJoust.R;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class myContests extends ActionBarActivity {
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contests);

        SharedPreferences prefs = getSharedPreferences("SportsJoust", MODE_PRIVATE);
        String mtoken = prefs.getString("mtoken", null);

        List<String> matches = new ArrayList<String>();
        final List<String> matchIds = new ArrayList<String>(); //matchesId to each match
        final List<String> contestList = new ArrayList<String>();

        String url = "http://52.24.226.232/muserContests?mtoken="+mtoken;
        try {

            String res = new GetRank().execute(url).get();
            //Log.i("please", res.toString());

            JSONObject json = new JSONObject(res);

            //turn json object to array with function
            JSONArray matchesJson = json.getJSONArray("matches");
            JSONArray matchIdsJson = json.getJSONArray("matchIds");
            JSONArray contestListJson = json.getJSONArray("contestList");
            for(int i=0;i<matchesJson.length();i++)
            {
                matches.add(matchesJson.get(i).toString());
                matchIds.add(matchIdsJson.get(i).toString());
                contestList.add(contestListJson.get(i).toString());
                //Log.i("match", matchesJson.get(i).toString());
                //Log.i("id",matchIdsJson.get(i).toString());
            }
        }
        catch (Exception e) {
            Log.d("listerror", e.toString());
        }

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, matches);


        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                Log.i("posClick", new Integer(itemPosition).toString());

                //go to matches main page
                Intent i = new Intent(getApplicationContext(), Contest.class);
                i.putExtra("matchId", matchIds.get(position));
                i.putExtra("contest", contestList.get(position));
                startActivity(i);

            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        setTitle("My Matches");
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
                Intent i = new Intent(myContests.this, Home.class);
                startActivity(i);
                return true;
            case R.id.logOut:
                // location found
                SharedPreferences prefs = getSharedPreferences("SportsJoust", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.commit();
                finish();

                Intent logOutI = new Intent(myContests.this, MainActivity.class);
                startActivity(logOutI);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class GetRank extends AsyncTask<String, String, String> {
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
}
