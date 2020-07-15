package com.example.orbit;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Message")
public class Message extends ParseObject  {

    public static final String KEY_BODY = "Messagebody";
    public static final String KEY_PICTURE = "picture";
    public static final String KEY_AUTHOR= "Author";
    public static final String KEY_CREATEDAT = "createdAt";
    public static final String KEY_HEADER = "Header";
    public static final String KEY_LOCATION = "Location";

    public ParseGeoPoint getLocation(){
        return getParseGeoPoint("Location");
    }

    public void setLocation(ParseGeoPoint geoPoint){
        put(KEY_LOCATION, geoPoint);
    }

    public String getMessagebody(){
        return getString(KEY_BODY);
    }
    public void setMessagebody(String description){
        put(KEY_BODY, description);

    }

    public String getHeader(){
        return getString(KEY_HEADER);
    }
    public void setHeader(String description){
        put(KEY_HEADER, description);

    }
    public ParseFile getPicture(){
        return getParseFile(KEY_PICTURE);
    }
    public void setPicture(ParseFile parseFile){
        put(KEY_PICTURE, parseFile);
    }
    public ParseUser getAuthor(){
        return getParseUser(KEY_AUTHOR);
    }
    public void setAuthor(ParseUser user){
        put(KEY_AUTHOR, user);

    }
    public String getKeyCreatedat(){
        return getCreatedAt().toString();
    }
}
