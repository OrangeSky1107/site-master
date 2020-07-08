package com.wintone.site.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.Face3DAngle;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.LivenessInfo;
import com.arcsoft.face.enums.DetectMode;
import com.blankj.utilcode.util.ThreadUtils;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.wintone.site.R;
import com.wintone.site.SiteApplication;
import com.wintone.site.network.OkHttpUtil;
import com.wintone.site.network.OkhttpClientRequest;
import com.wintone.site.networkmodel.AttendacedofUpload;
import com.wintone.site.utils.AppUtils;
import com.wintone.site.utils.Constant;
import com.wintone.site.utils.SPUtils;
import com.wintone.site.utils.camera.CameraHelper;
import com.wintone.site.utils.camera.CameraListener;
import com.wintone.site.utils.faceutils.ConfigUtil;
import com.wintone.site.utils.faceutils.DrawHelper;

import org.devio.takephoto.uitl.ImageRotateUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;

import androidx.appcompat.app.AppCompatActivity;

/**
 * create by ths on 2020/7/6
 */
public class FaceCheckActivity extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener{

    private static final String TAG = "FaceCheckActivity";
    private CameraHelper cameraHelper;
    private DrawHelper drawHelper;
    private Camera.Size previewSize;
    private Integer rgbCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private FaceEngine faceEngine;
    private int afCode = -1;
    private int processMask = FaceEngine.ASF_AGE | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_GENDER | FaceEngine.ASF_LIVENESS;
    /**
     * 相机预览显示的控件，可为SurfaceView或TextureView
     */
    private View previewView;

