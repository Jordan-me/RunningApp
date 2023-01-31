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
                                    "&zoom=15" + "&key=AIzaSyBxPa6cMY5ScErwH5_4z0IHbjvfynaJh6U";
//                            https://maps.googleapis.com/maps/api/staticmap?size=984x788&path=color:0x0000ff|weight:5|32.069046,34.8601458|32.0690464,34.8601454|32.0690466,34.8601452|32.0690467,34.8601451|32.0690467,34.8601451|32.0690467,34.860145|32.0690467,34.860145|32.0690467,34.860145|32.0690467,34.860145|32.0690467,34.860145|32.0690467,34.860145|32.0690467,34.860145|32.0690467,34.860145|32.0690467,34.860145|32.0690467,34.860145|32.0690467,34.860145|32.0690467,34.860145|32.0690467,34.860145|32.0690467,34.860145|32.0690467,34.860145|32.0690467,34.860145|32.0690467,34.860145|32.0690467,34.860145|32.0690467,34.860145|32.0690467,34.860145|32.0690581,34.8601473|32.0690555,34.860148|32.0690454,34.8601506|32.0690385,34.8601512|32.069028,34.860149|32.0690267,34.8601484|32.0690267,34.8601483|32.0690267,34.8601483|32.0690267,34.8601483|32.0690267,34.8601483|32.0691063,34.8601584|32.0692755,34.8601295|32.0694723,34.8600762|32.069557,34.8598801|32.0694834,34.8595864|32.0693498,34.8592379|32.0691965,34.858822|32.0691431,34.8586769|32.0690854,34.8585194|32.0689045,34.8580298|32.0687313,34.8574918|32.0685849,34.8569093|32.0684424,34.85628|32.0683084,34.8556109|32.068187,34.8549461|32.0680773,34.8542948|32.067967,34.8536617|32.0678622,34.8530491|32.0677617,34.8524914|32.0676806,34.8520174|32.0675943,34.8515313|32.0675271,34.8510391|32.0674589,34.8505374|32.0673813,34.8499985|32.0672926,34.8494342|32.0671653,34.8488171|32.0669975,34.8481968|32.0668056,34.8475764|32.0665813,34.8469413|32.0654483,34.844415|32.0651327,34.8435973|32.0649592,34.8428536|32.0648851,34.8421698|32.0648632,34.8414772|32.0648493,34.840825|32.0648454,34.8402478|32.0648834,34.839747|32.065073,34.8393578|32.0653658,34.8390908|32.065724,34.8390439|32.0660319,34.8391453|32.0663392,34.839352|32.0666683,34.8395756|32.0670052,34.8398105|32.0673326,34.8400438|32.0676232,34.8402352|32.0679387,34.8404501|32.0682717,34.8406883|32.0686301,34.8409371|32.0690169,34.8411876|32.0694114,34.8414629|32.0698038,34.841739|32.0702116,34.8420121|32.0705898,34.8422728|32.0710014,34.842506|32.0714409,34.8427344|32.0718968,34.842953|32.072359,34.8431621|32.0728062,34.8433388|32.0732161,34.8434878|32.0736382,34.8436335|32.0740817,34.8437523|32.07449,34.8438429|32.0748761,34.8439148|32.0752787,34.843981|32.0757153,34.8440536|32.0761746,34.8441256|32.076587,34.8441871|32.0769481,34.8442459|32.0773198,34.8443067|32.0777374,34.844379|32.0781891,34.8444927|32.0787049,34.8445938|32.079257,34.8446912|32.0798402,34.8448024|32.0804164,34.8449145|32.0809695,34.8450223&zoom=15&key=AIzaSyBxPa6cMY5ScErwH5_4z0IHbjvfynaJh6U
//                            https://maps.googleapis.com/maps/api/staticmap?&size=600x300&path=color:0xff0000ff%7Cweight:5%7C32.2856782,34.8441484%7C&center=32.2856782,34.8441484&zoom=15&key=AIzaSyBxPa6cMY5ScErwH5_4z0IHbjvfynaJh6U
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
