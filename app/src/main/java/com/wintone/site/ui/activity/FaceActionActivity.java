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
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.arcsoft.face.ActiveFileInfo;
import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.Face3DAngle;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.LivenessInfo;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.RuntimeABI;
import com.socks.library.KLog;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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

        activeEngine();
    }

    private void getIntentData(){
        Intent intent = getIntent();
        direction = intent.getStringExtra("commute");
    }

    public void activeEngine() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) {
                RuntimeABI runtimeABI = FaceEngine.getRuntimeABI();
                Log.i( "FaceAttendanceActivity","FaceAttendanceActivity subscribe: getRuntimeABI() " + runtimeABI);

                long start = System.currentTimeMillis();
                int activeCode = FaceEngine.activeOnline(FaceActionActivity.this, Constant.APP_ID, Constant.SDK_KEY);
                Log.i("FaceAttendanceActivity", "FaceAttendanceActivity subscribe cost: " + (System.currentTimeMillis() - start));
                emitter.onNext(activeCode);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer activeCode) {
                        if (activeCode == ErrorInfo.MOK) {
                            Log.i("FaceAttendanceActivity","FaceAttendanceActivity active is success ");
                        } else if (activeCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
                            Log.i("FaceAttendanceActivity","FaceAttendanceActivity active is ALREADY ");
                        } else {
                            Log.i("FaceAttendanceActivity","FaceAttendanceActivity active is failed code = " + activeCode);
                        }

                        ActiveFileInfo activeFileInfo = new ActiveFileInfo();
                        int res = FaceEngine.getActiveFileInfo(FaceActionActivity.this, activeFileInfo);
                        if (res == ErrorInfo.MOK) {
                            Log.i("FaceAttendanceActivity",activeFileInfo.toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("FaceAttendanceActivity","look at error message = " + e.getMessage().toString());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
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
        Log.i("FaceAttendanceActivity", "initEngine:  init: " + faceVideoEngineCOde);
        if (faceVideoEngineCOde == ErrorInfo.MOK) {
            Log.i("FaceAttendanceActivity", "initEngine:  init success ");
        }
    }

    private void unDestroyEngine() {
        if (faceVideoEngineCOde == 0) {
            faceVideoEngineCOde = faceVideoEngine.unInit();
            Log.i("FaceAttendanceActivity", "unInitEngine: " + faceVideoEngineCOde);
        }
    }

    private boolean flag = true;

    private void initCamera() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        CameraListener cameraListener = new CameraListener() {
            @Override
            public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
                Log.i("FaceAttendanceActivity", "onCameraOpened: " + cameraId + "  " + displayOrientation + " " + isMirror);
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
                long start = System.currentTimeMillis();
                int code = faceVideoEngine.detectFaces(nv21, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList);
                Log.i("FaceAttendanceActivity", "code id = " + code);
                Log.i("FaceAttendanceActivity", "size = " + faceInfoList.size());
                if (code == ErrorInfo.MOK && faceInfoList.size() > 0) {
                    code = faceVideoEngine.process(nv21, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList, processMask);
                    Log.i("FaceAttendanceActivity", "process code = " + code);
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
                int ageCode = faceVideoEngine.getAge(ageInfoList);
                int genderCode = faceVideoEngine.getGender(genderInfoList);
                int face3DAngleCode = faceVideoEngine.getFace3DAngle(face3DAngleList);
                int livenessCode = faceVideoEngine.getLiveness(faceLivenessInfoList);
                // 有其中一个的错误码不为ErrorInfo.MOK，return
                if ((ageCode | genderCode | face3DAngleCode | livenessCode) != ErrorInfo.MOK) {
                    return;
                }
                if (faceRectView != null && drawHelper != null) {
                    List<DrawInfo> drawInfoList = new ArrayList<>();
                    for (int i = 0; i < faceInfoList.size(); i++) {
                        drawInfoList.add(new DrawInfo(drawHelper.adjustRect(faceInfoList.get(i).getRect()), genderInfoList.get(i).getGender(), ageInfoList.get(i).getAge(), faceLivenessInfoList.get(i).getLiveness(), RecognizeColor.COLOR_UNKNOWN, null));
                    }
                    drawHelper.draw(faceRectView, drawInfoList);
                }
                if(faceInfoList.size() > 0){
                    if(flag){
                        flag = false;
                        String imgPath = saveCurrentPreView(nv21,camera);
                        faceAttendanceAction(imgPath);
                        if (cameraHelper != null) {
                            cameraHelper.stop();
                            cameraHelper.release();
                            cameraHelper = null;
                        }
                        unDestroyEngine();
                    }
                }
            }

            @Override
            public void onCameraClosed() {
                Log.i("FaceAttendanceActivity", "onCameraClosed: ");
            }

            @Override
            public void onCameraError(Exception e) {
                Log.i("FaceAttendanceActivity", "onCameraError: " + e.getMessage());
            }

            @Override
            public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {
                if (drawHelper != null) {
                    drawHelper.setCameraDisplayOrientation(displayOrientation);
                }
                Log.i("FaceAttendanceActivity", "onCameraConfigurationChanged: " + cameraID + "  " + displayOrientation);
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
        long systemTime = System.currentTimeMillis();
        String imageName = systemTime + "-face";
        Log.i("ScanCamera","look at face name = " + imageName);
        return saveToLocal(bitmap,imageName);
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

        KLog.i("look at json data = " + JSON.toJSONString(attendacedofUpload));
        String url = Constant.BASE_URL + Constant.ATTENDANCE_RECORD_URL;
        KLog.i("look at url  = " + url);

        String token = (String)SPUtils.getShare(this,Constant.USER_TOKEN,"");

        OkHttpUtil.getInstance().uploadAndAttendance(attendacedofUpload,url,token,pathName,new OkhttpClientRequest() {
            @Override
            public void responseFailure(String errorMessage) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showPhoneDialog("打卡出现错误:"+errorMessage);
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
                            showPhoneDialog("打卡成功");
                        }else{
                            String message = (String) map.get("message");
                            showPhoneDialog(message);
                        }
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
            cameraHelper.release();
            cameraHelper = null;
        }
        unDestroyEngine();
        super.onDestroy();
    }
}
