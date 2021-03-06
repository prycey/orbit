package com.example.orbit.fragments;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.orbit.Message;
import com.example.orbit.R;
import com.example.orbit.locationGive;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComposeFragment} factory method to
 * create an instance of this fragment.
 */
public class ComposeFragment extends Fragment implements OnMapReadyCallback {
    public static final String Tag = "MainActivity";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 45;
    private EditText body;
    private EditText header;
    private ImageView ivPostImage;
    private Button btnCaptureImage;
    private BottomNavigationView bottomNavigationView;
    private Button btnSubmit;
    private File photoFile;
    private ParseGeoPoint point = new ParseGeoPoint();
    public String photoFileName = "photo.jpg";
    private FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;


    public ComposeFragment() {
        // Required empty public constructor
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        body = view.findViewById(R.id.body);
        btnCaptureImage = view.findViewById(R.id.capture);
        ivPostImage = view.findViewById(R.id.picture);
        btnSubmit = view.findViewById(R.id.submit);
        header = view.findViewById(R.id.header);
        bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);



        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bodyTemp = body.getText().toString();
                String headerTemp = header.getText().toString();
                if(bodyTemp.isEmpty()||headerTemp.isEmpty()){
                    Toast.makeText(getContext(), "Description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(photoFile == null || ivPostImage.getDrawable() == null){
                    Toast.makeText(getContext(), "Description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseUser currentUser = ParseUser.getCurrentUser();
                saveMessage(headerTemp,bodyTemp, currentUser, photoFile);
            }
        });

        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera(view);
            }
        });

        if(locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    point.setLatitude(latitude);
                    point.setLongitude(longitude);
                }
            });
        }
        else if(locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    point.setLatitude(latitude);
                    point.setLongitude(longitude);
                }
            });
        }

    }

    private void launchCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        if (intent.resolveActivity(getContext().getPackageManager()) != null)
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivPostImage.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
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

    private void saveMessage(String headTemp, String bodyTemp, ParseUser authorTemp, File photoFile) {
        Message post = new Message();
        post.setHeader(headTemp);
        post.setAuthor(authorTemp);
        post.setMessagebody(bodyTemp);
        post.setPicture(new ParseFile(photoFile));
        post.setLocation( locationGive.userLoc(getContext()));

        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null) {
                    Log.e("main activity", "Error while saving", e);
                    Toast.makeText(getContext(), "error while saving", Toast.LENGTH_SHORT);
                }
                Log.i("TAG", "post was sucessfull");
                header.setText("");
                body.setText("");
                ivPostImage.setImageResource(0);
            }

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e("tag", "dance");
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}