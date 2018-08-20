package com.vincit.mechanic.mrmechanic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.HashMap;

public class CatList extends AppCompatActivity {

    ScrollView _Cat_List_Scroll;
    LinearLayout _Cat_List;
    int CAT_INDEX = 0;
    private Context ctx;
    private static int PICK_SUBCAT_REQUEST = 1;
    private CheckBox checkBox;
    String SUBCAT2 = "";
    String[] VENDOR_CAT_IDS;
    String[] VENDOR_SUBCAT_IDS;
    HashMap<String,String> SUBCAT_IDS = new HashMap<>();
    HashMap<String,String> SUBCATID_BY_CATID = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select Services");

        ctx = this;

        _Cat_List = (LinearLayout) findViewById(R.id._cat_list);

        LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        checkParams.setMargins(20, 30, 20, 20);



        VENDOR_CAT_IDS = Settings.VENDOR_CAT_ID.split("\\|");
        VENDOR_SUBCAT_IDS = Settings.VENDOR_SUBCAT_ID.split("\\|");

        HashMap<String,String> CAT_IDS = new HashMap<>();
        for(int i=0;i<Settings.CAT.size();i++){
            Cat cat = (Cat)Settings.CAT.get(i);
            CAT_IDS.put(cat.cat_id,cat.cat_name);
        }

        HashMap<String,String> SUBCAT_NAMES = new HashMap<>();
        for(int i=0;i<Settings.SUBCAT.size();i++){
            SubCat cat = (SubCat)Settings.SUBCAT.get(i);
            SUBCAT_IDS.put(cat.subcat_id,cat.cat_id);
            SUBCAT_NAMES.put(cat.subcat_id,cat.subcat_name);
            SUBCATID_BY_CATID.put(cat.cat_id,cat.subcat_id);
        }

       // Toast.makeText(this,String.valueOf(SUBCAT_NAMES.get("77")),Toast.LENGTH_LONG).show();


