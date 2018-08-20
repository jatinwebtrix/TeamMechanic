package com.vincit.mechanic.mrmechanic;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class SelectAccount extends AppCompatActivity {


    private Context ctx;
    static  String MyPREFERENCES = "CREDENTIALS";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_account);

        ctx = this;


        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
//--------------TESTING------------------------------------------------------


//        SharedPreferences.Editor editor = sharedpreferences.edit();
//        Settings.TYPE = 1;
//        Settings.MOBILE = "8178895383";           //VENDOR = 9671355005 , CUSTOMER = 8178895383
//        Settings.PASSWORD = "358953064619339";    //VENDOR = 358953064619339 , CUSTOMER = 865874031057260
//        editor.putInt("TYPE",Settings.TYPE);
//        editor.putString("MOBILE", Settings.MOBILE.trim());
//        editor.putString("PASSWORD", Settings.PASSWORD.trim());
//        editor.commit();


//--------------------------------------------------------------------------------------


      //  SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if(!sharedpreferences.getString("MOBILE","-1").equalsIgnoreCase("-1")){

            // if(sharedpreferences.getInt("TYPE",-1) == 0)
            {
                Settings.MOBILE = sharedpreferences.getString("MOBILE", "-1");
                Settings.PASSWORD = sharedpreferences.getString("PASSWORD", "-1");
                LoginSyn LS = new LoginSyn(this);
                LS.execute("");
            }
        }
        else{
            //ShowSelection();
        }
    }

    public void ShowSelection(){
        final Dialog dialog = new Dialog(this);
        // Include dialog.xml file
        dialog.setContentView(R.layout.register_dlg);
        dialog.setCancelable(false);
        // Set dialog title
        dialog.setTitle("Team Mechanic");

        // set values for custom dialog components - text, image and button
        TextView resgister_user = (TextView) dialog.findViewById(R.id.register_user);
        TextView resgister_vendor = (TextView) dialog.findViewById(R.id.register_vendor);

        resgister_user.setText("Register as a customer");
        resgister_vendor.setText("Register as a vendor");


        dialog.show();

        // if decline button is clicked, close the custom dialog
        resgister_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Settings.TYPE = 0;
                Intent intent = new Intent(ctx,Verify_Phone.class);
                ctx.startActivity(intent);

                finish();
            }
        });

        resgister_vendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  dialog.dismiss();

            }
        });
    }


    public void _customer_click(View v){

        Settings.TYPE = 0;
        Intent intent = new Intent(ctx,Verify_Phone.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        ctx.startActivity(intent);
        overridePendingTransition(0,0);
        finish();

    }
    public void _mechanic_click(View v){
        Settings.TYPE = 1;
        Intent intent = new Intent(ctx,Verify_Phone.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        overridePendingTransition(0,0);
        ctx.startActivity(intent);
        finish();
    }


    class LoginSyn extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog = new ProgressDialog(SelectAccount.this);
        InputStream inputStream = null;
        String result = "";
        Context ctx;

        public LoginSyn(Context ctx){
            this.ctx = ctx;

        }

        protected void onPreExecute() {
            progressDialog.setMessage("Signing In...");
            progressDialog.show();
            progressDialog.setCancelable(false);
        }
        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                String sUrl = Settings.LOGIN_URL
                        + "mobile=" +  URLEncoder.encode(Settings.MOBILE.trim(), "UTF-8") +"&"
                        + "password="+ URLEncoder.encode(Settings.PASSWORD.trim(), "UTF-8");
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


                        if(mainObject.getString("type").equalsIgnoreCase("customer")) {
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

                            editor.putInt("TYPE",Settings.TYPE);
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
                        else
                        {



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

                            Settings.VENDOR_BANK_ACCOUNT = mainObject.getString("bank_account");
                            Settings.VENDOR_BANK_IFSC = mainObject.getString("bank_ifsc");
                            Settings.VENDOR_BANK_BRANCH = mainObject.getString("bank_branch");
                            Settings.VENDOR_BANK_ACCOUNT_TYPE = mainObject.getString("bank_account_type");

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


                    }
                    else
                    {
                        Toast.makeText(ctx,"Invalid Username Or Password",Toast.LENGTH_LONG).show();
                    }

                }
                catch(Exception e){


                }


            }


        }

    }



}
