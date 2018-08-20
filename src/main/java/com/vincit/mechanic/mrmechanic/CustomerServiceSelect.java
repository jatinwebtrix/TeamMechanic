package com.vincit.mechanic.mrmechanic;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CustomerServiceSelect extends AppCompatActivity {

    RecyclerView customerServiceSelect;
    RecyclerView.LayoutManager mLayoutManager;
    private String CATIDS="-1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service_select);


        Settings.CUSTOMER_SERVICE_SELECT_ACTIVITY = this;


        Init();
    }

    void Init(){



        customerServiceSelect = (RecyclerView)findViewById(R.id.customer_service_grid);
        mLayoutManager = new GridLayoutManager(this,3);
        customerServiceSelect.setLayoutManager(mLayoutManager);

        CustomerServiceAdapter CSA = new CustomerServiceAdapter(this);
        customerServiceSelect.setAdapter(CSA);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Settings.CUSTOMER_SERVICE_SELECT = "-1";

    }
}

class CustomerServiceAdapter extends RecyclerView.Adapter<CustomerServiceAdapter.ServiceHolder>{


    private Context mContext;
    public CustomerServiceAdapter(Context context){
        mContext = context;

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
                     Settings.CUSTOMER_SERVICE_SELECT = serviceName.getTag().toString().trim();
                    boolean isSubCat = false;
                    for(int i=0;i<Settings.SUBCAT.size();i++){
                        if(Settings.CUSTOMER_SERVICE_SELECT.equalsIgnoreCase(((SubCat)Settings.SUBCAT.get(i)).cat_id)){
                            isSubCat = true;
                            break;
                        }
                    }
                    if(isSubCat) {
                        Intent intent = new Intent(v.getContext(), CustomerSubServiceSelect.class);
                        v.getContext().startActivity(intent);
                    }else{
                        Intent intent = new Intent(v.getContext(),CustomerMapSelect.class);
                        v.getContext().startActivity(intent);
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
        String Name = ((Cat)(Settings.CAT.get(position))).cat_name;
        String Id = ((Cat)(Settings.CAT.get(position))).cat_id;
        int iconId = -1;
        holder.serviceName.setText(Name);
        holder.serviceName.setTag(Id);

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
       // holder.serviceIcon.setImageResource(iconId);
    }
    @Override
    public int getItemCount(){
        return Settings.CAT.size();
    }



}



