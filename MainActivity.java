package com.fantasysmash.SportsJoust;

import com.android.volley.toolbox.StringRequest;
import com.fantasysmash.SportsJoust.app.AppController;
import com.fantasysmash.SportsJoust.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.parse.Parse;
import com.parse.ParseInstallation;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    Button btnLogin;
    TextView regBtn;
    EditText userName;
    EditText pwd;

    private static String TAG = MainActivity.class.getSimpleName();
    SharedPreferences sharedpreferences;

    //parse
    //ParseInstallation installation = ParseInstallation.getCurrentInstallation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //parse stuff
        initParse();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = (Button)findViewById(R.id.btnlogin);
        regBtn = (TextView)findViewById(R.id.register);
        userName   = (EditText)findViewById(R.id.userName);
        pwd = (EditText)findViewById(R.id.pwd);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SessionIdentifierGenerator generator = new SessionIdentifierGenerator();
                String newToken = generator.nextSessionId();

                final String username = userName.getText().toString();
                final String password = pwd.getText().toString();
                //final String url = "http://52.24.226.232/mlogin?userName="+username+"&pwd="+password+"&mtoken="+newToken;

                // making json object request
                //makeJsonObjectRequest(url);

                //post string volley
                strObjReq("http://52.24.226.232/mlogin", username, password, newToken);
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(MainActivity.this, Register.class);
                startActivity(regIntent);
            }
        });
    }

    private void strObjReq(String url,final String userName, final String pwd, final String newToken) {

        StringRequest myReq = new StringRequest(Method.POST,
                url,
                createMyReqSuccessListener(newToken),
                createMyReqErrorListener()) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userName", userName);
                params.put("pwd", pwd);
                params.put("mtoken", newToken);
                return params;
            };
        };
        AppController.getInstance().addToRequestQueue(myReq);

    }

    private Response.Listener<String> createMyReqSuccessListener(final String mtoken) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.equals("yes"))
                {
                    //set session
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    String u = userName.getText().toString();
                    String p = pwd.getText().toString();
                    editor.putString("userName", u);
                    editor.putString("pwd", p);
                    editor.putString("mtoken", mtoken);
                    //editor.putString("bal",bal);
                    editor.commit();

                    //save user to parse
                    ParseInstallation.getCurrentInstallation().put("device_id", u);
                    ParseInstallation.getCurrentInstallation().saveInBackground();

                    //switch to home screen
                    Intent myIntent = new Intent(MainActivity.this, Home.class);
                    startActivity(myIntent);


                } else if (response.equals("no un")){
                    Toast.makeText(getApplicationContext(),"username not registered",Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),"incorrect password",Toast.LENGTH_LONG).show();
                }
                //Log.i("logSponse",response);
            }
        };
    }


    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTvResult.setText(error.getMessage());
                Log.i("logSponse",error.toString());
            }
        };
    }

    //checks to see if use
    @Override
    protected void onResume() {
        sharedpreferences=getSharedPreferences("SportsJoust",
                Context.MODE_PRIVATE);
        if (sharedpreferences.contains("userName"))
        {
            if(sharedpreferences.contains("pwd")){
                Intent i = new Intent(this,
                        Home.class);
                startActivity(i);
            }
        }
        super.onResume();
    }

    public final class SessionIdentifierGenerator {
        private SecureRandom random = new SecureRandom();

        public String nextSessionId() {
            return new BigInteger(130, random).toString(32);
        }
    }

    private void initParse() {
        try {
            Parse.initialize(getApplication(), "IR5aGhW4MpLSVz4p4hXWXsc2uzbS7oDoThW35NJm", "mKkZGbWr7A0YK5lPhbhz6twjMOFFbxom9irPeSWt");
            ParseInstallation.getCurrentInstallation().saveInBackground();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}