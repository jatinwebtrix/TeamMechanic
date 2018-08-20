package com.vincit.mechanic.mrmechanic;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Accept2 extends Activity {

    static  String MyPREFERENCES = "CREDENTIALS";
    private String RESPONSE_ID = "-1";
    private String AMOUNT = "-1";
    private Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept2);

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(ns);
        notificationManager.cancel(1);


        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        RESPONSE_ID = sharedpreferences.getString("RESPONSE_ID","-1");
        AMOUNT = sharedpreferences.getString("AMOUNT","-1");

        // REQUEST_ID = getIntent().getStringExtra("REQUEST_ID");
        this.ctx = this;
       // Toast.makeText(this,RESPONSE_ID,Toast.LENGTH_LONG).show();
        //finish();

        if(!RESPONSE_ID.equalsIgnoreCase("-1")) {
           // ShowBox();
            Events();
        }
    }
    private void ShowBox(){
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setTitle("Team Mechanic");
        // Include dialog.xml file
        dialog.setContentView(R.layout.accept2_dlg);
        // Set dialog title
        dialog.setCancelable(false);
        dialog.setTitle("Team Mechanic");


        TextView amount = (TextView)dialog.findViewById(R.id.amount);
        amount.setText(AMOUNT);


        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        // set values for custom dialog components - text, image and button
        Button send = (Button) dialog.findViewById(R.id.accept);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                NotifyVendor2Sync RS = new NotifyVendor2Sync(ctx);
                RS.execute("");


            }
        });


        dialog.show();



    }

    public void Events(){
        TextView amount = (TextView)findViewById(R.id.amount);
        amount.setText("Accept this offer for : " + AMOUNT);


        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // set values for custom dialog components - text, image and button
        Button send = (Button) findViewById(R.id.accept);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                NotifyVendor2Sync RS = new NotifyVendor2Sync(ctx);
                RS.execute("");


            }
        });

    }

    class NotifyVendor2Sync extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog = new ProgressDialog(Accept2.this);
        InputStream inputStream = null;
        String result = "";
        Context ctx;


        public NotifyVendor2Sync(Context ctx){
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
                String sUrl = Settings.NOTIFY_VENDOR_URL2
                        + "rid=" +  URLEncoder.encode(RESPONSE_ID.trim(), "UTF-8");


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
