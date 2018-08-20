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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

public class Login extends AppCompatActivity {

    private Context ctx;
    private EditText Mobile,Password;
    static  String MyPREFERENCES = "CREDENTIALS";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ctx = this;
        Mobile = (EditText)findViewById(R.id.login_mobile);
        Password = (EditText)findViewById(R.id.login_password);

        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if(!sharedpreferences.getString("MOBILE","-1").equalsIgnoreCase("-1")){

           // if(sharedpreferences.getInt("TYPE",-1) == 0)
            {
                Settings.MOBILE = sharedpreferences.getString("MOBILE", "-1");
                Settings.PASSWORD = sharedpreferences.getString("PASSWORD", "-1");
                Mobile.setText(Settings.MOBILE);
                Password.setText(Settings.PASSWORD);
                LoginSyn LS = new LoginSyn(this);
                LS.execute("");
            }
        }


    }

    public void Login_Click(View v){
        Settings.MOBILE = Mobile.getText().toString();
        Settings.PASSWORD = Password.getText().toString();

        if(Settings.MOBILE.trim().length()>0 && Settings.PASSWORD.trim().length()>0) {
            LoginSyn LS = new LoginSyn(this);
            LS.execute("");
        }else{
            Toast.makeText(this,"Please Enter Username/Password First",Toast.LENGTH_SHORT).show();
        }


    }

    public void SignUp_Click(View v){
        final Dialog dialog = new Dialog(this);
        // Include dialog.xml file
        dialog.setContentView(R.layout.register_dlg);
        // Set dialog title
        dialog.setTitle("Team Mechanic");

        // set values for custom dialog components - text, image and button
        TextView resgister_user = (TextView) dialog.findViewById(R.id.register_user);
        TextView resgister_vendor = (TextView) dialog.findViewById(R.id.register_vendor);



        dialog.show();

        // if decline button is clicked, close the custom dialog
        resgister_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
               // finish();
               // Intent intent = new Intent(ctx,RegisterUser.class);
               // startActivity(intent);
                GetData GD = new GetData(ctx,0);
                GD.execute("");
            }
        });

        resgister_vendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                GetData GD = new GetData(ctx,1);
                GD.execute("");
            }
        });
    }



    public void Guest_Click(View v){
        User user = new User();
        user.name = "Guest";
        user.email = "N/A";
        user.mobile = "N/A";
        user.address = "N/A";
        user.sid = "N/A";
        user.ccid = "N/A";
        user.cat_id = "N/A";
        user.vehicle_make ="N/A";
        user.vehicle_model = "N/A";
        user.vehicle_no = "N/A";
        user.vehicle_year = "N/A";
        user.password = "N/A";
        user.FCM = FirebaseInstanceId.getInstance().getToken();
        user.photo = "default";
        Settings.MOBILE = user.mobile;
        Settings.PASSWORD = user.password;

        RegisterSync RS = new RegisterSync(this,user);
        RS.execute("");
    }

    class LoginSyn extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog = new ProgressDialog(Login.this);
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

                            finish();
                            Intent in = new Intent(ctx, AccountUser.class);
                            startActivity(in);
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


                }


            }


        }

    }

    class GetData extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog = new ProgressDialog(Login.this);
        InputStream inputStream = null;
        String result = "";
        Context ctx;

        int M;
        public GetData(Context ctx,int m){
            this.ctx = ctx;
            M = m;
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



                    finish();
                    if(M==0) {
                        Intent intent = new Intent(ctx, RegisterUser.class);
                        startActivity(intent);
                    }
                    else{
                        Intent intent = new Intent(ctx, RegisterVendor.class);
                        startActivity(intent);
                    }

                }
                catch(Exception e){


                }


            }


        }

    }


    class RegisterSync extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog = new ProgressDialog(Login.this);
        InputStream inputStream = null;
        String result = "";
        Context ctx;
        User user;
        public RegisterSync(Context ctx,User user){
            this.ctx = ctx;
            this.user = user;
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
                String sUrl = Settings.REGISTER_USER_URL
                        + "name=" +  URLEncoder.encode(user.name.trim(), "UTF-8") +"&"
                        + "email="+ URLEncoder.encode(user.email.trim(), "UTF-8") + "&"
                        + "mobile="+ URLEncoder.encode(user.mobile.trim(), "UTF-8") + "&"
                        + "address="+ URLEncoder.encode(user.address.trim(), "UTF-8") + "&"
                        + "sid="+ URLEncoder.encode(user.sid.trim(), "UTF-8") + "&"
                        + "ccid="+ URLEncoder.encode(user.ccid.trim(), "UTF-8") + "&"
                        + "cat_id="+ URLEncoder.encode(user.cat_id.trim(), "UTF-8") + "&"
                        + "vehicle_make="+ URLEncoder.encode(user.vehicle_make.trim(), "UTF-8") + "&"
                        + "vehicle_model="+ URLEncoder.encode(user.vehicle_model.trim(), "UTF-8") + "&"
                        + "vehicle_no="+ URLEncoder.encode(user.vehicle_no.trim(), "UTF-8") + "&"
                        + "vehicle_year="+ URLEncoder.encode(user.vehicle_year.trim(), "UTF-8") + "&"
                        + "fcm="+ URLEncoder.encode(user.FCM, "UTF-8") + "&"
                        + "password="+ URLEncoder.encode(user.password.trim(), "UTF-8");

                URL url = new URL(sUrl);
                connection = (HttpURLConnection) url.openConnection();
                //connection.setDoOutput(true);
                //connection.setRequestMethod("POST");
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

                            finish();
                            Intent in = new Intent(ctx, AccountUser.class);
                            startActivity(in);
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


