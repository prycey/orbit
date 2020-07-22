package com.example.orbit.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.example.orbit.DetailActivity;
import com.example.orbit.EndlessRecyclerViewScrollListener;
import com.example.orbit.MainActivity;
import com.example.orbit.Message;
import com.example.orbit.MessageAdapter;
import com.example.orbit.R;
import com.example.orbit.locationGive;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessagesFragment extends Fragment {
    private RecyclerView rvPosts;
    private EndlessRecyclerViewScrollListener scrollListener;
    protected MessageAdapter adapter;
    protected List<Message> allPosts;
    public ParseGeoPoint userLocation;
    LocationManager locationManager ;
    SeekBar seekBar ;
    int radius;



    public MessagesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessagesFragment newInstance(String param1, String param2) {
        MessagesFragment fragment = new MessagesFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        rvPosts = view.findViewById(R.id.rvPosts);
        seekBar = view.findViewById(R.id.seekBar);
        userLocation = new ParseGeoPoint();

        allPosts = new ArrayList<>();
        adapter = new MessageAdapter(getContext(), allPosts);
        rvPosts.setAdapter(adapter);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext());
        rvPosts.setLayoutManager(linearLayout);
        queryPosts();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                radius = i;
                adapter.clear();
                queryPosts();
                Log.i("i", String.valueOf(radius) + " " +String.valueOf(.000305));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        final SwipeRefreshLayout swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                adapter.clear();
                queryPosts();
                swipeContainer.setRefreshing(false);

            }
        });
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayout) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromAPI(20);
            }
        };


        rvPosts.addOnScrollListener(scrollListener);

    }

    private void loadNextDataFromAPI(int offset) {
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.include(Message.KEY_AUTHOR);
        query.setSkip(offset);
        query.addDescendingOrder(Message.KEY_CREATEDAT);
        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> objects, ParseException e) {
                if(e != null){
                    Log.e("Post", "issue with getting posts", e);
                }
                for(Message post : objects){
                    Log.i("Post", "Post:" + post.getMessagebody() + ", username:" + post.getAuthor().getUsername());
                }
                allPosts.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void queryPosts(){
        userLocation = locationGive.userLoc(getContext());
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.include(Message.KEY_AUTHOR);
        query.setLimit(20);
        Log.i("i", userLocation.toString());
        query.whereWithinKilometers("Location", userLocation, radius * .000305);
        query.addDescendingOrder(Message.KEY_CREATEDAT);
        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> objects, ParseException e) {
                if(e != null){
                    Log.e("Post", "issue with getting posts", e);
                }
                for(Message post : objects){
                    Log.i("Post", "Post:" + post.getMessagebody() + ", username:" + post.getAuthor().getUsername());
                }
                allPosts.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }
}