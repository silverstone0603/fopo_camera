package com.teamfopo.fopo.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.teamfopo.fopo.R;
import com.teamfopo.fopo.module.modProtocol;
import com.teamfopo.fopo.nodes.PointCloudNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import uk.co.appoly.arcorelocation.LocationMarker;
import uk.co.appoly.arcorelocation.LocationScene;
import uk.co.appoly.arcorelocation.utils.ARLocationPermissionHelper;
import uk.co.appoly.arcorelocation.utils.DemoUtils;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static android.widget.Toast.makeText;

/**
 * This is a simple example that shows how to create an augmented reality (AR) application using the
 * ARCore and Sceneform APIs.
 */
public class LocationActivity extends AppCompatActivity {
    private boolean installRequested;
    private boolean hasFinishedLoading = false;

    private Snackbar loadingMessageSnackbar = null;

    private modProtocol protMain;
    private ArSceneView arSceneView;
    private PointCloudNode pointCloudNode;

    // Renderables for this example
    private ModelRenderable markerRenderable;
    private ViewRenderable distanceRenderable;

    // Our ARCore-Location scene
    private LocationScene locationScene;

    private String jsonString = "";
    private JSONArray jsonArray;
    private JSONObject jsonObject;

    public ArrayList<ViewRenderable> distanceRenderables = new ArrayList<>();
    public ArrayList<CompletableFuture> completableFutures = new ArrayList<>();

    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    // CompletableFuture requires api level 24
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_camera);
        arSceneView = findViewById(R.id.fopo_arcore_sceneview);
        pointCloudNode = new PointCloudNode(this);
        protMain = new modProtocol();

        // CloudNode를 추가함
        arSceneView.getScene().onAddChild(pointCloudNode);

        // 데이터 가져오기
        jsonString = protMain.getResultString("http://106.10.51.32/ajax_process/location_process",new String[]{},new String[]{});
        Log.d("ARCore", "가져온 값 : "+jsonString);

        initFragment();

        // Lastly request CAMERA & fine location permission which is required by ARCore-Location.
        ARLocationPermissionHelper.requestPermission(this);
    }

    // renderable을 빌드하고 그려주는 함수
    private void initFragment(){
        try {
            jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                // Build a renderable from a 2D View.
                CompletableFuture<ViewRenderable> distanceLayout =
                        ViewRenderable.builder()
                                .setView(this, R.layout.arcore_inform_view)
                                .build();

                completableFutures.add(i, distanceLayout);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

        // When you build a Renderable, Sceneform loads its resources in the background while returning
        // a CompletableFuture. Call thenAccept(), handle(), or check isDone() before calling get().
        CompletableFuture<ModelRenderable> marker = ModelRenderable.builder()
                .setSource(this, Uri.parse("fopoMarker.sfb"))
                .build()
                .exceptionally(throwable -> {
                    Toast toastMessage = new Toast(this);
                    toastMessage.makeText(this, "마커를 불러오지 못했습니다.", Toast.LENGTH_LONG);
                    toastMessage.setGravity(Gravity.CENTER, 0, 0);
                    toastMessage.show();
                    return null;
                });

        CompletableFuture.allOf(
                completableFutures.get(0),
                marker)
                .handle(
                        (notUsed, throwable) -> {
                            // When you build a Renderable, Sceneform loads its resources in the background while
                            // returning a CompletableFuture. Call handle(), thenAccept(), or check isDone()
                            // before calling get().

                            if (throwable != null) {
                                DemoUtils.displayError(this, "Unable to load renderables", throwable);
                                return null;
                            }

                            try {
                                try {
                                    jsonArray = new JSONArray(jsonString);

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        distanceRenderables.add(i, (ViewRenderable) completableFutures.get(i).get());
                                    }

                                }catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                markerRenderable = marker.get();
                                hasFinishedLoading = true;

                                // Marker와 distance를 render하고 추가시키는 부분
                                arSceneView
                                        .getScene()
                                        .addOnUpdateListener(
                                            frameTime -> {
                                            if (!hasFinishedLoading) return;
                                            if (locationScene == null) {
                                                // If our locationScene object hasn't been setup yet, this is a good time to do it
                                                // We know that here, the AR components have been initiated.
                                                locationScene = new LocationScene(this, arSceneView);
                                                try {
                                                    jsonArray = new JSONArray(jsonString);

                                                    for (int i = 0; i < jsonArray.length(); i++) {
                                                        jsonObject = jsonArray.getJSONObject(i);

                                                        String title = jsonObject.getString("title");
                                                        String address = jsonObject.getString("address");
                                                        String time = jsonObject.getString("time");
                                                        Double latitude = jsonObject.getDouble("latitude");
                                                        Double longitude = jsonObject.getDouble("longitude");

                                                        // 노드 생성 및 Renderable 지정
                                                        Node base = new Node();
                                                        base.setRenderable(distanceRenderables.get(i));
                                                        View eView = distanceRenderables.get(i).getView();

                                                        // distance layout 위도 및 경도 설정, 노드 설정
                                                        LocationMarker layoutLocationMarker = new LocationMarker(
                                                                longitude,
                                                                latitude,
                                                                base
                                                        );

                                                        // An example "onRender" event, called every frame
                                                        // Updates the layout with the markers distance
                                                        layoutLocationMarker.setRenderEvent(node -> {
                                                            TextView distanceTextView = eView.findViewById(R.id.text_distance);
                                                            distanceTextView.setText(node.getDistance() + "m");
                                                        });

                                                        // distance layout meter 반경 설정
                                                        layoutLocationMarker.setOnlyRenderWhenWithin(1000);

                                                        // distance layout 높이 설정
                                                        layoutLocationMarker.setHeight((float) 3);

                                                        // distanceView 추가
                                                        locationScene.mLocationMarkers.add(layoutLocationMarker);

                                                        // 3D marker 위도 및 경도 설정, 노드 설정
                                                        LocationMarker locationMarker = new LocationMarker(
                                                                longitude,
                                                                latitude,
                                                                getAndy(title, address, time)
                                                        );

                                                        // 3D marker meter 반경 설정
                                                        locationMarker.setOnlyRenderWhenWithin(500);

                                                        // 3D marker 높이 설정
                                                        locationMarker.setHeight((float) -0.6);

                                                        // 3D marker 추가
                                                        locationScene.mLocationMarkers.add(locationMarker);

                                                        Log.d("ARCore", "위도 및 경도 : " + latitude.toString() + " / " + longitude.toString());
                                                    }
                                                }catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            Frame frame = arSceneView.getArFrame();
                                            if (frame == null) return;
                                            if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) return;
                                            if (locationScene != null) locationScene.processFrame(frame);

                                            if (loadingMessageSnackbar != null) {
                                                for (Plane plane : frame.getUpdatedTrackables(Plane.class)) {
                                                    if (plane.getTrackingState() == TrackingState.TRACKING) {
                                                        hideLoadingMessage();
                                                    }
                                                }
                                            }
                                        });

                            } catch (InterruptedException | ExecutionException ex) {
                                DemoUtils.displayError(this, "Unable to load renderables", ex);
                            }

                            return null;
                        });

    }

    private Node getAndy(String title, String address, String time) {
        Node base = new Node();
        base.setRenderable(markerRenderable);
        Context c = this;
        base.setOnTapListener((v, event) -> {
            preview(title, address, time);
        });
        return base;
    }

    // ar preview, alertdialog로 띄어주는 부분
    public void preview(String title, String address, String time){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // 제목셋팅
        alertDialogBuilder.setTitle(title);

        // AlertDialog 셋팅
        alertDialogBuilder
                .setMessage("선택하신 포토존에서 어떤 작업을 진행할까요?")
                .setCancelable(false)
                .setPositiveButton("사진 찍기",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                /*
                                Intent intent = new Intent(getApplicationContext(), BoardShowActivity.class);
                                intent.putExtra("title", title);
                                intent.putExtra("address", address);
                                startActivity(intent);
                                */
                                Log.d("ARCore","열려지는 동작까지 됐습니다.");
                            }
                        })
                .setNegativeButton("포포존 바로가기",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 다이얼로그를 취소한다
                                dialog.cancel();
                            }
                        });

        // 다이얼로그 생성
        AlertDialog alertDialog = alertDialogBuilder.create();

        // 다이얼로그 보여주기
        alertDialog.show();
    }

    /**
     * Make sure we call locationScene.resume();
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (locationScene != null) {
            locationScene.resume();
        }

        if (arSceneView.getSession() == null) {
            // If the session wasn't created yet, don't resume rendering.
            // This can happen if ARCore needs to be updated or permissions are not granted yet.
            try {
                Session session = DemoUtils.createArSession(this, installRequested);
                if (session == null) {
                    installRequested = ARLocationPermissionHelper.hasPermission(this);
                    return;
                } else {
                    arSceneView.setupSession(session);
                }
            } catch (UnavailableException e) {
                DemoUtils.handleSessionException(this, e);
            }
        }

        try {
            arSceneView.resume();
        } catch (CameraNotAvailableException ex) {
            DemoUtils.displayError(this, "Unable to get camera", ex);
            finish();
            return;
        }

        if (arSceneView.getSession() != null) {
            showLoadingMessage();
        }
    }

    /**
     * Make sure we call locationScene.pause();
     */
    @Override
    public void onPause() {
        super.onPause();

        if (locationScene != null) {
            locationScene.pause();
        }

        arSceneView.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        arSceneView.destroy();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        if (!ARLocationPermissionHelper.hasPermission(this)) {
            if (!ARLocationPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                ARLocationPermissionHelper.launchPermissionSettings(this);
            } else {
                makeText(
                        this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                        .show();
            }
            finish();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Standard Android full-screen functionality.
            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void showLoadingMessage() {
        if (loadingMessageSnackbar != null && loadingMessageSnackbar.isShownOrQueued()) {
            return;
        }

        loadingMessageSnackbar =
                Snackbar.make(
                        LocationActivity.this.findViewById(android.R.id.content),
                        "포포존 정보를 가져오고 있습니다...",
                        Snackbar.LENGTH_INDEFINITE);
        loadingMessageSnackbar.getView().setBackgroundColor(0xbf323232);
        loadingMessageSnackbar.show();
    }

    private void hideLoadingMessage() {
        if (loadingMessageSnackbar == null) {
            return;
        }

        loadingMessageSnackbar.dismiss();
        loadingMessageSnackbar = null;
    }

    private Node getdistanceView() {
        Node base = new Node();
        base.setRenderable(distanceRenderable);
        Context c = this;
        // Add  listeners etc here
        View eView = distanceRenderable.getView();

        return base;
    }

}