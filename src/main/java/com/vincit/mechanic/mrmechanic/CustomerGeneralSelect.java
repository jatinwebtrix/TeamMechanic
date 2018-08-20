package com.vincit.mechanic.mrmechanic;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

public class CustomerGeneralSelect extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_general_select);
        Settings.CUSTOMER_GENERAL_SELECT_ACTIVITY = this;

        ((EditText)findViewById(R.id.customer_mobile_select)).setText(Settings.MOBILE);
    }
    public void CustomerSelectSubmit(View v){
        ServiceSync SS = new ServiceSync(this);
        SS.execute("");
        Toast.makeText(this,Settings.CUSTOMER_SERVICE_SELECT,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Settings.CUSTOMER_ADDRESS_SELECT = "-1";
        Settings.CUSTOMER_LAT_SELECT = "-1";
        Settings.CUSTOMER_LONG_SELECT = "-1";
    }

    class ServiceSync extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog = new ProgressDialog(CustomerGeneralSelect.this);
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
                        + "address="+ URLEncoder.encode(Settings.CUSTOMER_ADDRESS_SELECT, "UTF-8") + "&"
                        + "lat="+ URLEncoder.encode(Settings.CUSTOMER_LAT_SELECT.trim(), "UTF-8") + "&"
                        + "service=" +  URLEncoder.encode(Settings.CUSTOMER_SERVICE_SELECT, "UTF-8") +"&"
                        + "sub_service=" +  URLEncoder.encode(Settings.CUSTOMER_SUB_SERVICE_SELECT, "UTF-8") +"&"
                        + "long="+ URLEncoder.encode(Settings.CUSTOMER_LONG_SELECT.trim(), "UTF-8");

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

                    Toast.makeText(ctx,"Request sent with id : "+Settings.REQUEST_ID,Toast.LENGTH_LONG).show();

                    if(Settings.CUSTOMER_SERVICE_SELECT_ACTIVITY !=null) {
                        Settings.CUSTOMER_SERVICE_SELECT_ACTIVITY.finish();
                    }
                    if(Settings.CUSTOMER_SUB_SERVICE_SELECT_ACTIVITY !=null) {
                        Settings.CUSTOMER_SUB_SERVICE_SELECT_ACTIVITY.finish();
                    }
                    if(Settings.CUSTOMER_MAP_SELECT_ACTIVITY !=null) {
                        Settings.CUSTOMER_MAP_SELECT_ACTIVITY.finish();
                    }
                    finish();


                }
                catch(Exception e){

                    Toast.makeText(ctx,e.getMessage(),Toast.LENGTH_LONG).show();
                }


            }


        }

    }

}
