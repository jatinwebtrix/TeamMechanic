package com.vincit.mechanic.mrmechanic;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

public class AccountVendor extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static ArrayList<Integer> CURRENT_SCREEN = new ArrayList();
    static String MyPREFERENCES = "CREDENTIALS";
    RecyclerView listview;
    RecyclerView.LayoutManager layoutManager;
    SwipeRefreshLayout refreshLayout;

    private LinearLayout chips;



    private String CATIDS="-1";
    private String SUBCATIDS = "-1";
    int mHour,mMinute;
private Context mContext;
    private TextView openingTime,closingTime;
    private String BITMAP_PROFILE_STRING = "";
    private String BITMAP_CHEQUE_STRING = "";
    private String BITMAP_WORK_STRING = "";
    private TextView headerTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_vendor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account");

        CURRENT_SCREEN.add(2);
        LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        checkParams.setMargins(5, 5, 5, 5);
        chips = (LinearLayout) findViewById(R.id.service_chips);
        String[] VENDOR_CAT_IDS = Settings.VENDOR_CAT_ID.split("\\|");
        if(VENDOR_CAT_IDS.length>0) {
            HashMap<String, String> CAT_IDS = new HashMap<>();
            for (int i = 0; i < Settings.CAT.size(); i++) {
                Cat cat = (Cat) Settings.CAT.get(i);
                CAT_IDS.put(cat.cat_id, cat.cat_name);
            }
            for (int i = 0; i < VENDOR_CAT_IDS.length; i++) {
                TextView b = new TextView(getApplicationContext());
                if(CAT_IDS.get(VENDOR_CAT_IDS[i]) == null){
                    b.setText("No service selected");
                    b.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));
                    b.setLayoutParams(checkParams);
                    b.setBackgroundResource(R.drawable.chip);
                    chips.addView(b);
                    break;
                }
                b.setText(String.valueOf(CAT_IDS.get(VENDOR_CAT_IDS[i])));
                b.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));
                b.setLayoutParams(checkParams);
                b.setBackgroundResource(R.drawable.chip);
                chips.addView(b);
            }
        }else{
            TextView b = new TextView(getApplicationContext());
            b.setText("No service selected");
            b.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));
            b.setLayoutParams(checkParams);
            b.setBackgroundResource(R.drawable.chip);
            chips.addView(b);
        }


        ((TextView)findViewById(R.id.address1)).setText(String.valueOf(Settings.CUSTOMER_CURRENT_LOCATION));

        ImageView VENDOR_PROFILE = (ImageView)findViewById(R.id.vendor_profile);
        if(Settings.VENDOR_PHOTO.length() >5) {
            Picasso.with(VENDOR_PROFILE.getContext()).load((String) Settings.VENDOR_PROFILE_URL + Settings.VENDOR_PHOTO).fit().into(VENDOR_PROFILE);
        }

        Button VENDOR_WORK_PHOTO = (Button)findViewById(R.id.vendor_work_photo);
