package com.teamfopo.fopo.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.hardware.Camera
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.widget.*
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.core.exceptions.UnavailableException
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.teamfopo.fopo.R
import com.teamfopo.fopo.module.modCameraProcess
import com.teamfopo.fopo.module.modDBMS
import com.teamfopo.fopo.module.modProtocol
import com.teamfopo.fopo.nodes.PointCloudNode
import kotlinx.android.synthetic.main.content_camera.*
import kotlinx.android.synthetic.main.layout_filter_lists.*
import kotlinx.android.synthetic.main.layout_pose_lists.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import uk.co.appoly.arcorelocation.LocationMarker
import uk.co.appoly.arcorelocation.LocationScene
import uk.co.appoly.arcorelocation.utils.ARLocationPermissionHelper
import uk.co.appoly.arcorelocation.utils.DemoUtils
import uk.co.senab.photoview.PhotoViewAttacher
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 *
 */
class CameraActivity : Fragment(), View.OnClickListener, Scene.OnUpdateListener {

    // Fragment(), View.OnClickListener, Scene.OnTouchListener, Scene.OnPeekTouchListener, Scene.OnUpdateListener

    private var installRequested: Boolean = false
    private var hasFinishedLoading = false

    private var viewCamera: View ?= null

    private var loadingMessageSnackbar: Snackbar? = null

    private var protMain: modProtocol? = null
    private var modDBMS: modDBMS?= null
    private var arSceneView: ArSceneView? = null
    private var pointCloudNode: PointCloudNode? = null

    // Add Button for Click Event Listener
    private var btnCapture: ImageButton? = null

    // Renderables for this example
    private var markerRenderable: ModelRenderable? = null
    private val distanceRenderable: ViewRenderable? = null

    // Our ARCore-Location scene
    private var locationScene: LocationScene? = null

    private var jsonString = ""
    private var jsonArray: JSONArray? = null
    private var jsonObject: JSONObject? = null

    var distanceRenderables: ArrayList<ViewRenderable> = ArrayList()
    var completableFutures: ArrayList<CompletableFuture<*>> = ArrayList()

    // Camera Functions Setting
    private var CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_FRONT
    private var m_imageview: ImageView? = null
    private var mAttacher: PhotoViewAttacher? = null

    private var myCameraPreview: modCameraProcess? = null
    private var horizontalScrollView1: HorizontalScrollView? = null
    private var horizontalScrollView2: HorizontalScrollView? = null
    private var mCamera: Camera? = null
    private var str_filter: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewCamera = inflater.inflate(R.layout.content_camera, container, false)

