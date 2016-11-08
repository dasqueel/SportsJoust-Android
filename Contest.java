package com.fantasysmash.SportsJoust;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
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

public class Contest extends ActionBarActivity {

    TextView matchUser;
    TextView matchOp;
    TextView uQbName;
    TextView uQbStats;
    TextView uQbPts;
    TextView uRb1Name;
    TextView uRb1Stats;
    TextView uRb1Pts;
    TextView uRb2Name;
    TextView uRb2Stats;
    TextView uRb2Pts;
    TextView uWr1Name;
    TextView uWr1Stats;
    TextView uWr1Pts;
    TextView uWr2Name;
    TextView uWr2Stats;
    TextView uWr2Pts;
    TextView uTeName;
    TextView uTeStats;
    TextView uTePts;
    TextView uDefName;
    TextView uDefStats;
    TextView uDefPts;
    TextView opQbName;
    TextView opQbStats;
    TextView opQbPts;
    TextView opRb1Name;
    TextView opRb1Stats;
    TextView opRb1Pts;
    TextView opRb2Name;
    TextView opRb2Stats;
    TextView opRb2Pts;
    TextView opWr1Name;
    TextView opWr1Stats;
    TextView opWr1Pts;
    TextView opWr2Name;
    TextView opWr2Stats;
    TextView opWr2Pts;
    TextView opTeName;
    TextView opTeStats;
    TextView opTePts;
    TextView opDefName;
    TextView opDefStats;
    TextView opDefPts;
    TextView userTotal;
    TextView opTotal;

