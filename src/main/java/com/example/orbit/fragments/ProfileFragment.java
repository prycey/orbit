package com.example.orbit.fragments;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.orbit.EndlessRecyclerViewScrollListener;
import com.example.orbit.Message;
import com.example.orbit.MessageAdapter;
import com.example.orbit.ParseApplication;
import com.example.orbit.R;
import com.example.orbit.UserImage;
import com.example.orbit.locationGive;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
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
    private TextView name;
    private File photoFile;
    public String photoFileName = "photo.jpg";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 45;
    protected MessageAdapter adapter;
    protected List<Message> allPosts;
    public ParseGeoPoint userLocation;
    public int radius;


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


        name = view.findViewById(R.id.name);
        name.setText(ParseUser.getCurrentUser().getUsername());
        rvMessages.setAdapter(adapter);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext());
        rvMessages.setLayoutManager(linearLayout);
        queryPosts();
        queryProfile();
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
        photoFile = getPhotoFileUri(photoFileName);
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        if (intent.resolveActivity(getContext().getPackageManager()) != null)
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }
    private File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Tag");
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d("TAG", "failed to create directory");
        }
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
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                image.setImageBitmap(takenImage);
                UserImage profile = (UserImage)ParseObject.create("UserImage");
                profile.setImage(new ParseFile(photoFile));
                profile.setUser(ParseUser.getCurrentUser());
                try {
                    profile.save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void queryProfile(){
        userLocation = locationGive.userLoc(getContext());
        ParseQuery<UserImage> query = ParseQuery.getQuery(UserImage.class);
        query.include(UserImage.KEY_USER);
        query.setLimit(1);
        query.whereEqualTo(UserImage.KEY_USER,ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<UserImage>() {
            @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void done(List<UserImage> objects, ParseException e) {
                if(e != null){
                    Log.e("Post", "issue with getting posts", e);
                }
                for(UserImage post : objects){
                    if(image != null) {
                        Glide.with(getContext()).load(post.getImage().getUrl()).into(image);
                        image.setRotation(90);
                    }
                }
            }
        });


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