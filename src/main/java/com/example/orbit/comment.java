package com.example.orbit;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("comment")
public class comment extends ParseObject {
    public static final String USER_ID_KEY = "userid";
    public static final String BODY_KEY = "body";
    public static final String MESSAGE_KEY = "message";

    public ParseUser getUserId() {
        return getParseUser(USER_ID_KEY);
    }
    public String getBody() {
        return getString(BODY_KEY);
    }
    public Message getMessage(){
   return (Message)getParseObject("message");
    }
    public void setUserId(ParseUser userId) {
        put(USER_ID_KEY, userId);
    }
    public void setBody(String body) {
        put(BODY_KEY, body);
    }
    public void setMessage(Message m){
        put(MESSAGE_KEY, m);
    }
}