    protected KProgressHUD mHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_action);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams attributes = getWindow().getAttributes();
            attributes.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            getWindow().setAttributes(attributes);
        }

        // Activity启动后就锁定为启动时的方向
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        previewView = findViewById(R.id.texture_preview);
        //在布局结束后才做初始化操作
        previewView.getViewTreeObserver().addOnGlobalLayoutListener(this);

        initProgress();
    }

    private void initProgress() {
        mHUD = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDetailsLabel("加载中...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    private void initEngine() {
        faceEngine = new FaceEngine();
        afCode = faceEngine.init(this, DetectMode.ASF_DETECT_MODE_VIDEO, ConfigUtil.getFtOrient(this),
                16, 20, FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_AGE | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_GENDER | FaceEngine.ASF_LIVENESS);
        Log.i(TAG, "initEngine:  init: " + afCode);
        if (afCode != ErrorInfo.MOK) {
            Log.i(TAG, "initEngine:  init: " + afCode);
        }
    }

    private void unInitEngine() {
        if (afCode == 0) {
            afCode = faceEngine.unInit();
            Log.i(TAG, "unInitEngine: " + afCode);
        }
    }

    @Override
    protected void onDestroy() {
        if (cameraHelper != null) {
            cameraHelper.release();
            cameraHelper = null;
        }
        unInitEngine();
        mHUD = null;
        super.onDestroy();
    }

    private boolean checkCurrent = true;

    private void initCamera() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        CameraListener cameraListener = new CameraListener() {
            @Override
            public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
                Log.i(TAG, "onCameraOpened: " + cameraId + "  " + displayOrientation + " " + isMirror);
                previewSize = camera.getParameters().getPreviewSize();
                drawHelper = new DrawHelper(previewSize.width, previewSize.height, previewView.getWidth(), previewView.getHeight(), displayOrientation
                        , cameraId, isMirror, false, false);
            }


            @Override
            public void onPreview(byte[] nv21, Camera camera) {
                List<FaceInfo> faceInfoList = new ArrayList<>();

                int code = faceEngine.detectFaces(nv21, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList);
                if (code == ErrorInfo.MOK && faceInfoList.size() > 0) {
                    code = faceEngine.process(nv21, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList, processMask);
                    if (code != ErrorInfo.MOK) {
                        return;
                    }
                } else {
                    return;
                }

                List<AgeInfo> ageInfoList = new ArrayList<>();
                List<GenderInfo> genderInfoList = new ArrayList<>();
                List<Face3DAngle> face3DAngleList = new ArrayList<>();
                List<LivenessInfo> faceLivenessInfoList = new ArrayList<>();
                int ageCode = faceEngine.getAge(ageInfoList);
                int genderCode = faceEngine.getGender(genderInfoList);
                int face3DAngleCode = faceEngine.getFace3DAngle(face3DAngleList);
                int livenessCode = faceEngine.getLiveness(faceLivenessInfoList);

                // 有其中一个的错误码不为ErrorInfo.MOK，return
                if ((ageCode | genderCode | face3DAngleCode | livenessCode) != ErrorInfo.MOK) {
                    return;
                }

                if(faceInfoList.size() > 0){
                    if(checkCurrent){
                        checkCurrent = false;
                        mHUD.show();
                        ExecutorService service = ThreadUtils.getCachedPool();
                        service.submit(new Runnable() {
                            @Override
                            public void run() {
                                String imgPath = saveCurrentPreView(nv21);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        faceAttendanceAction(imgPath);
                                    }
                                });
                            }
                        });
                    }
                }
            }

            @Override
            public void onCameraClosed() {
                Log.i(TAG, "onCameraClosed: ");
            }

            @Override
            public void onCameraError(Exception e) {
                Log.i(TAG, "onCameraError: " + e.getMessage());
            }

            @Override
            public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {
                if (drawHelper != null) {
                    drawHelper.setCameraDisplayOrientation(displayOrientation);
                }
                Log.i(TAG, "onCameraConfigurationChanged: " + cameraID + "  " + displayOrientation);
            }
        };
        cameraHelper = new CameraHelper.Builder()
                .previewViewSize(new Point(previewView.getMeasuredWidth(), previewView.getMeasuredHeight()))
                .rotation(getWindowManager().getDefaultDisplay().getRotation())
                .specificCameraId(rgbCameraId != null ? rgbCameraId : Camera.CameraInfo.CAMERA_FACING_FRONT)
                .isMirror(false)
                .previewOn(previewView)
                .cameraListener(cameraListener)
                .build();
        cameraHelper.init();
        cameraHelper.start();
    }

    private String saveCurrentPreView(byte[] data){
        Camera.Size previewSize = cameraHelper.previewSize;
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        YuvImage yuvimage = new YuvImage(
                data,
                ImageFormat.NV21,
                previewSize.width,
                previewSize.height,
                null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 100, baos);// 80--JPG图片的质量[0-100],100最高
        byte[] rawImage = baos.toByteArray();
        Log.i("ScanCamera","look at byte size = " + rawImage.length);
        //将rawImage转换成bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length, options);
        Bitmap rotationMap = null;
        Integer integer = (Integer) SPUtils.getShare(SiteApplication.getInstance(), Constant.CAMERA_SWITCH,1);
        if(integer == 1){
            rotationMap = ImageRotateUtil.of().rotateBitmapByDegree(bitmap,-90);
        }else{
            rotationMap = ImageRotateUtil.of().rotateBitmapByDegree(bitmap,-270);
        }
        long systemTime = System.currentTimeMillis();
        String imageName = systemTime + "-face";
        Log.i("ScanCamera","look at face name = " + imageName);
        return saveToLocal(rotationMap,imageName);
    }

    private String saveToLocal(Bitmap bitmap, String bitName) {
        String IMAGE_PATH = "/storage/emulated/0/Android/data/com.tomcat.ocr.idcard/cache/";
        String path = IMAGE_PATH + bitName + ".jpg";
        File file = new File(IMAGE_PATH + bitName + ".jpg");
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
                return path;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("FaceActionActivity","recycler bitmap 1");
            bitmap.recycle();
            Log.i("FaceActionActivity","recycler bitmap 2");
        }
        return path;
    }

    private void faceAttendanceAction(String pathName){
        String projectId = (String) SPUtils.getShare(this,Constant.PROJECT_ID,"");
        String constructionId = (String) SPUtils.getShare(this,Constant.CONSTRUCTION_ID,"");
        String way = "1";
        String deviceType = "2";
        String deviceSn = AppUtils.getPhoneSign();

        AttendacedofUpload attendacedofUpload = new AttendacedofUpload();
        attendacedofUpload.setProjectId(projectId);
        attendacedofUpload.setConstructionId(constructionId);
        attendacedofUpload.setDirection(getIntent().getStringExtra("commute"));
        attendacedofUpload.setDeviceType(deviceType);
        attendacedofUpload.setDeviceSn(deviceSn);
        attendacedofUpload.setWay(way);

        String url = Constant.BASE_URL + Constant.ATTENDANCE_RECORD_URL;

        String token = (String)SPUtils.getShare(this,Constant.USER_TOKEN,"");

        OkHttpUtil.getInstance().uploadAndAttendance(attendacedofUpload,url,token,pathName,new OkhttpClientRequest() {
            @Override
            public void responseFailure(String errorMessage) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showPhoneDialog("打卡出现错误:"+errorMessage);
                        Log.i(TAG,"response failure = " + errorMessage);
                        mHUD.dismiss();
                    }
                });
            }

            @Override
            public void responseSuccess(String successMessage) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        HashMap map = JSON.parseObject(successMessage,HashMap.class);
                        Integer code = (Integer) map.get("code");
                        String message = (String) map.get("message");
                        if(code == 1000){
                            showPhoneDialog("打卡成功!");
                        }else{
                            showPhoneDialog(message);
                        }
                        mHUD.dismiss();
                    }
                });
            }
        });
    }

    private void showPhoneDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FaceCheckActivity.this);
        builder.setTitle("考勤打卡");
        builder.setMessage(message);

        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

        builder.setPositiveButton("确认",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

        builder.setCancelable(false);

        builder.show();
    }

    /**
     * 在{@link #previewView}第一次布局完成后，去除该监听，并且进行引擎和相机的初始化
     */
    @Override
    public void onGlobalLayout() {
        previewView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        initEngine();
        initCamera();
    }
}