        for(int i=0;i<Settings.CAT.size();i++){
             String Status = "";
             final Cat c = (Cat)Settings.CAT.get(i);
             CheckBox cb = new CheckBox(getApplicationContext());
            // cb.setText(Status);
             cb.setTextColor(Color.parseColor("#44ADE1"));
             cb.setTextSize(16f);
             cb.setId(i);
             if(VENDOR_CAT_IDS.length>0) {
                 for (int k = 0; k < VENDOR_CAT_IDS.length; k++) {
                     if(VENDOR_CAT_IDS[k] != null) {
                         if (c.cat_id.equalsIgnoreCase(VENDOR_CAT_IDS[k])) {
                             cb.setChecked(true);
                             break;
                         }
                     }
                 }
             }

             SUBCAT2 = "";
             if(VENDOR_SUBCAT_IDS.length>0) {
                 for (int k = 0; k < VENDOR_SUBCAT_IDS.length; k++) {
                     if(VENDOR_SUBCAT_IDS[k]!=null && SUBCAT_IDS.get(VENDOR_SUBCAT_IDS[k])!=null) {
                         if (SUBCAT_IDS.get(VENDOR_SUBCAT_IDS[k]).equalsIgnoreCase(c.cat_id)) {
                             Status += SUBCAT_NAMES.get(VENDOR_SUBCAT_IDS[k]) + ",";
                             SUBCAT2 += VENDOR_SUBCAT_IDS[k] + "|";
                         }
                     }
                 }
             }
            SUBCAT2 = replaceLast(SUBCAT2,"|","");

             Status = replaceLast(Status,",","");

           cb.setTag(SUBCAT2);
            if(cb.isChecked() && SUBCATID_BY_CATID.get(c.cat_id) != null)
                Status = ((Cat)Settings.CAT.get(i)).cat_name + "  ( " + Status + "  )";
            else
                Status = ((Cat)Settings.CAT.get(i)).cat_name;
             cb.setText(Status);

             cb.setLayoutParams(checkParams);
             cb.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {

                     CheckBox c1 = (CheckBox)v;
                     checkBox = (CheckBox)v;



                     if(SUBCATID_BY_CATID.get(((Cat)Settings.CAT.get(c1.getId())).cat_id) != null) {
                         Intent intent = new Intent(ctx, SubCatList.class);
                         intent.putExtra("CATNAME", c.cat_name);
                         intent.putExtra("CATID", ((Cat) Settings.CAT.get(c1.getId())).cat_id);
                         intent.putExtra("SUBCAT2", String.valueOf(c1.getTag()));
                         if (!c1.isChecked())
                             intent.putExtra("HASSUB", true);
                         else
                             intent.putExtra("HASSUB", false);
                         startActivityForResult(intent, PICK_SUBCAT_REQUEST);
                     }else{
                         if(c1.isChecked()) {
                             Toast.makeText(ctx, "Currently no sub category for this", Toast.LENGTH_LONG).show();
                             c1.setTag("-1");
                         }
                     }


                     CAT_INDEX = c1.getId();
                    // Toast.makeText(getApplicationContext(),String.valueOf(c.getId()),Toast.LENGTH_LONG).show();
                 }
             });
             setCheckBoxColor(cb,Color.parseColor("#44ADE1"),Color.parseColor("#0374AE"));
             _Cat_List.addView(cb);
         }


    }
    String replaceLast(String string, String substring, String replacement)
    {
        int index = string.lastIndexOf(substring);
        if (index == -1)
            return string;
        return string.substring(0, index) + replacement
                + string.substring(index+substring.length());
    }
    public void setCheckBoxColor(CheckBox checkBox, int checkedColor, int uncheckedColor) {
        int states[][] = {{android.R.attr.state_checked}, {}};
        int colors[] = {checkedColor, uncheckedColor};
        CompoundButtonCompat.setButtonTintList(checkBox, new
                ColorStateList(states, colors));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_SUBCAT_REQUEST) {
            if (resultCode == RESULT_OK) {

                String STATUS = data.getStringExtra("CAT_STATUS");
                String[] _CATID = STATUS.split("\\$");
                String CATID = _CATID[0];
                String SELECTEDIDS_TAG = data.getStringExtra("SELECTEDIDS_TAG");
                SELECTEDIDS_TAG = replaceLast(SELECTEDIDS_TAG,"|","");
//                if(_CATID.length==1 && !data.getBooleanExtra("HASSUB",true)) {
//                    checkBox.setChecked(false);
//                }
//                else if(_CATID.length==1 && data.getBooleanExtra("HASSUB",true)){
//                    checkBox.setChecked(true);
//                }
                if(_CATID.length==1) {
                    checkBox.setChecked(false);
                    checkBox.setText(data.getStringExtra("CATNAME"));
                }
                else{
                    for(int i=0;i<Settings.CAT.size();i++){
                        Cat c = (Cat)Settings.CAT.get(i);
                        if(c.cat_id.equalsIgnoreCase(CATID)){
                            checkBox.setText(c.cat_name);
                            break;
                        }

                    }

                    checkBox.setText(checkBox.getText()+"   ( " + _CATID[1] + " )");
                    checkBox.setChecked(true);
                }
                checkBox.setTag(SELECTEDIDS_TAG);

            }
        }
    }



   public void SelectServicesDone(View v){
       String SUBCATIDS_STRING = "";
       String CATIDS_STRING = "";
       for(int i=0;i<_Cat_List.getChildCount();i++) {
           CheckBox cb = (CheckBox) _Cat_List.getChildAt(i);
           if(cb.isChecked() && !String.valueOf(cb.getTag()).equalsIgnoreCase("-1")){
               SUBCATIDS_STRING += "|" + String.valueOf(cb.getTag());

           }
           if(cb.isChecked()){
               Cat c = (Cat)Settings.CAT.get(cb.getId());
               CATIDS_STRING += "|" + c.cat_id;
           }
       }
       if(SUBCATIDS_STRING.length()>2) {
           SUBCATIDS_STRING = SUBCATIDS_STRING.substring(1, SUBCATIDS_STRING.length());
       }else{
           SUBCATIDS_STRING = "";
       }
       if(CATIDS_STRING.length()>2) {
           CATIDS_STRING = CATIDS_STRING.substring(1, CATIDS_STRING.length());
       }else {
           CATIDS_STRING = "";
       }

       Intent resultIntent = new Intent();
       resultIntent.putExtra("CATIDS", CATIDS_STRING);
       resultIntent.putExtra("SUBCATIDS", SUBCATIDS_STRING);
       setResult(Activity.RESULT_OK, resultIntent);
       finish();

   }

    @Override
    public void onBackPressed() {

    }
}
