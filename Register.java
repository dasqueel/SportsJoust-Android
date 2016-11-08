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
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class Register extends Activity {

    Button regBtn;
    EditText userNameInput;
    EditText firstInput;
    EditText lastInput;
    EditText emailInput;
    EditText pwdInput;
    EditText rpwdInput;

    private static String TAG = MainActivity.class.getSimpleName();
    SharedPreferences sharedpreferences;

    //parse
    //ParseInstallation installation = ParseInstallation.getCurrentInstallation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedpreferences = getSharedPreferences("SportsJoust", Context.MODE_PRIVATE);

        //parse stuff
        //Parse.initialize(this, "uBK0GW8nzkRjRD3l1xnlJBTlBk8DdopTEdGddjLz", "AB7zBYrXQCnLOQobtJfluh2Ngqe4hNmbrl8WgUl0");
        //ParseInstallation.getCurrentInstallation().saveInBackground();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        regBtn = (Button)findViewById(R.id.regBtn);
        userNameInput   = (EditText)findViewById(R.id.userNameInput);
        firstInput   = (EditText)findViewById(R.id.firstInput);
        lastInput   = (EditText)findViewById(R.id.lastInput);
        emailInput   = (EditText)findViewById(R.id.emailInput);
        pwdInput = (EditText)findViewById(R.id.pwdInput);
        rpwdInput   = (EditText)findViewById(R.id.rpwdInput);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SessionIdentifierGenerator generator = new SessionIdentifierGenerator();
                String newToken = generator.nextSessionId();

                final String firstName = firstInput.getText().toString();
                final String lastName = lastInput.getText().toString();
                final String email = emailInput.getText().toString();
                final String username = userNameInput.getText().toString();
                final String pwd = pwdInput.getText().toString();
                final String rpwd = rpwdInput.getText().toString();

                // making json object request
                //makeJsonObjectRequest(url);

                //post string volley
                strObjReq("http://52.24.226.232/mregister", firstName, lastName, email, username, pwd, rpwd, newToken);
            }
        });

    }

    private void strObjReq(String url,final String firstName, final String lastName, final String email, final String userName, final String pwd, final String rpwd, final String newToken) {

        StringRequest myReq = new StringRequest(Method.POST,
                url,
                createMyReqSuccessListener(newToken),
                createMyReqErrorListener()) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("firstName", firstName);
                params.put("lastName", lastName);
                params.put("email", email);
                params.put("userName", userName);
                params.put("pwd", pwd);
                params.put("rpwd", rpwd);
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

                if (response.equals("registered"))
                {
                    //set session
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    String u = userNameInput.getText().toString();
                    String p = pwdInput.getText().toString();
                    editor.putString("userName", u);
                    editor.putString("pwd", p);
                    editor.putString("mtoken", mtoken);
                    //editor.putString("bal",bal);
                    editor.commit();

                    // Save new user data into Parse.com Data Storage
                    ParseUser user = new ParseUser();
                    user.setUsername(u);
                    user.setPassword(p);
                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                // Hooray! Let them use the app now.
                            } else {
                                // Sign up didn't succeed. Look at the ParseException
                                // to figure out what went wrong
                            }
                        }
                    });

                    //save user to parse
                    ParseInstallation.getCurrentInstallation().put("device_id", u);
                    ParseInstallation.getCurrentInstallation().saveInBackground();

                    //switch to home screen
                    Intent myIntent = new Intent(Register.this, Home.class);
                    startActivity(myIntent);


                } else if (response.equals("please fill in all data")){
                    Toast.makeText(getApplicationContext(),"please fill in all data",Toast.LENGTH_LONG).show();
                } else if (response.equals("passwords dont match, try again")){
                    Toast.makeText(getApplicationContext(),"passwords dont match, try again",Toast.LENGTH_LONG).show();
                } else if (response.equals("passwords dont match, try again")){
                    Toast.makeText(getApplicationContext(),"passwords dont match, try again",Toast.LENGTH_LONG).show();
                } else if (response.equals("userName has been taken")){
                    Toast.makeText(getApplicationContext(),"userName has been taken",Toast.LENGTH_LONG).show();
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

    public final class SessionIdentifierGenerator {
        private SecureRandom random = new SecureRandom();

        public String nextSessionId() {
            return new BigInteger(130, random).toString(32);
        }
    }

}