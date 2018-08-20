package com.vincit.mechanic.mrmechanic;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by R on 6/14/2017.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.NewsHolder> {

    private static ArrayList<Vendor2> VENDOR;
    private static Context context;
    private static int Position;

    private static String ID = "";
    public static class NewsHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView VendorName;
        public NewsHolder(View v){
            super(v);
            VendorName = (TextView)v.findViewById(R.id.vendor_name);

            v.setOnClickListener(this);
//            Favorite = (ImageView)v.findViewById(R.id.favorite);
//            Favorite.setOnClickListener(this);






        }

        @Override
        public void onClick(View v) {

//
//                Intent intent = new Intent(v.getContext(),test2.class);
//                intent.putExtra("url",(String)NEWS.get(getLayoutPosition()).SourceUrl);
//                intent.putExtra("title",(String)NEWS.get(getLayoutPosition()).Headline);
//                intent.putExtra("source",(String)NEWS.get(getLayoutPosition()).SourceName);
//                v.getContext().startActivity(intent);

          //  Toast.makeText(v.getContext(),(String)VENDOR.get(getLayoutPosition()).vendor_id,Toast.LENGTH_LONG).show();


            Intent intent =new Intent(v.getContext(),AccountUser.class);
            intent.putExtra("ID",(String)VENDOR.get(getLayoutPosition()).vendor_id);
            v.getContext().startActivity(intent);

        }
        public void Notify(Context ctx,int position){


        }



    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    int layoutIndex = 0;
    public  MainAdapter(ArrayList<Vendor2> VENDOR, Context ctx){
        this.VENDOR = VENDOR;
        context = ctx;

    }

    @Override
    public int getItemCount() {
        return VENDOR.size();
    }

    @Override
    public NewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.vendor_row, parent, false);




        return new NewsHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(NewsHolder holder, int position) {

        Position = position;

        holder.VendorName.setText((String)VENDOR.get(position).vendor_name);


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
