package com.vincit.mechanic.mrmechanic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by R on 6/14/2017.
 */

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestHolder> {

    private static ArrayList<Request> REQUESTS;
    private  Context context;
    private static int Position;
    private static HashMap<String,String> SERVICE_HASH = new HashMap<>();
    private static HashMap<String,String> SUB_SERVICE_HASH = new HashMap<>();

    public static class RequestHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView Rating;
        private TextView Distance;
        private TextView Address;
        private TextView Status;
        private TextView Name;
        private TextView Phone;
        private ImageView RequestLocation;
        private TextView ServiceRequest;
        public RequestHolder(View v){
            super(v);
            Cat cat = null;
            SubCat subCat = null;
            for(int i=0;i<Settings.CAT.size();i++){
                cat = (Cat)Settings.CAT.get(i);
                SERVICE_HASH.put(cat.cat_id,cat.cat_name);
            }
            for(int i=0;i<Settings.SUBCAT.size();i++){
                subCat = (SubCat) Settings.SUBCAT.get(i);
                SUB_SERVICE_HASH.put(subCat.subcat_id,subCat.subcat_name);
            }
            Rating = (TextView)v.findViewById(R.id.request_rating);
            Distance = (TextView)v.findViewById(R.id.request_distance);
            Address = (TextView)v.findViewById(R.id.request_address);
            Status = (TextView)v.findViewById(R.id.request_status);
            Name = (TextView)v.findViewById(R.id.request_name);
            Phone = (TextView)v.findViewById(R.id.request_phone);
            ServiceRequest = (TextView)v.findViewById(R.id.request_service);
            RequestLocation = (ImageView)v.findViewById(R.id.request_location);
            if(RequestLocation != null) {
                RequestLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String REQUEST_LAT = REQUESTS.get(getLayoutPosition()).Lat;
                        String REQUEST_LANG = (String) REQUESTS.get(getLayoutPosition()).Lang;
                        //Toast.makeText(v.getContext(),REQUEST_LAT,Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(v.getContext(), SelectMap.class);
                        intent.putExtra("LAT", REQUEST_LAT);
                        intent.putExtra("LANG", REQUEST_LANG);
                        v.getContext().startActivity(intent);


                    }
                });
            }
            v.setOnClickListener(this);







        }

        @Override
        public void onClick(View v) {


//                Intent intent = new Intent(v.getContext(),test2.class);
//                intent.putExtra("url",(String)NEWS.get(getLayoutPosition()).SourceUrl);
//                intent.putExtra("title",(String)NEWS.get(getLayoutPosition()).Headline);
//                intent.putExtra("source",(String)NEWS.get(getLayoutPosition()).SourceName);
//                intent.putExtra("id",R.id.progress_view);
//                v.getContext().startActivity(intent);

            String Status = (String)REQUESTS.get(getLayoutPosition()).Status;
            if(Status.equalsIgnoreCase("1")) {
                String REQUEST_ID = (String) REQUESTS.get(getLayoutPosition()).rid;
                String MyPREFERENCES = "CREDENTIALS";

                SharedPreferences sharedpreferences = v.getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString("REQUEST_ID", REQUEST_ID);
                editor.commit();

                Intent switchIntent = new Intent(v.getContext(), Accept.class);
                v.getContext().startActivity(switchIntent);
            }

        }

    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    int layoutIndex = 0;
    public RequestAdapter(ArrayList<Request> REQUESTS, Context ctx){
        this.REQUESTS = REQUESTS;
        context = ctx;

    }

    @Override
    public int getItemCount() {
        return REQUESTS.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public RequestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = null;
        String status = (String)REQUESTS.get(viewType).Status;

        if(status.equalsIgnoreCase("3")){
            inflatedView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.accepted_request, parent, false);
        }else{
            inflatedView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.request_row, parent, false);
        }



        return new RequestHolder(inflatedView);
    }
    String[] ssArray = null;
    @Override
    public void onBindViewHolder(RequestHolder holder, int position) {


        try {
            Position = position;
            Request request = REQUESTS.get(position);
            String Status = (String) request.Status;
            holder.Rating.setText("Rating: "+(String) request.Rating);
            holder.Distance.setText("Distance: "+(String) request.Distance +" kms");
            holder.Address.setText((String) request.Address);
            if (SERVICE_HASH.get(request.Service) != null) {
                ssArray = request.SubService.split("\\|");
                String SubService = "";
                for (String ss : ssArray) {
                    if (SUB_SERVICE_HASH.get(ss) != null)
                        SubService += SUB_SERVICE_HASH.get(ss) + ", ";
                }
                holder.ServiceRequest.setText("Requested Service : " + SERVICE_HASH.get(request.Service) + " ( " + SubService + " )");
            } else {
                holder.ServiceRequest.setText("No service selected");
            }
            if (Status.equalsIgnoreCase("1")) {
                holder.Status.setText("Request Status : Pending");
            } else if (Status.equalsIgnoreCase("2")) {
                holder.Status.setText("Request Status : Proposal Sent");
            } else if (Status.equalsIgnoreCase("3")) {
                holder.Status.setText("Request Status : Proposal Accepted");
            }

            if (holder.Name != null) {
                holder.Name.setText("Customer Name : " + (String) request.Name);
                holder.Phone.setText("Customer Phone : " + (String) request.Phone);
            }
        }
        catch (Exception e){
            Log.e("aslam",e.getMessage());
        }

    }


    private int lastPosition = -1;

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(new Random().nextInt(501));//to make duration random number between [0,501)
            viewToAnimate.startAnimation(anim);
            lastPosition = position;
        }
    }

}
