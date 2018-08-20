package com.vincit.mechanic.mrmechanic;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomerSubServiceSelect extends AppCompatActivity {



    RecyclerView customerSubServiceSelect;
    RecyclerView.LayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_sub_service_select);
        Settings.CUSTOMER_SUB_SERVICE_SELECT_ACTIVITY = this;


        Init();


        /*
        LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        checkParams.setMargins(5, 5, 5, 5);
        Container = (LinearLayout)findViewById(R.id.customer_sub_service_container);
        for(int i=0;i<Settings.SUBCAT.size();i++){
            SubCat subCat = (SubCat)Settings.SUBCAT.get(i);
            if(Settings.CUSTOMER_SERVICE_SELECT.equalsIgnoreCase(subCat.cat_id)) {
                CheckBox cb = new CheckBox(getApplicationContext());
                cb.setTextColor(Color.parseColor("#44ADE1"));
                cb.setTextSize(16f);
                cb.setId(i);
                cb.setTag(subCat.subcat_id);
                cb.setText(subCat.subcat_name);
                cb.setLayoutParams(checkParams);
                setCheckBoxColor(cb,Color.parseColor("#44ADE1"),Color.parseColor("#0374AE"));
                Container.addView(cb);
            }
        }
        */
    }
    void Init(){

        Settings.CUSTOMER_SUB_SERVICE_FOR_SELECT.clear();
        for(int i=0;i<Settings.SUBCAT.size();i++){
            SubCat subCat = (SubCat)Settings.SUBCAT.get(i);
            if(Settings.CUSTOMER_SERVICE_SELECT.equalsIgnoreCase(subCat.cat_id)){
                Settings.CUSTOMER_SUB_SERVICE_FOR_SELECT.add(subCat);
            }
        }

        customerSubServiceSelect = (RecyclerView)findViewById(R.id.customer_sub_service_grid);
        mLayoutManager = new GridLayoutManager(this,3);
        customerSubServiceSelect.setLayoutManager(mLayoutManager);

        CustomerSubServiceAdapter CSSA = new CustomerSubServiceAdapter(this);
        customerSubServiceSelect.setAdapter(CSSA);

    }
    public void setCheckBoxColor(CheckBox checkBox, int checkedColor, int uncheckedColor) {
        int states[][] = {{android.R.attr.state_checked}, {}};
        int colors[] = {checkedColor, uncheckedColor};
        CompoundButtonCompat.setButtonTintList(checkBox, new
                ColorStateList(states, colors));
    }


    public void CustomerSelectSubSrviceDone(View v){
        Settings.CUSTOMER_SUB_SERVICE_SELECT = "";
        for(Map.Entry<String, Integer> entry : Settings.CUSTOMER_SUB_SERVICE_SELECTED_HASH.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            if(value==1){
                Settings.CUSTOMER_SUB_SERVICE_SELECT += "|" + String.valueOf(key);
            }
        }
        if(Settings.CUSTOMER_SUB_SERVICE_SELECT.equalsIgnoreCase("")){
            Toast.makeText(this,"Please Select Atleast one",Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(this,CustomerMapSelect.class);
            startActivity(intent);
        }
        /*
        Settings.CUSTOMER_SUB_SERVICE_SELECT = "";
        for(int i=0;i<Container.getChildCount();i++) {
            CheckBox cb = (CheckBox) Container.getChildAt(i);
            if(cb.isChecked()){
                Settings.CUSTOMER_SUB_SERVICE_SELECT += "|" + String.valueOf(cb.getTag());

            }
        }
       if(Settings.CUSTOMER_SUB_SERVICE_SELECT.equalsIgnoreCase("")){
           Toast.makeText(this,"Please Select Atleast one",Toast.LENGTH_SHORT).show();
       }else{
           Intent intent = new Intent(this,CustomerMapSelect.class);
           startActivity(intent);
       }
       */
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Settings.CUSTOMER_SUB_SERVICE_SELECT = "-1";
    }
}


class CustomerSubServiceAdapter extends RecyclerView.Adapter<CustomerSubServiceAdapter.ServiceHolder>{



    private Context mContext;
    public CustomerSubServiceAdapter(Context context){
        mContext = context;
        Settings.CUSTOMER_SUB_SERVICE_SELECTED_HASH.clear();
        for(int i=0;i<Settings.CUSTOMER_SUB_SERVICE_FOR_SELECT.size();i++){
            SubCat subCat = (SubCat)Settings.CUSTOMER_SUB_SERVICE_FOR_SELECT.get(i);
            Settings.CUSTOMER_SUB_SERVICE_SELECTED_HASH.put(subCat.subcat_id,0);
        }

    }
    public static class ServiceHolder extends RecyclerView.ViewHolder{
        public ImageView serviceIcon;
        public TextView serviceName;
        public ServiceHolder(View v){
            super(v);
            serviceIcon = (ImageView)v.findViewById(R.id.customer_service_icon);
            serviceName = (TextView)v.findViewById(R.id.customer_service_name);
            serviceIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   Integer isChecked = Settings.CUSTOMER_SUB_SERVICE_SELECTED_HASH.get(serviceName.getTag().toString());
                    if(isChecked==0) {
                        v.setBackgroundColor(Color.parseColor("#ffffff"));
                        Settings.CUSTOMER_SUB_SERVICE_SELECTED_HASH.put(serviceName.getTag().toString(),1);
                        serviceName.setBackgroundColor(Color.parseColor("#000000"));
                    }
                    else {
                        v.setBackgroundColor(Color.parseColor("#ffffff"));
                        Settings.CUSTOMER_SUB_SERVICE_SELECTED_HASH.put(serviceName.getTag().toString(),0);
                        serviceName.setBackgroundColor(Color.parseColor("#bb0374AE"));
                    }


                }
            });
        }
    }

    @Override
    public ServiceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.customer_service_select_column,parent,false);
        ServiceHolder vh = new ServiceHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ServiceHolder holder, int position) {
        // super.onBindViewHolder(holder, position, payloads);
        String Name = ((SubCat)(Settings.CUSTOMER_SUB_SERVICE_FOR_SELECT.get(position))).subcat_name;
        String Id = ((SubCat)(Settings.CUSTOMER_SUB_SERVICE_FOR_SELECT.get(position))).subcat_id;
        int iconId = -1;
        holder.serviceName.setText(Name);
        holder.serviceName.setTag(Id);
        /*
        switch(Name){
            case "cars":
                iconId = R.drawable.car;
                break;
            case "scooty":
                iconId = R.drawable.scooty;
                break;
            case "activa":
                iconId = R.drawable.scooty;
                break;
            case "scooter":
                iconId = R.drawable.scooter;
                break;
            case "bus":
                iconId = R.drawable.bus;
                break;
            case "bike":
                iconId = R.drawable.bike;
                break;
            default:
                iconId = R.drawable.car;
                break;
        }
        */
      //  holder.serviceIcon.setImageResource(R.drawable.car);

    }
    @Override
    public int getItemCount(){
        return Settings.CUSTOMER_SUB_SERVICE_FOR_SELECT.size();
    }



}



