package com.teamfopo.fopo.fragments

import android.app.AlertDialog
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.teamfopo.fopo.R
import com.teamfopo.fopo.nodes.PointCloudNode
import uk.co.appoly.arcorelocation.LocationScene
import java.util.concurrent.CompletableFuture


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

    private val TAG = "CameraActivity"
    private var trackableGestureDetector: GestureDetector? = null

    private var arFragment: ArFragment? = null
    private lateinit var markerRenderable: CompletableFuture<ModelRenderable>
    private lateinit var pointCloudNode: PointCloudNode

    private var strTitle = ""

    private var lsMain: LocationScene ?= null
    private var lmMain : LocationManager ? = null

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

        /*
        // Create persistent LocationManager reference
        lmMain = super.getActivity()!!.getSystemService(LOCATION_SERVICE) as LocationManager?
        */

        initArFragment(viewCamera)

        return viewCamera
    }

    fun initArFragment(viewRoot: View) {
        /*
            AR Fragment 생성 부분

        arFragment = childFragmentManager.findFragmentById(R.id.fopo_arcore_sceneview) as? ArFragment
        arFragment!!.planeDiscoveryController.hide()
        arFragment!!.planeDiscoveryController.setInstructionView(null)

        pointCloudNode = PointCloudNode(this.activity)
        arFragment!!.arSceneView.scene.addChild(pointCloudNode)

        arFragment!!.arSceneView.planeRenderer.isEnabled = true

        arFragment!!.arSceneView.scene.addOnUpdateListener(this)

        // LocationScene 추가
        markerRenderable = ModelRenderable.builder()
                .setSource(this.activity, Uri.parse("fopoMarker.sfb"))
                .build()
                .exceptionally { throwable ->
                    val toast = Toast.makeText(this.activity, "마커를 불러오지 못했습니다.", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    null
                }
        */
    }

    override fun onUpdate(frameTime: FrameTime?) {
        arFragment!!.onUpdate(frameTime)
        val frame = arFragment!!.arSceneView.arFrame

        // Visualize tracked points.
        val pointCloud = frame!!.acquirePointCloud()
        pointCloudNode.update(pointCloud)

        // Application is responsible for releasing the point cloud resources after using it.
        pointCloud.release()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnCapture -> {

            }else -> {

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
        builder.show();
        return blSelect
    }

    private fun addModelToScene(anchor: Anchor, modelRenderable: ModelRenderable) {
        val anchorNode = AnchorNode(anchor)
        val transformableNode = TransformableNode(arFragment!!.getTransformationSystem())

        transformableNode.setParent(anchorNode)
        transformableNode.renderable = modelRenderable
        transformableNode.setLocalScale(Vector3(0.55f, 0.55f, 0.55f))
        transformableNode.rotationController.isEnabled = false
        transformableNode.scaleController.isEnabled = false
        transformableNode.setOnTapListener { hitTestResult: HitTestResult, motionEvent: MotionEvent ->
            var blSelect: Boolean = showDialogBox("포포존 이동", "선택하신 포포존으로 이동할까요?", "네", "아니오")
            if (blSelect == true) Log.d("ARCore","포포존으로 이동이 실행되는 부분입니다.")
            else hitTestResult.node!!.removeChild(hitTestResult.node)
        }

        arFragment!!.getArSceneView().scene.addChild(anchorNode)
        transformableNode.select()
    }
}