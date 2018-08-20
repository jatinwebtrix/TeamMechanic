package com.vincit.mechanic.mrmechanic;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class AccountUser extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static ArrayList<Integer> CURRENT_SCREEN = new ArrayList();
    RecyclerView listview;
    RecyclerView.LayoutManager layoutManager;
    SwipeRefreshLayout refreshLayout;

    static String MyPREFERENCES = "CREDENTIALS";
    private Context mContext;
    private ImageView USER_PROFILE;
    private TextView headerTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Home");
        mContext = this;
        CURRENT_SCREEN.add(1);

        USER_PROFILE = (ImageView)findViewById(R.id.user_profile);
        Picasso.with(USER_PROFILE.getContext()).load((String)Settings.CUSTOMER_PHOTO_URL+Settings.CUSTOMER_PHOTO).fit().into(USER_PROFILE);

        listview = (RecyclerView)findViewById(R.id.cust_response_list);
        listview.setHasFixedSize(true);


        layoutManager = new LinearLayoutManager(this);
        listview.setLayoutManager(layoutManager);


        ((EditText)findViewById(R.id.user_full_name)).setText(Settings.CUSTOMER_NAME);
        ((EditText)findViewById(R.id.user_full_email)).setText(Settings.CUSTOMER_EMAIL);
        ((EditText)findViewById(R.id.user_phone)).setText(Settings.MOBILE);
        //((EditText)findViewById(R.id.user_address)).setText(Settings.CUSTOMER_ADDRESS);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header=navigationView.getHeaderView(0);
        headerTitle = (TextView)header.findViewById(R.id.title_name);
        headerTitle.setText("Hi , "+Settings.CUSTOMER_NAME);
        ((TextView)header.findViewById(R.id.title_email)).setText(Settings.CUSTOMER_EMAIL);

        ImageView headerImage = (de.hdodenhof.circleimageview.CircleImageView)header.findViewById(R.id.imageView);
        Picasso.with(headerImage.getContext()).load((String)Settings.CUSTOMER_PHOTO_URL+Settings.CUSTOMER_PHOTO).fit().into(headerImage);

        try {
            String ID = getIntent().getStringExtra("ID");
            if (!ID.equalsIgnoreCase("")) {
                NotifyVendor NV = new NotifyVendor(this, ID);
                NV.execute("");
            }
        }
        catch (Exception e){

        }

    }


    //--------IMAGE HANDLER---------------------

    public void updateProfileClick(View v){
        showFileChooser(v);
    }

    private static int PICK_IMAGE_REQUEST = 06;
    private Bitmap bitmap;
    public void showFileChooser(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                Settings.GET_BITMAP_STRING = getStringImage(bitmap);

                USER_PROFILE.setImageBitmap(bitmap);
                //Setting the Bitmap to ImageView
                //imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(String _Name,String _Email,String _Phone){
        final String KEY_IMAGE = "image";
        final String KEY_TYPE = "type";
        final String KEY_ID = "id";

        final String Name = _Name;
        final String Email = _Email;
        final String Phone = _Phone;

        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Team Mechanic","Syncing Account...",false,false);
        loading.setCancelable(false);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, Settings.IMAGE_HANDLER_URL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();

                        UpdateUserAsync updateUserAsync = new UpdateUserAsync(mContext,Name,Email,Phone);
                        updateUserAsync.execute("");

                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(AccountUser.this, "Something went wrong, try again...", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();



                //Adding parameters
                params.put(KEY_IMAGE, Settings.GET_BITMAP_STRING);
                params.put(KEY_TYPE, String.valueOf(Settings.TYPE));
                params.put(KEY_ID, Settings.CUSTOMER_ID);
                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }


    //--------------------------------------------




    public void UpdateUser(View v){

        String Name = ((EditText)findViewById(R.id.user_full_name)).getText().toString();
        String Email = ((EditText)findViewById(R.id.user_full_email)).getText().toString();
        String Phone = ((EditText)findViewById(R.id.user_phone)).getText().toString();



        uploadImage(Name,Email,Phone);


//
//        UpdateUserAsync updateUserAsync = new UpdateUserAsync(this,Name,Email,Phone);
//        updateUserAsync.execute("");
    }

    private static final int REQUEST_READ_LOCATION  = 9;
    public void Service_Click(View v){


        int permissionCheck1 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED && permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_READ_LOCATION);
        } else {
//            GetData GD = new GetData(this);
//            GD.execute("");

            Intent intent = new Intent(this,CustomerServiceSelect.class);
            startActivity(intent);
        }



    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_LOCATION:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Intent intent = new Intent(this,RegisterUser2.class);
                    startActivity(intent);
                }else{

                }
                break;

            default:
                break;
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
           // super.onBackPressed();
            if(CURRENT_SCREEN.size()>1){
                CURRENT_SCREEN.remove(CURRENT_SCREEN.size()-1);
                int index = CURRENT_SCREEN.get(CURRENT_SCREEN.size()-1);
                switch (index){
                    case 1:
                        showHome();
                        break;
                    case 2:
                        showResponse();
                        break;
                    case 3:
                        showEdit();
                        break;
                }
            }else{
                super.onBackPressed();
            }


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.account_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(id == R.id.nav_home){
            showHome();
            if(CURRENT_SCREEN.get(CURRENT_SCREEN.size()-1)!= 1) {
                CURRENT_SCREEN.add(1);
            }
        }
        if(id == R.id.nav_request_user){
            showResponse();
            if(CURRENT_SCREEN.get(CURRENT_SCREEN.size()-1)!= 2) {
                CURRENT_SCREEN.add(2);
            }
        }
        else if (id == R.id.nav_edit_user) {

            showEdit();
            if(CURRENT_SCREEN.get(CURRENT_SCREEN.size()-1)!= 3) {
                CURRENT_SCREEN.add(3);
            }
        }
