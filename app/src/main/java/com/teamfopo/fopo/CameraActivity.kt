package com.teamfopo.fopo

import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.Toast
import android.widget.ToggleButton
import com.google.ar.core.*
import com.google.ar.sceneform.*
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.gun0912.tedpermission.PermissionListener
import com.teamfopo.fopo.nodes.PointCloudNode
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Math.*
import java.nio.ByteBuffer
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 *
 */
class CameraActivity : Fragment(), View.OnClickListener, Scene.OnTouchListener, Scene.OnPeekTouchListener, Scene.OnUpdateListener {

    private var isCamera: Boolean? = true
    private var isPermission: Boolean? = true

    private val TAG = "CameraActivity"
    private var trackableGestureDetector: GestureDetector? = null

    private var arFragment: ArFragment? = null
    private lateinit var markerRenderable: ModelRenderable
    private lateinit var pointCloudNode: PointCloudNode

    private var strTitle = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.content_camera)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var viewCamera: View
        viewCamera = inflater.inflate(R.layout.content_camera, container, false)

        tedPermission(viewCamera)
        initArFragment(viewCamera)

        return viewCamera
    }

    fun initArFragment(viewRoot: View){
        /*
            AR Fragment 생성 부분
        */
        arFragment = childFragmentManager.findFragmentById(R.id.fopo_ar_fragment) as? ArFragment
        arFragment!!.planeDiscoveryController.hide()
        arFragment!!.planeDiscoveryController.setInstructionView(null)

        pointCloudNode = PointCloudNode(this.activity)
        arFragment!!.arSceneView.scene.addChild(pointCloudNode)

        arFragment!!.arSceneView.planeRenderer.isEnabled = false

        arFragment!!.arSceneView.scene.addOnUpdateListener(::onFrame)

        ModelRenderable.builder()
            .setSource(this.activity, Uri.parse("fopoMarker.sfb"))
            .build()
            .thenAccept { renderable -> markerRenderable = renderable }
            .exceptionally { throwable ->
                val toast = Toast.makeText(this.activity, "마커를 불러오지 못했습니다.", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
                null
            }

        // set on tap listener
        arFragment!!.arSceneView.scene.addOnPeekTouchListener(this)
        arFragment!!.arSceneView.scene.setOnTouchListener(this)
        arFragment!!.arSceneView.scene.addOnUpdateListener(this)
        arFragment!!.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane, motionEvent: MotionEvent ->
                Toast.makeText(this.context,"해당 위치에 새로운 node가 생성 되었습니다",Toast.LENGTH_LONG).show()
        }

        trackableGestureDetector = GestureDetector(this.activity,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    onSingleTap(e)
                    return true
                }

                override fun onDown(e: MotionEvent): Boolean {
                    return true
                }
            }
        )

        // Capture the Image
        var btnLocation: ToggleButton = viewRoot.findViewById(R.id.btnLocation) as ToggleButton
        btnLocation.setOnClickListener(this)

        var btnCapture: Button = viewRoot.findViewById(R.id.btnCapture) as Button
        btnCapture.setOnClickListener(this)

        var btnFopozone: Button = viewRoot.findViewById(R.id.btnFopozone) as Button
        btnFopozone.setOnClickListener(this)

        /*
        arFragment!!.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            val anchor = hitResult.createAnchor()

            ModelRenderable.builder()
                .setSource(this.activity, Uri.parse("fopoMarker.sfb"))
                .build()
                .thenAccept({ modelRenderable -> addModelToScene(anchor, modelRenderable) })
                .exceptionally({ throwable ->
                    val builder = AlertDialog.Builder(this.context)
                    builder.setMessage(throwable.message)
                        .show()
                    null
                })
        }
        */



        // 제발 되게 해주세요

    }

    fun onFrame(frameTime: FrameTime) {
        arFragment!!.onUpdate(frameTime)
        val frame = arFragment!!.arSceneView.arFrame
        val camera = frame!!.camera

        // If not tracking, don't draw 3d objects.
        if (camera.trackingState == TrackingState.PAUSED) {
            return
        }

        // Visualize tracked points.
        val pointCloud = frame.acquirePointCloud()
        pointCloudNode.update(pointCloud)

        // Application is responsible for releasing the point cloud resources after using it.
        pointCloud.release()

    }

    fun onSingleTap(motionEvent: MotionEvent) {
        val arSceneView = arFragment!!.arSceneView
        val frame = arSceneView.arFrame
        if (frame != null && frame.camera.trackingState == TrackingState.TRACKING) {
            for (hit in frame.hitTest(motionEvent)) {
                Log.d("ARCore", "적중 거리 : ${hit.distance}")
                // Toast.makeText(this.context,"적중 거리 : ${hit.distance}",Toast.LENGTH_SHORT).show()

                Log.d("ARCore", "선택 개체 정보 : ${hit.hitPose.toString()}")

                val trackable = hit.trackable
                if (trackable is Plane) {
                    // Anchor down
                    val anchor = hit.createAnchor()
                    val anchorNode = AnchorNode(anchor)
                    anchorNode.setParent(arSceneView.scene)

                    /*
                    markerRenderable.let{
                        Node().apply{
                            setParent(anchorNode)
                            renderable = it
                            localPosition = Vector3(-0.0f, 0.0f, 0.0f)
                        }

                        Toast.makeText(this.context,"개체 : 앵커가 생성되고 선택 되었습니다",Toast.LENGTH_LONG).show()
                    }
                    */

                    val node = Node()
                    node.setParent(anchorNode)
                    node.worldRotation = Quaternion()
                    /*
                    node.setOnTapListener{hitTestResult: HitTestResult, motionEvent: MotionEvent ->
                        Toast.makeText(this.context,"개체 : 개체를 선택했습니다.",Toast.LENGTH_LONG).show()
                    }
                    */

                    // Create node and add to the anchor
                    val andy = TransformableNode(arFragment!!.transformationSystem)
                    andy.setParent(node)
                    andy.renderable = markerRenderable

                    andy.select()

                    Toast.makeText(this.context,"개체 : 앵커가 생성되고 선택 되었습니다",Toast.LENGTH_LONG).show()

                    Log.d("ARCore", "인스턴스 이름 : ${trackable.javaClass.name}")
                    Toast.makeText(this.context,"인스턴스 이름 : ${trackable.javaClass.name}",Toast.LENGTH_SHORT).show()


                }else if (trackable is Point){
                    // Toast.makeText(this.context,"개체 : 앵커가 선택 되었습니다",Toast.LENGTH_LONG).show()
                }
            }
        }
        Log.i(this.TAG, "Tracking state: ${frame!!.camera.trackingState}")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnCapture -> {
                takePhoto()
            }
            R.id.btnLocation -> {
                Toast.makeText(context, "GPS On/Off 버튼입니다", Toast.LENGTH_LONG).show()
            }
            R.id.btnFopozone -> {
                Toast.makeText(context, "해당 포토존으로 이동합니다", Toast.LENGTH_LONG).show()
                // Set title bar
                (activity as MainActivity).setActionBarTitle("포포맵")
                (activity as MainActivity).setTestGOGO()
                //val fragment1 = FopomapActivity()
                val fragment2 = FopozoneActivity()
                val bundle = Bundle()
                bundle.putString("zone_no", "3")
                fragment2.arguments = bundle
                val fragmentManager = fragmentManager
                val fragmentTransaction = fragmentManager!!.beginTransaction()
                fragmentTransaction.replace(R.id.fraMain, fragment2)
                fragmentTransaction.commit()

            }else -> {
        }
        }
    }

    private fun addModelToScene(anchor: Anchor, modelRenderable: ModelRenderable) {
        val anchorNode = AnchorNode(anchor)
        val transformableNode = TransformableNode(arFragment!!.getTransformationSystem())
        transformableNode.setParent(anchorNode)
        transformableNode.renderable = modelRenderable
        arFragment!!.getArSceneView().scene.addChild(anchorNode)
        transformableNode.select()
    }

    fun onCameraClick(buffer: ByteBuffer, width : Int, height: Int): Bitmap {

        buffer.rewind()
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(buffer)
        return bitmap
    }

    fun getScreenShot(view: View): Bitmap {
        val screenView = view.rootView
        screenView.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(screenView.drawingCache)
        screenView.isDrawingCacheEnabled = false
        return bitmap
    }

    override fun onSceneTouch(hitTestResult: HitTestResult?, motionEvent: MotionEvent?): Boolean {
        if(hitTestResult!!.node !== null){
            // Toast.makeText(this.context,"터치 : "+hitTestResult!!.node.toString(),Toast.LENGTH_SHORT).show()
            Log.d("ARCore", "터치 : "+motionEvent?.getY().toString() + ","+motionEvent?.getY().toString())
        }else{
            Log.d("ARCore", "터치 : 개체가 없습니다")
        }
        return true
    }

    override fun onPeekTouch(hitTestResult: HitTestResult?, motionEvent: MotionEvent?) {
        arFragment!!.onPeekTouch(hitTestResult, motionEvent)

        if (hitTestResult!!.node != null) {
            // Toast.makeText(this.context, "노드를 터치 했습니다.", Toast.LENGTH_LONG).show()
            // Log.d("ARCore", "Sceneform 노드를 터치하고 있습니다.")
        }

        trackableGestureDetector!!.onTouchEvent(motionEvent)
    }

    private fun tedPermission(viewRoot: View) {

        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                // 권한 요청 성공
                isPermission = true
            }
            override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                // 권한 요청 실패
                isPermission = false
            }
        }
        /*
        TedPermission.with(view!!.context)
            .setPermissionListener(permissionListener)
            .setRationaleMessage(resources.getString(R.string.permission_2))
            .setDeniedMessage(resources.getString(R.string.permission_1))
            .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA)
            .check()
            */

    }

    fun getDegre(latSource: Double, lngSource: Double, latDestination: Double, lngDestination: Double):Double{
        val lat1:Double = latSource/180 * Math.PI
        val lng1:Double = lngSource/180 * Math.PI
        val lat2:Double = latDestination/180 * Math.PI
        val lng2:Double = lngDestination/180 * Math.PI

        val y = sin(lng2 - lng1)*cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(lng2 - lng1)

        val tan2 = atan2(y, x)
        val degre = tan2 * 180 / Math.PI
        if (degre < 0) {
            return degre+360
        }else {
            return degre
        }
    }

    override fun onUpdate(frameTime: FrameTime) {
        /*
        try {
            // var currentFrame: Frame = arFragment.arSceneView.scene.view.getArFrame()
            var currentImage: Image = arFragment!!.arSceneView.arFrame!!.acquireCameraImage() as Image
            var imageFormat = currentImage.format
            if (imageFormat == ImageFormat.YUV_420_888) {
                Log.d("ARCore", "이미지 변환이 정상적으로 처리 되었으며, 포맷은 YUV_420_888 입니다.")
            }
        }catch (e: Exception){
            Log.d("ARCore","오류가 발생했습니다 : "+e.toString())
        }
        */
    }

    fun takePhoto(){
        try {
            // var currentFrame: Frame = arFragment.arSceneView.scene.view.getArFrame()
            var currentImage: Image = arFragment!!.arSceneView.arFrame!!.acquireCameraImage() as Image
            var imageFormat = currentImage.format
            if (imageFormat == ImageFormat.YUV_420_888) {
                Log.d("CameraCore", "이미지 변환이 정상적으로 처리 되었으며, 포맷은 YUV_420_888 입니다.")
                var tmpPath = getFileName()
                if(!tmpPath.equals("")){
                    Log.d("CameraCore","저장 경로 : $tmpPath")

                    WriteImageInformation(currentImage, "$tmpPath")
                    Log.d("CameraCore", "사진이 저장 되었습니다.")
                    Toast.makeText(context, "사진을 저장 했습니다.", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(context, "사진 저장에 실패 했습니다.", Toast.LENGTH_LONG).show()
                }
            }else{
                Log.d("CameraCore", "촬영 조건이 맞지 않습니다.")
                Toast.makeText(context, "촬영 조건을 다시 맞춰주세요.", Toast.LENGTH_LONG).show()
            }
        }catch (e: Exception){
            Log.d("CameraCore","오류가 발생했습니다 : "+e.toString())
        }
    }


    fun YUV_420_888toNV21(image: Image): ByteArray {
        val nv21: ByteArray
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        nv21 = ByteArray(ySize + uSize + vSize)

        //U and V are swapped
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        return nv21
    }

    fun NV21toJPEG(nv21: ByteArray, width: Int, height: Int): ByteArray {
        val out = ByteArrayOutputStream()
        val yuv = YuvImage(nv21, ImageFormat.NV21, width, height, null)
        yuv.compressToJpeg(Rect(0, 0, width, height), 100, out)
        return out.toByteArray()
    }

    fun getFileName(): String{

        var path = "/sdcard/DCIM/" // this.activity!!.applicationContext.filesDir.path.toString()

        var datetime = DateTimeFormatter
                                    .ofPattern("yyyyMMddHHmmss")
                                    .withZone(ZoneOffset.UTC)
                                    .format(Instant.now())

        path = path + "/FOPO_"+ datetime +".jpg"
        var file = File(path)

        Log.d("FileManager","파일 경로 : $path")

        // create a new file
        val isNewFileCreated :Boolean = file.createNewFile()

        if(isNewFileCreated){
            Log.d("FileManager","파일이 생성 되었습니다 : $path")
            return path
        } else{
            Log.d("FileManager","파일이 이미 존재합니다 : $path")
        }

        // 이미 파일이 존재하더라도 한번 더 생성을 진행함
        val isFileCreated :Boolean = file.createNewFile()

        if(isFileCreated){
            Log.d("FileManager","파일이 생성 되었습니다 : $path")
            return path
        } else{
            Log.d("FileManager","파일이 이미 존재합니다 : $path")
            return ""
        }
    }

    fun WriteImageInformation(image: Image, path: String) {
        var data: ByteArray? = null
        data = NV21toJPEG(
            YUV_420_888toNV21(image),
            image.width, image.height
        )
        val bos = BufferedOutputStream(FileOutputStream(path))
        bos.write(data)
        bos.flush()
        bos.close()
    }

}