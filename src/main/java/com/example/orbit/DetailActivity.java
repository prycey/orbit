package com.example.orbit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    static final String USER_ID_KEY = "userId";
    static final String BODY_KEY = "body";
    static final String MESSAGE_KEY = "message";

    EditText etMessage;
    Button btSend;
    TextView Header;
    TextView user;
    TextView descripition;
    ImageView image;
    Message main;
    ArrayList<comment> mMessages;
    ChatAdapter mAdapter;
    RecyclerView rvChat;
    Button delButton;

    // Keep track of initial load to scroll to the bottom of the ListView
    boolean mFirstLoad;

    // Setup message field and posting





    // Get the userId from the cached currentUser object
    void startWithCurrentUser() {
        setupMessagePosting();
    }

    // Setup button event handler which posts the entered message to Parse
    void setupMessagePosting() {
        // Find the text field and button

        // When send button is clicked, create message object on Parse
        etMessage = (EditText) findViewById(R.id.etMessage);
        btSend = (Button) findViewById(R.id.btSend);
        rvChat = (RecyclerView) findViewById(R.id.rvComments);
        mMessages = new ArrayList<>();
        mFirstLoad = true;
        final String userId = ParseUser.getCurrentUser().getObjectId();
        mAdapter = new ChatAdapter(DetailActivity.this, userId, mMessages);
        rvChat.setAdapter(mAdapter);

        // associate the LayoutManager with the RecylcerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DetailActivity.this);
        linearLayoutManager.setReverseLayout(true);
        rvChat.setLayoutManager(linearLayoutManager);



        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = etMessage.getText().toString();
                ParseObject comment = ParseObject.create("comment");
                comment.put(USER_ID_KEY, ParseUser.getCurrentUser());
                comment.put(BODY_KEY, data);
                comment.put(MESSAGE_KEY, main);
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
    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;
    static final int POLL_INTERVAL = 1000; // milliseconds
    Handler myHandler = new android.os.Handler();
    Runnable mRefreshMessagesRunnable = new Runnable() {
        @Override
        public void run() {
            refreshMessages();
            myHandler.postDelayed(this, POLL_INTERVAL);
        }
    };






    // Query messages from Parse so we can load them into the chat adapter
    void refreshMessages() {
        // Construct query to execute
        ParseQuery<comment> query = ParseQuery.getQuery(comment.class);
        // Configure limit and sort order
        query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);
        query.whereEqualTo(MESSAGE_KEY, main);

        // get the latest 50 messages, order will show up newest to oldest of this group
        query.orderByDescending("createdAt");
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<comment>() {
            public void done(List<comment> messages, ParseException e) {
                if (e == null) {
                    mMessages.clear();
                    mMessages.addAll(messages);
                    mAdapter.notifyDataSetChanged(); // update adapter
                    // Scroll to the bottom of the list on initial load
                    if (mFirstLoad) {
                        rvChat.scrollToPosition(0);
                        mFirstLoad = false;
                    }
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (ParseUser.getCurrentUser() != null) {
            startWithCurrentUser();
        }
        myHandler.postDelayed(mRefreshMessagesRunnable, POLL_INTERVAL);
        etMessage = (EditText) findViewById(R.id.etMessage);
        btSend = (Button) findViewById(R.id.btSend);
        delButton = (Button) findViewById(R.id.del);
        user = findViewById(R.id.author);
        Header = findViewById(R.id.header);
        descripition = findViewById(R.id.description);
        image = findViewById(R.id.itempicture);
        main = Parcels.unwrap(getIntent().getParcelableExtra("Message"));
        Log.i("tag2", main.getAuthor().toString() + " "+ ParseUser.getCurrentUser().toString() );
        if(ParseUser.getCurrentUser().getObjectId().equals(main.getAuthor().getObjectId())){
        delButton.setVisibility(View.VISIBLE);
        }
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    main.delete();
                    Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                    startActivity(intent);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

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