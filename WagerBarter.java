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
import android.widget.EditText;
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

public class WagerBarter extends ActionBarActivity {

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
    EditText mwager;

    Button sendWager;
    Button accept;

    //get mtoken from savedPref
    String mtoken;
    String maxWager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wagerbarter);

        SharedPreferences prefs = getSharedPreferences("SportsJoust", MODE_PRIVATE);
        String token = prefs.getString("mtoken", null);
        mtoken = token;

        Bundle extras = getIntent().getExtras();
        final String matchId = extras.getString("matchId");
        final String contest = extras.getString("contest");
        final String turn = extras.getString("turn");

        matchUser = (TextView)findViewById(R.id.matchUser);
        matchOp = (TextView)findViewById(R.id.matchOp);
        uQbName = (TextView)findViewById(R.id.uQbName);
        uRb1Name = (TextView)findViewById(R.id.uRb1Name);
        uRb2Name = (TextView)findViewById(R.id.uRb2Name);
        uWr1Name = (TextView)findViewById(R.id.uWr1Name);
        uWr2Name = (TextView)findViewById(R.id.uWr2Name);
        uTeName = (TextView)findViewById(R.id.uTeName);
        uDefName = (TextView)findViewById(R.id.uDefName);
        opQbName = (TextView)findViewById(R.id.opQbName);
        opRb1Name = (TextView)findViewById(R.id.opRb1Name);
        opRb2Name = (TextView)findViewById(R.id.opRb2Name);
        opWr1Name = (TextView)findViewById(R.id.opWr1Name);
        opWr2Name = (TextView)findViewById(R.id.opWr2Name);
        opTeName = (TextView)findViewById(R.id.opTeName);
        opDefName = (TextView)findViewById(R.id.opDefName);
        sendWager = (Button) findViewById(R.id.sendWager);
        accept = (Button) findViewById(R.id.accept);
        mwager = (EditText) findViewById(R.id.wager);

        //disable buttons if not users turn
        if (turn.equals("Opp Turn")) {
            sendWager.setEnabled(false);
            accept.setEnabled(false);
        }

        String userText = prefs.getString("userName", null);
        matchUser.setText(userText);

        //set get and set teams
        displayMatch(matchId,contest,mtoken);

        sendWager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int wage = Integer.valueOf(mwager.getText().toString());
                postWager("http://52.24.226.232/msetWager", contest, mtoken, matchId, wage);
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postAccept("http://52.24.226.232/maccept", contest, mtoken, matchId);
            }
        });

    }

    private void postWager(String url, String contest, String mtoken, String matchId, int wage) {
        JSONObject postJson = new JSONObject();
        try {
            postJson.put("contest", contest);
            postJson.put("mtoken", mtoken);
            postJson.put("matchId", matchId);
            postJson.put("wager", wage);
        }
        catch (Exception e) {
            //
            Log.d("postOc",e.toString());
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, postJson, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                //Log.d("volleyResp", response.getString("outcome"));

                try {
                    // Parsing json object response
                    // response will be a json object
                    String resp = response.getString("outcome");

                    if (resp.equals("set new wager and turn"))
                    {
                        //go to matches main page
                        Intent i = new Intent(getApplicationContext(), Home.class);
                        startActivity(i);

                    } else if (resp.equals("user insufficient")) {
                        //error handle -- nothing went through
                        Toast.makeText(getApplicationContext(),
                                "You have insufficient funds",
                                Toast.LENGTH_LONG).show();
                        mwager.setHint("Max Wager: $" + maxWager);

                    } else if (resp.equals("opp insufficient")) {
                        //error handle -- nothing went through
                        Toast.makeText(getApplicationContext(),
                                "Opponent has insufficient funds",
                                Toast.LENGTH_LONG).show();
                        mwager.setHint("Max Wager: $" + maxWager);
                    } else {
                        //nothing worked
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("jsonError", e.toString());
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
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

    private void postAccept(String url, String contest, String mtoken, String matchId) {
        JSONObject postJson = new JSONObject();
        try {
            postJson.put("contest", contest);
            postJson.put("mtoken", mtoken);
            postJson.put("matchId", matchId);
        }
        catch (Exception e) {
            //
            //Log.d("postAccept",e.toString());
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, postJson, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                //Log.d("volleyResp", response.getString("outcome"));

                try {
                    // Parsing json object response
                    // response will be a json object
                    String resp = response.getString("outcome");
                    String maxWager = response.getString("maxWager");

                    if (resp.equals("match accepted"))
                    {
                        //go to matches main page
                        Intent i = new Intent(getApplicationContext(), Home.class);
                        startActivity(i);

                        Toast.makeText(getApplicationContext(),
                                "match made!",
                                Toast.LENGTH_LONG).show();

                    } else if (resp.equals("user insufficient")) {
                        //error handle -- nothing went through
                        Toast.makeText(getApplicationContext(),
                                "You have insufficient funds",
                                Toast.LENGTH_LONG).show();
                        mwager.setHint("Max Wager: $" + maxWager);
                        Log.i("maxAccept",maxWager);

                    } else if (resp.equals("opp insufficient")) {
                        //error handle -- nothing went through
                        Toast.makeText(getApplicationContext(),
                                "Opponent has insufficient funds",
                                Toast.LENGTH_LONG).show();
                        mwager.setHint("Max Wager: $" + maxWager);
                    } else {
                        //nothing worked
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("jsonError", e.toString());
                    Toast.makeText(getApplicationContext(),
                            "You have insufficient funds",
                            Toast.LENGTH_LONG).show();
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
        String url = "http://52.24.226.232/mbarter?mtoken="+mtoken+"&matchId="+matchId+"&contest="+contest;
        try {
            String res = new GetMatch().execute(url).get();
            //Log.i("please", stuff);

            JSONObject json = new JSONObject(res);

            //contest = json.getString("contest");

            //turn json object to array with function
            String uQb = json.getString("uQb");
            uQbName.setText(uQb);
            String uRb1 = json.getString("uRb1");
            uRb1Name.setText(uRb1);
            String uRb2 = json.getString("uRb2");
            uRb2Name.setText(uRb2);
            String uWr1 = json.getString("uWr1");
            uWr1Name.setText(uWr1);
            String uWr2 = json.getString("uWr2");
            uWr2Name.setText(uWr2);
            String uTe = json.getString("uTe");
            uTeName.setText(uTe);
            String uDef = json.getString("uDef");
            uDefName.setText(uDef);
            String matchOpName = json.getString("opTeam");
            matchOp.setText(matchOpName);
            String opQb = json.getString("opQb");
            opQbName.setText(opQb);
            String opRb1 = json.getString("opRb1");
            opRb1Name.setText(opRb1);
            String opRb2 = json.getString("opRb2");
            opRb2Name.setText(opRb2);
            String opWr1 = json.getString("opWr1");
            opWr1Name.setText(opWr1);
            String opWr2 = json.getString("opWr2");
            opWr2Name.setText(opWr2);
            String opTe = json.getString("opTe");
            opTeName.setText(opTe);
            String opDef = json.getString("opDef");
            opDefName.setText(opDef);

            maxWager = json.getString("maxWager");
            //set maxwager hint
            mwager.setHint("Max Wager: $"+maxWager);

            //set accept button current wage
            String currentWager = json.getString("currentWager");

            accept.setText("Accept $"+currentWager);


            //got if from match thru activity passing
            //String wager = json.getString("wager");

            Log.i("current", currentWager);
            //Log.i("opQb", opQb);
        }
        catch (Exception e) {
            //Log.d("stuff", e.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        setTitle("Set Wager");
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
                Intent i = new Intent(WagerBarter.this, Home.class);
                startActivity(i);
                return true;
            case R.id.logOut:
                // location found
                SharedPreferences prefs = getSharedPreferences("SportsJoust", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.commit();
                finish();

                Intent logOutI = new Intent(WagerBarter.this, MainActivity.class);
                startActivity(logOutI);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
