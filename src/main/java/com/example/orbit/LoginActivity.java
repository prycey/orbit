package com.example.orbit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {
   public EditText username;
   public EditText password;
   public Button submit;
   public Button newuser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        password = findViewById(R.id.editTextTextPassword);
        username = findViewById(R.id.etUsername);
        submit = findViewById(R.id.etbutton);
        newuser = findViewById(R.id.rbutton);

        newuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser(username.getText().toString(), password.getText().toString());
            }
        });
    }

    public void loginUser(final String usernametemp, final String passwordtemp){
        ParseUser.logInInBackground(usernametemp, passwordtemp, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e != null){
                    Log.e("hey", "esgo", e);
                    Log.e("hey", usernametemp + " " + passwordtemp);
                    return;
                }
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
}