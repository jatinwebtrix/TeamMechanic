package com.vincit.mechanic.mrmechanic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class AccountUserListVendor extends AppCompatActivity {

    RecyclerView list;
    RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_user_list_vendor);

        list = (RecyclerView) findViewById(R.id.list);
        MainAdapter mainAdapter = new MainAdapter(Settings.VENDOR_LIST,this);
        layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);
        list.setAdapter(mainAdapter);
    }
}
