package com.teamfopo.fopo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.teamfopo.fopo.module.FOPOService
import com.teamfopo.fopo.module.modBoardProcess
import com.teamfopo.fopo.module.modImageResizeUtils
import kotlinx.android.synthetic.main.activity_write.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class WriteActivity : AppCompatActivity() {

    private var isCamera: Boolean? = true
    private var isPermission: Boolean? = true
    private var tempFile: File? = null
    private var image_bitmapToString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        tedPermission()

        //뒤로가기 클릭
        val backClick = findViewById(R.id.imageButton) as ImageButton

        backClick.setOnClickListener {
            //startActivity(Intent(this,FopozoneActivity::class.java))
            finish()
        }

        // 공유 버튼
        val shareClick  = findViewById(R.id.Btn_Share) as ImageButton

        shareClick.setOnClickListener {
            goToImageUpload()
        }

        //사진추가 버튼
        val addClick  = findViewById(R.id.imageBtn_add) as ImageButton

        addClick.setOnClickListener {

            if (isPermission!!)
                goToAlbum()
        }
    }

    private fun goToImageUpload() {
        var getBitmapImageToString = image_bitmapToString

        var str_content = edit_oneLine.text.toString()

        var getImageUpload = modBoardProcess().Write()
        var uploadResult = getImageUpload.execute(FOPOService.dataMemberVO!!.mem_no,"3","$str_content", "$getBitmapImageToString").get()

        var brd_no = Integer.parseInt(uploadResult.trim())

        val i = Intent(this@WriteActivity, ViewActivity::class.java)
        i.putExtra("m_select", brd_no)
        startActivityForResult(i, 1)
        finish()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show()

            if (tempFile != null) {
                if (tempFile!!.exists()) {
                    if (tempFile!!.delete()) {
                        Log.e(TAG, tempFile!!.absolutePath + " 삭제 성공")
                        tempFile = null
                    }
                }
            }
            return
        }

        if (requestCode == PICK_FROM_ALBUM) {

            val photoUri = data!!.data
            Log.d(TAG, "PICK_FROM_ALBUM photoUri : " + photoUri!!)

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
                Log.d(TAG, "tempFile Uri : " + Uri.fromFile(tempFile))
                setImage()
            } finally {
                cursor?.close()
            }
        } else if (requestCode == PICK_FROM_CAMERA) {
            setImage()
        }
    }

    /**
     * 앨범에서 이미지 가져오기
     */
    private fun goToAlbum() {
        isCamera = true

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, PICK_FROM_ALBUM)
    }

/*
    //카메라에서 이미지 가져오기
    private fun takePhoto() {

        isCamera = true

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try {
            tempFile = createImageFile()
        } catch (e: IOException) {
            Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            finish()
            e.printStackTrace()
        }

        if (tempFile != null) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                val photoUri = FileProvider.getUriForFile(this,
                    "kr.ac.yju.com.slidemenutest.provider", tempFile!!)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(intent, PICK_FROM_CAMERA)

            } else {

                val photoUri = Uri.fromFile(tempFile)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(intent, PICK_FROM_CAMERA)

            }
        }
    }
*/


    /**
     * 폴더 및 파일 만들기
     */


    @Throws(IOException::class)
    private fun createImageFile(): File {

        // 이미지 파일 이름 ( blackJin_{시간}_ )
        val timeStamp = SimpleDateFormat("HHmmss").format(Date())
        val imageFileName = "blackJin_" + timeStamp + "_"

        // 이미지가 저장될 폴더 이름 ( blackJin )
        val storageDir = File(Environment.getExternalStorageDirectory().toString() + "/blackJin/")
        if (!storageDir.exists()) storageDir.mkdirs()

        // 파일 생성
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        Log.d(TAG, "createImageFile : " + image.absolutePath)

        return image
    }



    /**
     * tempFile 을 bitmap 으로 변환 후 ImageView 에 설정한다.
     */
    private fun setImage() {

        val imageView = findViewById<ImageView>(R.id.img_show)

        modImageResizeUtils.resizeFile(tempFile!!, tempFile!!, 1280, isCamera!!)

        val options = BitmapFactory.Options()
        val originalBm = BitmapFactory.decodeFile(tempFile!!.absolutePath, options)
        //Log.d(TAG, "setImage : " + tempFile!!.absolutePath)

        imageView.setImageBitmap(originalBm)
        imageBtn_add.visibility = View.INVISIBLE    //이미지 가져온후 사진 추가버튼 숨기기



        image_bitmapToString = encode(originalBm)
        //println("TESTTEST" + image_bitmapToString)

        /**
         * tempFile 사용 후 null 처리를 해줘야 합니다.
         * (resultCode != RESULT_OK) 일 때 tempFile 을 삭제하기 때문에
         * 기존에 데이터가 남아 있게 되면 원치 않은 삭제가 이뤄집니다.
         */
        tempFile = null

    }

    /**
     * 권한 설정
     */
    private fun tedPermission() {

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

        TedPermission.with(this)
            .setPermissionListener(permissionListener)
            .setRationaleMessage(resources.getString(R.string.permission_2))
            .setDeniedMessage(resources.getString(R.string.permission_1))
            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
            .check()

    }

    companion object {

        private val TAG = "blackjin"

        private val PICK_FROM_ALBUM = 1
        private val PICK_FROM_CAMERA = 2

        fun encode(image: Bitmap): String {
            val baos = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val b = baos.toByteArray()

            return Base64.encodeToString(b, Base64.DEFAULT)
        }
    }

}