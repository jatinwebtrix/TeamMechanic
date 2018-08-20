package com.vincit.mechanic.mrmechanic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SelectMap extends AppCompatActivity implements OnMapReadyCallback {

    static final LatLng HAMBURG = new LatLng(53.558, 9.927);
    static final LatLng KIEL = new LatLng(53.551, 9.993);

    static double LAT = 0.0;
    static double LONG = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        LAT = Double.valueOf(getIntent().getStringExtra("LAT"));
        LONG = Double.valueOf(getIntent().getStringExtra("LANG"));


    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng sydney = new LatLng(LAT, LONG);
        googleMap.addMarker(new MarkerOptions().position(sydney)
                .title("Customer Location")).showInfoWindow();
        //googleMap.animateCamera( CameraUpdateFactory.zoomTo( 17.0f ) );
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,15.0f));
    }
    public void MapDone(View v){
        finish();
    }

}
