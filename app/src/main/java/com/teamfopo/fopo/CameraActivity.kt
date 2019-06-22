package com.teamfopo.fopo

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.Toast
import com.google.ar.core.Anchor
import com.google.ar.core.Point
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.*
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.gun0912.tedpermission.PermissionListener
import com.teamfopo.fopo.nodes.PointCloudNode
import java.nio.ByteBuffer




// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 *
 */
class CameraActivity : Fragment(), View.OnClickListener, Scene.OnTouchListener, Scene.OnPeekTouchListener {
    private var isCamera: Boolean? = true
    private var isPermission: Boolean? = true

    private val TAG = "CameraActivity"
    private var trackableGestureDetector: GestureDetector? = null

    private var arFragment: ArFragment? = null
    private lateinit var markerRenderable: ModelRenderable
    private lateinit var pointCloudNode: PointCloudNode

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
        var btnCapture: Button = viewRoot.findViewById(R.id.btnCapture) as Button
        btnCapture.setOnClickListener(this)

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
                Log.i(this.TAG, "적중 거리 : ${hit.distance}")
                Toast.makeText(this.context,"적중 거리 : ${hit.distance}",Toast.LENGTH_SHORT).show()
                val trackable = hit.trackable
                if (trackable is Point) {
                    // Anchor down
                    val anchor = hit.createAnchor()
                    val anchorNode = AnchorNode(anchor)
                    anchorNode.setParent(arSceneView.scene)

                    val node = Node()
                    node.setParent(anchorNode)
                    node.worldRotation = Quaternion()

                    // Create node and add to the anchor
                    val andy = TransformableNode(arFragment!!.transformationSystem)
                    andy.setParent(node)
                    andy.renderable = markerRenderable
                    andy.select()
                }
                Log.i(this.TAG, "Instance of ${trackable.javaClass.name}")
                Toast.makeText(this.context,"인스턴스 이름 : ${trackable.javaClass.name}",Toast.LENGTH_SHORT).show()
            }
        }
        Log.i(this.TAG, "Tracking state: ${frame!!.camera.trackingState}")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnCapture -> {
                Toast.makeText(context,"사진을 저장 했습니다.", Toast.LENGTH_LONG).show()
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

    override fun onSceneTouch(p0: HitTestResult?, p1: MotionEvent?): Boolean {
        return true
    }

    override fun onPeekTouch(hitTestResult: HitTestResult?, motionEvent: MotionEvent?) {
        arFragment!!.onPeekTouch(hitTestResult, motionEvent)

        if (hitTestResult!!.node != null) {
            // Toast.makeText(this.context, "노드를 터치 했습니다.", Toast.LENGTH_LONG).show()
            Log.d(TAG, "Touching a Sceneform node")
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

}