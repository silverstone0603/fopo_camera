package com.teamfopo.fopo

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.teamfopo.fopo.module.modDBMS
import java.text.SimpleDateFormat
import java.util.*


class SplashActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback  {
    private val PERMISSIONS_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        val dbms = modDBMS(this)
        var dataMemberVO = dbms.getMember()

        super.onCreate(savedInstanceState)

        /* (날짜계산이 정상적으로 되는지?) 날짜바꿔주기 귀찮아서 몇일지나면 테스트해봄 ㅎㅎ
        if (!dataMemberVO.lastlogin.equals("")) {
            System.out.println("가나다라: " + dataMemberVO.lastlogin)
            System.out.println("TESTTEST: " + calcDaysBetweenNowAndLastDate(dataMemberVO.lastlogin))
        }
        */

        if(getAppsPermission() == false){
            Log.d("권한 체크", "권한을 요청하기 위해서 사용자에게 Dialog를 띄웁니다")
            val dialogClickListener = DialogInterface.OnClickListener{_,which ->
                when(which){
                    DialogInterface.BUTTON_POSITIVE -> setAppsPermission()
                    DialogInterface.BUTTON_NEGATIVE -> finish()
                }
            }
            showAlertDialog("권한 요청","FOPO를 정상적으로 이용하기 위해서는 요청하는 모든 권한을 활성화 해주세요.","확인","취소",dialogClickListener)
        }else {
            if (dataMemberVO.token.equals("") || calcDaysBetweenNowAndLastDate(dataMemberVO.lastlogin) > 5) {
                // 기존의 sqlite에 컬럼이있을시 삭제 필요.
                var intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
            } else {
                // sess_lastdate, lastdate 업데이트 필요.
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

            finish()
        }
    }
    private fun setAppsPermission(){
        var arrPer: Array<String> = arrayOf(
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION)

        ActivityCompat.requestPermissions( this, arrPer, PERMISSIONS_REQUEST_CODE)
    }
    private fun getAppsPermission(): Boolean{
        var isGrantPermission: Int = 0

        val arrPer: Array<Int> = arrayOf(0, 0, 0, 0, 0, 0)
        var tmpPerInternet = android.Manifest.permission.INTERNET
        var tmpPerCamera = android.Manifest.permission.CAMERA
        var tmpPerReadIO = android.Manifest.permission.READ_EXTERNAL_STORAGE
        var tmpPerWriteIO = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        var tmpPerLocation1 = android.Manifest.permission.ACCESS_FINE_LOCATION
        var tmpPerLocation2 = android.Manifest.permission.ACCESS_COARSE_LOCATION

        if (Build.VERSION.SDK_INT >= 23) {
            // 6.0 이상일때만 권한 체크 하도록 설정
            isGrantPermission = 0

            if (checkSelfPermission(tmpPerInternet) == PackageManager.PERMISSION_GRANTED) arrPer[0]=1
            else Log.d("권한 체크", "인터넷 권한 비활성화")

            if (checkSelfPermission(tmpPerCamera) == PackageManager.PERMISSION_GRANTED) arrPer[1]=1
            else Log.d("권한 체크", "카메라 권한 비활성화")

            if (checkSelfPermission(tmpPerReadIO) == PackageManager.PERMISSION_GRANTED) arrPer[2]=1
            else Log.d("권한 체크", "파일 읽기 권한 비활성화")

            if (checkSelfPermission(tmpPerWriteIO) == PackageManager.PERMISSION_GRANTED) arrPer[3]=1
            else Log.d("권한 체크", "파일 쓰기 권한 비활성화")

            if (checkSelfPermission(tmpPerLocation1) == PackageManager.PERMISSION_GRANTED) arrPer[4]=1
            else Log.d("권한 체크", "위치 찾기 권한 비활성화")

            if (checkSelfPermission(tmpPerLocation2) == PackageManager.PERMISSION_GRANTED) arrPer[5]=1
            else Log.d("권한 체크", "세부 위치 찾기 권한 비활성화")

            for(i in arrPer){
                if(i>=1) isGrantPermission++
            }

            if(isGrantPermission >= 6) return true
            else return false
        }else{
            Log.d("권한 체크", "모든 권한에 대해서 허용 되었습니다.")
            return true
        }
    }

    private fun showAlertDialog(title:String, message:String, textYes: String, textNo: String, clickListener: DialogInterface.OnClickListener){
        lateinit var dialog: AlertDialog
        val Builder = AlertDialog.Builder(this)

        Builder.setTitle(title)
        Builder.setMessage(message)

        Builder.setPositiveButton(textYes, clickListener)
        if(textNo.equals("") == false) Builder.setNegativeButton(textNo, clickListener)

        dialog = Builder.create() // AlertDialog 표시
        dialog.show() // Dialog 표시
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var arrPer: Array<String> = arrayOf(
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION)

        if (requestCode === PERMISSIONS_REQUEST_CODE && grantResults.size === arrPer.size) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            var check_result = true
            // 모든 퍼미션을 허용했는지 체크합니다.
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false
                    break
                }
            }

            if (check_result) {
                var intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, arrPer[0]) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, arrPer[1]) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, arrPer[2]) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, arrPer[3]) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, arrPer[4]) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, arrPer[5])
                        ) {

                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    val dialogClickListener = DialogInterface.OnClickListener{_,which ->
                        when(which){
                            DialogInterface.BUTTON_POSITIVE -> finish()
                        }
                    }
                    showAlertDialog("권한 요청 실패","FOPO 실행에 필요한 권한 요청이 거부되었습니다. 다시 실행하여 권한을 허용해주세요.","확인","",dialogClickListener)
                    return
                } else {
                    // “다시 묻지 않음”을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    val dialogClickListener = DialogInterface.OnClickListener{_,which ->
                        when(which){
                            DialogInterface.BUTTON_POSITIVE -> finish()
                        }
                    }
                    showAlertDialog("권한 요청 실패","FOPO 실행에 필요한 권한 요청이 거부되었습니다. 설정(앱 정보)에서 권한을 허용한 후 다시 실행해주세요.","확인","",dialogClickListener)
                    return
                }
            }
        }
    }

    private fun calcDaysBetweenNowAndLastDate(lastdate: String): Long { // 현재날짜와 인자값의 날짜의 차이 일수 반환.
        var formatter = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA)
        var currentDate = Date()

        var lastLoginDate = formatter.parse(lastdate)

        val calDate = currentDate.getTime() - lastLoginDate.getTime()
        var calDateDays = calDate / ( 24 * 60 * 60 * 1000 )

        calDateDays = Math.abs(calDateDays)

        return calDateDays
    }
}