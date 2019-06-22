package com.teamfopo.fopo.module

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Toast

class modGPSCore(activity: Activity, context: Context) : LocationListener, Runnable {
    /**
    Location 제공자에서 정보를 얻어오기(GPS)
    1. Location을 사용하기 위한 권한을 얻어와야한다 AndroidManifest.xml
        ACCESS_FINE_LOCATION : NETWORK_PROVIDER, GPS_PROVIDER
        ACCESS_COARSE_LOCATION : NETWORK_PROVIDER
    2. LocationManager 를 통해서 원하는 제공자의 리스너 등록
    3. GPS 는 에뮬레이터에서는 기본적으로 동작하지 않는다
    4. 실내에서는 GPS_PROVIDER 를 요청해도 응답이 없다.  특별한 처리를 안하면 아무리 시간이 지나도
       응답이 없다.
       해결방법은
        ① 타이머를 설정하여 GPS_PROVIDER 에서 일정시간 응답이 없는 경우 NETWORK_PROVIDER로 전환
        ② 혹은, 둘다 한꺼번헤 호출하여 들어오는 값을 사용하는 방식.
    */
    private lateinit var mLocationListener: LocationListener
    private lateinit var mLocationManager: LocationManager
    private lateinit var mActivity: Activity
    private lateinit var mContext: Context

    private var mStatus: String = "" // 상태
    private var mProvider: String = "" // 위치 제공자
    private var mLat: Double = 0.0 // 위도
    private var mLng: Double = 0.0 // 경도
    private var mAlt: Double = 0.0 // 고도
    private var mAccuracy: Float = 0.0f // 정확도
    private var mHeight: Float = 0.0f // 높이

    private var blGPS: Boolean = false

    private final val REQUEST_PERMISSION_LOCATION = 2

    init{
        mActivity = activity
        mContext = context

        // LocationManager 객체를 얻어온다
        mLocationManager = mContext.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        run()
    }

    fun getStatus(): String{
        return mStatus
    }

    fun getLatitude(): Double{
        return mLat
    }
    fun getLongitude(): Double{
        return mLng
    }
    fun getAltitude(): Double{
        return mAlt
    }
    fun getAccuracy(): Float{
        return mAccuracy
    }
    fun getProvider(): String{
        return mProvider
    }

    fun setStatus(str: String){
        mStatus = str
    }

    override fun run(){
        try {
            if (blGPS) {
                setStatus("사용")
                // GPS 제공자의 정보가 바뀌면 콜백하도록 리스너 등록하기~!!!
                mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                    100.toLong(), // 통지사이의 최소 시간간격 (miliSecond)
                    1.toFloat(), // 통지사이의 최소 변경거리 (m)
                    mLocationListener
                )
                mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                    100.toLong(), // 통지사이의 최소 시간간격 (miliSecond)
                    1.toFloat(), // 통지사이의 최소 변경거리 (m)
                    mLocationListener
                )
            } else {
                setStatus("미사용")
                mLocationManager.removeUpdates(mLocationListener)  //  미수신할때는 반드시 자원해체를 해주어야 한다.
            }
        } catch (ex: SecurityException) {
        }
        Log.d("GPSCore","현재 GPS 상태 : "+getStatus())
        }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        // 변경시
        Log.d("GPSCore", "onStatusChanged, provider:$provider, status:$status ,Bundle:$extras")
    }

    override fun onProviderEnabled(provider: String?) {
        // Enabled시
        Log.d("GPSCore", "onProviderEnabled, provider:$provider")
    }

    override fun onProviderDisabled(provider: String?) {
        // Disabled시
        Log.d("GPSCore", "onProviderDisabled, provider:$provider")
    }

    override fun onLocationChanged(location: Location?) {
        // 여기서 위치값이 갱신되면 이벤트가 발생한다.
        // 값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

        Log.d("GPSCore", "onLocationChanged, location:$location")
        mLat = location!!.latitude // 위도
        mLng = location!!.longitude // 경도
        mAlt = location!!.altitude // 고도
        mAccuracy = location!!.accuracy // 정확도
        mProvider = location!!.provider // 위치제공자
        // GPS 위치제공자에 의한 위치변화. 오차범위가 좁다.
        // Network 위치제공자에 의한 위치변화
        // Network 위치는 GPS에 비해 정확도가 많이 떨어진다.
    }

    override fun toString(): String {
        var tmpString = ("위치정보 : " + mProvider + "\n위도 : " + mLat + "\n경도 : " + mLng
                + "\n고도 : " + mAlt + "\n정확도 : " + mProvider)
        return super.toString()
    }

    fun checkPermissionForLocation(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED){
                true
            }else{
                // Show the permission request
                ActivityCompat.requestPermissions(mActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSION_LOCATION)
                false
            }
        } else {
            true
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have to add startlocationUpdate() method later instead of Toast
                Toast.makeText(mContext,"Permission granted",Toast.LENGTH_SHORT).show()
            }
        }
    }

} // end of class