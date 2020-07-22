package com.example.orbit;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

public class ParseApplication extends Application {





    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(comment.class);
        // Use for troubleshooting -- remove this line for production
        // Use for monitoring Parse OkHttp traffic
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        // See http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("orbitparseapp") // should correspond to APP_ID env variable
                .clientKey("letsdancethenightaway")  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("http://orbitparseapp.herokuapp.com/parse").build());
    }

}
