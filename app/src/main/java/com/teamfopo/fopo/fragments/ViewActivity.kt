package com.teamfopo.fopo.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.teamfopo.fopo.R
import com.teamfopo.fopo.module.modBoardProcess
import com.teamfopo.fopo.module.modReplyProcess
import com.teamfopo.fopo.module.modViewProcess
import kotlinx.android.synthetic.main.content_view.*

class ViewActivity : AppCompatActivity() {
    var preReplyItem = -1
    var selectedReplyItem = -1
    var istoggle:Boolean = true

    var re_orgin:String = "NULL"

    var likeCnt = 0 // 임시로 써놓은 좋아요 개수 ( 게시글 뷰할때 서버에서 받아온당 )
    var isLike = false // 임시로 써놓은 좋아요 여부 ( true false의 값은 서버에서 받아온당 )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_view)

        window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)

        //ListActivity에서 클릭한 file_no Intent값 전달받은거.
        var i = getIntent()
        var getInt = i.getIntExtra("m_select", 1) // m_select값을 얻어내고... 만약 m_select값이 없다면 디폴트로 0을 준다.

        Log.i("m_select", getInt.toString())

        SetPicture(getInt)
        ViewReplyLists("$getInt", false) // 댓글리스트불러옴

        btnReply.setOnClickListener {
            if ( TextUtils.isEmpty(edit_reply.text ) ) {
                Toast.makeText(this, "댓글을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                var re_content: String = edit_reply.text.toString()

                var ReplyWrite = modBoardProcess().ReplyWrite()
                ReplyWrite.execute("3", "$getInt", "1", "$re_orgin", "$re_content")

                ViewReplyLists("$getInt", true)
            }
        }

        txtLike.setOnClickListener {
            if ( isLike == false ) { // 좋아요를 누르지 않았을시..
                likeCnt++ // db에있는 좋아요수를 한개올리고 좋아요수를 받아옴
                txtLike.text = "♥" + "$likeCnt"  // 좋아용
                isLike = true // 좋아요를 누른상태로 바꿔줌

                Toast.makeText(this, "좋아요를 눌렀습니다", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "이미 좋아요를 눌렀습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        btnFilterInfo.setOnClickListener {
            if ( btnFilterInfo.isChecked ) {
                txtFilterInfo.visibility = View.VISIBLE
            } else {
                txtFilterInfo.visibility = View.INVISIBLE
            }
        }
    }   //onCreate 끝부분

    private fun SetPicture(getBrd_no: Int) {
        var getBoardInfo = modBoardProcess().GetView()
        var brdInfo = getBoardInfo.execute(getBrd_no.toString()).get()

        showPicTest(brdInfo)
    }

    private fun showPicTest(brdInfo: modViewProcess) {
        var getSelectImage = modBoardProcess().GetImage()
        var bitmap: Bitmap
        bitmap = getSelectImage.execute(brdInfo.file_no).get()

        selectPic_imageView.setImageBitmap(bitmap)  //이미지 뿌려주기
        txtID.text=brdInfo.mem_nick+"님이 공유한 사진"
        content_TextView.text=brdInfo.brd_content
    }

    fun ViewReplyLists(brd_no: String, focusable: Boolean) {
        llReply.removeViewsInLayout(1, llReply.childCount - 1 ) // llReply안의 뷰 비우기

        var GetReplyLists = modBoardProcess().ReplyLists()
        var ReplyListsInfo = GetReplyLists.execute("$brd_no").get()

        var replyCount = ReplyListsInfo.size.toString()

        txtReplyCount.text = "$replyCount" + "개의 댓글이 있습니다."

        for ( i in 0..ReplyListsInfo.size - 1 ) {
            if ( ReplyListsInfo[i].rre_no == 0 ) {

                val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater //시스템 서비스에서 inflater객체 호출
                inflater.inflate(R.layout.item_reply, llReply, true) //서브 xml을 메모리에 띄워 container에 추가한다
                getReplyItem(brd_no, ReplyListsInfo[i])

                for (j in i..ReplyListsInfo.size - 1) {
                    if (ReplyListsInfo[j].rre_no == ReplyListsInfo[i].re_no) {
                        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater //시스템 서비스에서 inflater객체 호출
                        inflater.inflate(R.layout.item_reply, llReply, true) //서브 xml을 메모리에 띄워 container에 추가한다
                        getReplyItem(brd_no, ReplyListsInfo[j])
                    }
                }
            }
        }

        edit_reply.text = null

        if ( focusable ) {
            llReply.getChildAt(llReply.childCount-1).isFocusableInTouchMode = true
            llReply.getChildAt(llReply.childCount-1).requestFocus()
        }

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(edit_reply.getWindowToken(), 0) // 키보드 내리기

        preReplyItem = - 1
        selectedReplyItem = -1
        istoggle = true
        re_orgin= "NULL"
    }

    fun getReplyItem(brd_no: String, replyInfo: modReplyProcess) {
        var llReplyChild = llReply.childCount - 1

        var txtID = llReply.getChildAt(llReplyChild).findViewById(R.id.txtID) as TextView
        var txtReply = llReply.getChildAt(llReplyChild).findViewById(R.id.txtReply) as TextView
        var txtReplyDate = llReply.getChildAt(llReplyChild).findViewById(R.id.txtReplyDate) as TextView
        var btnReplyOption = llReply.getChildAt(llReplyChild).findViewById(R.id.btnReplyOption) as ImageButton

        var llReplyItem = llReply.getChildAt(llReplyChild).findViewById(R.id.llReply_Item) as LinearLayout
        var llReply_Item_wrap = llReply.getChildAt(llReplyChild).findViewById(R.id.llReply_Item_wrap) as LinearLayout

        if ( replyInfo.rre_no > 0 ) {
            llReplyItem.setPadding(50,0,0,0)
        }

        llReply_Item_wrap.setOnClickListener(View.OnClickListener { v ->
            if ( replyInfo.rre_no == 0 ) {
                re_orgin = replyInfo.re_no.toString()

                selectedReplyItem = llReply.indexOfChild(llReply_Item_wrap)

                var llReply_Item_wrapa = llReply.getChildAt(selectedReplyItem).findViewById(R.id.llReply_Item_wrap) as LinearLayout

                if ( selectedReplyItem == preReplyItem ) {
                    if ( istoggle ) {
                        re_orgin = "NULL"
                        llReply_Item_wrap.setBackgroundColor(Color.WHITE)
                        istoggle = !istoggle
                    } else {
                        llReply_Item_wrap.setBackgroundColor(Color.parseColor("#FFD9FA"))
                        istoggle = !istoggle
                    }
                } else {
                    if ( preReplyItem == -1 ) preReplyItem = selectedReplyItem
                    var llReply_Item_wrapb = llReply.getChildAt(preReplyItem).findViewById(R.id.llReply_Item_wrap) as LinearLayout
                    llReply_Item_wrapb.setBackgroundColor(Color.WHITE)

                    llReply_Item_wrap.setBackgroundColor(Color.parseColor("#FFD9FA"))
                    // 선택한 댓글 인덱스값
                    // Toast.makeText(application, "$selectedReplyItem", Toast.LENGTH_SHORT).show()
                    preReplyItem = selectedReplyItem
                }
            }
        })

        btnReplyOption.setOnClickListener(View.OnClickListener { v ->
            val popup = PopupMenu(applicationContext, v)//v는 클릭된 뷰를 의미

            menuInflater.inflate(R.menu.reply_menu, popup.menu)

            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {

                override fun onMenuItemClick(item: MenuItem): Boolean {
                    when (item.itemId) {
                        R.id.item_replyDelete -> {
                            var re_no = replyInfo.re_no.toString()
                            var ReplyDelete = modBoardProcess().ReplyDelete()
                            ReplyDelete.execute(re_no)
                            ViewReplyLists(brd_no, false)
                            Toast.makeText(application, "댓글이 삭제 되었습니다.", Toast.LENGTH_SHORT).show()
                            // Toast.makeText(application, re_no, Toast.LENGTH_SHORT).show()
                        }
                        R.id.item_replyReport -> Toast.makeText(application, "신고 접수가 완료되었습니다. 감사합니다.", Toast.LENGTH_SHORT).show()
                        else -> {
                        }
                    }
                    return false
                }
            })

            popup.show()//Popup Menu 보이기
        })

        txtID.text = replyInfo.mem_nick
        txtReply.text = replyInfo.re_comment
        txtReplyDate.text = "· " + replyInfo.re_date
    }
}