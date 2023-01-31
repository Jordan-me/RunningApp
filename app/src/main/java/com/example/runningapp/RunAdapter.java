package com.example.runningapp;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import android.annotation.SuppressLint;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RunAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Run> runs ;
    private Fragment context;
    private String API_KEY;

    public RunAdapter(Fragment context, ArrayList<Run> runs) {
        this.context = context;
        this.runs = runs;
        API_KEY = context.getResources().getString(R.string.google_map_api_key);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // inflate the run item layout and return a new RunViewHolder
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_run_record, viewGroup, false);

        return new RunViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Run run = getItem(position);
        RunViewHolder runViewHolder = (RunViewHolder) holder;
        runViewHolder.bind(run);
    }

    @Override
    public int getItemCount() {
        if (runs==null){
            return 0;
        }
        return runs.size();
    }

    private Run getItem(int position) {
        return runs.get(position);
    }

    public void setRuns(ArrayList<Run> runs) {
        this.runs = runs;
        notifyDataSetChanged();
    }
    public class RunViewHolder  extends RecyclerView.ViewHolder {
        private CircleImageView list_IMG_profile;
        private MaterialTextView list_TXT_name;
        private MaterialTextView list_TXT_date;
        private MaterialTextView list_TXT_run_title;
        private MaterialTextView list_TXT_distance;
        private MaterialTextView list_TXT_time;
        private ImageView list_IMG_map;
        private MaterialCardView list_CARD_item;


        public RunViewHolder(@NonNull View itemView) {
            super(itemView);
            list_IMG_profile = itemView.findViewById(R.id.list_IMG_profile);
            list_TXT_name = itemView.findViewById(R.id.list_TXT_name);
            list_TXT_date = itemView.findViewById(R.id.list_TXT_date);
            list_TXT_run_title = itemView.findViewById(R.id.list_TXT_run_title);
            list_TXT_distance = itemView.findViewById(R.id.list_TXT_distance);
            list_TXT_time = itemView.findViewById(R.id.list_TXT_time);
            list_IMG_map = itemView.findViewById(R.id.list_IMG_map);
            list_CARD_item = itemView.findViewById(R.id.list_CARD_item);
            //set profile image

        }

        public void bind(Run run) {
            // Bind the data from the Run object to the views
            if(run.getUserId() == null){
                return;
            }
            FirebaseDatabaseManager.getInstance().getRunnerByUid(run.getUserId(), new FirebaseDatabaseManager.FirebaseCallback() {
                @SuppressLint({"LogNotTimber", "SetTextI18n"})
                @Override
                public void onCallback(DataSnapshot dataSnapshot) {
                    Log.d("RunViewHolder", "onCallback");
                    if (dataSnapshot != null) {
                        // Get the Runner object from the DataSnapshot
                        Runner runner = dataSnapshot.getValue(Runner.class);
                        if(runner != null)
                        {
                            // Bind the data from the Run object and the Runner object to the views
                            list_TXT_name.setText(runner.getFirstName() + " " + runner.getLastName());
                            Glide.with(context)
                                    .load(MSPV.getInstance().getString(MSPV.USER_IMAGE,"") )
                                    .error(R.drawable.ic_profile)
                                    .into(list_IMG_profile);

                            list_TXT_date.setText(run.getDate().toString());
                            list_TXT_run_title.setText(run.getTitle());
                            list_TXT_distance.setText(run.getDistance());
                            list_TXT_time.setText(run.getTime());
                            StringBuilder path = toStringBuildr(run.getRoute());
                            String center = getCenterString(run.getRoute());
                            int width = list_CARD_item.getWidth() + 40;
                            int height = list_IMG_map.getHeight();
                            String url = "https://maps.googleapis.com/maps/api/staticmap?" +

                                    "size="+ width +"x"+ height +
                                    "&path=color:0x0000ff|weight:5|" + path.toString() +
                                    "&zoom=15" + "&key=" + API_KEY;
                            Log.d("RunViewHolder", "URL: " + url);
                            Glide.with(context)
                                    .load(url)
                                    .into(list_IMG_map);
                        } else {
                            Log.d("RunViewHolder", "Runner not found");
                        }

                    } else {
                        Log.d("RunViewHolder", "Error retrieving runner data");
                    }
                }
            });

        }

        private String getCenterString(List<com.example.runningapp.LatLng> route) {
            int mid = route.size()/2;
            return "" + route.get(mid).getLatitude() + "," + route.get(mid).getLongitude();

        }

        private StringBuilder toStringBuildr(List<com.example.runningapp.LatLng> route) {
            StringBuilder path = new StringBuilder();
            if(route == null)
                return null;
            for (com.example.runningapp.LatLng point : route) {
                path.append(point.getLatitude() + "," + point.getLongitude() + "|");
            }
            return new StringBuilder(path.substring(0, path.length() - 1));
        }
        private List<LatLng> toLatLng(List<com.example.runningapp.LatLng> route) {
            ArrayList<LatLng> arr = new ArrayList<>();
            for(com.example.runningapp.LatLng latLng: route){
                arr.add(new LatLng(latLng.getLatitude(),latLng.getLongitude()));
            }
            return arr;
        }
    }
}
