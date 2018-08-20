package com.vincit.mechanic.mrmechanic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
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
import java.util.Random;

/**
 * Created by R on 6/14/2017.
 */

public class ResponseAdapter extends RecyclerView.Adapter<ResponseAdapter.ResponseHolder> {

    private static ArrayList<Response> RESPONSES;
    private  Context context;
    private static int Position;

    public static class ResponseHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView Rating;
        private TextView Amount;
        private TextView Eta;
        private TextView Name;
        private TextView Phone;
        public ResponseHolder(View v){
            super(v);
            Rating = (TextView)v.findViewById(R.id.response_rating);
            Amount = (TextView)v.findViewById(R.id.response_amount);
            Eta = (TextView)v.findViewById(R.id.response_eta);
            Name = (TextView)v.findViewById(R.id.response_vendor_name);
            Phone = (TextView)v.findViewById(R.id.response_vendor_phone);
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

            String Status = (String)RESPONSES.get(getLayoutPosition()).status;
            if(Status.equalsIgnoreCase("2")) {
                String RESPONSE_ID = (String) RESPONSES.get(getLayoutPosition()).rid;
                String MyPREFERENCES = "CREDENTIALS";

                SharedPreferences sharedpreferences = v.getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString("RESPONSE_ID", RESPONSE_ID);
                editor.commit();

                // Toast.makeText(v.getContext(),RESPONSE_ID.toString(),Toast.LENGTH_LONG).show();

                Intent switchIntent = new Intent(v.getContext(), Accept2.class);
                v.getContext().startActivity(switchIntent);
            }else{
                Toast.makeText(v.getContext(),"Already Accepted",Toast.LENGTH_LONG).show();
            }

        }

    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    int layoutIndex = 0;
    public ResponseAdapter(ArrayList<Response> RESPONSES, Context ctx){
        this.RESPONSES = RESPONSES;
        context = ctx;

    }

    @Override
    public int getItemCount() {
        return RESPONSES.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public ResponseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = null;
        String status = (String)RESPONSES.get(viewType).status;

        if(status.equalsIgnoreCase("2")){
            inflatedView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.response_row, parent, false);
        }else{
            inflatedView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.accepted_response, parent, false);
        }



        return new ResponseHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(ResponseHolder holder, int position) {

        Position = position;
        holder.Rating.setText("Rating: "+(String)RESPONSES.get(position).Rating);
        holder.Amount.setText((String)RESPONSES.get(position).amount + " Rs.");
        holder.Eta.setText("Estimated Time Of Arrival "+(String)RESPONSES.get(position).eta + " min.");
        if(holder.Name != null) {
            holder.Name.setText("Name of mechanic : " + (String) RESPONSES.get(position).name);
            holder.Phone.setText("Phone No. of mechanic : " + (String) RESPONSES.get(position).phone);
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
