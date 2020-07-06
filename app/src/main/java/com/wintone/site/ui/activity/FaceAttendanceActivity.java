package com.wintone.site.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.Face3DAngle;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.LivenessInfo;
import com.arcsoft.face.enums.CompareModel;
import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectModel;
import com.arcsoft.imageutil.ArcSoftImageFormat;
import com.arcsoft.imageutil.ArcSoftImageUtil;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.wintone.site.R;
import com.wintone.site.network.NetService;
import com.wintone.site.network.NetWorkUtils;
import com.wintone.site.networkmodel.AttendanceRecord;
import com.wintone.site.networkmodel.ResponseModel;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FaceAttendanceActivity extends BaseActivity implements ViewTreeObserver.OnGlobalLayoutListener{

    private Bitmap mBitmap = null;

    private CameraHelper cameraHelper;
    private DrawHelper drawHelper;

    private Camera.Size previewSize;
    private Integer rgbCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private int processMask = FaceEngine.ASF_AGE | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_GENDER | FaceEngine.ASF_LIVENESS;

    private FaceEngine faceImageEngine;
    private FaceEngine faceVideoEngine;

    private int faceEngineCode = -1;
    private int faceVideoEngineCOde = -1;

    private FaceFeature imageFace;

    private String way = "";

    @BindView(R.id.face_rect_view)  FaceRectView faceRectView;
    @BindView(R.id.texture_preview) View previewView;

    @Override
    protected int getContentView() {
        return R.layout.activity_face_attendance;
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

        initImageEngine();
    }

    private void getIntentData(){
        Intent intent = getIntent();
        way = intent.getStringExtra("commute");
    }

    @Override
    protected void initData() {
    }

    @Override
    public void onGlobalLayout() {
        initFaceEngine();
        initCamera();
    }

    private void initImageEngine() {
        faceImageEngine = new FaceEngine();
        faceEngineCode = faceImageEngine.init(this, DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority.ASF_OP_ALL_OUT,
                16, 10, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT |
                        FaceEngine.ASF_AGE | FaceEngine.ASF_GENDER | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_LIVENESS);
        Log.i("FaceAttendanceActivity","initEngine: init: " + faceEngineCode);

        if (faceEngineCode == ErrorInfo.MOK) {
            Log.i("FaceAttendanceActivity"," init engine is success");
            process();
        }
    }

    private void destroyImageEngine() {
        if (faceImageEngine != null) {
            faceEngineCode = faceImageEngine.unInit();
            faceImageEngine = null;
            Log.i("FaceAttendanceActivity","unInitEngine: " + faceEngineCode);
        }
    }

    private void process(){
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                processImage();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(Object value) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override public void onComplete() {}
                });
    }

    private void processImage(){
        if (mBitmap == null) {
             String faceUrl = (String) SPUtils.getShare(FaceAttendanceActivity.this,Constant.FACE_URL,"");
            try {
                mBitmap = Glide.with(FaceAttendanceActivity.this).asBitmap().load(faceUrl).submit().get();
                
                if(null != mBitmap){
                    Log.i("FaceAttendanceActivity"," bitmap is success ");
                }else{
                    Log.i("FaceAttendanceActivity"," bitmap is failed ");
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
                Log.i("FaceAttendanceActivity"," bitmap error message1 = " + e.getMessage().toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.i("FaceAttendanceActivity"," bitmap error message2 = " + e.getMessage().toString());
            }
        }
        // 图像对齐
        Bitmap bitmap = ArcSoftImageUtil.getAlignedBitmap(mBitmap,true);
        // bitmap转bgr24
        byte[] bgr24 = ArcSoftImageUtil.createImageData(bitmap.getWidth(), bitmap.getHeight(), ArcSoftImageFormat.BGR24);
        // 转换码
        int transformCode = ArcSoftImageUtil.bitmapToImageData(bitmap, bgr24, ArcSoftImageFormat.BGR24);

        List<FaceInfo> faceInfoList = new ArrayList<>();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int detectCode = faceImageEngine.detectFaces(bgr24, width, height, FaceEngine.CP_PAF_BGR24, DetectModel.RGB, faceInfoList);
        if (detectCode == ErrorInfo.MOK) {
            Log.i("FaceAttendanceActivity"," image is success ");
        }

        if(faceInfoList.size() > 0){
            FaceFeature[] faceFeatures = new FaceFeature[faceInfoList.size()];
            int[] extractFaceFeatureCodes = new int[faceInfoList.size()];

            for (int i = 0; i < faceInfoList.size(); i++) {
                faceFeatures[i] = new FaceFeature();
                //从图片解析出人脸特征数据
                long frStartTime = System.currentTimeMillis();
                extractFaceFeatureCodes[i] = faceImageEngine.extractFaceFeature(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList.get(i), faceFeatures[i]);

                if (extractFaceFeatureCodes[i] != ErrorInfo.MOK) {
                    Log.i("FaceAttendanceActivity"," extract failed, code is " + extractFaceFeatureCodes[i]);
                } else {

                    imageFace = faceFeatures[i];
                    Log.i("FaceAttendanceActivity","processImage: fr costTime = " + (System.currentTimeMillis() - frStartTime));
                }
            }
        }
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
                    FaceFeature faceFeature = new FaceFeature();
                    int fcCode;
                    for(int i=0;i<faceInfoList.size();i++){

                        fcCode = faceVideoEngine.extractFaceFeature(nv21, previewSize.width, previewSize.height,
                                FaceEngine.CP_PAF_NV21, faceInfoList.get(i), faceFeature);

                        if(fcCode != ErrorInfo.MOK){
                        }else{
                            FaceSimilar faceSimilar = new FaceSimilar();
                            faceVideoEngine.compareFaceFeature(imageFace,faceFeature, CompareModel.LIFE_PHOTO,faceSimilar);
                            if(faceSimilar.getScore() >= 0.8){
                                if(flag){
                                    flag = false;
                                    if (cameraHelper != null) {
                                        cameraHelper.stop();
                                        cameraHelper.release();
                                        cameraHelper = null;
                                    }
                                    destroyImageEngine();
                                    unDestroyEngine();
                                    showPhoneDialog();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCameraClosed() {
            }

            @Override
            public void onCameraError(Exception e) {
            }

            @Override
            public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {
                if (drawHelper != null) {
                    drawHelper.setCameraDisplayOrientation(displayOrientation);
                }
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

    private void showPhoneDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FaceAttendanceActivity.this);
        builder.setTitle("考勤打卡");
        builder.setMessage("考勤打卡成功!");

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
                        entryAttendance();
                    }
                });

        builder.setCancelable(false);

        builder.show();
    }

    private void entryAttendance(){
        String token = (String)SPUtils.getShare(FaceAttendanceActivity.this,Constant.USER_TOKEN,"");

        AttendanceRecord attendanceRecord = new AttendanceRecord();
        String empId = (String)SPUtils.getShare(FaceAttendanceActivity.this,Constant.EMP_ID,"");
        attendanceRecord.setEmployeeId(empId);
        attendanceRecord.setDirection(way);
        attendanceRecord.setWay(1);
        String faceUrl = (String)SPUtils.getShare(FaceAttendanceActivity.this,Constant.FACE_URL,"");
        attendanceRecord.setSitePhoto(faceUrl);
        attendanceRecord.setDeviceType("2");
        attendanceRecord.setDeviceSn(AppUtils.getPhoneSign());

        NetWorkUtils.getInstance().createService(NetService.class)
                .postAttendanceRecord(token,Constant.ATTENDANCE_RECORD_URL,attendanceRecord)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseModel>() {
                    @Override public void onSubscribe(Disposable d) { }

                    @Override
                    public void onNext(ResponseModel value) {
                        if(value.getCode() == 1000){
                            ToastUtils.showLong("考勤成功!");
                            finish();
                        }else{
                            ToastUtils.showLong("考勤失败,请联系管理员!");
                            finish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        finish();
                    }

                    @Override public void onComplete() { }
                });
    }

    @Override
    protected void onDestroy() {
        if (cameraHelper != null) {
            cameraHelper.release();
            cameraHelper = null;
        }
        destroyImageEngine();
        unDestroyEngine();
        super.onDestroy();
    }
    
}
