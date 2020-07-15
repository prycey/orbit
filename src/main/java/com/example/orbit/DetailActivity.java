package com.example.orbit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

public class DetailActivity extends AppCompatActivity {
    static final String USER_ID_KEY = "userId";
    static final String BODY_KEY = "body";

    EditText etMessage;
    Button btSend;
    TextView Header;
    TextView user;
    TextView descripition;
    ImageView image;
    Message main;



    // Get the userId from the cached currentUser object
    void startWithCurrentUser() {
        setupMessagePosting();
    }

    // Setup button event handler which posts the entered message to Parse
    void setupMessagePosting() {
        // Find the text field and button

        // When send button is clicked, create message object on Parse
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = etMessage.getText().toString();
                ParseObject comment = ParseObject.create("Comment");
                comment.put(USER_ID_KEY, ParseUser.getCurrentUser().getObjectId());
                comment.put(BODY_KEY, data);
                comment.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null) {
                            Toast.makeText(DetailActivity.this, "Successfully created comment on Parse",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("hfdsjh", "Failed to save comment", e);
                        }
                    }
                });
                etMessage.setText(null);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        etMessage = (EditText) findViewById(R.id.etMessage);
        btSend = (Button) findViewById(R.id.btSend);
        user = findViewById(R.id.author);
        Header = findViewById(R.id.header);
        descripition = findViewById(R.id.description);
        image = findViewById(R.id.itempicture);
        main = Parcels.unwrap(getIntent().getParcelableExtra("Message"));

        Log.i("pop", main.toString());
        user.setText(main.getAuthor().getUsername());
        Header.setText(main.getHeader());
        descripition.setText(main.getMessagebody());
        ParseFile image2 = main.getPicture();


        if (image != null) {
            Glide.with(this).load(main.getPicture().getUrl()).into(image);
        }
        //dvDate.setText(detailView.getKeyCreatedat());

        setupMessagePosting();

    }
}