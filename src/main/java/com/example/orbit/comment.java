package com.example.orbit;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("comment")
public class comment extends ParseObject {
    public static final String USER_ID_KEY = "userid";
    public static final String BODY_KEY = "body";
    public static final String MESSAGE_KEY = "message";



    public String getUserId() {
        return getString(USER_ID_KEY);
    }

    public String getBody() {
        return getString(BODY_KEY);
    }
    public Message getMessage(){
   return (Message)getParseObject("message");
    }

    public void setUserId(String userId) {
        put(USER_ID_KEY, userId);
    }

    public void setBody(String body) {
        put(BODY_KEY, body);
    }
    public void setMessage(Message m){
        put(MESSAGE_KEY, m);
    }
}
