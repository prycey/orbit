package com.example.orbit.fragments;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.orbit.EndlessRecyclerViewScrollListener;
import com.example.orbit.Message;
import com.example.orbit.MessageAdapter;
import com.example.orbit.ParseApplication;
import com.example.orbit.R;
import com.example.orbit.UserImage;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private RecyclerView rvMessages;
    private EndlessRecyclerViewScrollListener scrollListener;
    private ImageView image;
    private File photoFile;
    public String photoFileName = "photo.jpg";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 45;
    protected MessageAdapter adapter;
    protected List<Message> allPosts;

    public ParseGeoPoint userLocation;

    public void setUserLocation(ParseGeoPoint userLocation) {
        this.userLocation = userLocation;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int radius;




    public ParseGeoPoint getUserLocation() {
        return userLocation;
    }


    public ProfileFragment() {
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
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        rvMessages = view.findViewById(R.id.rvProfile);
        allPosts = new ArrayList<>();
        adapter = new MessageAdapter(getContext(), allPosts);
        image = view.findViewById(R.id.imageView);

        //bar.setProgress(getRadius());
        rvMessages.setAdapter(adapter);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext());
        rvMessages.setLayoutManager(linearLayout);
        queryPosts();
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
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera(view);
            }
        });
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayout) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromAPI(20);
            }
        };



        rvMessages.addOnScrollListener(scrollListener);

    }
    private void launchCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        // Start the image capture intent to take photo
        if (intent.resolveActivity(getContext().getPackageManager()) != null)
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }
    private File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Tag");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d("TAG", "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }


    private void loadNextDataFromAPI(int offset) {
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.include(Message.KEY_AUTHOR);
        query.setSkip(offset);
        query.whereEqualTo(Message.KEY_AUTHOR, ParseUser.getCurrentUser());
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                image.setImageBitmap(takenImage);
                UserImage profile = new UserImage();
                profile.setImage(new ParseFile(photoFile));

            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void queryPosts(){
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.include(Message.KEY_AUTHOR);
        query.whereEqualTo(Message.KEY_AUTHOR, ParseUser.getCurrentUser());
        query.setLimit(20);
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}