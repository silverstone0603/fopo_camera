package com.teamfopo.fopo

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.Toast
import com.teamfopo.fopo.module.FOPOService
import com.teamfopo.fopo.module.modBoardProcess
import com.teamfopo.fopo.module.modImageResizeUtils
import com.teamfopo.fopo.module.modMemProcess
import kotlinx.android.synthetic.main.activity_my_info.*
import kotlinx.android.synthetic.main.content_view.*
import java.io.File

class MyInfoActivity : AppCompatActivity() {
    private var isCamera: Boolean? = true
    private var tempFile: File? = null

    private var Profile: String? = null

    companion object {

        private val TAG = "blackjin"

        private val PICK_FROM_ALBUM = 1
        private val PICK_FROM_CAMERA = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_info)

        val mem_id = FOPOService.dataMemberVO!!.mem_id
        val mem_no = FOPOService.dataMemberVO!!.mem_no

        var getMyInfo = modMemProcess().myInfo()
        var myInfo = getMyInfo.execute("$mem_no").get()

        editMyNick.setText(myInfo.mem_nick)
        txtEmail.text = myInfo.mem_email
        editPhone.setText(myInfo.mem_phone)

        txtMyID.text = "$mem_id"

        if ( myInfo.mem_picfile != 0 ) {
            var getSelectImage = modBoardProcess().GetImage()
            var bitmap: Bitmap
            bitmap = getSelectImage.execute(myInfo.mem_picfile).get()

            imgProfile.setImageBitmap(bitmap)  //이미지 뿌려주기
            Profile = WriteActivity.encode(bitmap)
        }

        btnChange.setOnClickListener {
            val mem_nick = editMyNick.text.toString()
            val mem_newpw = editChangePassword.text.toString()
            val mem_newpwCheck = editChangePasswordCheck.text.toString()
            val mem_phone = editPhone.text.toString()
            val mem_no = FOPOService.dataMemberVO!!.mem_no

            if ( mem_newpw.equals(mem_newpwCheck) ) {
                var InfoModify = modMemProcess().infomodify()
                var result = InfoModify.execute("$mem_no", "$Profile", "$mem_nick","$mem_newpw","$mem_phone").get()

                if ( result.status.equals("success") ) {
                    Toast.makeText(this, "내정보 변경 성공!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "내정보 변경 실패", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "입력한 비밀번호가 같지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        imgProfile.setOnClickListener {v->
            val popup = PopupMenu(applicationContext, v)//v는 클릭된 뷰를 의미
            var popupMenu = popup.menu

            menuInflater.inflate(R.menu.profile_menu, popup.menu)

            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem): Boolean {
                    when (item.itemId) {
                        R.id.itemProfileChange -> {
                            val intent = Intent(Intent.ACTION_PICK)
                            intent.type = MediaStore.Images.Media.CONTENT_TYPE
                            startActivityForResult(intent, PICK_FROM_ALBUM)
                        }
                        R.id.itemProfileDelete -> {
                            Profile = null
                            imgProfile.setImageResource(R.drawable.ic_launcher_foreground)
                        }
                        else -> {
                        }
                    }
                    return false
                }
            })

            popup.show()//Popup Menu 보이기
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show()

            if (tempFile != null) {
                if (tempFile!!.exists()) {
                    if (tempFile!!.delete()) {
                        tempFile = null
                    }
                }
            }
            return
        }

        if (requestCode == PICK_FROM_ALBUM) {
            val photoUri = data!!.data

            var cursor: Cursor? = null

            try {
                /*
                 *  Uri 스키마를
                 *  content:/// 에서 file:/// 로  변경한다.
                 */
                val proj = arrayOf(MediaStore.Images.Media.DATA)

                assert(photoUri != null)
                cursor = contentResolver.query(photoUri, proj, null, null, null)
                assert(cursor != null)

                val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                tempFile = File(cursor.getString(column_index))

                modImageResizeUtils.resizeFile(tempFile!!, tempFile!!, 320, isCamera!!)

                val options = BitmapFactory.Options()
                val originalBm = BitmapFactory.decodeFile(tempFile!!.absolutePath, options)

                imgProfile.setImageBitmap(originalBm)

                Profile = WriteActivity.encode(originalBm)
            } finally {
                cursor?.close()
            }

        }
    }
}
