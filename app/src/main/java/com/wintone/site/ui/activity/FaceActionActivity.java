package com.wintone.site.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.enums.DetectMode;
import com.wintone.site.R;
import com.wintone.site.network.OkHttpUtil;
import com.wintone.site.network.OkhttpClientRequest;
import com.wintone.site.networkmodel.AttendacedofUpload;
import com.wintone.site.ui.base.activity.BaseActivity;
import com.wintone.site.utils.AppUtils;
import com.wintone.site.utils.Constant;
import com.wintone.site.utils.SPUtils;
import com.wintone.site.utils.camera.CameraHelper;
import com.wintone.site.utils.camera.CameraListener;
import com.wintone.site.utils.faceutils.ConfigUtil;
import com.wintone.site.utils.faceutils.DrawHelper;
import com.wintone.site.utils.faceutils.RecognizeColor;
import com.wintone.site.widget.face.DrawInfo;
import com.wintone.site.widget.face.FaceRectView;

import org.devio.takephoto.uitl.ImageRotateUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class FaceActionActivity extends BaseActivity implements ViewTreeObserver.OnGlobalLayoutListener{

    private CameraHelper cameraHelper;
    private DrawHelper drawHelper;

    private Camera.Size previewSize;
    private Integer rgbCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private int processMask = FaceEngine.ASF_AGE | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_GENDER | FaceEngine.ASF_LIVENESS;

    private FaceEngine faceVideoEngine;

    private int faceVideoEngineCOde = -1;

    private String direction = "";

    private static final String IMAGE_PATH = "/storage/emulated/0/Android/data/com.tomcat.ocr.idcard/cache/";

    @BindView(R.id.face_rect_view)  FaceRectView faceRectView;
    @BindView(R.id.texture_preview) View previewView;

    @Override
    protected int getContentView() {
        return R.layout.activity_face_action;
    }

    @Override
    protected void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams attributes = getWindow().getAttributes();
            attributes.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            getWindow().setAttributes(attributes);
        }

        getIntentData();

        // Activity启动后就锁定为启动时的方向
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        previewView.getViewTreeObserver().addOnGlobalLayoutListener(this);

        mHUD.setDetailsLabel("考勤打卡...");
    }

    private void getIntentData(){
        Intent intent = getIntent();
        direction = intent.getStringExtra("commute");
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onGlobalLayout() {
        initFaceEngine();
        initCamera();
    }
    private void initFaceEngine(){
        faceVideoEngine = new FaceEngine();
        faceVideoEngineCOde = faceVideoEngine.init(this, DetectMode.ASF_DETECT_MODE_VIDEO, ConfigUtil.getFtOrient(this),
                16, 10, FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_AGE |FaceEngine.ASF_FACE_RECOGNITION|
                        FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_GENDER | FaceEngine.ASF_LIVENESS);
        Log.i("FaceActionActivity", "initEngine:  init: " + faceVideoEngineCOde);
        if (faceVideoEngineCOde == ErrorInfo.MOK) {
            Log.i("FaceActionActivity", "initEngine:  init success ");
        }else if(faceVideoEngineCOde == 90128){
            Log.i("FaceActionActivity", "initEngine:error");
        }
    }

    private void unDestroyEngine() {
        if (faceVideoEngineCOde == 0) {
            faceVideoEngineCOde = faceVideoEngine.unInit();
            Log.i("FaceActionActivity", "unInitEngine: " + faceVideoEngineCOde);
        }
    }

    private boolean flag = true;
    private boolean attendanceFlag = true;

    private void initCamera() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        CameraListener cameraListener = new CameraListener() {
            @Override
            public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
                Log.i("FaceActionActivity", "onCameraOpened: " + cameraId + "  " + displayOrientation + " " + isMirror);
                previewSize = camera.getParameters().getPreviewSize();
                drawHelper = new DrawHelper(previewSize.width, previewSize.height, previewView.getWidth(), previewView.getHeight(), displayOrientation
                        , cameraId, isMirror, false, false);
            }

            @Override
            public void onPreview(byte[] nv21, Camera camera) {
                if (faceRectView != null) {
                    faceRectView.clearFaceInfo();
                }
                List<FaceInfo> faceInfoList = new ArrayList<>();

                int code = faceVideoEngine.detectFaces(nv21, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList);

                if (code == ErrorInfo.MOK && faceInfoList.size() > 0) {
                    code = faceVideoEngine.process(nv21, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList, processMask);
                    if (code != ErrorInfo.MOK) {
                        return;
                    }
                } else {
                    return;
                }

                if(faceInfoList.size() > 0){
                    if (faceRectView != null && drawHelper != null) {
                        List<DrawInfo> drawInfoList = new ArrayList<>();
                        for (int i = 0; i < faceInfoList.size(); i++) {
                            int color = flag ? RecognizeColor.COLOR_UNKNOWN : RecognizeColor.COLOR_SUCCESS;
                            String name = flag ? null : "识别成功,正在打卡!";
                            drawInfoList.add(new DrawInfo(drawHelper.adjustRect(faceInfoList.get(i).getRect()), color , name));
                        }
                        drawHelper.draw(faceRectView, drawInfoList);
                    }

                    if(attendanceFlag){
                        Observable.timer(3000, TimeUnit.MILLISECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<Long>() {
                                    @Override public void onSubscribe(Disposable d) {
                                        attendanceFlag = false;
                                    }

                                    @Override
                                    public void onNext(Long value) {
                                        flag = false;
                                        mHUD.show();
                                        String imgPath = saveCurrentPreView(nv21,camera);
                                        faceAttendanceAction(imgPath);
                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCameraClosed() {
                Log.i("FaceActionActivity", "onCameraClosed: ");
            }

            @Override
            public void onCameraError(Exception e) {
                Log.i("FaceActionActivity", "onCameraError: " + e.getMessage());
            }

            @Override
            public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {
                if (drawHelper != null) {
                    drawHelper.setCameraDisplayOrientation(displayOrientation);
                }
                Log.i("FaceActionActivity", "onCameraConfigurationChanged: " + cameraID + "  " + displayOrientation);
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
        try{
            cameraHelper.start();
        }catch (Exception e){
            e.printStackTrace();
            androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("功能权限授权")
                    .setMessage("相机权限被拒绝,请从新安装该APP")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                            startActivity(intent);
                        }
                    })
                    .setCancelable(false)
                    .create();

            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    private String saveCurrentPreView(byte[] data, Camera camera){
        Camera.Size previewSize = camera.getParameters().getPreviewSize();//获取尺寸,格式转换的时候要用到
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
        Bitmap rotationMap = ImageRotateUtil.of().rotateBitmapByDegree(bitmap,-90);
        long systemTime = System.currentTimeMillis();
        String imageName = systemTime + "-face";
        Log.i("ScanCamera","look at face name = " + imageName);
        return saveToLocal(rotationMap,imageName);
    }

    private String saveToLocal(Bitmap bitmap, String bitName) {
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
        attendacedofUpload.setDirection(direction);
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
                        if (cameraHelper != null) {
                            cameraHelper.stop();
                            cameraHelper.release();
                            cameraHelper = null;
                        }
                        unDestroyEngine();
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
                        if(code == 1000){
                            showPhoneDialog("打卡成功!");
                        }else{
                            String message = (String) map.get("message");
                            showPhoneDialog(message);
                        }
                        if (cameraHelper != null) {
                            cameraHelper.stop();
                            cameraHelper.release();
                            cameraHelper = null;
                        }
                        unDestroyEngine();
                        mHUD.dismiss();
                    }
                });
            }
        });
    }

    private void showPhoneDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FaceActionActivity.this);
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

    @Override
    protected void onDestroy() {
        if (cameraHelper != null) {
            cameraHelper.stop();
            cameraHelper.release();
            cameraHelper = null;
        }
        unDestroyEngine();
        super.onDestroy();
    }
}
