package com.fantasysmash.SportsJoust;

import com.parse.Parse;
import com.parse.ParseACL;

import com.parse.ParseInstallation;
import com.parse.ParseUser;

import android.app.Application;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Add your initialization code here
        Parse.initialize(this, "IR5aGhW4MpLSVz4p4hXWXsc2uzbS7oDoThW35NJm", "mKkZGbWr7A0YK5lPhbhz6twjMOFFbxom9irPeSWt");
        ParseInstallation.getCurrentInstallation().saveInBackground();

        /*
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();

        // If you would like all objects to be private by default, remove this
        // line.
        defaultACL.setPublicReadAccess(true);

        ParseACL.setDefaultACL(defaultACL, true);
        */
    }

}