//        else if (id == R.id.nav_logout) {
//
//            Logout();
//        }
        else if (id == R.id.nav_exit) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_contactus) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void showHome()
    {
        RelativeLayout parentLayout = (RelativeLayout) findViewById(R.id.layout_parent);
        parentLayout.setBackground(getResources().getDrawable(R.drawable.back));

        LinearLayout homeLayout = (LinearLayout) findViewById(R.id.layout_home);
        homeLayout.setVisibility(View.VISIBLE);

        LinearLayout responseLayout = (LinearLayout) findViewById(R.id.layout_response);
        responseLayout.setVisibility(View.GONE);

        LinearLayout editLayout = (LinearLayout) findViewById(R.id.layout_edit_user);
        editLayout.setVisibility(View.GONE);

        getSupportActionBar().setTitle("Home");
    }
    public void showResponse(){
        RelativeLayout parentLayout = (RelativeLayout) findViewById(R.id.layout_parent);
        parentLayout.setBackgroundColor(Color.parseColor("#ffffff"));

        LinearLayout homeLayout = (LinearLayout) findViewById(R.id.layout_home);
        homeLayout.setVisibility(View.GONE);

        LinearLayout responseLayout = (LinearLayout) findViewById(R.id.layout_response);
        responseLayout.setVisibility(View.VISIBLE);

        LinearLayout editLayout = (LinearLayout) findViewById(R.id.layout_edit_user);
        editLayout.setVisibility(View.GONE);

        GetResponse GR = new GetResponse(this);
        GR.execute("");

        getSupportActionBar().setTitle("Responses");
    }

    public void showEdit(){

        RelativeLayout parentLayout = (RelativeLayout) findViewById(R.id.layout_parent);
        parentLayout.setBackgroundColor(Color.parseColor("#44ADE1"));

        LinearLayout homeLayout = (LinearLayout) findViewById(R.id.layout_home);
        homeLayout.setVisibility(View.GONE);

        LinearLayout responseLayout = (LinearLayout) findViewById(R.id.layout_response);
        responseLayout.setVisibility(View.GONE);

        LinearLayout editLayout = (LinearLayout) findViewById(R.id.layout_edit_user);
        editLayout.setVisibility(View.VISIBLE);

        getSupportActionBar().setTitle("Account");
    }



    public void Logout(){


        /*
        SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        Settings.TYPE = -1;
        editor.putInt("TYPE",Settings.TYPE);
        editor.putString("MOBILE", "-1");
        editor.putString("PASSWORD", "-1");
        editor.commit();

        finish();
        Intent in = new Intent(this, Login.class);
        startActivity(in);
        */
    }
    class NotifyVendor extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;
        InputStream inputStream = null;
        String result = "";
        Context ctx;

        String id;
        public NotifyVendor(Context ctx,String id){
            this.ctx = ctx;
            this.id = id;
            progressDialog = new ProgressDialog(ctx);

        }

        protected void onPreExecute() {
            progressDialog.setMessage("Requesting...");
            progressDialog.show();
            progressDialog.setCancelable(false);
        }
        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                String sUrl = Settings.NOTIFY_VENDOR_URL
                        + "id=" +  URLEncoder.encode(id.trim(), "UTF-8") +"&"
                        + "lat=" +  URLEncoder.encode(Settings.CURRENT_LAT, "UTF-8") +"&"
                        + "long=" +  URLEncoder.encode(Settings.CURRENT_LONG, "UTF-8") +"&"
                        + "service=" +  URLEncoder.encode(Settings.CUSTOMER_SERVICE_SELECT, "UTF-8") +"&"
                        + "sub_service=" +  URLEncoder.encode(Settings.CUSTOMER_SUB_SERVICE_SELECT, "UTF-8") +"&"
                        + "address="+ URLEncoder.encode(Settings.CUSTOMER_ADDRESS.trim(), "UTF-8");


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


                }
                catch(Exception e){

                    Toast.makeText(ctx,e.getMessage(),Toast.LENGTH_LONG).show();
                }


            }


        }

    }

    class UpdateUserAsync extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;
        InputStream inputStream = null;
        String result = "";
        Context ctx;

        String Name,Email,Phone,Address,Password;
        public UpdateUserAsync(Context ctx,String Name,String Email,String Phone){
            this.ctx = ctx;
            progressDialog = new ProgressDialog(ctx);

            this.Name = Name;
            this.Email = Email;
            this.Phone = Phone;

        }

        protected void onPreExecute() {
            progressDialog.setMessage("Requesting...");
            progressDialog.show();
            progressDialog.setCancelable(false);
        }
        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                String sUrl = Settings.UPDATE_USER_URL
                        + "id=" +  URLEncoder.encode(Settings.CUSTOMER_ID, "UTF-8") +"&"
                        + "name=" +  URLEncoder.encode(Name.trim(), "UTF-8") +"&"
                        + "email=" +  URLEncoder.encode(Email.trim(), "UTF-8") +"&"
                        + "phone=" +  URLEncoder.encode(Phone.trim(), "UTF-8");


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
                   Toast.makeText(ctx,result,Toast.LENGTH_LONG).show();
                    Settings.CUSTOMER_NAME = Name.trim();
                    Settings.CUSTOMER_EMAIL = Email.trim();
                    Settings.MOBILE = Phone.trim();
                    Settings.TYPE = 0;
                    SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    editor.putInt("TYPE",Settings.TYPE);
                    editor.putString("MOBILE", Settings.MOBILE.trim());
                    editor.putString("PASSWORD", Settings.PASSWORD.trim());
                    editor.commit();

                    headerTitle.setText("Hi , "+Settings.CUSTOMER_NAME);

                }
                catch(Exception e){

                    Toast.makeText(ctx,e.getMessage(),Toast.LENGTH_LONG).show();
                }


            }


        }

    }


    class GetResponse extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;
        InputStream inputStream = null;
        String result = "";
        Context ctx;

        String Name,Email,Phone,Address,Password;
        public GetResponse(Context ctx){
            this.ctx = ctx;
            progressDialog = new ProgressDialog(ctx);



        }

        protected void onPreExecute() {
            progressDialog.setMessage("Getting Requests...");
            progressDialog.show();
            progressDialog.setCancelable(false);
        }
        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                String sUrl = Settings.GET_RESPONSE_URL
                        + "cid=" +  URLEncoder.encode(Settings.CUSTOMER_ID, "UTF-8");


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
                    Settings.RESPONSE_LIST.clear();
                    mainArray = new JSONArray(result);
                    for(int i=0;i<mainArray.length();i++){
                        JSONObject request = mainArray.getJSONObject(i);
                        Response r = new Response();
                        r.Rating = request.getString("rating");
                        r.rid = request.getString("rid");
                        r.amount = request.getString("amount");
                        r.eta = request.getString("eta");
                        r.name = request.getString("name");
                        r.phone = request.getString("phone");
                        r.status = request.getString("status");
                        Settings.RESPONSE_LIST.add(r);

                    }
                    Toast.makeText(ctx,String.valueOf(Settings.RESPONSE_LIST.size()),Toast.LENGTH_LONG).show();
                    ResponseAdapter RA = new ResponseAdapter(Settings.RESPONSE_LIST,ctx);
                    listview.setAdapter(RA);

                }
                catch(Exception e){

                    Toast.makeText(ctx,e.getMessage(),Toast.LENGTH_LONG).show();
                }


            }


        }

    }




    class GetData extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog = new ProgressDialog(AccountUser.this);
        InputStream inputStream = null;
        String result = "";
        Context ctx;


        public GetData(Context ctx){
            this.ctx = ctx;

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

//                        Intent intent = new Intent(ctx,RegisterUser2.class);
//                        startActivity(intent);

                    Intent intent = new Intent(ctx,CustomerServiceSelect.class);
                    startActivity(intent);




                }
                catch(Exception e){


                }


            }


        }

    }


}
