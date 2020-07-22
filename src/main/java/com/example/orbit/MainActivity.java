package com.example.orbit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.orbit.fragments.ComposeFragment;
import com.example.orbit.fragments.MapFragment;
import com.example.orbit.fragments.MessagesFragment;
import com.example.orbit.fragments.ProfileFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    public BottomNavigationView bottomNavigationView;
    final FragmentManager fragmentManager = getSupportFragmentManager();
    LocationManager locationManager ;
    //public SeekBar seekBar = findViewById(R.id.seekBar);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        //seekBar = findViewById(R.id.seekBar);
        final Context context = this;
        final Fragment fragment1 = new ComposeFragment();
        final Fragment fragment2 = new MessagesFragment();
        final Fragment fragment3 = new ProfileFragment();
        final SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        mapFragment.getMapAsync(this);
 locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


                switch (menuItem.getItemId()) {
                    case R.id.broadcast:
                        // do something here
                        Toast.makeText(MainActivity.this, "Home!", Toast.LENGTH_SHORT).show();
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment1).commit();
                        return true;
                    case R.id.map:
                        mapFragment.getMapAsync((OnMapReadyCallback) context);
                        fragmentManager.beginTransaction().replace(R.id.flContainer, mapFragment).commit();
                        return true;
                    case R.id.profile:
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment3).commit();
                        return true;
                    case R.id.stream:
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment2).commit();
                        return true;
                    default:
                        break;

                }
                return true;
            }

        });
        bottomNavigationView.setSelectedItemId(R.id.broadcast);


    }
    public void onMapReady(final GoogleMap googleMap) {
        // [START_EXCLUDE silent]
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.

            ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
            query.include(Message.KEY_AUTHOR);
            query.setLimit(20);
            query.addDescendingOrder(Message.KEY_CREATEDAT);
            query.findInBackground(new FindCallback<Message>() {
                                       @Override
                                       public void done(List<Message> objects, ParseException e) {
                                           if (e != null) {
                                               Log.e("Post", "issue with getting posts", e);
                                           }
                                           for (Message post : objects) {
                                               Log.i("Post", "Post:" + post.getLocation().getLatitude()+ ", username:" +  post.getLocation().getLongitude());
                                               if(post.getLocation() != null) {
                                                   LatLng postLocal = new LatLng(post.getLocation().getLatitude(), post.getLocation().getLongitude());
                                                   googleMap.addMarker(new MarkerOptions().position(postLocal).title(post.getHeader()));
                                               }
                                           }
                                       }
                                   });
        ParseGeoPoint point = locationGive.userLoc(this);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(point.getLatitude(), point.getLongitude()), 18));
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.side, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.logout){
            ParseUser.logOut();
            Toast.makeText(this, "logged out!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}