//        if(Settings.VENDOR_WORK_PHOTO.length()>5) {
//           // VENDOR_WORK_PHOTO.setText(Settings.VENDOR_WORK_PHOTO);
//        }
        LinearLayout requestLayout = (LinearLayout) findViewById(R.id.layout_list);
        requestLayout.setVisibility(View.GONE);

        LinearLayout editLayout = (LinearLayout) findViewById(R.id.layout_edit);
        editLayout.setVisibility(View.VISIBLE);




        GetRequest GR = new GetRequest(this);
        GR.execute("");


        mContext = this;

        openingTime = (TextView)findViewById(R.id.opening_time);
        closingTime = (TextView)findViewById(R.id.closing_time);



        ArrayList STATES = new ArrayList();
        for(int i = 0;i<Settings.STATE.size();i++) {
            State state = (State)Settings.STATE.get(i);
            STATES.add(state.sname);
        }
        Spinner State = (Spinner)findViewById(R.id.vendor_state);
        ArrayAdapter adapter = new ArrayAdapter(this,R.layout.spinner_row1,R.id.title, STATES);
        State.setAdapter(adapter);


        ArrayList CITIES = new ArrayList();
        for(int i = 0;i<Settings.CITY.size();i++) {
            City city = (City)Settings.CITY.get(i);
            CITIES.add(city.ccname);
        }
        Spinner City = (Spinner)findViewById(R.id.vendor_city);
        ArrayAdapter adapter1 = new ArrayAdapter(this,R.layout.spinner_row1,R.id.title, CITIES);
        City.setAdapter(adapter1);



        listview = (RecyclerView)findViewById(R.id.vendor_request_list);
        listview.setHasFixedSize(true);


        layoutManager = new LinearLayoutManager(this);
        listview.setLayoutManager(layoutManager);

        ((EditText)findViewById(R.id.vendor_full_name)).setText(Settings.VENDOR_NAME);
        ((EditText)findViewById(R.id.vendor_email)).setText(Settings.VENDOR_EMAIL);
        ((EditText)findViewById(R.id.vendor_phone)).setText(Settings.MOBILE);
        ((EditText)findViewById(R.id.vendor_pin)).setText(Settings.VENDOR_PINCODE);
        ((EditText)findViewById(R.id.vendor_address1)).setText(Settings.VENDOR_ADDRESS1);
        ((EditText)findViewById(R.id.vendor_address2)).setText(Settings.VENDOR_ADDRESS2);
        ((TextView)findViewById(R.id.address1)).setText(Settings.CUSTOMER_CURRENT_LOCATION);
        ((EditText)findViewById(R.id.vendor_description)).setText(Settings.VENDOR_DESCRIPTION);
        ((EditText)findViewById(R.id.vendor_bank_account)).setText(Settings.VENDOR_BANK_ACCOUNT);
        ((EditText)findViewById(R.id.vendor_bank_ifsc)).setText(Settings.VENDOR_BANK_IFSC);
        ((EditText)findViewById(R.id.vendor_bank_branch)).setText(Settings.VENDOR_BANK_BRANCH);
        ((EditText)findViewById(R.id.vendor_bank_account_type)).setText(Settings.VENDOR_BANK_ACCOUNT_TYPE);
        openingTime.setText(Settings.VENDOR_START_TIME);
        closingTime.setText(Settings.VENDOR_END_TIME);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header=navigationView.getHeaderView(0);
        headerTitle = (TextView)header.findViewById(R.id.title_vendor);
        headerTitle.setText("Hi , "+Settings.VENDOR_NAME);
        ImageView headerImage = (de.hdodenhof.circleimageview.CircleImageView)header.findViewById(R.id.imageView);
        if(Settings.VENDOR_PHOTO.length() >5) {
            Picasso.with(headerImage.getContext()).load((String) Settings.VENDOR_PROFILE_URL + Settings.VENDOR_PHOTO).fit().into(headerImage);
        }
        ((TextView)header.findViewById(R.id.title_email)).setText(Settings.VENDOR_EMAIL);
    }



    private static int PICK_IMAGE_REQUEST_PROFILE = 01;
    private static int PICK_IMAGE_REQUEST_CHEQUE = 02;
    private static int PICK_IMAGE_REQUEST_WORK = 03;
    private Bitmap bitmap1,bitmap2,bitmap3;
    public void showFileChooserProfile(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_PROFILE);
    }
    public void showFileChooserCheque(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_CHEQUE);
    }
    public void showFileChooserWork(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_WORK);
    }


    private static int PLACE_PICKER_REQUEST = 88;
    private ProgressDialog pd_map_open;
    public void SelectMap(View v){
        pd_map_open = new ProgressDialog(AccountVendor.this);
        pd_map_open.setMessage("Opening Map...");
        pd_map_open.show();
        pd_map_open.setCancelable(false);

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();


        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
            pd_map_open.dismiss();
        }

    }
    String LAT = "-1";
    String LONG = "-1";


    private static int PICK_SERVICE_REQUEST = 101;
    public void SelectServices(View v){
        Intent intent = new Intent(this,CatList.class);
        startActivityForResult(intent,PICK_SERVICE_REQUEST);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {



                Place selectedPlace = PlacePicker.getPlace(this, data);

                LAT = String.valueOf(selectedPlace.getLatLng().latitude);
                LONG = String.valueOf(selectedPlace.getLatLng().longitude);
                Settings.CUSTOMER_CURRENT_LOCATION = selectedPlace.getAddress().toString();
                ((TextView)findViewById(R.id.address1)).setText(selectedPlace.getAddress());
                Settings.CURRENT_LAT = LAT;
                Settings.CURRENT_LONG = LONG;
                ((Button)findViewById(R.id.vendor_location)).setText("Change Location");

            }
        }
        if (requestCode == PICK_IMAGE_REQUEST_PROFILE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                BITMAP_PROFILE_STRING = getStringImage(bitmap1);

                ((ImageView)findViewById(R.id.vendor_profile)).setImageBitmap(bitmap1);
                //Setting the Bitmap to ImageView
                //imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == PICK_IMAGE_REQUEST_CHEQUE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {

                ((Button)findViewById(R.id.vendor_cancel_cheque)).setText(getFileName(filePath));
                bitmap2 = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                BITMAP_CHEQUE_STRING = getStringImage(bitmap2);

                //Setting the Bitmap to ImageView
                //imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == PICK_IMAGE_REQUEST_WORK && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                ((Button)findViewById(R.id.vendor_work_photo)).setText(getFileName(filePath));
                bitmap3 = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                BITMAP_WORK_STRING = getStringImage(bitmap3);

                //Setting the Bitmap to ImageView
                //imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == PICK_SERVICE_REQUEST) {
            if (resultCode == RESULT_OK) {

                CATIDS = data.getStringExtra("CATIDS");
                SUBCATIDS = data.getStringExtra("SUBCATIDS");
            }
        }



        if(pd_map_open != null){
            pd_map_open.dismiss();
        }

    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
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




    public void openingTimeClick(View v){

        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,R.style.TimePickerTheme,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        String mMinute = String.valueOf(minute);
                        if(mMinute.length()==1)
                            mMinute = "0"+mMinute;
                        if(hourOfDay>12) {
                            openingTime.setText(String.valueOf(hourOfDay-12) + ":" + mMinute+ " pm");
                        }else if(hourOfDay==12 && minute>=0){
                            openingTime.setText(String.valueOf(hourOfDay) + ":" + mMinute+ " pm");
                        }else if(hourOfDay<12){
                            openingTime.setText(String.valueOf(hourOfDay) + ":" + mMinute+ " am");
                        }
                    }
                }, mHour, mMinute, false);
        timePickerDialog.setCancelable(false);
        timePickerDialog.show();

    }
    public void closingTimeClick(View v){

        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,R.style.TimePickerTheme,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        String mMinute = String.valueOf(minute);
                        if(mMinute.length()==1)
                            mMinute = "0"+mMinute;
                        if(hourOfDay>12) {
                            closingTime.setText(String.valueOf(hourOfDay-12) + ":" + mMinute+ " pm");
                        }else if(hourOfDay==12 && minute>=0){
                            closingTime.setText(String.valueOf(hourOfDay) + ":" + mMinute+ " pm");
                        }else if(hourOfDay<12){
                            closingTime.setText(String.valueOf(hourOfDay) + ":" + mMinute+ " am");
                        }
                    }
                }, mHour, mMinute, false);
        timePickerDialog.setCancelable(false);
        timePickerDialog.show();

    }

    public void UpdateVendor(View v){

        Vendor vendor = new Vendor();
        vendor.name = ((EditText)findViewById(R.id.vendor_full_name)).getText().toString();
        vendor.email = ((EditText)findViewById(R.id.vendor_email)).getText().toString();
        vendor.mobile = ((EditText)findViewById(R.id.vendor_phone)).getText().toString();
        vendor.pincode = ((EditText)findViewById(R.id.vendor_pin)).getText().toString();
        vendor.address1 = ((EditText)findViewById(R.id.vendor_address1)).getText().toString();
        vendor.address2 = ((EditText)findViewById(R.id.vendor_address2)).getText().toString();
        vendor.sid = ((State)Settings.STATE.get(((Spinner)findViewById(R.id.vendor_state)).getSelectedItemPosition())).sid;
        vendor.ccid = ((City)Settings.CITY.get(((Spinner)findViewById(R.id.vendor_city)).getSelectedItemPosition())).cid;
        vendor.location = Settings.CUSTOMER_CURRENT_LOCATION;
        vendor.description = ((EditText)findViewById(R.id.vendor_description)).getText().toString();
        vendor.bank_account = ((EditText)findViewById(R.id.vendor_bank_account)).getText().toString();
        vendor.bank_ifsc = ((EditText)findViewById(R.id.vendor_bank_ifsc)).getText().toString();
        vendor.bank_branch = ((EditText)findViewById(R.id.vendor_bank_branch)).getText().toString();
        vendor.bank_account_type = ((EditText)findViewById(R.id.vendor_bank_account_type)).getText().toString();
        vendor.start_time = ((TextView)findViewById(R.id.opening_time)).getText().toString();
        vendor.end_time = ((TextView)findViewById(R.id.closing_time)).getText().toString();
        vendor.cat_id = CATIDS;
        vendor.subcat_id = SUBCATIDS;

       // Toast.makeText(this,vendor.cat_id.trim()+ "&&" + vendor.subcat_id.trim(),Toast.LENGTH_LONG).show();

        if(!BITMAP_PROFILE_STRING.equalsIgnoreCase("")){
            uploadImageProfile(vendor);
        }else if(!BITMAP_WORK_STRING.equalsIgnoreCase("")){
            uploadImageWork(vendor);
        }
        else{
            UpdateVendorAsync UVA = new UpdateVendorAsync(this,vendor);
            UVA.execute("");
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            if(CURRENT_SCREEN.size()>1){
               CURRENT_SCREEN.remove(CURRENT_SCREEN.size()-1);
               int index = CURRENT_SCREEN.get(CURRENT_SCREEN.size()-1);
               switch (index){
                   case 1:
                       showRequest();
                       break;
                   case 2:
                       showEdit();
                       break;
                   case 3:
                       showEditBank();
                       break;
                   case 4:
                       showEditServices();
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
       // getMenuInflater().inflate(R.menu.account_vendor, menu);
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

        if(id == R.id.nav_request){
            showRequest();
            if(CURRENT_SCREEN.get(CURRENT_SCREEN.size()-1)!= 1) {
                CURRENT_SCREEN.add(1);
            }
        }
        else if (id == R.id.nav_edit) {

            showEdit();
            if(CURRENT_SCREEN.get(CURRENT_SCREEN.size()-1)!= 2) {
                CURRENT_SCREEN.add(2);
            }
        }
        else if (id == R.id.nav_edit_bank) {

            showEditBank();
            if(CURRENT_SCREEN.get(CURRENT_SCREEN.size()-1)!= 3) {
                CURRENT_SCREEN.add(3);
            }
        }
        else if (id == R.id.nav_edit_services) {

            showEditServices();
            if(CURRENT_SCREEN.get(CURRENT_SCREEN.size()-1)!= 4) {
                CURRENT_SCREEN.add(4);
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

    public void showRequest(){
        LinearLayout requestLayout = (LinearLayout) findViewById(R.id.layout_list);
        requestLayout.setVisibility(View.VISIBLE);

        LinearLayout editLayout = (LinearLayout) findViewById(R.id.layout_edit);
        editLayout.setVisibility(View.GONE);

        LinearLayout editLayoutBank = (LinearLayout) findViewById(R.id.layout_edit_bank);
        editLayoutBank.setVisibility(View.GONE);

        LinearLayout editLayoutServices = (LinearLayout) findViewById(R.id.layout_edit_services);
        editLayoutServices.setVisibility(View.GONE);


        GetRequest GR = new GetRequest(this);
        GR.execute("");

        getSupportActionBar().setTitle("Requests");

    }

    public void showEdit(){
        LinearLayout requestLayout = (LinearLayout) findViewById(R.id.layout_list);
        requestLayout.setVisibility(View.GONE);

        LinearLayout editLayout = (LinearLayout) findViewById(R.id.layout_edit);
        editLayout.setVisibility(View.VISIBLE);

        LinearLayout editLayoutBank = (LinearLayout) findViewById(R.id.layout_edit_bank);
        editLayoutBank.setVisibility(View.GONE);

        LinearLayout editLayoutServices = (LinearLayout) findViewById(R.id.layout_edit_services);
        editLayoutServices.setVisibility(View.GONE);

        getSupportActionBar().setTitle("PROFILE");


    }
    public void showEditBank(){
        LinearLayout requestLayout = (LinearLayout) findViewById(R.id.layout_list);
        requestLayout.setVisibility(View.GONE);

        LinearLayout editLayout = (LinearLayout) findViewById(R.id.layout_edit);
        editLayout.setVisibility(View.GONE);

        LinearLayout editLayoutBank = (LinearLayout) findViewById(R.id.layout_edit_bank);
        editLayoutBank.setVisibility(View.VISIBLE);

        LinearLayout editLayoutServices = (LinearLayout) findViewById(R.id.layout_edit_services);
        editLayoutServices.setVisibility(View.GONE);

        getSupportActionBar().setTitle("BANK DETAILS");
    }
    public void showEditServices(){
        LinearLayout requestLayout = (LinearLayout) findViewById(R.id.layout_list);
        requestLayout.setVisibility(View.GONE);

        LinearLayout editLayout = (LinearLayout) findViewById(R.id.layout_edit);
        editLayout.setVisibility(View.GONE);

        LinearLayout editLayoutBank = (LinearLayout) findViewById(R.id.layout_edit_bank);
        editLayoutBank.setVisibility(View.GONE);

        LinearLayout editLayoutServices = (LinearLayout) findViewById(R.id.layout_edit_services);
        editLayoutServices.setVisibility(View.VISIBLE);

        getSupportActionBar().setTitle("SERVICES");
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


    class UpdateVendorAsync extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;
        InputStream inputStream = null;
        String result = "";
        Context ctx;

        Vendor vendor;
        public UpdateVendorAsync(Context ctx,Vendor vendor){
            this.ctx = ctx;
            progressDialog = new ProgressDialog(ctx);

            this.vendor = vendor;

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
                String sUrl = Settings.UPDATE_VENDOR_URL
                        + "id=" +  URLEncoder.encode(Settings.VENDOR_ID, "UTF-8") +"&"
                        + "name=" +  URLEncoder.encode(vendor.name.trim(), "UTF-8") +"&"
                        + "email=" +  URLEncoder.encode(vendor.email.trim(), "UTF-8") +"&"
                        + "phone=" +  URLEncoder.encode(vendor.mobile.trim(), "UTF-8") +"&"
                        + "pincode=" +  URLEncoder.encode(vendor.pincode.trim(), "UTF-8") +"&"
                        + "address1=" +  URLEncoder.encode(vendor.address1.trim(), "UTF-8") +"&"
                        + "address2=" +  URLEncoder.encode(vendor.address2.trim(), "UTF-8") +"&"
                        + "sid=" +  URLEncoder.encode(vendor.sid.trim(), "UTF-8") +"&"
                        + "ccid=" +  URLEncoder.encode(vendor.ccid.trim(), "UTF-8") +"&"
                        + "cat_id=" +  URLEncoder.encode(vendor.cat_id.trim(), "UTF-8") +"&"
                        + "subcat_id=" +  URLEncoder.encode(vendor.subcat_id.trim(), "UTF-8") +"&"
                        + "location=" +  URLEncoder.encode(Settings.CUSTOMER_CURRENT_LOCATION, "UTF-8") +"&"
                        + "latitude=" +  URLEncoder.encode(Settings.CURRENT_LAT, "UTF-8") +"&"
                        + "longitude=" +  URLEncoder.encode(Settings.CURRENT_LONG, "UTF-8") +"&"
                        + "description=" +  URLEncoder.encode(vendor.description.trim(), "UTF-8") +"&"
                        + "bank_account=" +  URLEncoder.encode(vendor.bank_account.trim(), "UTF-8") +"&"
                        + "bank_ifsc=" +  URLEncoder.encode(vendor.bank_ifsc.trim(), "UTF-8") +"&"
                        + "bank_branch=" +  URLEncoder.encode(vendor.bank_branch.trim(), "UTF-8") +"&"
                        + "bank_account_type=" +  URLEncoder.encode(vendor.bank_account_type.trim(), "UTF-8") +"&"
                        + "start_time=" +  URLEncoder.encode(vendor.start_time.trim(), "UTF-8") +"&"
                        + "end_time=" +  URLEncoder.encode(vendor.end_time.trim(), "UTF-8");



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


                    Settings.VENDOR_NAME = vendor.name.trim();
                    Settings.VENDOR_EMAIL = vendor.email.trim();
                    Settings.MOBILE = vendor.mobile.trim();
                    Settings.VENDOR_PINCODE = vendor.pincode.trim();
                    Settings.VENDOR_ADDRESS1 = vendor.address1.trim();
                    Settings.VENDOR_ADDRESS2 = vendor.address2.trim();
                    Settings.VENDOR_SID = vendor.sid.trim();
                    Settings.VENDOR_CCID = vendor.ccid.trim();
                    Settings.VENDOR_LOCATION = vendor.location.trim();
                    Settings.VENDOR_LAT = Settings.CURRENT_LAT.trim();
                    Settings.VENDOR_LONG = Settings.CURRENT_LONG.trim();
                    Settings.VENDOR_CAT_ID = vendor.cat_id.trim();
                    Settings.VENDOR_SUBCAT_ID = vendor.subcat_id.trim();

                    Settings.VENDOR_PHOTO = vendor.vendor_photo.trim();
                    Settings.VENDOR_WORK_PHOTO = vendor.work_photo.trim();
                    Settings.VENDOR_DESCRIPTION = vendor.description.trim();
                    Settings.VENDOR_START_TIME = vendor.start_time.trim();
                    Settings.VENDOR_END_TIME = vendor.end_time.trim();
                    //Settings.VENDOR_PASSWORD = mainObject.getString("password");

                    Settings.VENDOR_BANK_ACCOUNT = vendor.bank_account.trim();
                    Settings.VENDOR_BANK_IFSC = vendor.bank_ifsc.trim();
                    Settings.VENDOR_BANK_BRANCH = vendor.bank_branch.trim();
                    Settings.VENDOR_BANK_ACCOUNT_TYPE = vendor.bank_account_type.trim();

                    Settings.TYPE = 1;
                    SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    editor.putInt("TYPE",Settings.TYPE);
                    editor.putString("MOBILE", Settings.MOBILE.trim());
                    //editor.putString("PASSWORD", Settings.PASSWORD.trim());
                    editor.commit();


                    LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    checkParams.setMargins(5, 5, 5, 5);
                    chips = (LinearLayout) findViewById(R.id.service_chips);
                    chips.removeAllViews();

                    String[] VENDOR_CAT_IDS = Settings.VENDOR_CAT_ID.split("\\|");
                    if(VENDOR_CAT_IDS.length>0) {
                        HashMap<String, String> CAT_IDS = new HashMap<>();
                        for (int i = 0; i < Settings.CAT.size(); i++) {
                            Cat cat = (Cat) Settings.CAT.get(i);
                            CAT_IDS.put(cat.cat_id, cat.cat_name);
                        }
                        for (int i = 0; i < VENDOR_CAT_IDS.length; i++) {
                            TextView b = new TextView(getApplicationContext());
                            if(CAT_IDS.get(VENDOR_CAT_IDS[i]) == null){
                                b.setText("No service selected");
                                b.setLayoutParams(checkParams);
                                b.setBackgroundResource(R.drawable.chip);
                                b.setTextColor(ContextCompat.getColor(ctx,R.color.colorPrimary));
                                chips.addView(b);
                                break;
                            }
                            b.setText(String.valueOf(CAT_IDS.get(VENDOR_CAT_IDS[i])));
                            b.setLayoutParams(checkParams);
                            b.setBackgroundResource(R.drawable.chip);
                            b.setTextColor(ContextCompat.getColor(ctx,R.color.colorPrimary));
                            chips.addView(b);
                        }
                    }else{
                        TextView b = new TextView(getApplicationContext());
                        b.setText("No service selected");
                        b.setLayoutParams(checkParams);
                        b.setTextColor(ContextCompat.getColor(ctx,R.color.colorPrimary));
                        b.setBackgroundResource(R.drawable.chip);
                        chips.addView(b);
                    }
                    headerTitle.setText("Hi , "+Settings.VENDOR_NAME);


                }
                catch(Exception e){

                    Toast.makeText(ctx,e.getMessage(),Toast.LENGTH_LONG).show();
                }


            }


        }

    }


    class GetRequest extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;
        InputStream inputStream = null;
        String result = "";
        Context ctx;

        String Name,Email,Phone,Address,Password;
        public GetRequest(Context ctx){
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
                String sUrl = Settings.GET_REQUESTS_URL
                        + "vid=" +  URLEncoder.encode(Settings.VENDOR_ID, "UTF-8");


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
                    Settings.REQUESTS_LIST.clear();
                    mainArray = new JSONArray(result);
                    for(int i=0;i<mainArray.length();i++){
                        JSONObject request = mainArray.getJSONObject(i);
                        Request r = new Request();
                        r.rid = request.getString("rid");
                        r.Rating = request.getString("rating");
                        r.Distance = request.getString("distance");
                        r.Address = request.getString("address");
                        r.Name = request.getString("name");
                        r.Phone = request.getString("phone");
                        r.Status = request.getString("status");
                        r.Lat = request.getString("lat");
                        r.Lang = request.getString("lang");
                        r.Service = request.getString("service");
                        r.SubService = request.getString("sub_service");
                        r.Date = request.getString("date1");

                        Settings.REQUESTS_LIST.add(r);
                    }
                    Collections.reverse(Settings.REQUESTS_LIST);
                    RequestAdapter RA = new RequestAdapter(Settings.REQUESTS_LIST,ctx);
                    listview.setAdapter(RA);



                }
                catch(Exception e){

                    Toast.makeText(ctx,e.getMessage(),Toast.LENGTH_LONG).show();
                }


            }


        }

    }


    private void uploadImageProfile(Vendor vendor){
        final String KEY_IMAGE = "image";
        final String KEY_TYPE = "type";
        final String KEY_ID = "id";

        final Vendor vendor1 = vendor;


        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Team Mechanic","Syncing Account...",false,false);
        loading.setCancelable(false);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, Settings.IMAGE_HANDLER_URL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();

                        BITMAP_PROFILE_STRING = "";
                        if(!BITMAP_WORK_STRING.equalsIgnoreCase("")){
                            uploadImageWork(vendor1);
                        }else{
                            UpdateVendorAsync updateVendorAsync = new UpdateVendorAsync(AccountVendor.this,vendor1);
                            updateVendorAsync.execute("");
                        }



                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(AccountVendor.this, "Something went wrong, try again...", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();



                //Adding parameters
                params.put(KEY_IMAGE, BITMAP_PROFILE_STRING);
                params.put(KEY_TYPE, "1");
                params.put(KEY_ID, Settings.VENDOR_ID);
                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }
    private void uploadImageWork(Vendor vendor){
        final String KEY_IMAGE = "image";
        final String KEY_TYPE = "type";
        final String KEY_ID = "id";

        final Vendor vendor1 = vendor;


        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Team Mechanic","Syncing Account...",false,false);
        loading.setCancelable(false);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, Settings.IMAGE_HANDLER_URL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        BITMAP_WORK_STRING = "";
                        UpdateVendorAsync updateVendorAsync = new UpdateVendorAsync(AccountVendor.this,vendor1);
                        updateVendorAsync.execute("");

                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(AccountVendor.this, "Something went wrong, try again...", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();



                //Adding parameters
                params.put(KEY_IMAGE, BITMAP_WORK_STRING);
                params.put(KEY_TYPE, "2");
                params.put(KEY_ID, Settings.VENDOR_ID);
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
