package com.teamfopo.fopo.module;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.*;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.ArraySet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.*;
import java.util.Calendar;

@SuppressLint("ViewConstructor")
public class modCameraProcess extends SurfaceView implements SurfaceHolder.Callback {
    private final String TAG = "modCameraProcess";

    private SurfaceHolder mHolder;

    private int frameId;
    private int mCameraID;

    private Camera mCamera;
    private Camera.CameraInfo mCameraInfo;

    // 사진 촬영시 EXIF 태그 정보
    private Boolean blEXIF = false;
    private String strEXIF_Model = "Android Device";
    private String strEXIF_Software = "FOPO by FOPO TEAM";
    private String strEXIF_Orientation;
    private Double strEXIF_Latitude;
    private Double strEXIF_Longitude;

    private int mDisplayOrientation;

    String mRootPath;
    static final String PICFOLDER = "FOPO";
    boolean blIsTakePhoto;

    // 포커싱 성공하면 촬영 허가
    AutoFocusCallback mAutoFocus = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            blIsTakePhoto = success;
        }
    };

    AutoFocusMoveCallback mAutoFocusMove = new AutoFocusMoveCallback() {
        @Override
        public void onAutoFocusMoving(boolean success, Camera camera) {
            blIsTakePhoto = success;
        }
    };

    public modCameraProcess(Context context, Camera camera , int cameraId) {
        super(context);
        Log.d(TAG, "MyCameraPreview cameraId : " + cameraId);

        // init frameId zero
        frameId = 0;

        // 0    ->     CAMERA_FACING_BACK
        // 1    ->     CAMERA_FACING_FRONT
        mCameraID = cameraId;

        try {
            // attempt to get a Camera instance
            mCamera = Camera.open(mCameraID);
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.e(TAG, "Camera is not available");
        }


        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // get display orientation
        mDisplayOrientation = ((Activity)context).getWindowManager().getDefaultDisplay().getRotation();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");

        // retrieve camera's info.
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraID, cameraInfo);

        mCameraInfo = cameraInfo;

        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.set("orientation", "portrait");
            mCamera.setParameters(parameters);
            mCamera.setPreviewDisplay(holder);
            int m_resWidth;
            int m_resHeight;

            m_resWidth = mCamera.getParameters().getPictureSize().width;
            m_resHeight = mCamera.getParameters().getPictureSize().height;

            //아래 숫자를 변경하여 자신이 원하는 해상도로 변경한다

            m_resWidth = 1280;
            m_resHeight = 720;

            parameters.setPictureSize(m_resWidth, m_resHeight);
            mCamera.setParameters(parameters);
            mCamera.startPreview();
            mCamera.autoFocus(mAutoFocus);
            mCamera.setAutoFocusMoveCallback(mAutoFocusMove);

            mRootPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/" + PICFOLDER;
            File fRoot = new File(mRootPath);
            if (fRoot.exists() == false) {
                if (fRoot.mkdir() == false) {
                    Log.d(TAG,"사진 폴더 생성에 실패 했습니다.");
                }
            }

        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");
        // empty. Take care of releasing the Camera preview in your activity.

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Log.d(TAG, "surfaceChanged");
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            Log.e(TAG, "preview surface does not exist");
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
            Log.d(TAG, "Preview stopped.");
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }



        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        int orientation = calculatePreviewOrientation(mCameraInfo, mDisplayOrientation);
        mCamera.setDisplayOrientation(orientation);

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    /**
     * 안드로이드 디바이스 방향에 맞는 카메라 프리뷰를 화면에 보여주기 위해 계산합니다.
     */
    public int calculatePreviewOrientation(Camera.CameraInfo info, int rotation) {
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        return result;
    }

    /**
     *  이미지 캡처
     */
    public boolean takePicture(){
        try{
            mCamera.autoFocus(mAutoFocus);
            if(blIsTakePhoto == true) {
                setFrameId(0);
                mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
                return true;
            }else {
                return false;
            }
        }catch(Exception e){
            return false;
        }
    }


    /**
     *  이미지에 EXIF 정보 추가
     */
    public String[] getEXIFInfo(String filePath) throws IOException {
        ExifInterface exif = new ExifInterface(filePath);
        String[] arrDatas = {
                exif.getAttribute(ExifInterface.TAG_MODEL),
                exif.getAttribute(ExifInterface.TAG_SOFTWARE),
                exif.getAttribute(ExifInterface.TAG_ORIENTATION),
                exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE),
                exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)
        };
        return arrDatas;
    }

    /**
     *  클래스에 EXIF 정보 추가
     */
    public void setEXIF(Double latitude, Double longitude, String orientation) {
        this.strEXIF_Latitude = latitude;
        this.strEXIF_Longitude = longitude;
        this.strEXIF_Orientation = orientation;
    }

    /**
     *  이미지에 EXIF 정보 추가
     */
    public void setEXIFInfo(String filePath, Double latitude, Double longitude, String orientation) throws IOException {
        ExifInterface exif = new ExifInterface(filePath);
        exif.setAttribute(ExifInterface.TAG_MODEL, strEXIF_Model);
        exif.setAttribute(ExifInterface.TAG_SOFTWARE, strEXIF_Software);
        exif.setAttribute(ExifInterface.TAG_ORIENTATION, orientation);
        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, latitude.toString());
        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, longitude.toString());
        exif.saveAttributes();
    }

    /**
     *  이미지 캡처 시 배경 선택
     */
    public void setFrameId(int frameId) {
        this.frameId = frameId;
    }

    /**
     * 이미지 저장을 위한 콜백 함수
     */
    private Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {

        }
    };

    private Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            /*
            Calendar calendar = Calendar.getInstance();
            String FileName = String.format("FOPO-%02d%02d%02d-%02d%02d%02d.jpg",
                    calendar.get(Calendar.YEAR) % 100, calendar.get(Calendar.MONTH)+1,
                    calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
            String path = mRootPath + "/" + FileName;

            File file = new File(path);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(data);
                fos.flush();
                fos.close();
            } catch (Exception e) {
                return;
            }

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.parse("file://" + path);
            intent.setData(uri);
            sendBroadcast(intent);

            camera.startPreview();
            */
        }
    };


    private Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {

            //이미지의 너비와 높이 결정
            int w = camera.getParameters().getPictureSize().width;
            int h = camera.getParameters().getPictureSize().height;
            int orientation = calculatePreviewOrientation(mCameraInfo, mDisplayOrientation);

            //Log.d("MyTag","이미지 캡처 시 -> orientation : " + orientation);

            //byte array 를 bitmap 으로 변환
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeByteArray( data, 0, data.length, options);

            //이미지를 디바이스 방향으로 회전
            Matrix matrix = new Matrix();

            matrix.postRotate(orientation);
            bitmap =  Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);


            /**
             * frameId 가 있는 경우 해당 이미지를 합성해 준다.
             */
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            if(0 != frameId) {

                Bitmap bp = BitmapFactory.decodeResource(getContext().getResources(),frameId);
                Bitmap resizeBp = Bitmap.createScaledBitmap(bp, width, height, true);

                Canvas canvas = new Canvas(bitmap);
                canvas.drawBitmap(resizeBp, 0f, 0f,null);

            }

            //bitmap 을  byte array 로 변환
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] currentData = stream.toByteArray();

            //파일로 저장
            new modCameraProcess.SaveImageTask().execute(currentData);

        }
    };

    /**
     * 이미지 저장을 위한 콜백 클레스
     */
    @SuppressLint("StaticFieldLeak")
    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground(byte[]... data) {
            FileOutputStream outStream = null;

            try {
                Calendar calendar = Calendar.getInstance();
                String FileName = String.format("FOPO-%02d%02d%02d-%02d%02d%02d.jpg",
                        calendar.get(Calendar.YEAR) % 100, calendar.get(Calendar.MONTH)+1,
                        calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));

                File outputFile = new File(mRootPath, FileName);

                outStream = new FileOutputStream(outputFile);
                outStream.write(data[0]);
                outStream.flush();
                outStream.close();

                Log.d(TAG, "사진 촬영(JPEG) - Bytes: " + data.length + " to "
                        + outputFile.getAbsolutePath());


                // EXIF 정보 추가
                setEXIFInfo(outputFile.getPath(), strEXIF_Latitude, strEXIF_Longitude, strEXIF_Orientation);
                Log.d(TAG, "EXIF 수정할 사진 경로 :"+outputFile.getPath());

                mCamera.startPreview();

                // 갤러리에 반영
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(Uri.fromFile(outputFile));
                getContext().sendBroadcast(mediaScanIntent);

                try {
                    mCamera.setPreviewDisplay(mHolder);
                    mCamera.startPreview();
                    Log.d(TAG, "카메라 프리뷰가 시작되었습니다.");
                } catch (Exception e) {
                    Log.d(TAG, "카메라 프리뷰를 시작하는데 실패했습니다 : " + e.getMessage());
                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

    }


}
