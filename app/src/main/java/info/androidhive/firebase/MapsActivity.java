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
    public void onMapClick(LatLng point) {  // Map 상에서 마우스 짧게 클릭 시
        Toast.makeText(this, "Click: " + String.valueOf(point.latitude) + "," + String.valueOf(point.longitude),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapLongClick(LatLng point) {  // Map 상에서 마우스 길게 클릭 시
        Toast.makeText(this, "Long Click: " + String.valueOf(point.latitude) + "," + String.valueOf(point.longitude),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {   // Map을 열어 줌
        Intent intent = getIntent();    // ChatRoomActivity에서 보낸 intent를 받아 옴
        String la = intent.getStringExtra("latitude");  // intent에 담아서 보낸 경도 값을 스트링 la에 넣어줌
        String lo = intent.getStringExtra("longitude"); // intent에 담아서 보낸 위도 값을 스트링 lo에 넣어줌
        double latitude = Double.parseDouble(la);   // 경도(la) : String 값을 double type으로 바꿔줌
        double longitude = Double.parseDouble(lo);  // 위도(lo) : String 값을 double type으로 바꿔줌

        mMap = googleMap;

        LatLng location = new LatLng(latitude, longitude);  // double type의 경도, 위도 값을 사용하기 위해 LatLng에 넣어줌

        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);  // 위성으로 위치를 받아오는 구글 맵 타입으로 설정

        UiSettings mapSettings;
        mapSettings = mMap.getUiSettings();
        mapSettings.setZoomControlsEnabled(true);   // 줌 제어 기능 활성화
        mapSettings.setCompassEnabled(true);        // 나침반 기능 활성화
        mapSettings.setIndoorLevelPickerEnabled(true);  // 레벨 선택기 컨트롤 활성화
        mapSettings.setMapToolbarEnabled(true);     // 맵의 툴바 활성화
        mapSettings.setScrollGesturesEnabled(true); // 스크롤 동작 인식 활성화
        mapSettings.setTiltGesturesEnabled(true);   // 기울이는 동작 인식 활성화
        mapSettings.setRotateGesturesEnabled(true); // 회전 동작 인식 활성화
        mapSettings.setMyLocationButtonEnabled(true);   // 내 현재 위치 표시해주는 버튼 활성황
        Marker mju = mMap.addMarker(new MarkerOptions()
                .position(location) // 지역 위치
                .title("title: 검색한 위치입니다.") // 제목
                .snippet("Snippet: Right Here"));   // 소제목
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));

        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
    }
}