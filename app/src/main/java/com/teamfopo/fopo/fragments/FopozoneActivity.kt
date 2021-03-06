package com.teamfopo.fopo.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.Toast
import com.google.a.b.a.a.a.e
import com.teamfopo.fopo.MainActivity
import com.teamfopo.fopo.R
import com.teamfopo.fopo.ViewActivity
import com.teamfopo.fopo.module.modBoardProcess
import com.teamfopo.fopo.module.modListProcess
import kotlinx.android.synthetic.main.content_fopozone.*

class FopozoneActivity : Fragment(), View.OnClickListener {

    private val TAG = "FopozoneActivity"
    private var trackableGestureDetector: GestureDetector? = null
    private var actLayoutInflater: LayoutInflater? = null

    lateinit var viewRoot: View

    var zone_no = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.content_camera)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (arguments != null) {
            zone_no = arguments!!.getString("zone_no")
        }

        // Inflate the layout for this fragment
        viewRoot = inflater.inflate(R.layout.content_fopozone, container, false)

        //this.actInflater =
        this.actLayoutInflater = inflater

        initFragment(viewRoot)

        return viewRoot
    }

    fun initFragment(viewRoot: View){

         var thread = ThreadClass()
         thread.start()

        /*
            Bundle 설정시

            var userId = arguments!!.getString("userId")
            println(userId)
        */

        //var getBoardList = modBoardProcess().GetList()
        //var brdList = getBoardList.execute(zone_no).get()
        //getListView(viewRoot, brdList)

    }

    inner class ThreadClass: Thread() {
        override fun run() {
            activity!!.runOnUiThread {
                try {
                    var getBoardList = modBoardProcess().GetList()
                    var brdList = getBoardList.execute(zone_no).get()

                    if (brdList.size > 0) {
                        addTitle_textView.text = brdList[0].zone_placename
                        getListView(viewRoot, brdList)
                    } else {
                        Log.d(TAG, "업로드된 사진이 없습니다.")
                        addTitle_textView.text = "포포존에 사진이 없습니다."
                    }
                    viewRoot.invalidate()
                } catch (e: Exception) {
                    Log.d(TAG, "업로드된 사진이 없거나 다른 이유로 인해 오류가 발생했습니다 : " + e.printStackTrace())
                }
            }
        }
    }

    fun getListView(viewRoot: View, brdList: Array<modListProcess>) {
        val inflater = actLayoutInflater!!.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var glItem = viewRoot!!.findViewById(R.id.glBoardList) as GridLayout

        try {
            for (i in 0..brdList.size - 1) {
                var test = brdList[i].brd_no

                //시스템 서비스에서 inflater객체 호출
                // var context = parentFragment.context.getSystemService() as Context
                inflater.inflate(R.layout.item_boardlist, glItem, true) //서브 xml을 메모리에 띄워 container에 추가한다

                //println(i)
                //println(viewRoot.toString())
                var imgBtnView = glItem.getChildAt(i + 1).findViewById(R.id.imgBtnView) as ImageButton

                var getBoardImage = modBoardProcess().GetImage()
                var bm: Bitmap
                bm = getBoardImage.execute(brdList[i].file_no).get()
                imgBtnView.setImageBitmap(bm)

                imgBtnView.setOnClickListener {
                    Toast.makeText(context, "$test" + "번 글로 들어갑니다", Toast.LENGTH_SHORT).show()

                    if (imgBtnView != null) {
                        val i = Intent(super.getContext(), ViewActivity::class.java)
                        i.putExtra("m_select", test)
                        startActivityForResult(i, 1)
                    }

                }

            }
        }catch(e: Exception){
            Log.d(TAG, "ListView를 불러오는 도중 문제가 발생 했습니다 : "+ e.printStackTrace())
            Toast.makeText(viewRoot.context, "잘못된 사진 정보가 있어서 작업을 중단 했습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            // 클릭 이벤트..
        }
    }
}