package com.example.orbit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.orbit.fragments.ComposeFragment;
import com.example.orbit.fragments.MapFragment;
import com.example.orbit.fragments.MessagesFragment;
import com.example.orbit.fragments.ProfileFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {
    public BottomNavigationView bottomNavigationView;
    final FragmentManager fragmentManager = getSupportFragmentManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        final Fragment fragment1 = new ComposeFragment();
        final Fragment fragment2 = new MessagesFragment();
        final Fragment fragment3 = new ProfileFragment();
        final SupportMapFragment mapFragment = SupportMapFragment.newInstance();


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