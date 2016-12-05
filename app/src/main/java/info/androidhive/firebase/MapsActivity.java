package info.androidhive.firebase;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapClick(LatLng point) {
        Toast.makeText(this, "Click: " + String.valueOf(point.latitude) + "," + String.valueOf(point.longitude),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapLongClick(LatLng point) {
        Toast.makeText(this, "Long Click: " + String.valueOf(point.latitude) + "," + String.valueOf(point.longitude),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Intent intent = getIntent();
        String la = intent.getStringExtra("latitude");
        String lo = intent.getStringExtra("longitude");
        double latitude = Double.parseDouble(la);
        double longitude = Double.parseDouble(lo);

        mMap = googleMap;

        LatLng loc_mju = new LatLng(latitude, longitude);

        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        UiSettings mapSettings;
        mapSettings = mMap.getUiSettings();
        mapSettings.setZoomControlsEnabled(true);
        mapSettings.setCompassEnabled(true);
        mapSettings.setIndoorLevelPickerEnabled(true);
        mapSettings.setMapToolbarEnabled(true);
        mapSettings.setScrollGesturesEnabled(true);
        mapSettings.setTiltGesturesEnabled(true);
        mapSettings.setRotateGesturesEnabled(true);
        mapSettings.setMyLocationButtonEnabled(true);
        Marker mju = mMap.addMarker(new MarkerOptions()
                .position(loc_mju)
                .title("title: 검색한 위치입니다.")
                .snippet("Snippet: Right Here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc_mju));

        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
    }
}