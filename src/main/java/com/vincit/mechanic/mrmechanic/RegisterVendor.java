package com.vincit.mechanic.mrmechanic;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.iid.FirebaseInstanceId;

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
import java.util.concurrent.Exchanger;

public class RegisterVendor extends AppCompatActivity{


    static  String MyPREFERENCES = "CREDENTIALS";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_vendor);

//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);




        ArrayList STATES = new ArrayList();
        for(int i = 0;i<Settings.STATE.size();i++) {
            State state = (State)Settings.STATE.get(i);
            STATES.add(state.sname);
        }

        Spinner State = (Spinner)findViewById(R.id.vendor_state);
        ArrayAdapter adapter = new ArrayAdapter(this,R.layout.spinner_row1,R.id.title, STATES);
        State.setAdapter(adapter);


        ArrayList CITIES = new ArrayList();
        for(int i = 0;i<Settings.CITY.size();i++) {
            City city = (City)Settings.CITY.get(i);
            CITIES.add(city.ccname);
        }
        Spinner City = (Spinner)findViewById(R.id.vendor_city);
        ArrayAdapter adapter_city = new ArrayAdapter(this,R.layout.spinner_row1,R.id.title, CITIES);
        City.setAdapter(adapter_city);

        ArrayList AVAILABLE = new ArrayList();
        AVAILABLE.add("Available in nights");
        AVAILABLE.add("Yes");
        AVAILABLE.add("No");
        Spinner Av = (Spinner)findViewById(R.id.vendor_available);
        ArrayAdapter adapter_available = new ArrayAdapter(this,R.layout.spinner_row1,R.id.title, AVAILABLE);
        Av.setAdapter(adapter_available);

//        ArrayList GALLERY = new ArrayList();
//        GALLERY.add("Workshop Gallery");
//        GALLERY.add("Workshop");
//        GALLERY.add("Gallery");
//        Spinner Wg = (Spinner)findViewById(R.id.vendor_gallery);
//        ArrayAdapter adapter_gallery = new ArrayAdapter(this,R.layout.spinner_row1,R.id.title, GALLERY);
//        Wg.setAdapter(adapter_gallery);

