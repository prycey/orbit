package com.example.orbit;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.orbit.Message;
import com.example.orbit.R;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;

import org.parceler.Parcels;

import java.util.Date;
import java.util.List;

import static com.example.orbit.timeFixer.getTimeAgo;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private Context context;
    private List<Message> posts;
    LocationManager locationManager;

    ParseGeoPoint point = new ParseGeoPoint();
    Location loc = new Location("");


    public MessageAdapter(Context context, List<Message> posts){
        this.context = context;
        this.posts = posts;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message post = posts.get(position);
        holder.bind(post);
    }


    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Message> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }



    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView author;
        private ImageView item;
        private TextView header;
        private TextView feetfrom;
        private TextView datefrom;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.name);
            header = itemView.findViewById(R.id.header);
            item = itemView.findViewById(R.id.item);
            feetfrom = itemView.findViewById(R.id.feetfrom);
            datefrom = itemView.findViewById(R.id.time);
        }

        public void bind(Message post) {
            header.setText(post.getHeader());
            ParseFile image = post.getPicture();
            ParseGeoPoint point = post.getLocation();
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            Date date = post.getCreatedAt();
            Long dateL = date.getTime();
            Location loc2 = new Location("");
            if(image != null) {
                Glide.with(context).load(post.getPicture().getUrl()).into(item);
            }
            author.setText(post.getAuthor().getUsername());
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Message post2 = posts.get(position);
                        Intent intent = new Intent(context, DetailActivity.class);
                        intent.putExtra("Message", Parcels.wrap(post2));
                        context.startActivity(intent);
                    }


                }
            });
            if(locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER)) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLatitude();
                        loc = location;
                    }
                });
            }
            else if(locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLatitude();
                        loc = location;
                    }
                });
            }
            Log.e("tag",(String.valueOf(loc.distanceTo(loc2))));
            feetfrom.setText(String.valueOf(loc.distanceTo(loc2)));
            datefrom.setText(getTimeAgo(dateL,context));
        }
    }

}
