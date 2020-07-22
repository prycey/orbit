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
    Location loc;


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
            final Location loc2 = new Location("");
            final Location loc = new Location("");
            loc2.setLatitude(point.getLatitude());
            loc2.setLongitude(point.getLongitude());
            ParseGeoPoint parseGeoPoint = locationGive.userLoc(context);
            Log.i("i", String.valueOf(parseGeoPoint.distanceInKilometersTo(point)));

            Log.i("help3", parseGeoPoint.toString());
            loc.setLongitude(parseGeoPoint.getLongitude());
            loc.setLatitude(parseGeoPoint.getLatitude());

            Log.i("help me", loc.toString() + " "+loc2.toString());

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

            feetfrom.setText(String.valueOf(loc.distanceTo(loc2) * 3.28084));
            datefrom.setText(getTimeAgo(dateL,context));
        }
    }

}
