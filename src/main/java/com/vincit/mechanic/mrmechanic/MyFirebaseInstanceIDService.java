package com.vincit.mechanic.mrmechanic;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

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

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService
{
   // String UPDATE_FCM_URL2 = "http://vedmal.com/lotus/updatetoken.php?";
   static  String MyPREFERENCES = "CREDENTIALS";
    String UPDATE_FCM_URL2 = "http://vedmal.com/mechanic/mech/app/updatetoken.php?";
    public String Token = "";
    public String imei = "";
    public String Mobile = "";
    public int Type = -1;
    public String TypeForService = "-1";
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        Token = FirebaseInstanceId.getInstance().getToken();
        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Mobile = sharedpreferences.getString("MOBILE","-1");
        Type = sharedpreferences.getInt("TYPE",-1);
        if(!Mobile.equalsIgnoreCase("-1")){
            if(Type == 0){
                TypeForService = "c";
            }else{
                TypeForService = "v";
            }
        }
    }

    public void UpdateToken(){

        updateToken ut = new updateToken();
        ut.execute("");
    }
    class updateToken extends AsyncTask<String, String, String> {

        InputStream inputStream = null;
        String result = "";


        public updateToken(){

        }

        protected void onPreExecute() {

        }
        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                String sUrl = UPDATE_FCM_URL2
                        + "mobile=" +  URLEncoder.encode(Mobile, "UTF-8") +"&"
                        + "type=" +  URLEncoder.encode(TypeForService, "UTF-8") +"&"
                        + "fcm="+ URLEncoder.encode(Token, "UTF-8");
                URL url = new URL(sUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                //    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

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

                JSONObject mainObject = null;
                JSONArray adsArray = null;




            }




    }


}
