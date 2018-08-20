package com.vincit.mechanic.mrmechanic;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Accept extends Activity {

    static  String MyPREFERENCES = "CREDENTIALS";
    private String REQUEST_ID = "-1";
    private Context ctx;
    private TextView Distance;
    private TextView Service;
    private TextView Date;
    private TextView Address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept);

        Distance = (TextView)findViewById(R.id.distance);
        Service = (TextView)findViewById(R.id.service);
        Date = (TextView)findViewById(R.id.date);
        Address = (TextView)findViewById(R.id.address);

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(ns);
        notificationManager.cancel(1);


        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        REQUEST_ID = sharedpreferences.getString("REQUEST_ID","-1");


       // REQUEST_ID = getIntent().getStringExtra("REQUEST_ID");
        this.ctx = this;
        //Toast.makeText(this,REQUEST_ID,Toast.LENGTH_LONG).show();
        //finish();

        if(!REQUEST_ID.equalsIgnoreCase("-1")) {
            //ShowBox();
            Events();
        }
    }
    public void Events(){


        GetSingleRequest GSR = new GetSingleRequest(this);
        GSR.execute("");

        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // set values for custom dialog components - text, image and button
        Button send = (Button) findViewById(R.id.send);
        final String Amount = ((EditText)findViewById(R.id.amount)).getText().toString();

        final String Eta = ((EditText)findViewById(R.id.eta)).getText().toString();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                EditText amount = (EditText) findViewById(R.id.amount);
                EditText eta = (EditText) findViewById(R.id.eta);

                if(!amount.getText().toString().trim().equalsIgnoreCase("") && !amount.getText().toString().trim().equalsIgnoreCase("0") && !eta.getText().toString().trim().equalsIgnoreCase("")) {
                    ResponseSync RS = new ResponseSync(ctx, amount.getText().toString(), eta.getText().toString());
                    RS.execute("");
                }else{
                    Toast.makeText(ctx,"Please fill all required fields",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void ShowBox(){
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setTitle("Team Mechanic");
        // Include dialog.xml file
        dialog.setContentView(R.layout.accept_dlg);
        // Set dialog title
        dialog.setCancelable(false);
        dialog.setTitle("Team Mechanic");



        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        // set values for custom dialog components - text, image and button
        Button send = (Button) dialog.findViewById(R.id.send);
        final String Amount = ((EditText)dialog.findViewById(R.id.amount)).getText().toString();

        final String Eta = ((EditText)dialog.findViewById(R.id.eta)).getText().toString();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 EditText amount = (EditText) dialog.findViewById(R.id.amount);
                 EditText eta = (EditText) dialog.findViewById(R.id.eta);

                ResponseSync RS = new ResponseSync(ctx,amount.getText().toString(),eta.getText().toString());
                RS.execute("");


            }
        });


        dialog.show();



    }

    class GetSingleRequest extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog = new ProgressDialog(Accept.this);
        InputStream inputStream = null;
        String result = "";
        Context ctx;

        public GetSingleRequest(Context ctx){
            this.ctx = ctx;

        }

        protected void onPreExecute() {
            progressDialog.setMessage("Getting request...");
            progressDialog.show();
            progressDialog.setCancelable(false);
        }
        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                String sUrl = Settings.GET_SINGLE_RQUEST_URL
                        + "rid=" +  URLEncoder.encode(REQUEST_ID.trim(), "UTF-8");


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
                    JSONObject requestJSON =  new JSONObject(result);
                    Double integer = Double.parseDouble(requestJSON.getString("distance"));
                    if(integer<5000) {
                        Distance.setText("Approx. Distance : " + requestJSON.getString("distance") + " kms");
                    }else{
                        Distance.setText("Approx. Distance : Distance not available");
                    }
                    Service.setText("Services requested : " +requestJSON.getString("sub_service"));
                    Date.setText("Date : " +requestJSON.getString("date1"));
                    Address.setText("Address : " +requestJSON.getString("address"));
                }
                catch(Exception e){

                    Toast.makeText(ctx,e.getMessage(),Toast.LENGTH_LONG).show();
                }


            }


        }

    }



    class ResponseSync extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog = new ProgressDialog(Accept.this);
        InputStream inputStream = null;
        String result = "";
        Context ctx;

        String Amount,Eta;
        public ResponseSync(Context ctx,String Amount,String Eta){
            this.ctx = ctx;
            this.Amount = Amount;
            this.Eta = Eta;

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
                String sUrl = Settings.NOTIFY_CUST_URL
                        + "rid=" +  URLEncoder.encode(REQUEST_ID.trim(), "UTF-8") +"&"
                        + "amount="+ URLEncoder.encode(Amount.trim(), "UTF-8") +"&"
                        + "eta=" +  URLEncoder.encode(Eta.trim(), "UTF-8");


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


                    Toast.makeText(ctx,result,Toast.LENGTH_LONG).show();
                    finish();

                }
                catch(Exception e){

                    Toast.makeText(ctx,e.getMessage(),Toast.LENGTH_LONG).show();
                }


            }


        }

    }


}
