package com.vincit.mechanic.mrmechanic;

import android.*;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

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

public class RegisterUser2 extends AppCompatActivity {



    EditText UserFullName2,UserEmail2,UserPhone2,UserAddress2,UserVehicleMo2,
            UserVehicleN2,UserVehicleYear2;
    Spinner UserState2,UserCity2,UserVehicleM2;

    private static final int REQUEST_READ_LOCATION  = 9;
    private Context mContext;
    private Spinner USER_SUB_SERVICE_LIST;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user2);

        mContext = this;
        //((EditText)findViewById(R.id.user_full_name2)).setText(Settings.CUSTOMER_NAME);
        //((EditText)findViewById(R.id.user_email2)).setText(Settings.CUSTOMER_EMAIL);
        ((EditText)findViewById(R.id.user_phone2)).setText(Settings.MOBILE);
        ((TextView)findViewById(R.id.address)).setText(String.valueOf(Settings.CUSTOMER_CURRENT_LOCATION));



        ArrayList USER_SERVICE = new ArrayList();
        for(int i = 0;i<Settings.CAT.size();i++) {
            Cat cat = (Cat)Settings.CAT.get(i);
            USER_SERVICE.add(cat.cat_name);
        }

        Spinner USER_SERVICE_LIST = (Spinner)findViewById(R.id.user_service);

        ArrayAdapter adapter_user_service = new ArrayAdapter(this,R.layout.spinner_row,R.id.title, USER_SERVICE);
        USER_SERVICE_LIST.setAdapter(adapter_user_service);


        ArrayList USER_SUB_SERVICE = new ArrayList();
        for(int i = 0;i<Settings.SUBCAT.size();i++) {
            SubCat subcat = (SubCat)Settings.SUBCAT.get(i);
            USER_SUB_SERVICE.add(subcat.subcat_name);
        }

        USER_SUB_SERVICE_LIST = (Spinner)findViewById(R.id.user_sub_service);
        ArrayAdapter adapter_user_sub_service = new ArrayAdapter(this,R.layout.spinner_row,R.id.title, USER_SUB_SERVICE);
        USER_SUB_SERVICE_LIST.setAdapter(adapter_user_sub_service);


        USER_SERVICE_LIST.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here

                ArrayList SUB_CAT = new ArrayList();
                int index = 0;
                String catId = ((Cat)Settings.CAT.get(position)).cat_id;
                for(int i=0;i<Settings.SUBCAT.size();i++){
                    SubCat subCat = (SubCat)Settings.SUBCAT.get(i);
                    if(subCat.cat_id.equalsIgnoreCase(catId)){

                        SUB_CAT.add(subCat.subcat_name);
                    }
                }
                if(SUB_CAT.size()>0) {
                    ArrayAdapter adapter_submanu = new ArrayAdapter(mContext, R.layout.spinner_row, R.id.title, SUB_CAT);
                    USER_SUB_SERVICE_LIST.setAdapter(adapter_submanu);
                    USER_SUB_SERVICE_LIST.setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.sub_service_type)).setVisibility(View.VISIBLE);
                }else{
                    USER_SUB_SERVICE_LIST.setVisibility(View.GONE);
                    ((TextView)findViewById(R.id.sub_service_type)).setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });




    }

    private static int PLACE_PICKER_REQUEST = 88;
    private ProgressDialog pd_map_open;
    public void SelectMap(View v){
        pd_map_open = new ProgressDialog(RegisterUser2.this);
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
    String LAT = "-1";
    String LONG = "-1";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {



                Place selectedPlace = PlacePicker.getPlace(this, data);

                LAT = String.valueOf(selectedPlace.getLatLng().latitude);
                LONG = String.valueOf(selectedPlace.getLatLng().longitude);
                Settings.CUSTOMER_ADDRESS = selectedPlace.getAddress().toString();
                ((TextView)findViewById(R.id.address)).setText(selectedPlace.getAddress());
                ((Button)findViewById(R.id.user_location)).setText("Change Location");

            }
        }



        if(pd_map_open != null){
            pd_map_open.dismiss();
        }

    }



    public void Submit_Click2(View v){

        ServiceSync SS = new ServiceSync(this);
        SS.execute("");
    }

    class ServiceSync extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog = new ProgressDialog(RegisterUser2.this);
        InputStream inputStream = null;
        String result = "";
        Context ctx;

        public ServiceSync(Context ctx){
            this.ctx = ctx;

        }

        protected void onPreExecute() {
            progressDialog.setMessage("Sending...");
            progressDialog.show();
            progressDialog.setCancelable(false);
        }
        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                String sUrl = Settings.NOTIFY_VENDOR_URL
                        + "id=" +  URLEncoder.encode(Settings.CUSTOMER_ID.trim(), "UTF-8") +"&"
                        + "address="+ URLEncoder.encode(Settings.CUSTOMER_ADDRESS, "UTF-8") + "&"
                        + "lat="+ URLEncoder.encode(LAT.trim(), "UTF-8") + "&"
                        + "long="+ URLEncoder.encode(LONG.trim(), "UTF-8");

                URL url = new URL(sUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();





                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (progressDialog.isShowing()){
                progressDialog.dismiss();
                JSONObject mainObject = null;
                JSONArray adsArray = null;
                JSONArray mainArray = null;
                try{
//
//                    mainArray = new JSONArray(result);
//
//                    Settings.VENDOR_LIST.clear();
//                    for(int i =0;i<mainArray.length();i++){
//                        JSONObject vendorJSON =  mainArray.getJSONObject(i);
//                        Vendor2 vendor = new Vendor2();
//                        vendor.vendor_id = vendorJSON.getString("vendor_id");
//                        vendor.vendor_name = vendorJSON.getString("name");
//                        Settings.VENDOR_LIST.add(vendor);
//                    }
//
//                    Intent intent = new Intent(ctx,AccountUserListVendor.class);
//                    startActivity(intent);

                    Settings.REQUEST_ID = result;

                    Toast.makeText(ctx,Settings.REQUEST_ID,Toast.LENGTH_LONG).show();


                }
                catch(Exception e){

                    Toast.makeText(ctx,e.getMessage(),Toast.LENGTH_LONG).show();
                }


            }


        }

    }


}
