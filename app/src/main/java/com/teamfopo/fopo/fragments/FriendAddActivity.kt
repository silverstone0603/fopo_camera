package com.teamfopo.fopo.fragments


import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.getSystemService
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.teamfopo.fopo.MainActivity
import com.teamfopo.fopo.R
import com.teamfopo.fopo.module.FOPOService
import com.teamfopo.fopo.module.FriendsVO
import com.teamfopo.fopo.module.UserVO
import com.teamfopo.fopo.module.modFriendProcess
import kotlinx.android.synthetic.main.content_friend_add.*
import kotlinx.android.synthetic.main.content_view.*
import org.w3c.dom.Text


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
//
/**
 * A simple [Fragment] subclass.
 *
 */
class FriendAddActivity : Fragment(), View.OnClickListener {
    lateinit var viewRoot: View
    lateinit var findUser_result: UserVO

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewRoot = inflater.inflate(R.layout.content_friend_add, container, false)
        var btnFriendSearch: Button = viewRoot.findViewById(R.id.btnFriendSearch) as Button
        var btnFriendAdd: Button = viewRoot.findViewById(R.id.btnFriendAdd) as Button

        var txtMyID: TextView = viewRoot.findViewById(R.id.txtMyID) as TextView

        txtMyID.text = FOPOService.dataMemberVO!!.mem_id

        btnFriendSearch.setOnClickListener(this)
        btnFriendAdd.setOnClickListener(this)


        return viewRoot
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnFriendSearch -> {
                val editUserID: EditText = viewRoot.findViewById(R.id.editUserID)
                val strUserID = editUserID.text.toString()

                if ( strUserID.equals("") ) {
                    Toast.makeText(context, "포포친구의 아이디를 입력해주세요!", Toast.LENGTH_SHORT).show()
                    return
                }

                var findUser = modFriendProcess().findUser()
                findUser_result = findUser.execute("$strUserID").get()

                if ( findUser_result.mem_no <= 0 ) {
                    llFoundFriend.visibility = View.INVISIBLE
                    Toast.makeText(context, "사용자를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    return
                }

                if ( findUser_result.status  == 2) {
                    llFoundFriend.visibility = View.INVISIBLE
                    Toast.makeText(context, "자기자신은 팔로우 할수 없습니다.", Toast.LENGTH_SHORT).show()
                    return
                }

                if ( findUser_result.status  == 1) {
                    Toast.makeText(context, "이미 팔로우한 사용자입니다.", Toast.LENGTH_SHORT).show()
                    btnFriendAdd.text = "이미 팔로우한 사용자입니다."
                    btnFriendAdd.isEnabled = false
                } else {
                    btnFriendAdd.text = "친구 추가"
                    btnFriendAdd.isEnabled = true
                }

                Log.d("TESTTESTTEST", "${findUser_result.status}")

                txtFoundNick.setText(findUser_result.mem_nick)
                txtFoundArticleCount.setText("${findUser_result.art_cnt} 개의 게시글")

                llFoundFriend.visibility = View.VISIBLE

                editUserID.text = null

                (activity as MainActivity).CloseKeyBoard()
            }

            R.id.btnFriendAdd -> {
                var addFriend = modFriendProcess().addFriend()
                var str = addFriend.execute("${findUser_result.mem_no}").get()

                if ( str.equals("success")) {
                    (activity as MainActivity).supportActionBar?.title = "친구 목록"
                    (activity as MainActivity).actFopomap.setChildFragment(FriendListActivity())
                    Toast.makeText(context, "친구추가 완료!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