//
//        ArrayList SERVICES = new ArrayList();
//        SERVICES.add("Services");
//        SERVICES.add("Service A");
//        SERVICES.add("Service B");
//        Spinner S = (Spinner)findViewById(R.id.vendor_service);
//        ArrayAdapter adapter_service = new ArrayAdapter(this,R.layout.spinner_row1,R.id.title, SERVICES);
//        S.setAdapter(adapter_service);

    }


    public void RegisterVendor(View v){

        Vendor vendor = new Vendor();
        vendor.name = ((EditText)findViewById(R.id.vendor_full_name)).getText().toString().trim();
        vendor.email = ((EditText)findViewById(R.id.vendor_email)).getText().toString().trim();
        vendor.mobile = ((EditText)findViewById(R.id.vendor_phone)).getText().toString().trim();
        vendor.pincode = ((EditText)findViewById(R.id.vendor_pin)).getText().toString().trim();
        vendor.address1 = ((EditText)findViewById(R.id.vendor_address1)).getText().toString().trim();
        vendor.address2 = ((EditText)findViewById(R.id.vendor_address2)).getText().toString().trim();
        vendor.sid = ((State)Settings.STATE.get(((Spinner)findViewById(R.id.vendor_state)).getSelectedItemPosition())).sid;
        vendor.ccid = ((City)Settings.CITY.get(((Spinner)findViewById(R.id.vendor_city)).getSelectedItemPosition())).cid;
        vendor.location = Settings.Address;
        vendor.lat = Settings.Latitude;
        vendor.lng = Settings.Longitude;
       // vendor.cat_id = ((EditText)findViewById(R.id.user_password)).getText().toString().trim();
       // vendor.subcat_id = ((EditText)findViewById(R.id.user_password)).getText().toString().trim();
       // vendor.vendor_photo = ((EditText)findViewById(R.id.user_password)).getText().toString().trim();
       // vendor.work_photo = ((EditText)findViewById(R.id.user_password)).getText().toString().trim();
        vendor.description = ((EditText)findViewById(R.id.vendor_profile)).getText().toString().trim();
        vendor.bank_account = ((EditText)findViewById(R.id.vendor_bank_account)).getText().toString().trim();
        vendor.bank_ifsc = ((EditText)findViewById(R.id.vendor_bank_ifsc)).getText().toString().trim();
        vendor.bank_account_type = ((EditText)findViewById(R.id.vendor_bank_account_type)).getText().toString().trim();
        vendor.bank_branch = ((EditText)findViewById(R.id.vendor_bank_branch)).getText().toString().trim();
       // vendor.start_time = ((EditText)findViewById(R.id.user_password)).getText().toString().trim();
       // vendor.end_time = ((EditText)findViewById(R.id.user_password)).getText().toString().trim();
        vendor.password = ((EditText)findViewById(R.id.vendor_password)).getText().toString().trim();
        vendor.FCM = FirebaseInstanceId.getInstance().getToken();
        Settings.MOBILE = vendor.mobile;
        Settings.PASSWORD = vendor.password;

        RegisterSync RS = new RegisterSync(this,vendor);
        RS.execute("");



    }



    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(this,Login.class);
        startActivity(intent);
    }
    private static int PLACE_PICKER_REQUEST = 88;
    public void SelectMap(View v){
  //      Intent intent = new Intent(this,SelectMap.class);
  //      startActivity(intent);





        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();


        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place selectedPlace = PlacePicker.getPlace(this, data);

                Settings.Latitude = String.valueOf(selectedPlace.getLatLng().latitude);
                Settings.Longitude = String.valueOf(selectedPlace.getLatLng().longitude);
                Settings.Address = selectedPlace.getAddress().toString();

                ((TextView)findViewById(R.id.address)).setText(Settings.Address);
                ((Button)findViewById(R.id.user_location)).setText("Change Location");
                //Toast.makeText(this,selectedPlace.getLatLng().l,Toast.LENGTH_LONG).show();
                // Do something with the place
            }
        }
    }



    class RegisterSync extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog = new ProgressDialog(RegisterVendor.this);
        InputStream inputStream = null;
        String result = "";
        Context ctx;
        Vendor vendor;
        public RegisterSync(Context ctx,Vendor vendor){
            this.ctx = ctx;
            this.vendor = vendor;
        }

        protected void onPreExecute() {
            progressDialog.setMessage("Registering...");
            progressDialog.show();
            progressDialog.setCancelable(false);
        }
        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                String sUrl = Settings.REGISTER_VENDOR_URL
                        + "name=" +  URLEncoder.encode(vendor.name.trim(), "UTF-8") +"&"
                        + "email="+ URLEncoder.encode(vendor.email.trim(), "UTF-8") + "&"
                        + "mobile="+ URLEncoder.encode(vendor.mobile.trim(), "UTF-8") + "&"
                        + "pincode="+ URLEncoder.encode(vendor.pincode.trim(), "UTF-8") + "&"
                        + "address1="+ URLEncoder.encode(vendor.address1.trim(), "UTF-8") + "&"
                        + "address2="+ URLEncoder.encode(vendor.address2.trim(), "UTF-8") + "&"
                        + "sid="+ URLEncoder.encode(vendor.sid.trim(), "UTF-8") + "&"
                        + "ccid="+ URLEncoder.encode(vendor.ccid.trim(), "UTF-8") + "&"
                        + "location="+ URLEncoder.encode(vendor.location.trim(), "UTF-8") + "&"
                        + "latitude="+ URLEncoder.encode(vendor.lat.trim(), "UTF-8") + "&"
                        + "longitude="+ URLEncoder.encode(vendor.lng.trim(), "UTF-8") + "&"
                        + "cat_id="+ URLEncoder.encode(vendor.cat_id.trim(), "UTF-8") + "&"
                        + "subcat_id="+ URLEncoder.encode(vendor.subcat_id.trim(), "UTF-8") + "&"
                        + "vendor_phot="+ URLEncoder.encode(vendor.vendor_photo.trim(), "UTF-8") + "&"
                        + "work_photo="+ URLEncoder.encode(vendor.work_photo.trim(), "UTF-8") + "&"
                        + "description="+ URLEncoder.encode(vendor.description.trim(), "UTF-8") + "&"
                        + "bank_account="+ URLEncoder.encode(vendor.bank_account.trim(), "UTF-8") + "&"
                        + "bank_ifsc="+ URLEncoder.encode(vendor.bank_ifsc.trim(), "UTF-8") + "&"
                        + "bank_branch="+ URLEncoder.encode(vendor.bank_branch.trim(), "UTF-8") + "&"
                        + "bank_account_type="+ URLEncoder.encode(vendor.bank_account_type.trim(), "UTF-8") + "&"
                        + "start_time="+ URLEncoder.encode(vendor.start_time.trim(), "UTF-8") + "&"
                        + "end_time="+ URLEncoder.encode(vendor.end_time.trim(), "UTF-8") + "&"
                        + "password="+ URLEncoder.encode(vendor.password.trim(), "UTF-8") + "&"
                        + "fcm="+ URLEncoder.encode(vendor.FCM, "UTF-8");



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
                try{

                    mainObject = new JSONObject(result);
                    if(mainObject.getString("isValid").equalsIgnoreCase("0")) {


                        if(mainObject.getString("type").equalsIgnoreCase("vendor")) {
                            Settings.VENDOR_ID = mainObject.getString("vendor_id");
                            Settings.VENDOR_NAME = mainObject.getString("name");
                            Settings.VENDOR_EMAIL = mainObject.getString("email");
                            Settings.VENDOR_PINCODE = mainObject.getString("pincode");
                            Settings.VENDOR_ADDRESS1 = mainObject.getString("address1");
                            Settings.VENDOR_ADDRESS2 = mainObject.getString("address2");
                            Settings.VENDOR_SID = mainObject.getString("sid");
                            Settings.VENDOR_CCID = mainObject.getString("ccid");
                            Settings.VENDOR_LOCATION = mainObject.getString("location");
                            Settings.VENDOR_LAT = mainObject.getString("lat");
                            Settings.VENDOR_LONG = mainObject.getString("long");
                            Settings.VENDOR_CAT_ID = mainObject.getString("cat_id");
                            Settings.VENDOR_SUBCAT_ID = mainObject.getString("subcat_id");

                            Settings.VENDOR_PHOTO = mainObject.getString("vendor_photo");
                            Settings.VENDOR_WORK_PHOTO = mainObject.getString("work_photo");
                            Settings.VENDOR_DESCRIPTION = mainObject.getString("description");
                            Settings.VENDOR_START_TIME = mainObject.getString("start_time");
                            Settings.VENDOR_END_TIME = mainObject.getString("end_time");
                            Settings.VENDOR_PASSWORD = mainObject.getString("password");

                            Settings.TYPE = 1;

                            SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();

                            editor.putInt("TYPE",Settings.TYPE);
                            editor.putString("MOBILE", Settings.MOBILE.trim());
                            editor.putString("PASSWORD", Settings.PASSWORD.trim());
                            editor.commit();

                            finish();
                            Intent in = new Intent(ctx, AccountVendor.class);
                            startActivity(in);
                        }



                    }
                    else
                    {
                        Toast.makeText(ctx,"Invalid Username Or Password",Toast.LENGTH_LONG).show();
                    }

                }
                catch(Exception e){

                    Toast.makeText(ctx,result,Toast.LENGTH_LONG).show();
                }


            }


        }

    }



}
