package com.teamfopo.fopo

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;
import android.location.Location;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.skt.Tmap.MapUtils.getDistance
import com.skt.Tmap.TMapTapi;

import java.util.ArrayList;
import java.util.jar.Manifest;

public class MapActivity:  Fragment() implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private final int REQUEST_CODE_PERMISSIONS = 1000;
    private LatLng MyLocation;

    private TMapTapi tmaptapi;

    private ClusterManager<MyItems> mClusterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient((this));

    }

    //퍼미션 체크
    public void permission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_PERMISSIONS);
            return;
        }

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    MyLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    /*mMap.addMarker(new MarkerOptions()
                            .position(myLocation).title("현재 위치")
                            .position(myLocation).snippet("위도 : " + String.valueOf(location.getLatitude()) + ", 경도 : " + String.valueOf(location.getLongitude())));*/
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        tmaptapi = new TMapTapi(this);
        // Add a marker in Sydney and move the camera
        LatLng 포토존 = new LatLng(35.89627845671536, 128.6223662256039);
        //mMap.addMarker(new MarkerOptions().position(포토존).title("영진전문대학교"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(포토존));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));


        mClusterManager = new ClusterManager<MyItems>(this,mMap);

        mMap.setOnCameraIdleListener(mClusterManager);

        mMap.setOnMarkerClickListener(mClusterManager);
       // mMap.setOnMarkerClickListener(this);

        mMap.setOnMapLongClickListener(this);
        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItems>() {
            @Override
            public boolean onClusterClick(Cluster<MyItems> myItem) {
                mMap.animateCamera(CameraUpdateFactory.zoomTo(3.0f));

                return true;
            }
        });
        addItems();
        permission();

        mMap.setOnMarkerClickListener(new com.google.android.gms.maps.GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));

                String snippet = String.format("%.2f", getDistance(marker.getPosition(),MyLocation)/1000);

                if(getDistance(marker.getPosition(),MyLocation) == 0) marker.setSnippet("현재위치 입니다.");

                else marker.setSnippet("이곳까지의 거리는 " + snippet +"km 입니다.");

                marker.showInfoWindow();
                return false;
            }
        });
        mClusterManager.setRenderer(new MarkerClusterRenderer(this, googleMap, mClusterManager));
    }

    public double getDistance(LatLng LatLng1, LatLng LatLng2) {
        double distance = 0;
        Location locationA = new Location("A");
        locationA.setLatitude(LatLng1.latitude);
        locationA.setLongitude(LatLng1.longitude);
        Location locationB = new Location("B");
        locationB.setLatitude(LatLng2.latitude);
        locationB.setLongitude(LatLng2.longitude);
        distance = locationA.distanceTo(locationB);

        return distance;
    }
/*
    @Override
    public boolean onMarkerClick(Marker marker){

        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
        String snippet = String.format("%.2f", getDistance(marker.getPosition(),MyLocation)/1000);

        if(getDistance(marker.getPosition(),MyLocation) == 0) marker.setSnippet("현재위치 입니다.");

        else marker.setSnippet("이곳까지의 거리는 " + snippet +"km 입니다.");

        marker.showInfoWindow();
        return true;
    }
*/

    private void addItems(){
        double lat = 35.89627845671536;
        double lng = 128.6223662256039;

        for(int i=0; i<10;i++) {
            double offset = i / 60d;
            String title = i + "번 포토존";
            lat = lat + offset;
            lng = lng + offset;
            String snippet = "이곳까지의 거리는 15km입니다.\n 경로보기->";
            LatLng 포토존 = new LatLng(lat, lng);
            MyItems offsetItem = new MyItems(lat, lng, title, snippet);
            mClusterManager.addItem(offsetItem);
        }
    }
/*
    public void onLastLocationButtonClicked(View view) {

        mMap.moveCamera(CameraUpdateFactory.newLatLng((MyLocation)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));

    }
    */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "권한 체크 거부 됨", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if(MyLocation == latLng){
            Toast.makeText(this, "현재 위치에서는 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }else {
            tmaptapi.setSKTMapAuthentication("6fae3939-c16b-45ad-ba81-d9c6054d4728");
            boolean isTmapApp = tmaptapi.isTmapApplicationInstalled();

            if (isTmapApp == false) {
                ArrayList result = tmaptapi.getTMapDownUrl();
                Uri uri = (Uri) Uri.parse(String.valueOf(result));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addCategory(Intent.CATEGORY_APP_BROWSER);
            } else {
                float lat = (float) latLng.latitude;
                float lng = (float) latLng.longitude;
                tmaptapi.invokeRoute("테스트", lng, lat);
            }
        }
    }
}
