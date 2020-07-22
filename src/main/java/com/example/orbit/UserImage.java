package com.example.orbit;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("UserImage")
public class UserImage extends ParseObject {
    public static final String KEY_USER = "User";
    public static final String KEY_IMAGE = "image";

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }
    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }
    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }
    public void setImage(ParseFile file){
      put(KEY_IMAGE, file);
    }
}
