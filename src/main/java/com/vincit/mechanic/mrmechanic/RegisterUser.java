package com.vincit.mechanic.mrmechanic;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import static android.R.attr.bitmap;

public class RegisterUser extends AppCompatActivity {

    static  String MyPREFERENCES = "CREDENTIALS";
    Spinner Manu1;
    private Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        this.ctx = this;

        ArrayList STATES = new ArrayList();
        for(int i = 0;i<Settings.STATE.size();i++) {
            State state = (State)Settings.STATE.get(i);
            STATES.add(state.sname);
        }

        Spinner State = (Spinner)findViewById(R.id.user_state);
        ArrayAdapter adapter = new ArrayAdapter(this,R.layout.spinner_row,R.id.title, STATES);
        State.setAdapter(adapter);

        ArrayList CITIES = new ArrayList();
        for(int i = 0;i<Settings.CITY.size();i++) {
            City city = (City)Settings.CITY.get(i);
            CITIES.add(city.ccname);
        }

        Spinner City = (Spinner)findViewById(R.id.user_city);

        ArrayAdapter adapter_city = new ArrayAdapter(this,R.layout.spinner_row,R.id.title, CITIES);
        City.setAdapter(adapter_city);

        ArrayList MANU = new ArrayList();
        for(int i = 0;i<Settings.CAT.size();i++) {
            Cat cat = (Cat)Settings.CAT.get(i);
            MANU.add(cat.cat_name);
        }

        Spinner Manu = (Spinner)findViewById(R.id.user_vehicle_cat);

        ArrayAdapter adapter_manu = new ArrayAdapter(this,R.layout.spinner_row,R.id.title, MANU);
        Manu.setAdapter(adapter_manu);

        ArrayList SUB_CAT = new ArrayList();
        for(int i = 0;i<Settings.SUBCAT.size();i++) {
            SubCat subcat = (SubCat)Settings.SUBCAT.get(i);
            SUB_CAT.add(subcat.subcat_name);
        }

        Manu1 = (Spinner)findViewById(R.id.user_vehicle_subcat);
        ArrayAdapter adapter_submanu = new ArrayAdapter(this,R.layout.spinner_row,R.id.title, SUB_CAT);
        Manu1.setAdapter(adapter_submanu);


        Manu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                    ArrayAdapter adapter_submanu = new ArrayAdapter(ctx, R.layout.spinner_row, R.id.title, SUB_CAT);
                    Manu1.setAdapter(adapter_submanu);
                    Manu1.setVisibility(View.VISIBLE);
                }else{
                    Manu1.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


    }

    public void RegisterUser(View v){

        User user = new User();
        user.name = ((EditText)findViewById(R.id.user_full_name)).getText().toString().trim();
        user.email = ((EditText)findViewById(R.id.user_full_email)).getText().toString().trim();
        user.mobile = ((EditText)findViewById(R.id.user_phone)).getText().toString().trim();
        user.address = ((EditText)findViewById(R.id.user_address)).getText().toString().trim();
        user.sid = ((State)Settings.STATE.get(((Spinner)findViewById(R.id.user_state)).getSelectedItemPosition())).sid;
        user.ccid = ((City)Settings.CITY.get(((Spinner)findViewById(R.id.user_city)).getSelectedItemPosition())).cid;
        user.cat_id = ((Cat)Settings.CAT.get(((Spinner)findViewById(R.id.user_vehicle_cat)).getSelectedItemPosition())).cat_id;
        user.vehicle_make =((EditText)findViewById(R.id.user_vehicle_make)).getText().toString().trim();
        user.vehicle_model = ((EditText)findViewById(R.id.user_vehicle_mo)).getText().toString().trim();
        user.vehicle_no = ((EditText)findViewById(R.id.user_vehicle_n)).getText().toString().trim();
        user.vehicle_year = ((EditText)findViewById(R.id.user_vehicle_year)).getText().toString().trim();
        user.password = ((EditText)findViewById(R.id.user_password)).getText().toString().trim();
        user.FCM = FirebaseInstanceId.getInstance().getToken();
        user.photo = getStringImage(null);
        Settings.MOBILE = user.mobile;
        Settings.PASSWORD = user.password;

        RegisterSync RS = new RegisterSync(this,user);
        RS.execute("");


    }



    private static int PICK_IMAGE_REQUEST = 06;
    private Bitmap bitmap;
    public void showFileChooser(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                Settings.GET_BITMAP_STRING = getStringImage(bitmap);

                //Setting the Bitmap to ImageView
                //imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(String image,String type,String Id){
        final String KEY_IMAGE = "image";
        final String KEY_TYPE = "type";

        final String VALUE_IMAGE = image;
        final String VALUE_TYPE = type;
        final String ID = Id;

        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Team Mechanic","Syncing Account...",false,false);
        loading.setCancelable(false);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, Settings.IMAGE_HANDLER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        finish();
                        Intent in = new Intent(ctx, AccountUser.class);
                        startActivity(in);
                        //Toast.makeText(RegisterUser.this, s , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(RegisterUser.this, "Something went wrong, try again...", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(KEY_IMAGE, VALUE_IMAGE);
                params.put(KEY_TYPE, VALUE_TYPE);
                params.put("id",ID);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }




    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(this,Login.class);
        startActivity(intent);
    }


    private String hashMapToUrl(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
    class RegisterSync extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog = new ProgressDialog(RegisterUser.this);
        InputStream inputStream = null;
        String result = "";
        Context ctx;
        User user;
        public RegisterSync(Context ctx,User user){
            this.ctx = ctx;
            this.user = user;
        }

        protected void onPreExecute() {
            progressDialog.setMessage("Updating...");
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


                            if(!Settings.GET_BITMAP_STRING.equalsIgnoreCase("-1")) {
                                uploadImage(Settings.GET_BITMAP_STRING, "0", Settings.CUSTOMER_ID);
                            }

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



    public String getStringImage(Bitmap bmp){

        if(bmp==null) {
            bmp= BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    R.drawable.icon);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


}
