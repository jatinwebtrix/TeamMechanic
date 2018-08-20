package com.vincit.mechanic.mrmechanic;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.pm.ActivityInfoCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {


    private Context ctx;
    private Timer timer;
    private Handler handler;
    private static final int REQUEST_READ_PHONE_STATE  = 8;
    LocationManager mLocationManager;
    static  String LOCATION = "LOCATION";
    private String provider;
    private DatabaseHandler db = new DatabaseHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


       // this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



        setContentView(R.layout.activity_main);



        ctx = this;
        timer = new Timer();
        handler = new Handler();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {

                            timer.cancel();
                            timer = null;
//                            finish();
//                            Intent intent = new Intent(ctx,SelectAccount.class);
//                            startActivity(intent);
                        } catch (Exception e) {

                        }
                    }
                });
            }
        };
      // timer.schedule(doAsynchronousTask, 5000, 1000);

//        Intent intent = new Intent(ctx,Login.class);
//        startActivity(intent);



//        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//
//        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);
//        int permissionCheck1 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
//        int permissionCheck2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
//
//        if (permissionCheck != PackageManager.PERMISSION_GRANTED && permissionCheck1 != PackageManager.PERMISSION_GRANTED && permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE,android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_READ_PHONE_STATE);
//        } else {
//            GetLocation();
//
//        }

        if(db==null){
            db = new DatabaseHandler(this);
        }
        if(!doesDatabaseExist(this,"dataManager")){
            GetData GD = new GetData(ctx,this);
            GD.execute("");
        }else{
            Settings.STATE = db.GetStateList();
            Settings.CITY = db.GetCityList();
            Settings.CAT = db.GetCatList();
            Settings.SUBCAT = db.GetSubCatList();

            int permissionCheck = ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.READ_PHONE_STATE);
            int permissionCheck1 = ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_COARSE_LOCATION);
            int permissionCheck2 = ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED && permissionCheck1 != PackageManager.PERMISSION_GRANTED && permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE,android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_READ_PHONE_STATE);
            } else {
                GetLocation();
            }
        }
    }

    public static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    int permissionCheck = ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.READ_PHONE_STATE);
                    int permissionCheck1 = ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_COARSE_LOCATION);
                    int permissionCheck2 = ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION);

                    if (permissionCheck != PackageManager.PERMISSION_GRANTED && permissionCheck1 != PackageManager.PERMISSION_GRANTED && permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE,android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_READ_PHONE_STATE);
                    } else {
                        GetLocation();
//                        finish();
//                        Intent intent = new Intent(ctx,SelectAccount.class);
//                        startActivity(intent);
                    }


                  //  GetLocation();


                }else{
                    finish();
                    Intent intent = new Intent(ctx,SelectAccount.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    overridePendingTransition(0,0);
                    startActivity(intent);

                }
                break;

            default:
                break;
        }
    }
    ProgressDialog pd;
    public void GetLocation(){


        SharedPreferences sharedpreferences = getSharedPreferences(LOCATION, Context.MODE_PRIVATE);
        String Location = sharedpreferences.getString("LOCATION", "-1");
        String Lat = sharedpreferences.getString("LAT", "-1");
        String Long = sharedpreferences.getString("LONG", "-1");

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        GPSTracker gps = new GPSTracker(MainActivity.this);
        Location location = null;
        if(gps.canGetLocation){
        location = gps.getLocation();}
        //Toast.makeText(this,String.valueOf(location.getLatitude()),Toast.LENGTH_LONG).show();
        if(Location.equalsIgnoreCase("-1")) {

            if(gps.isGPSEnabled) {
                pd = new ProgressDialog(this);
                pd.setMessage("Detecting you...");
                pd.setCancelable(false);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.show();

                try {

                    if (location != null && pd != null) {
                        Settings.CUSTOMER_CURRENT_LOCATION = getCompleteAddressString(location.getLatitude(), location.getLongitude());

                        Settings.CURRENT_LAT = String.valueOf(location.getLatitude());
                        Settings.CURRENT_LONG = String.valueOf(location.getLongitude());
                        SharedPreferences.Editor editor = sharedpreferences.edit();

                        editor.putString("LOCATION", Settings.CUSTOMER_CURRENT_LOCATION.trim());
                        editor.putString("LAT", Settings.CURRENT_LAT.trim());
                        editor.putString("LONG", Settings.CURRENT_LONG.trim());
                        editor.commit();


                        //mLocationManager.removeUpdates(this);
                        //mLocationManager = null;
                        pd.dismiss();
                        finish();
                        Intent intent = new Intent(ctx, SelectAccount.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        overridePendingTransition(0,0);
                        startActivity(intent);

                    } else if (pd == null && location != null) {
                        Settings.CUSTOMER_CURRENT_LOCATION = getCompleteAddressString(location.getLatitude(), location.getLongitude());
                        Settings.CURRENT_LAT = String.valueOf(location.getLatitude());
                        Settings.CURRENT_LONG = String.valueOf(location.getLongitude());
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("LOCATION", Settings.CUSTOMER_CURRENT_LOCATION.trim());
                        editor.commit();


                    }else{
                        if(pd!=null){
                            pd.dismiss();
                            finish();
                            Intent intent = new Intent(ctx,SelectAccount.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            overridePendingTransition(0,0);
                            startActivity(intent);
                        }
                    }
                } catch (SecurityException e) {
                    if(pd!=null){
                        pd.dismiss();
                        finish();
                        Intent intent = new Intent(ctx,SelectAccount.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        overridePendingTransition(0,0);
                        startActivity(intent);
                    }
                }
            }else{
                Toast.makeText(ctx,"Please enable your gps",Toast.LENGTH_LONG).show();
                finish();
                Intent intent = new Intent(ctx,SelectAccount.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                overridePendingTransition(0,0);
                startActivity(intent);
            }
        }else{

            if(!gps.isGPSEnabled)
                Toast.makeText(ctx,"Please enable your gps",Toast.LENGTH_LONG).show();

            Settings.CUSTOMER_CURRENT_LOCATION = Location;
            Settings.CURRENT_LAT = Lat;
            Settings.CURRENT_LONG = Long;
            finish();
            Intent intent = new Intent(ctx,SelectAccount.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            overridePendingTransition(0,0);
            startActivity(intent);
        }



        /*

        if(Location.equalsIgnoreCase("-1")) {
            pd = new ProgressDialog(this);
            pd.setMessage("Detecting you...");
            pd.setCancelable(false);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();
            LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            try {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0.0f, mLocationListener);
            } catch (SecurityException e) {

            }
        }else{
            Settings.CUSTOMER_CURRENT_LOCATION = Location;
            Settings.CURRENT_LAT = Lat;
            Settings.CURRENT_LONG = Long;
            finish();
            Intent intent = new Intent(ctx,SelectAccount.class);
            startActivity(intent);
        }
        */
    }

    public Location _GetLocation(){
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = mLocationManager.getBestProvider(criteria, false);
        Location location = null;
        try {
            location = mLocationManager.getLastKnownLocation(provider);
        }catch (SecurityException se){
            location = null;
        }
        return location;
    }


    public final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
            if(location != null && pd != null) {
                Settings.CUSTOMER_CURRENT_LOCATION = getCompleteAddressString(location.getLatitude(),location.getLongitude());

                Settings.CURRENT_LAT = String.valueOf(location.getLatitude());
                Settings.CURRENT_LONG= String.valueOf(location.getLongitude());
                SharedPreferences sharedpreferences = getSharedPreferences(LOCATION, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString("LOCATION", Settings.CUSTOMER_CURRENT_LOCATION.trim());
                editor.putString("LAT", Settings.CURRENT_LAT.trim());
                editor.putString("LONG", Settings.CURRENT_LONG.trim());
                editor.commit();

                mLocationManager.removeUpdates(this);
                mLocationManager = null;


                //mLocationManager.removeUpdates(this);
                //mLocationManager = null;
                pd.dismiss();
                finish();
                Intent intent = new Intent(ctx,SelectAccount.class);
                startActivity(intent);
            }else if(pd == null && location != null){
                Settings.CUSTOMER_CURRENT_LOCATION = getCompleteAddressString(location.getLatitude(),location.getLongitude());
                Settings.CURRENT_LAT = String.valueOf(location.getLatitude());
                Settings.CURRENT_LONG= String.valueOf(location.getLongitude());
                SharedPreferences sharedpreferences = getSharedPreferences(LOCATION, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("LOCATION", Settings.CUSTOMER_CURRENT_LOCATION.trim());
                editor.commit();
                mLocationManager.removeUpdates(this);
                mLocationManager = null;


            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private Location getLastBestLocation() {
        try {
            Location locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locationNet = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            long GPSLocationTime = 0;
            if (null != locationGPS) {
                GPSLocationTime = locationGPS.getTime();
            }

            long NetLocationTime = 0;

            if (null != locationNet) {
                NetLocationTime = locationNet.getTime();
            }

            if (0 < GPSLocationTime - NetLocationTime) {
                return locationGPS;
            } else {
                return locationNet;
            }
        }
        catch(SecurityException e){
            return null;
        }
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
               // Log.w("My Current loction address", "" + strReturnedAddress.toString());
            } else {
               // Log.w("My Current loction address", "No Address returned!");
                Toast.makeText(ctx,"Cannot locate you, please choose your location on map",Toast.LENGTH_LONG).show();
                strAdd = "No address chosen";
            }
        } catch (Exception e) {
            e.printStackTrace();
           // Log.w("My Current loction address", "Canont get Address!");
            Toast.makeText(ctx,"Cannot locate you, please choose your location on map",Toast.LENGTH_LONG).show();
            strAdd = "No address chosen";
        }
        return strAdd;
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if(timer != null){
//            timer.cancel();
//            timer = null;
//        }
    }


    public void AskForPermission(){

    }


    class GetData extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        InputStream inputStream = null;
        String result = "";
        Context ctx;

        Activity AC;

        public GetData(Context ctx,Activity AC){
            this.ctx = ctx;
            this.AC = AC;

        }

        protected void onPreExecute() {
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
            progressDialog.setCancelable(false);
        }
        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                String sUrl = Settings.GET_DATA_URL;
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

                    Settings.STATE.clear();
                    Settings.CITY.clear();
                    Settings.CAT.clear();
                    Settings.SUBCAT.clear();
                    mainObject = new JSONObject(result);
                    JSONArray stateArray = mainObject.getJSONArray("State");
                    for(int i=0;i<stateArray.length();i++){
                        JSONObject stateObject = stateArray.getJSONObject(i);
                        State state = new State();
                        state.sid = stateObject.getString("sid");
                        state.sname = stateObject.getString("sname");
                        state.scode = stateObject.getString("scode");
                        Settings.STATE.add(state);

                    }

                    JSONArray cityArray = mainObject.getJSONArray("City");
                    for(int i=0;i<cityArray.length();i++){
                        JSONObject cityObject = cityArray.getJSONObject(i);
                        City city = new City();
                        city.cid = cityObject.getString("cid");
                        city.sid = cityObject.getString("sid");
                        city.ccname = cityObject.getString("ccname");
                        Settings.CITY.add(city);
                    }

                    JSONArray catArray = mainObject.getJSONArray("Cat");
                    for(int i=0;i<catArray.length();i++){
                        JSONObject catObject = catArray.getJSONObject(i);
                        Cat cat = new Cat();
                        cat.cat_id = catObject.getString("cat_id");
                        cat.cat_name = catObject.getString("cat_name");
                        Settings.CAT.add(cat);
                    }

                    JSONArray subcatArray = mainObject.getJSONArray("SubCat");
                    for(int i=0;i<subcatArray.length();i++){
                        JSONObject subcatObject = subcatArray.getJSONObject(i);
                        SubCat subcat = new SubCat();
                        subcat.subcat_id = subcatObject.getString("subcat_id");
                        subcat.subcat_name = subcatObject.getString("subcat_name");
                        subcat.cat_id = subcatObject.getString("cat_id");
                        Settings.SUBCAT.add(subcat);
                    }

                    if(db==null){
                        db = new DatabaseHandler(ctx);
                    }
                    db.SaveStates(Settings.STATE);
                    db.SaveCities(Settings.CITY);
                    db.SaveCat(Settings.CAT);
                    db.SaveSubCat(Settings.SUBCAT);
                    //mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                    int permissionCheck = ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.READ_PHONE_STATE);
                    int permissionCheck1 = ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_COARSE_LOCATION);
                    int permissionCheck2 = ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION);

                    if (permissionCheck != PackageManager.PERMISSION_GRANTED && permissionCheck1 != PackageManager.PERMISSION_GRANTED && permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AC, new String[]{android.Manifest.permission.READ_PHONE_STATE,android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_READ_PHONE_STATE);
                    } else {
                        GetLocation();

                    }

                   // Toast.makeText(ctx,String.valueOf(db.GetStateList().size()),Toast.LENGTH_LONG).show();


                }
                catch(Exception e){


                }


            }


        }

    }


}



class LocationService implements LocationListener{
    private LocationManager locationManager;
    private String provider;
    private static final int REQUEST_READ_PHONE_STATE  = 8;
    Location location = null;
    public LocationService(Context context, Activity activity){
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        int permissionCheck = ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE);
        int permissionCheck1 = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck2 = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED && permissionCheck1 != PackageManager.PERMISSION_GRANTED && permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_PHONE_STATE,android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_READ_PHONE_STATE);
        } else {
            location = locationManager.getLastKnownLocation(provider);
        }

    }
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }



}
