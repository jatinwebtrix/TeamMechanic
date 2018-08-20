package com.vincit.mechanic.mrmechanic;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SubCatList extends AppCompatActivity {

    LinearLayout _SubCat_List;
    private String CAT_STATUS = "";
    private String CAT_ID = "";
    private boolean HAS_SUB = false;
    private String SUBCAT2 = "";
    String[] SELECTEDIDS;
    private String CAT_NAME = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_cat_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select Sub Categories");

        CAT_ID = getIntent().getStringExtra("CATID");
        HAS_SUB = getIntent().getBooleanExtra("HASSUB",false);
        SUBCAT2 = getIntent().getStringExtra("SUBCAT2");
        SELECTEDIDS = SUBCAT2.split("\\|");
        CAT_NAME = getIntent().getStringExtra("CATNAME");
        _SubCat_List = (LinearLayout) findViewById(R.id._subcat_list);
        LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        checkParams.setMargins(20, 30, 20, 20);



        for(int i=0;i<Settings.SUBCAT.size();i++){
            if(((SubCat)Settings.SUBCAT.get(i)).cat_id.equalsIgnoreCase(CAT_ID)) {
                CheckBox cb = new CheckBox(getApplicationContext());
                cb.setText(((SubCat) Settings.SUBCAT.get(i)).subcat_name);
                cb.setTextColor(Color.parseColor("#44ADE1"));
                cb.setTextSize(16f);
                cb.setId(i);

                if(SELECTEDIDS.length>0) {
                    SubCat sc = (SubCat) Settings.SUBCAT.get(i);
                    for(int m=0;m<SELECTEDIDS.length;m++){
                        if(sc.subcat_id.equalsIgnoreCase(SELECTEDIDS[m])){
                            cb.setChecked(true);
                            break;
                        }
                    }
                }


                cb.setLayoutParams(checkParams);
                cb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CheckBox c = (CheckBox) v;
                       // Toast.makeText(getApplicationContext(), String.valueOf(c.getId()), Toast.LENGTH_LONG).show();
                    }
                });

                setCheckBoxColor(cb, Color.parseColor("#44ADE1"), Color.parseColor("#0374AE"));
                _SubCat_List.addView(cb);
            }
        }


    }

    public void setCheckBoxColor(CheckBox checkBox, int checkedColor, int uncheckedColor) {
        int states[][] = {{android.R.attr.state_checked}, {}};
        int colors[] = {checkedColor, uncheckedColor};
        CompoundButtonCompat.setButtonTintList(checkBox, new
                ColorStateList(states, colors));
    }


    public void SelectSubServicesDone(View v){
        CAT_STATUS = CAT_ID + "$";
        String SELECTEDID_STRING = "";
        for(int i=0;i<_SubCat_List.getChildCount();i++) {
            CheckBox cb = (CheckBox) _SubCat_List.getChildAt(i);
            if(cb.isChecked()){
                    CAT_STATUS += String.valueOf(((SubCat) Settings.SUBCAT.get(cb.getId())).subcat_name) + ",";
                    SELECTEDID_STRING += String.valueOf(((SubCat) Settings.SUBCAT.get(cb.getId())).subcat_id) + "|";
            }
        }
        CAT_STATUS = CAT_STATUS.substring(0,CAT_STATUS.length()-1);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("CAT_STATUS", CAT_STATUS);
        resultIntent.putExtra("HASSUB", HAS_SUB);
        resultIntent.putExtra("SELECTEDIDS_TAG",SELECTEDID_STRING);
        resultIntent.putExtra("CATNAME",CAT_NAME);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {

    }
}
