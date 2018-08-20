package com.vincit.mechanic.mrmechanic;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.*;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Exchanger;

public class Verify_Phone extends AppCompatActivity {

    private Context ctx;
    private Activity activity;
    private static final int REQUEST_READ_PHONE_STATE  = 9;
    private String IMEI = "";
    static  String MyPREFERENCES = "CREDENTIALS";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify__phone);
        activity = this;
        ctx = this;
        int permissionCheck = ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            TelephonyManager tm = (TelephonyManager)getSystemService(this.TELEPHONY_SERVICE);
            IMEI = tm.getDeviceId();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    TelephonyManager tm = (TelephonyManager)getSystemService(this.TELEPHONY_SERVICE);
                    IMEI = tm.getDeviceId();


                }else{
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setTitle("You must give this permission");
                    alertDialogBuilder.setCancelable(true);

                    alertDialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);

                        }
                    });
                    alertDialogBuilder.show();

                }
                break;

            default:
                break;
        }
    }

    public void RegisterClick(View v){


    }


    public void RegisterData(){
        if(Settings.TYPE ==0 ) {
            User user = new User();
            user.name = "";
            user.email = "";
            user.mobile = ((EditText) findViewById(R.id.phone_to_verify)).getText().toString().trim();
            user.address = "";
            user.sid = "";
            user.ccid = "";
            user.cat_id = "";
            user.vehicle_make = "";
            user.vehicle_model = "";
            user.vehicle_no = "";
            user.vehicle_year = "";
            user.password = IMEI;
            user.FCM = FirebaseInstanceId.getInstance().getToken();
            Settings.MOBILE = user.mobile;
            Settings.PASSWORD = user.password;


            if (user.mobile.length() == 10) {
                RegisterUser(user);
            } else {
                Toast.makeText(this, "Please Enter 10 digit mobile number", Toast.LENGTH_LONG).show();
            }
        }
        else{
            Vendor vendor = new Vendor();
            vendor.name = "";
            vendor.email = "";
            vendor.mobile = ((EditText) findViewById(R.id.phone_to_verify)).getText().toString().trim();
            vendor.pincode = "";
            vendor.address1 = "";
            vendor.sid = "";
            vendor.ccid = "";
            vendor.location = "";
            vendor.lat = "";
            vendor.lng = "";
            vendor.cat_id = "";
            vendor.subcat_id = "";
            vendor.vendor_photo = "";
            vendor.work_photo = "";
            vendor.description = "";
            vendor.bank_account = "";
            vendor.bank_ifsc = "";
            vendor.bank_account_type = "";
            vendor.bank_branch = "";
            vendor.start_time = "";
            vendor.end_time = "";
            vendor.password = IMEI;
            vendor.FCM = FirebaseInstanceId.getInstance().getToken();
            Settings.MOBILE = vendor.mobile;
            Settings.PASSWORD = vendor.password;




            if (vendor.mobile.length() == 10) {
                RegisterVendor(vendor);
            } else {
                Toast.makeText(this, "Please Enter 10 digit mobile number", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void _verify_click(View v){
        int permissionCheck = ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            RegisterData();
        }

    }


    private void RegisterUser(User user){
        final String KEY_NAME = "name";
        final String KEY_EMAIl = "email";
        final String KEY_MOBILE = "mobile";
        final String KEY_ADDRESS = "address";
        final String KEY_SID = "sid";
        final String KEY_CCID = "ccid";
        final String KEY_CATID = "cat_id";
        final String KEY_VEHICLA_MAKE = "vehicle_make";
        final String KEY_VEHICLE_MODEL = "vehicle_model";
        final String KEY_VEHICLE_NO = "vehicle_no";
        final String KEY_VEHICLE_YEAR = "vehicle_year";
        final String KEY_PASSWORD = "password";
        final String KEY_FCM = "fcm";


        final String VALUE_NAME = user.name;
        final String VALUE_EMAIl = user.email;
        final String VALUE_MOBILE = user.mobile;
        final String VALUE_ADDRESS = user.address;
        final String VALUE_SID = user.sid;
        final String VALUE_CCID = user.ccid;
        final String VALUE_CATID = user.cat_id;
        final String VALUE_VEHICLA_MAKE = user.vehicle_make;
        final String VALUE_VEHICLE_MODEL = user.vehicle_model;
        final String VALUE_VEHICLE_NO = user.vehicle_no;
        final String VALUE_VEHICLE_YEAR = user.vehicle_year;
        final String VALUE_PASSWORD = user.password;
        final String VALUE_FCM = user.FCM;

        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Team Mechanic","Creating Account...",false,false);
        loading.setCancelable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Settings.REGISTER_USER_URL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        JSONObject mainObject = null;
                        JSONArray adsArray = null;
                        try {
                            mainObject = new JSONObject(s);
                            if (mainObject.getString("isValid").equalsIgnoreCase("0")) {


                                if (mainObject.getString("type").equalsIgnoreCase("customer")) {
                                    Settings.CUSTOMER_ID = mainObject.getString("customer_id");
                                    Settings.CUSTOMER_NAME = mainObject.getString("name");
                                    Settings.CUSTOMER_EMAIL = mainObject.getString("email");
                                    Settings.CUSTOMER_ADDRESS = mainObject.getString("address");
                                    Settings.CUSTOMER_SID = mainObject.getString("sid");
                                    Settings.CUSTOMER_CCID = mainObject.getString("ccid");
                                    Settings.CUSTOMER_CAT_ID = mainObject.getString("cat_id");
                                    Settings.CUSTOMER_VEHICLE_MAKE = mainObject.getString("vehicle_make");
                                    Settings.CUSTOMER_VEHICLE_MODEL = mainObject.getString("vehicle_model");
                                    Settings.CUSTOMER_VEHICLE_NO = mainObject.getString("vehicle_no");
                                    Settings.CUSTOMER_VEHICLE_YEAR = mainObject.getString("vehicle_year");
                                    Settings.CUSTOMER_PHOTO = mainObject.getString("customer_photo");
                                    Settings.CUSTOMER_DATE1 = mainObject.getString("date1");
                                    Settings.TYPE = 0;

                                    SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();

                                    editor.putInt("TYPE", Settings.TYPE);
                                    editor.putString("MOBILE", Settings.MOBILE.trim());
                                    editor.putString("PASSWORD", Settings.PASSWORD.trim());
                                    editor.commit();



                                    String isActive = mainObject.getString("isActive");
                                    if(isActive.equalsIgnoreCase("0")){
                                        finish();
                                        Intent in = new Intent(ctx, UnderProcess.class);
                                        in.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        overridePendingTransition(0,0);
                                        startActivity(in);
                                    }else{
                                        finish();
                                        Intent in = new Intent(ctx, AccountUser.class);
                                        in.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        overridePendingTransition(0,0);
                                        startActivity(in);
                                    }

                                }
                                else if(mainObject.getString("type").equalsIgnoreCase("vendor")) {
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

                                    String isActive = mainObject.getString("isActive");
                                    if(isActive.equalsIgnoreCase("1")){
                                        finish();
                                        Intent in = new Intent(ctx, AccountVendor.class);
                                        in.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        overridePendingTransition(0,0);
                                        startActivity(in);
                                    }else{
                                        finish();
                                        Intent in = new Intent(ctx, UnderProcess.class);
                                        in.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        overridePendingTransition(0,0);
                                        startActivity(in);
                                    }
                                }

                            } else {
                                Toast.makeText(ctx, "Invalid Username Or Password", Toast.LENGTH_LONG).show();
                            }
                        }
                        catch (Exception e){
                            Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("Aslam",mainObject.toString());
                        }

                    }



                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(Verify_Phone.this, "Something went wrong, try again...", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(KEY_NAME, VALUE_NAME);
                params.put(KEY_EMAIl, VALUE_EMAIl);
                params.put(KEY_MOBILE, VALUE_MOBILE);
                params.put(KEY_ADDRESS, VALUE_ADDRESS);
                params.put(KEY_SID, VALUE_SID);
                params.put(KEY_CCID, VALUE_CCID);
                params.put(KEY_CATID, VALUE_CATID);
                params.put(KEY_VEHICLA_MAKE, VALUE_VEHICLA_MAKE);
                params.put(KEY_VEHICLE_MODEL, VALUE_VEHICLE_MODEL);
                params.put(KEY_VEHICLE_NO, VALUE_VEHICLE_NO);
                params.put(KEY_VEHICLE_YEAR, VALUE_VEHICLE_YEAR);
                params.put(KEY_PASSWORD,VALUE_PASSWORD);
                params.put(KEY_FCM, VALUE_FCM);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }
    private void RegisterVendor(Vendor vendor){
        final String KEY_NAME = "name";
        final String KEY_EMAIL = "email";
        final String KEY_MOBILE = "mobile";
        final String KEY_PINCODE = "pincode";
        final String KEY_ADDRESS1 = "address1";
        final String KEY_ADDRESS2 = "address2";
        final String KEY_SID = "sid";
        final String KEY_CCID = "ccid";
        final String KEY_LOCATION = "location";
        final String KEY_LATITUDE = "latitude";
        final String KEY_LONGITUDE = "longitude";
        final String KEY_CATID = "cat_id";
        final String KEY_SUBCATID = "subcat_id";
        final String KEY_DESCRIPTION = "description";
        final String KEY_BANKACCOUNT = "bank_account";
        final String KEY_BANKIFSC = "bank_ifsc";
        final String KEY_BANKBRANCH = "bank_branch";
        final String KEY_BANKACCOUNTTYPE = "bank_account_type";
        final String KEY_STARTTIME = "start_time";
        final String KEY_ENDTIME = "end_time";
        final String KEY_PASSWORD = "password";
        final String KEY_FCM = "fcm";

        final String VALUE_NAME = vendor.name;
        final String VALUE_EMAIL = vendor.email;
        final String VALUE_MOBILE = vendor.mobile;
        final String VALUE_PINCODE = vendor.pincode;
        final String VALUE_ADDRESS1 = vendor.address1;
        final String VALUE_ADDRESS2 = vendor.address2;
        final String VALUE_SID = vendor.sid;
        final String VALUE_CCID = vendor.ccid;
        final String VALUE_LOCATION = vendor.location;
        final String VALUE_LATITUDE = vendor.lat;
        final String VALUE_LONGITUDE = vendor.lng;
        final String VALUE_CATID = vendor.cat_id;
        final String VALUE_SUBCATID = vendor.subcat_id;
        final String VALUE_DESCRIPTION = vendor.description;
        final String VALUE_BANKIFSC = vendor.bank_ifsc;
        final String VALUE_BANKBRANCH = vendor.bank_branch;
        final String VALUE_BANKACCOUNTTYPE = vendor.bank_account_type;
        final String VALUE_STARTTIME = vendor.start_time;
        final String VALUE_ENDTIME = vendor.end_time;
        final String VALUE_PASSWORD = vendor.password;
        final String VALUE_FCM = vendor.FCM;



        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Team Mechanic","Creating Account...",false,false);
        loading.setCancelable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Settings.REGISTER_VENDOR_URL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        Log.e("Aslam",s);
                        loading.dismiss();
                        JSONObject mainObject = null;
                        JSONArray adsArray = null;
                        try {
                            mainObject = new JSONObject(s);
                            if (mainObject.getString("isValid").equalsIgnoreCase("0")) {

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

                                        String isActive = mainObject.getString("isActive");
                                        if(isActive.equalsIgnoreCase("1")){
                                            finish();
                                            Intent in = new Intent(ctx, AccountVendor.class);
                                            in.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            overridePendingTransition(0,0);
                                            startActivity(in);
                                        }else{
                                            finish();
                                            Intent in = new Intent(ctx, UnderProcess.class);
                                            in.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            overridePendingTransition(0,0);
                                            startActivity(in);
                                        }
                                }

                            } else {
                                Toast.makeText(ctx, "Invalid Username Or Password", Toast.LENGTH_LONG).show();
                            }
                        }
                        catch (Exception e){
                            Toast.makeText(ctx, e.getMessage()+"Here", Toast.LENGTH_LONG).show();

                        }

                    }



                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(Verify_Phone.this, "Something went wrong, try again...", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(KEY_NAME, VALUE_NAME);
                params.put(KEY_EMAIL, VALUE_EMAIL);
                params.put(KEY_MOBILE, VALUE_MOBILE);
                params.put(KEY_PINCODE, VALUE_PINCODE);
                params.put(KEY_ADDRESS1, VALUE_ADDRESS1);
                params.put(KEY_ADDRESS2, VALUE_ADDRESS2);
                params.put(KEY_SID, VALUE_SID);
                params.put(KEY_CCID, VALUE_CCID);
                params.put(KEY_LOCATION, VALUE_LOCATION);
                params.put(KEY_LATITUDE, VALUE_LATITUDE);
                params.put(KEY_LONGITUDE, VALUE_LONGITUDE);
                params.put(KEY_CATID, VALUE_CATID);
                params.put(KEY_SUBCATID, VALUE_SUBCATID);
                params.put(KEY_DESCRIPTION, VALUE_DESCRIPTION);
                params.put(KEY_BANKIFSC, VALUE_BANKIFSC);
                params.put(KEY_BANKBRANCH, VALUE_BANKBRANCH);
                params.put(KEY_BANKACCOUNTTYPE, VALUE_BANKACCOUNTTYPE);
                params.put(KEY_STARTTIME, VALUE_STARTTIME);
                params.put(KEY_ENDTIME, VALUE_ENDTIME);
                params.put(KEY_PASSWORD, VALUE_PASSWORD);
                params.put(KEY_FCM, VALUE_FCM);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }




}