    //get mtoken from savedPref
    String mtoken;
    String contestCode;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contest);

        Bundle extras = getIntent().getExtras();
        final String matchId = extras.getString("matchId");
        final String contest = extras.getString("contest");
        contestCode = contest;

        SharedPreferences prefs = getSharedPreferences("SportsJoust", MODE_PRIVATE);
        String token = prefs.getString("mtoken", null);
        mtoken = token;

        matchUser = (TextView)findViewById(R.id.matchUser);
        matchOp = (TextView)findViewById(R.id.matchOp);
        uQbName = (TextView)findViewById(R.id.uQbName);
        uQbStats = (TextView)findViewById(R.id.uQbStats);
        uQbPts = (TextView)findViewById(R.id.uQbPts);
        uRb1Name = (TextView)findViewById(R.id.uRb1Name);
        uRb1Stats = (TextView)findViewById(R.id.uRb1Stats);
        uRb1Pts = (TextView)findViewById(R.id.uRb1Pts);
        uRb2Name = (TextView)findViewById(R.id.uRb2Name);
        uRb2Stats = (TextView)findViewById(R.id.uRb2Stats);
        uRb2Pts = (TextView)findViewById(R.id.uRb2Pts);
        uWr1Name = (TextView)findViewById(R.id.uWr1Name);
        uWr1Stats = (TextView)findViewById(R.id.uWr1Stats);
        uWr1Pts = (TextView)findViewById(R.id.uWr1Pts);
        uWr2Name = (TextView)findViewById(R.id.uWr2Name);
        uWr2Stats = (TextView)findViewById(R.id.uWr2Stats);
        uWr2Pts = (TextView)findViewById(R.id.uWr2Pts);
        uTeName = (TextView)findViewById(R.id.uTeName);
        uTeStats = (TextView)findViewById(R.id.uTeStats);
        uTePts = (TextView)findViewById(R.id.uTePts);
        uDefName = (TextView)findViewById(R.id.uDefName);
        uDefStats = (TextView)findViewById(R.id.uDefStats);
        uDefPts = (TextView)findViewById(R.id.uDefPts);
        opQbName = (TextView)findViewById(R.id.opQbName);
        opQbStats = (TextView)findViewById(R.id.opQbStats);
        opQbPts = (TextView)findViewById(R.id.opQbPts);
        opRb1Name = (TextView)findViewById(R.id.opRb1Name);
        opRb1Stats = (TextView)findViewById(R.id.opRb1Stats);
        opRb1Pts = (TextView)findViewById(R.id.opRb1Pts);
        opRb2Name = (TextView)findViewById(R.id.opRb2Name);
        opRb2Stats = (TextView)findViewById(R.id.opRb2Stats);
        opRb2Pts = (TextView)findViewById(R.id.opRb2Pts);
        opWr1Name = (TextView)findViewById(R.id.opWr1Name);
        opWr1Stats = (TextView)findViewById(R.id.opWr1Stats);
        opWr1Pts = (TextView)findViewById(R.id.opWr1Pts);
        opWr2Name = (TextView)findViewById(R.id.opWr2Name);
        opWr2Stats = (TextView)findViewById(R.id.opWr2Stats);
        opWr2Pts = (TextView)findViewById(R.id.opWr2Pts);
        opTeName = (TextView)findViewById(R.id.opTeName);
        opTeStats = (TextView)findViewById(R.id.opTeStats);
        opTePts = (TextView)findViewById(R.id.opTePts);
        opDefName = (TextView)findViewById(R.id.opDefName);
        opDefStats = (TextView)findViewById(R.id.opDefStats);
        opDefPts = (TextView)findViewById(R.id.opDefPts);
        userTotal = (TextView)findViewById(R.id.userTotal);
        opTotal = (TextView)findViewById(R.id.opTotal);

        String userText = prefs.getString("userName", null);
        matchUser.setText(userText);

        //set get and set teams
        displayMatch(matchId,contest,mtoken);

    }

    class GetMatch extends AsyncTask<String, String, String> {
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
            //Log.i("async", responseString);
            return responseString;

        }
    }

    public void displayMatch(String matchId,String contest,String mtoken) {
        String url = "http://52.24.226.232/mgetContest?mtoken="+mtoken+"&matchId="+matchId+"&contest="+contest;
        try {
            String res = new GetMatch().execute(url).get();
            //Log.i("please", stuff);

            JSONObject json = new JSONObject(res);

            //turn json object to array with function
            String uQb = json.getString("uQb");
            uQbName.setText(uQb);
            uQbStats.setText(json.getString("uQbStatStr"));
            uQbPts.setText(json.getString("uQbPts"));
            String uRb1 = json.getString("uRb1");
            uRb1Name.setText(uRb1);
            uRb1Stats.setText(json.getString("uRb1StatStr"));
            uRb1Pts.setText(json.getString("uRb1Pts"));
            String uRb2 = json.getString("uRb2");
            uRb2Name.setText(uRb2);
            uRb2Stats.setText(json.getString("uRb2StatStr"));
            uRb2Pts.setText(json.getString("uRb2Pts"));
            String uWr1 = json.getString("uWr1");
            uWr1Name.setText(uWr1);
            uWr1Stats.setText(json.getString("uWr1StatStr"));
            uWr1Pts.setText(json.getString("uWr1Pts"));
            String uWr2 = json.getString("uWr2");
            uWr2Name.setText(uWr2);
            uWr2Stats.setText(json.getString("uWr2StatStr"));
            uWr2Pts.setText(json.getString("uWr2Pts"));
            String uTe = json.getString("uTe");
            uTeName.setText(uTe);
            uTeStats.setText(json.getString("uTeStatStr"));
            uTePts.setText(json.getString("uTePts"));
            String uDef = json.getString("uDef");
            uDefName.setText(uDef);
            uDefStats.setText(json.getString("uDefStatStr"));
            uDefPts.setText(json.getString("uDefPts"));
            String matchOpName = json.getString("opTeam");
            matchOp.setText(matchOpName);
            String opQb = json.getString("opQb");
            opQbName.setText(opQb);
            opQbStats.setText(json.getString("opQbStatStr"));
            opQbPts.setText(json.getString("opQbPts"));
            String opRb1 = json.getString("opRb1");
            opRb1Name.setText(opRb1);
            opRb1Stats.setText(json.getString("opRb1StatStr"));
            opRb1Pts.setText(json.getString("opRb1Pts"));
            String opRb2 = json.getString("opRb2");
            opRb2Name.setText(opRb2);
            opRb2Stats.setText(json.getString("opRb2StatStr"));
            opRb2Pts.setText(json.getString("opRb2Pts"));
            String opWr1 = json.getString("opWr1");
            opWr1Name.setText(opWr1);
            opWr1Stats.setText(json.getString("opWr1StatStr"));
            opWr1Pts.setText(json.getString("opWr1Pts"));
            String opWr2 = json.getString("opWr2");
            opWr2Name.setText(opWr2);
            opWr2Stats.setText(json.getString("opWr2StatStr"));
            opWr2Pts.setText(json.getString("opWr2Pts"));
            String opTe = json.getString("opTe");
            opTeName.setText(opTe);
            opTeStats.setText(json.getString("opTeStatStr"));
            opTePts.setText(json.getString("opTePts"));
            String opDef = json.getString("opDef");
            opDefName.setText(opDef);
            opDefStats.setText(json.getString("opDefStatStr"));
            opDefPts.setText(json.getString("opDefPts"));
            userTotal.setText(json.getString("userTotal"));
            opTotal.setText(json.getString("opTotal"));

        }
        catch (Exception e) {
            //Log.d("stuff", e.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        setTitle(contestCode+" match");
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
                Intent i = new Intent(Contest.this, Home.class);
                startActivity(i);
                return true;
            case R.id.logOut:
                // location found
                SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.commit();
                finish();

                Intent logOutI = new Intent(Contest.this, MainActivity.class);
                startActivity(logOutI);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
