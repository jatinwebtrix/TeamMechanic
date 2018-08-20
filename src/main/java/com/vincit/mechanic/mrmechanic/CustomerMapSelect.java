package com.vincit.mechanic.mrmechanic;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

public class CustomerMapSelect extends AppCompatActivity {

    private static int PLACE_PICKER_REQUEST = 88;
    private ProgressDialog pd_map_open;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_customer_map_select);

        Settings.CUSTOMER_MAP_SELECT_ACTIVITY = this;

        pd_map_open = new ProgressDialog(this);
        pd_map_open.setMessage("Opening Map...");
        pd_map_open.show();
        pd_map_open.setCancelable(false);

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();


        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
            pd_map_open.dismiss();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place selectedPlace = PlacePicker.getPlace(this, data);
                Settings.CUSTOMER_LAT_SELECT = String.valueOf(selectedPlace.getLatLng().latitude);
                Settings.CUSTOMER_LONG_SELECT = String.valueOf(selectedPlace.getLatLng().longitude);
                Settings.CUSTOMER_ADDRESS = selectedPlace.getAddress().toString();
                Settings.CUSTOMER_ADDRESS_SELECT = Settings.CUSTOMER_ADDRESS;



                Intent intent = new Intent(this,CustomerGeneralSelect.class);
                startActivity(intent);
            }
        }
        if(pd_map_open != null){
            pd_map_open.dismiss();
        }
        finish();

    }
}
