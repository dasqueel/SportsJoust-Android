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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fantasysmash.SportsJoust.R;
import com.fantasysmash.SportsJoust.app.AppController;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class Match extends ActionBarActivity {
    // remember to get matchId from get request
    TextView matchUser;
    TextView matchOp;
    TextView uQbName;
    TextView uRb1Name;
    TextView uRb2Name;
    TextView uWr1Name;
    TextView uWr2Name;
    TextView uTeName;
    TextView uDefName;
    TextView opQbName;
    TextView opRb1Name;
    TextView opRb2Name;
    TextView opWr1Name;
    TextView opWr2Name;
    TextView opTeName;
    TextView opDefName;
    Button reject;
    Button accept;

    String mtoken;
    //get contest variable from chooseContestMatch activity
    //or each different contest has its own match activity, e.x. matchNfl
    String contest;
    String matchId;
    String opTeam;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match);

        Intent intent = getIntent();
        String contestCode = intent.getStringExtra("contest");
        contest = contestCode;

        SharedPreferences prefs = getSharedPreferences("SportsJoust", MODE_PRIVATE);
        String token = prefs.getString("mtoken", null);
        mtoken = token;

        matchUser = (TextView) findViewById(R.id.matchUser);
        matchOp = (TextView) findViewById(R.id.matchOp);
        uQbName = (TextView) findViewById(R.id.uQbName);
        uRb1Name = (TextView) findViewById(R.id.uRb1Name);
        uRb2Name = (TextView) findViewById(R.id.uRb2Name);
        uWr1Name = (TextView) findViewById(R.id.uWr1Name);
        uWr2Name = (TextView) findViewById(R.id.uWr2Name);
        uTeName = (TextView) findViewById(R.id.uTeName);
        uDefName = (TextView) findViewById(R.id.uDefName);
        opQbName = (TextView) findViewById(R.id.opQbName);
        opRb1Name = (TextView) findViewById(R.id.opRb1Name);
        opRb2Name = (TextView) findViewById(R.id.opRb2Name);
        opWr1Name = (TextView) findViewById(R.id.opWr1Name);
        opWr2Name = (TextView) findViewById(R.id.opWr2Name);
        opTeName = (TextView) findViewById(R.id.opTeName);
        opDefName = (TextView) findViewById(R.id.opDefName);
        reject = (Button) findViewById(R.id.reject);
        accept = (Button) findViewById(R.id.accept);

        String userText = prefs.getString("userName", null);
        matchUser.setText(userText);

        displayMatch(mtoken,contest);

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postOc("http://52.24.226.232/mpostMatch", opTeam, contest, mtoken, "reject", matchId);
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postOc("http://52.24.226.232/mpostMatch", opTeam, contest, mtoken, "accept", matchId);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        setTitle(contest + " matches");
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
                Intent i = new Intent(Match.this, Home.class);
                startActivity(i);
                return true;
            case R.id.logOut:
                // location found
                SharedPreferences prefs = getSharedPreferences("SportsJoust", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.commit();
                finish();

                Intent logOutI = new Intent(Match.this, MainActivity.class);
                startActivity(logOutI);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void displayMatch(String mtoken, String contest) {
        String url = "http://52.24.226.232/mgetMatch?mtoken=" + mtoken + "&contest=" + contest;
        try {
            String res = new GetMatch().execute(url).get();
            //Log.i("getMatchResp", res);

            if (res.equals("no possible matches")) {
                RelativeLayout formLayout = (RelativeLayout) findViewById(R.id.relaId);
                formLayout.removeAllViews();
                formLayout.addView(matchOp);
                matchOp.setText("no possible matches at this time");
            } else if (res.equals("no rankings entered")) {
                Intent i = new Intent(Match.this, QbsDSLV.class);
                i.putExtra("contest", contest);
                startActivity(i);
            }
            else if (res.equals("match not possible")) {
                displayMatch(mtoken,contest);
            }
            else {
                //Log.i("if","nope");
                JSONObject json = new JSONObject(res);

                matchId = json.getString("matchId");
                //Log.i("matchId", matchId);
                //turn json object to array with function
                String uQb = json.getString("uQb");
                uQbName.setText(uQb);
                //postJson.put("uQb",uQb);
                String uRb1 = json.getString("uRb1");
                uRb1Name.setText(uRb1);
                //postJson.put("uRb1", uRb1);
                String uRb2 = json.getString("uRb2");
                uRb2Name.setText(uRb2);
                //postJson.put("uRb2", uRb2);
                String uWr1 = json.getString("uWr1");
                uWr1Name.setText(uWr1);
                //postJson.put("uWr1", uWr1);
                String uWr2 = json.getString("uWr2");
                uWr2Name.setText(uWr2);
                //postJson.put("uWr2", uWr2);
                String uTe = json.getString("uTe");
                uTeName.setText(uTe);
                //postJson.put("uTe", uTe);
                String uDef = json.getString("uDef");
                uDefName.setText(uDef);
                //postJson.put("uDef", uDef);
                String matchOpName = json.getString("opTeam");
                opTeam = json.getString("opTeam");
                matchOp.setText(matchOpName);
                //postJson.put("opTeam", matchOpName);
                String opQb = json.getString("opQb");
                opQbName.setText(opQb);
                //postJson.put("opQb", opQb);
                String opRb1 = json.getString("opRb1");
                opRb1Name.setText(opRb1);
                //postJson.put("opRb1", opRb1);
                String opRb2 = json.getString("opRb2");
                opRb2Name.setText(opRb2);
                //postJson.put("opRb2", opRb2);
                String opWr1 = json.getString("opWr1");
                opWr1Name.setText(opWr1);
                //postJson.put("opWr1", opWr1);
                String opWr2 = json.getString("opWr2");
                opWr2Name.setText(opWr2);
                //postJson.put("opWr2", opWr2);
                String opTe = json.getString("opTe");
                opTeName.setText(opTe);
                //postJson.put("opTe", opTe);
                String opDef = json.getString("opDef");
                opDefName.setText(opDef);
                //postJson.put("opDef",opDef);

            }
        } catch (Exception e) {
            //Log.i("stuff", e.toString());
        }
        //return postJson;
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
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();
                    out.close();
                } else {
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

    public void postOc(String url, String opTeam, final String contest, final String mtoken, String outcome, final String matchId) {
        JSONObject jsonData = new JSONObject();
        //Log.i("opTeam",opTeam);
        try {
            jsonData.put("contest", contest);
            jsonData.put("opTeam", opTeam);
            jsonData.put("mtoken", mtoken);
            jsonData.put("outcome", outcome);
            jsonData.put("matchId", matchId);
        } catch (Exception e) {
            //
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonData, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                //Log.d("volleyResp", response.getString("outcome"));

                try {
                    // Parsing json object response
                    // response will be a json object
                    String matchResp = response.getString("matchResp");
                    //String matchId = response.getString("matchId");
                    String maxWager = response.getString("maxWager");
                    Log.i("postResp", matchResp);

                    if (matchResp.equals("rejection completed")) {
                        //get another match
                        displayMatch(mtoken, contest);
                        //refresh
                        //finish();
                        //startActivity(getIntent());

                    } else if (matchResp.equals("matched")) {
                        //make intest to wager activity
                        //pass match variables to wager activity
                        Intent i = new Intent(getApplicationContext(), Wager.class);
                        i.putExtra("contest", contest);
                        i.putExtra("matchId", matchId);
                        startActivity(i);
                        //Log.i("matchJson",response.toString());
                    } else if (matchResp.equals("added user to opponents potential")) {
                        //first to accept, get another match
                        //Log.i("check","got here");
                        displayMatch(mtoken, contest);
                        //refresh
                        //finish();
                        //startActivity(getIntent());
                    } else if (matchResp.equals("no rankings entered")) {
                        Intent i = new Intent(getApplicationContext(), QbsDSLV.class);
                        i.putExtra("contest", contest);
                        startActivity(i);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("jsonError", e.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("volleyError", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public class Globals {
        public String opTeam;
        public String matchId;
    }
}