        initArFragment()
        return viewCamera
    }

    @SuppressLint("WrongViewCast")
    fun initArFragment() {

        //ARCore 초기화
        arSceneView = viewCamera!!.findViewById(R.id.fopo_arcore_sceneview)
        pointCloudNode = PointCloudNode(viewCamera!!.context)
        protMain = modProtocol()
        modDBMS = modDBMS(viewCamera!!.context)

        // 버튼 등록
        btnCapture = viewCamera!!.findViewById(R.id.btnCapture)
        btnCapture!!.setOnClickListener(this)

        // CloudNode를 추가함
        arSceneView!!.scene.addChild(pointCloudNode)

        // 데이터 가져오기
        Log.d("ARCore","토큰 값 : "+ modDBMS!!.getMember().token)
        GlobalScope.launch {
            jsonString = protMain!!.getResultString("http://106.10.51.32/ajax_process/location_process", arrayOf("token"), arrayOf(modDBMS!!.getMember().token))
            Log.d("ARCore","가져온 값 : "+jsonString)
        }
        while(true){
            if(protMain!!.isFinish()) break
            // else Thread.sleep(100)
        }
        Log.d("ARCore","FOPO 서버로부터 포포존 정보를 성공적으로 가져왔습니다.")

        // 카메라 및 위치 권한 가져오기
        ARLocationPermissionHelper.requestPermission(this.activity)
        
        // 거리 표시 레이아웃 추가
        try{
            Log.d("ARCore","거리 표시 레이아웃을 추가합니다.")
            jsonArray = JSONArray(jsonString)
            for(i in 0 until jsonArray!!.length()){
                Log.d("ARCore","$i"+"번째 거리 표시 레이아웃 추가")
                var distanceLayout: CompletableFuture<ViewRenderable> = ViewRenderable.builder()
                    .setView(this.context, R.layout.arcore_inform_view)
                    .build()
                completableFutures.add(i, distanceLayout)
            }
        }catch (e: JSONException){
            e.printStackTrace()
        }

        // 마커 추가
        Log.d("ARCore","마커를 추가합니다.")
        var marker: CompletableFuture<ModelRenderable> = ModelRenderable.builder()
            .setSource(viewCamera!!.context, Uri.parse("fopoMarker.sfb"))
            .build()
            .exceptionally {t: Throwable? ->
                val toastMessage = Toast.makeText(viewCamera!!.context, "마커를 불러오지 못했습니다.", Toast.LENGTH_LONG)
                toastMessage.setGravity(Gravity.CENTER, 0, 0)
                toastMessage.show()
                null
            }

        Log.d("ARCore","CompletableFuture 작업을 계속 진행합니다.")
        CompletableFuture.allOf(completableFutures[0], marker)
            .handleAsync{ v, t->
                Log.d("ARCore","[CompletableFuture] throwable 개체가 있는지 확인합니다.")
                if(t != null){
                    DemoUtils.displayError(viewCamera!!.context, "Unable to load renderables", t)
                    Log.d("ARCore","마커 로드에 실패 했습니다.")
                }else {
                    Log.d("ARCore","[CompletableFuture] JSON 코드를 처리합니다.")
                    try {
                        // JSON 디코딩 오류가 발생할 시 이벤트 처리
                        try {
                            jsonArray = JSONArray(jsonString)

                            for(i in 0 until jsonArray!!.length()){
                                distanceRenderables.add(i, completableFutures[i].get() as ViewRenderable)
                                //distanceRenderables.add(i, (ViewRenderable) completableFutures.get(i).get());
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                        markerRenderable = marker.get()
                        hasFinishedLoading = true

                        // 마커와 거리 표시 distance layout을 추가하는 부분
                        Log.d("ARCore","SceneView에 객체를 추가합니다.")
                        arSceneView!!
                            .scene
                            .addOnUpdateListener { frameTime: FrameTime? ->
                                if (!hasFinishedLoading) null
                                if (locationScene == null) {
                                    locationScene = LocationScene(this.activity, arSceneView)
                                    try {
                                        jsonArray = JSONArray(jsonString)
                                        Log.d("ARCore","총 "+(jsonArray!!.length().toString())+"개의 포포존이 있습니다.")
                                        for (i in 0 until jsonArray!!.length()) {
                                            Log.d("ARCore","$i"+"번째 포포존 정보를 추가중입니다.")
                                            jsonObject = jsonArray!!.getJSONObject(i)

                                            var zone_no = jsonObject!!.getInt("zone_no")
                                            var title = jsonObject!!.getString("zone_placename")
                                            var address = jsonObject!!.getString("zone_placename")
                                            var time = jsonObject!!.getString("zone_regdate")
                                            var latitude = jsonObject!!.getDouble("zone_x")
                                            var longitude = jsonObject!!.getDouble("zone_y")

                                            // 노드 생성 및 Renderable 지정
                                            var base: Node = Node()
                                            base!!.renderable = distanceRenderables[i]
                                            var eView: View = distanceRenderables[i].view

                                            // distance Layout 위도 및 경도 설정, 노드 설정
                                            var layoutLocationMarker: LocationMarker =
                                                LocationMarker(longitude, latitude, base)

                                            // onRender 이벤트가 발생할때마다 마커의 거리가 표시된 레이아웃을 업데이트
                                            layoutLocationMarker.setRenderEvent { node ->
                                                var distanceTextView: TextView = eView.findViewById(R.id.text_distance)
                                                distanceTextView.text = node.distance.toString() + "m"
                                            }

                                            // distance Layout meter 반경 설정
                                            layoutLocationMarker.onlyRenderWhenWithin = 500

                                            // distance layout 높이 설정
                                            layoutLocationMarker.height = -3.5F

                                            // distanceView 추가
                                            locationScene!!.mLocationMarkers.add(layoutLocationMarker)

                                            // 3D marker 위도 및 경도 설정, 노드 설정
                                            var locationMarker: LocationMarker = LocationMarker(
                                                longitude,
                                                latitude,
                                                getMarker(viewCamera!!.context, zone_no, title, address, time)
                                            )

                                            // 3D marker emter 반경 설정
                                            locationMarker.onlyRenderWhenWithin = 500

                                            // 3D marker meter 높이 설정
                                            locationMarker.height = -0.6F

                                            // 3D marker 회전
                                            // locationMarker.node.localRotation = Quaternion.axisAngle(Vector3(0f, 0f, 0f), 60f)
                                            // locationMarker.node.localRotation.set(Vector3(0f, 0f, 0f), 60f)

                                            // 3D marker 추가
                                            locationScene!!.mLocationMarkers.add(locationMarker)

                                            Log.d("ARCore", "위도 및 경도 : " + latitude.toString() + " / " + longitude.toString())
                                        }
                                        Log.d("ARCore","포포존 정보 추가가 완료되었습니다.")
                                        hideLoadingMessage()
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }
                                }

                                var frame: Frame? = arSceneView!!.arFrame
                                if (frame == null) null
                                if (frame!!.camera.trackingState != TrackingState.TRACKING) null
                                if (locationScene != null) locationScene!!.processFrame(frame)

                                if (loadingMessageSnackbar != null) {
                                    for (plane: Plane in frame.getUpdatedTrackables(Plane::class.java)) {
                                        if (plane.trackingState == TrackingState.TRACKING) {
                                            Log.d("ARCore","포포존 정보 추가가 완료 되어 메시지가 제거됩니다.")
                                            hideLoadingMessage()
                                        }
                                    }
                                }
                            }
                    } catch (e: Exception) {
                        when (e) {
                            is InterruptedException, is ExecutionException -> {
                                DemoUtils.displayError(this.context, "Unable to load renderables", e)
                            }
                            else -> {
                            }
                        }
                    }
                }
                null
            }

        Log.d("ARCore","init 작업을 완료 했습니다.")
    }

    private fun getMarker(viewRoot: Context, zone_no: Int, title: String, address: String, time: String): Node{
        var base: Node = Node()
        base.renderable = markerRenderable
        var c: Context = viewRoot
        base.setOnTapListener{v, event ->
            preview(viewRoot, zone_no, title, address, time)
        }
        return base
    }

    fun preview(viewRoot: Context, zone_no: Int, title: String, address: String, time: String){
        var alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(viewRoot)

        // 제목 생성
        alertDialogBuilder.setTitle(title)

        // AlertDialog 셋팅
        alertDialogBuilder
            .setMessage("선택하신 포토존에서 어떤 작업을 진행할까요?")
            .setCancelable(true)
            .setPositiveButton("사진 촬영"){dialog, which ->
                Log.d("ARCore","포포존 "+zone_no+"번에서 사진을 촬영합니다.")
                camera_bottom_desc_layout.visibility = View.INVISIBLE
                camera_bottom_layout.visibility = View.VISIBLE

                fopo_arcore_sceneview.visibility = View.INVISIBLE
                fopo_camera_view.visibility = View.VISIBLE

                startCamera()
                horizontalScrollView1 = viewCamera!!.findViewById(R.id.hscPoseLayout) as HorizontalScrollView
                horizontalScrollView2 = viewCamera!!.findViewById(R.id.hscFilterLayout) as HorizontalScrollView
                initButton()
                dialog.cancel()
            }
            .setNegativeButton("포포존 바로가기"){dialog, which ->
                Log.d("ARCore","포포존 바로가기 선택하셨습니다.")
                dialog.cancel()
            }

        // 다이얼로그 생성 및 표시
        var alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showLoadingMessage(){
        if(loadingMessageSnackbar != null && loadingMessageSnackbar!!.isShownOrQueued) return
        loadingMessageSnackbar = Snackbar.make(
            viewCamera!!,
            "포포존 정보를 가져오고 있습니다...",
            Snackbar.LENGTH_INDEFINITE)
        loadingMessageSnackbar!!.view.setBackgroundColor(0xbf323232.toInt())
        loadingMessageSnackbar!!.show()
    }

    private fun hideLoadingMessage(){
        if(loadingMessageSnackbar == null) return
        loadingMessageSnackbar!!.dismiss()
        loadingMessageSnackbar = null
    }

    private fun getDistanceView(): Node{
        var base: Node = Node()
        base.renderable = distanceRenderable
        var c: Context = viewCamera!!.context
        var eView: View = distanceRenderable!!.view
        return base
    }

    /*
    * 카메라 전환 부분
    */

    private fun initButton() {
        val parameters: Camera.Parameters = mCamera?.parameters!!

        btnCapture?.setOnClickListener {
            if(myCameraPreview?.takePicture() == true){
                Toast.makeText(viewCamera!!.context, "사진이 저장 되었습니다.", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(viewCamera!!.context, "초점이 맞지 않습니다. 다시 시도하세요.", Toast.LENGTH_LONG).show();
            }
            // horizontalScrollView1!!.setVisibility(View.GONE)
            // horizontalScrollView2!!.setVisibility(View.GONE)
        }

        btnFilter.setOnClickListener {
            // 필터 선택시 위에 메뉴바 표시
            if (horizontalScrollView2!!.visibility == View.VISIBLE) {
                horizontalScrollView2!!.visibility = View.GONE
            } else {
                horizontalScrollView2!!.visibility = View.VISIBLE
                horizontalScrollView1!!.visibility = View.GONE
            }
        }

        btnPose.setOnClickListener{
            // 포즈 선택시 위에 메뉴바 표시
            if (horizontalScrollView1!!.visibility == View.VISIBLE) {
                horizontalScrollView1!!.visibility = View.GONE
            } else {
                horizontalScrollView1!!.visibility = View.VISIBLE
                horizontalScrollView2!!.visibility = View.GONE
            }
        }

        pose1.setOnClickListener {
            m_imageview = viewCamera!!.findViewById(R.id.fraPoseSet)
            mAttacher = PhotoViewAttacher(m_imageview)
            mAttacher!!.setScaleType(ImageView.ScaleType.FIT_XY)
            fraPoseSet.setImageResource(R.drawable.ic_frame1)
            myCameraPreview?.setFrameId(R.drawable.ic_frame1)
        }

        pose2.setOnClickListener {
            m_imageview = viewCamera!!.findViewById(R.id.fraPoseSet)
            mAttacher = PhotoViewAttacher(m_imageview)
            mAttacher!!.setScaleType(ImageView.ScaleType.FIT_XY)
            fraPoseSet.setImageResource(R.drawable.ic_frame2)
            myCameraPreview?.setFrameId(R.drawable.ic_frame2)
        }

        pose3.setOnClickListener {
            m_imageview = viewCamera!!.findViewById(R.id.fraPoseSet)
            mAttacher = PhotoViewAttacher(m_imageview)
            mAttacher!!.setScaleType(ImageView.ScaleType.FIT_XY)
            fraPoseSet.setImageResource(R.drawable.ic_frame3)
            myCameraPreview?.setFrameId(R.drawable.ic_frame3)
        }

        pose4.setOnClickListener {
            m_imageview = viewCamera!!.findViewById(R.id.fraPoseSet)
            mAttacher = PhotoViewAttacher(m_imageview)
            mAttacher!!.setScaleType(ImageView.ScaleType.FIT_XY)
            fraPoseSet.setImageResource(R.drawable.ic_frame4)
            myCameraPreview?.setFrameId(R.drawable.ic_frame4)
        }

        pose5.setOnClickListener {
            m_imageview = viewCamera!!.findViewById(R.id.fraPoseSet)
            mAttacher = PhotoViewAttacher(m_imageview)
            mAttacher!!.setScaleType(ImageView.ScaleType.FIT_XY)
            fraPoseSet.setImageResource(R.drawable.ic_frame5)
            myCameraPreview?.setFrameId(R.drawable.ic_frame5)
        }

        pose6.setOnClickListener {
            m_imageview = viewCamera!!.findViewById(R.id.fraPoseSet)
            mAttacher = PhotoViewAttacher(m_imageview)
            mAttacher!!.setScaleType(ImageView.ScaleType.FIT_XY)
            fraPoseSet.setImageResource(R.drawable.ic_frame6)
            myCameraPreview?.setFrameId(R.drawable.ic_frame6)
        }

        pose7.setOnClickListener {
            m_imageview = viewCamera!!.findViewById(R.id.fraPoseSet)
            mAttacher = PhotoViewAttacher(m_imageview)
            mAttacher!!.setScaleType(ImageView.ScaleType.FIT_XY)
            fraPoseSet.setImageResource(R.drawable.ic_frame7)
            myCameraPreview?.setFrameId(R.drawable.ic_frame7)
        }

        pose8.setOnClickListener {
            m_imageview = viewCamera!!.findViewById(R.id.fraPoseSet)
            mAttacher = PhotoViewAttacher(m_imageview)
            mAttacher!!.setScaleType(ImageView.ScaleType.FIT_XY)
            fraPoseSet.setImageResource(R.drawable.ic_frame8)
            myCameraPreview?.setFrameId(R.drawable.ic_frame8)
        }

        pose9.setOnClickListener {
            m_imageview = viewCamera!!.findViewById(R.id.fraPoseSet)
            mAttacher = PhotoViewAttacher(m_imageview)
            mAttacher!!.setScaleType(ImageView.ScaleType.FIT_XY)
            fraPoseSet.setImageResource(R.drawable.ic_frame9)
            myCameraPreview?.setFrameId(R.drawable.ic_frame9)
        }

        pose10.setOnClickListener {
            m_imageview = viewCamera!!.findViewById(R.id.fraPoseSet)
            mAttacher = PhotoViewAttacher(m_imageview)
            mAttacher!!.setScaleType(ImageView.ScaleType.FIT_XY)
            fraPoseSet.setImageResource(R.drawable.ic_frame10)
            myCameraPreview?.setFrameId(R.drawable.ic_frame10)
        }

        pose11.setOnClickListener {
            m_imageview = viewCamera!!.findViewById(R.id.fraPoseSet)
            mAttacher = PhotoViewAttacher(m_imageview)
            mAttacher!!.setScaleType(ImageView.ScaleType.FIT_XY)
            fraPoseSet.setImageResource(R.drawable.ic_frame11)
            myCameraPreview?.setFrameId(R.drawable.ic_frame11)
        }

        pose12.setOnClickListener {
            m_imageview = viewCamera!!.findViewById(R.id.fraPoseSet)
            mAttacher = PhotoViewAttacher(m_imageview)
            mAttacher!!.setScaleType(ImageView.ScaleType.FIT_XY)
            fraPoseSet.setImageResource(R.drawable.ic_frame12)
            myCameraPreview?.setFrameId(R.drawable.ic_frame12)
        }

        pose13.setOnClickListener {
            m_imageview = viewCamera!!.findViewById(R.id.fraPoseSet)
            mAttacher = PhotoViewAttacher(m_imageview)
            mAttacher!!.setScaleType(ImageView.ScaleType.FIT_XY)
            fraPoseSet.setImageResource(R.drawable.ic_frame13)
            myCameraPreview?.setFrameId(R.drawable.ic_frame13)
        }

        pose14.setOnClickListener {
            m_imageview = viewCamera!!.findViewById(R.id.fraPoseSet)
            mAttacher = PhotoViewAttacher(m_imageview)
            mAttacher!!.setScaleType(ImageView.ScaleType.FIT_XY)
            fraPoseSet.setImageResource(R.drawable.ic_frame14)
            myCameraPreview?.setFrameId(R.drawable.ic_frame14)
        }

        pose15.setOnClickListener {
            m_imageview = viewCamera!!.findViewById(R.id.fraPoseSet)
            mAttacher = PhotoViewAttacher(m_imageview)
            mAttacher!!.setScaleType(ImageView.ScaleType.FIT_XY)
            fraPoseSet.setImageResource(R.drawable.ic_frame15)
            myCameraPreview?.setFrameId(R.drawable.ic_frame15)
        }

        rlNone.setOnClickListener {
            parameters!!.colorEffect = Camera.Parameters.EFFECT_NONE
            mCamera?.parameters = parameters
            Toast.makeText(this.context, "기본필터 선택", Toast.LENGTH_LONG).show()
            str_filter = "기본필터"
        }

        rlMono.setOnClickListener {
            parameters!!.colorEffect = Camera.Parameters.EFFECT_MONO
            mCamera?.parameters = parameters
            Toast.makeText(this.context, "흑백필터 선택", Toast.LENGTH_LONG).show()
            str_filter = "흑백필터"
        }
        rlNegative.setOnClickListener {
            parameters!!.colorEffect = Camera.Parameters.EFFECT_MONO
            mCamera?.parameters = parameters
            Toast.makeText(this.context, "네거티브필터 선택", Toast.LENGTH_LONG).show()
            str_filter = "네거티브필터"
        }
        rlSepia.setOnClickListener {
            parameters!!.colorEffect = Camera.Parameters.EFFECT_MONO
            mCamera?.parameters = parameters
            Toast.makeText(this.context, "세피아필터 선택", Toast.LENGTH_LONG).show()
            str_filter = "세피아필터"
        }
    }

    private fun startCamera() {

        Log.e(TAG, "startCamera")
        mCamera = getCameraInstance()
        // Create our Preview view and set it as the content of our activity.
        myCameraPreview = modCameraProcess(this.context, mCamera ,CAMERA_FACING)

        fopo_camera_view.addView(myCameraPreview)

    }

    private fun getCameraInstance(): Camera? {
        var c: Camera? = null
        try {
            c = Camera.open()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return c
    }

     /*fun colorEffectFilter(v: View) {
        try {
            val parameters: Camera.Parameters = mCamera?.parameters!!
            when (v.id) {
                R.id.rlNone -> {
                    parameters!!.colorEffect = Camera.Parameters.EFFECT_NONE
                    mCamera?.parameters = parameters
                    Toast.makeText(this.context, "기본필터선택", Toast.LENGTH_LONG).show()
                    str_filter = "기본필터"
                }
                R.id.rlMono -> {
                    parameters!!.colorEffect = Camera.Parameters.EFFECT_MONO
                    Toast.makeText(this.context, "Mono필터선택", Toast.LENGTH_LONG).show()
                    mCamera?.setParameters(parameters)
                    str_filter = "Mono필터"
                }
                R.id.rlNegative -> {
                    parameters!!.colorEffect = Camera.Parameters.EFFECT_NEGATIVE
                    Toast.makeText(this.context, "Negative필터선택", Toast.LENGTH_LONG).show()
                    mCamera?.setParameters(parameters)
                    str_filter = "Negative필터"
                }
                R.id.rlSepia -> {
                    parameters!!.colorEffect = Camera.Parameters.EFFECT_SEPIA
                    Toast.makeText(this.context, "Sepia필터선택", Toast.LENGTH_LONG).show()
                    mCamera?.setParameters(parameters)
                    str_filter = "Sepia필터"
                }
            }
        } catch (ex: Exception) {
            Log.d(TAG, ex.message)
        }
    }*/


    override fun onUpdate(frameTime: FrameTime?) {
        // Visualize tracked points.
        val pointCloud = arSceneView!!.arFrame!!.acquirePointCloud()
        pointCloudNode!!.update(pointCloud)

        // Application is responsible for releasing the point cloud resources after using it.
        pointCloud.release()
    }

    override fun onResume() {
        super.onResume()

        if(locationScene != null) locationScene!!.resume()

        if(arSceneView!!.session == null){
            // 세션이 생성되지 않은 경우 렌더링을 다시 시작안함
            // ARCore를 업데이트 해야 하거나 권한이 없을 경우 시작됨
            try{
                var session: Session = DemoUtils.createArSession(this.activity, installRequested)
                if(session == null){
                    installRequested = ARLocationPermissionHelper.hasPermission(this.activity)
                    return
                }else{
                    arSceneView!!.setupSession(session)
                }
            }catch(e: UnavailableException){
                DemoUtils.handleSessionException(this.activity, e)
            }
        }

        try{
            arSceneView!!.resume()
        }catch(e: CameraNotAvailableException){
            DemoUtils.displayError(viewCamera!!.context, "카메라를 사용할 수 없습니다.", e)
            return
        }
        if(arSceneView!!.session != null) showLoadingMessage()
    }

    override fun onPause() {
        super.onPause()

        if(locationScene != null) locationScene!!.pause()
        arSceneView!!.pause()

        if (mCamera != null) {
            mCamera!!.stopPreview()
            mCamera!!.release()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        arSceneView!!.destroy()
        if (mCamera != null) {
            mCamera!!.release()
            mCamera = null
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(!ARLocationPermissionHelper.hasPermission(this.activity)){
            if(!ARLocationPermissionHelper.shouldShowRequestPermissionRationale(this.activity)){
                // 다시 묻지 않음을 확인
                ARLocationPermissionHelper.launchPermissionSettings(this.activity)
            }else{
                Toast.makeText(viewCamera!!.context, "FOPO를 사용하시려면 카메라 권한이 필요합니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onClick(v: View?) {
        val parameters: Camera.Parameters = mCamera?.parameters!!
        when (v?.id) {
            R.id.btnCapture -> {
                Toast.makeText(viewCamera!!.context, "사진 촬영 버튼을 누르셨습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun showDialogBox(title: String, contents: String, strYes: String, strNo: String): Boolean {
        var blSelect: Boolean = false
        var builder = AlertDialog.Builder(ContextThemeWrapper(this.context, R.style.Theme_AppCompat_Light_Dialog_Alert))
        builder.setTitle(title)
        builder.setMessage(contents)
        builder.setPositiveButton(strYes) { dialog, which ->
            blSelect = true
        }
        builder.setNegativeButton(strNo) { dialog, which ->
            blSelect = false
        }
        builder.show()
        return blSelect
    }
}