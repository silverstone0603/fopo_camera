package com.teamfopo.fopo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.skt.Tmap.TMapTapi;
import com.teamfopo.fopo.module.PhotozoneDTO;
import com.teamfopo.fopo.module.modPhotoProcess;
import com.teamfopo.fopo.nodes.MarkerClusterRenderer;
import com.teamfopo.fopo.nodes.MarkerMyItems;

import java.util.ArrayList;

public class MapActivity extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private final int REQUEST_CODE_PERMISSIONS = 1000;
    private LatLng MyLocation;

    private TMapTapi tmaptapi;

    private ClusterManager<MarkerMyItems> mClusterManager;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 438;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_map, container, false);

        if (mMap == null) {
            SupportMapFragment mapFragment =  (SupportMapFragment)getChildFragmentManager()
                    .findFragmentById(R.id.google_map);
            mapFragment.getMapAsync(this);
        }else{
            GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());
        }



        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        Log.d("MapActivity", "Map 액티비티 생성 완료");

        return view;
    }

    //퍼미션 체크
    public void permission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_PERMISSIONS);
            return;
        }

        mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
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
        Log.d("MapActivity", "Map 생성이 준비 중입니다.");

        if (ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("MapActivity", "Map 생성 중 권한이 충분치 않아 권한을 요청합니다.");

            ActivityCompat.requestPermissions(this.getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        Log.d("MapActivity", "Map 생성에 대한 권한을 승인받아 지도를 구성합니다.");

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        tmaptapi = new TMapTapi(super.getContext());
        // Add a marker in Sydney and move the camera
        LatLng 포토존 = new LatLng(35.89627845671536, 128.6223662256039);
        //mMap.addMarker(new MarkerOptions().position(포토존).title("영진전문대학교"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(포토존));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));


        mClusterManager = new ClusterManager<MarkerMyItems>(getActivity(), mMap);

        mMap.setOnCameraIdleListener(mClusterManager);

        mMap.setOnMarkerClickListener(mClusterManager);
        // mMap.setOnMarkerClickListener(this);

        mMap.setOnMapLongClickListener(this);
        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MarkerMyItems>() {
            @Override
            public boolean onClusterClick(Cluster<MarkerMyItems> myItem) {
                mMap.animateCamera(CameraUpdateFactory.zoomTo(3.0f));

                return true;
            }
        });
        addItems();
        permission();

        mMap.setOnInfoWindowClickListener(new com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener(){
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(getContext(), marker.getTitle() + " 인포 윈도우 클릭", Toast.LENGTH_SHORT).show();
            }
        });

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
        mClusterManager.setRenderer(new MarkerClusterRenderer(getActivity(), googleMap, mClusterManager));

        Log.d("MapActivity", "Map 생성 작업을 완료하였습니다.");
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

    private void addItems(){
        PhotozoneDTO[] PhotozoneDTO = null;

        modPhotoProcess photoProcess = new modPhotoProcess();
        modPhotoProcess.listPhotoZone listPhotoZone = photoProcess.new listPhotoZone();

        try {
            PhotozoneDTO = listPhotoZone.execute().get();
            System.out.println("포토존 목록");

            for (int i = 0; i < PhotozoneDTO.length; i++) {
                String title = PhotozoneDTO[i].getZone_no()+"번 포토존 : "+PhotozoneDTO[i].getZone_placename();
                double lat = PhotozoneDTO[i].getZone_lat();
                double lng = PhotozoneDTO[i].getZone_lng();
                String snippet = "이곳까지의 거리는 15km입니다.\n 경로보기->";

                MarkerMyItems offsetItem = new MarkerMyItems(lat, lng, title, snippet);

                mClusterManager.addItem(offsetItem);
            }
        } catch (Exception e) {
            Log.d("printList Method Error", e.toString());
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
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(super.getContext(), "권한 체크 거부 됨", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if(MyLocation == latLng){
            Toast.makeText(super.getContext(), "현재 위치에서는 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
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
