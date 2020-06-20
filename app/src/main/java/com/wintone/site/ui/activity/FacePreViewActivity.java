package com.wintone.site.ui.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import com.arcsoft.face.ActiveFileInfo;
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
import com.arcsoft.face.enums.RuntimeABI;
import com.arcsoft.imageutil.ArcSoftImageFormat;
import com.arcsoft.imageutil.ArcSoftImageUtil;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.wintone.site.R;
import com.wintone.site.ui.base.activity.BaseActivity;
import com.wintone.site.utils.Constant;
import com.wintone.site.utils.camera.CameraHelper;
import com.wintone.site.utils.camera.CameraListener;
import com.wintone.site.utils.faceutils.ConfigUtil;
import com.wintone.site.utils.faceutils.DrawHelper;
import com.wintone.site.utils.faceutils.RecognizeColor;
import com.wintone.site.widget.face.DrawInfo;
import com.wintone.site.widget.face.FaceRectView;

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

public class FacePreViewActivity extends BaseActivity implements ViewTreeObserver.OnGlobalLayoutListener{

//    private String headPath = "/storage/emulated/0/Android/data/com.tomcat.ocr.idcard/cache/1592213063907-head.jpg";
    private String headPath;

    private Bitmap mBitmap = null;

    private CameraHelper cameraHelper;
    private DrawHelper drawHelper;

    private Camera.Size previewSize;
    private Integer rgbCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private int processMask = FaceEngine.ASF_AGE | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_GENDER | FaceEngine.ASF_LIVENESS;

    private FaceEngine faceEngine;
    private FaceEngine faceVideoEngine;

    private int faceEngineCode = -1;
    private int faceVideoEngineCOde = -1;

    private FaceFeature imageFace;

    private HashMap hashMap = null;

    /**
     * 相机预览显示的控件，可为SurfaceView或TextureView
     */
    @BindView(R.id.face_rect_view) FaceRectView faceRectView;
    @BindView(R.id.texture_preview)View previewView;

    @Override
    protected int getContentView() {
        return R.layout.activity_face_preview;
    }

    @Override
    protected void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams attributes = getWindow().getAttributes();
            attributes.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            getWindow().setAttributes(attributes);
        }

        // Activity启动后就锁定为启动时的方向
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        if(bundle != null){
            hashMap = (HashMap) bundle.getSerializable("data");
            headPath = hashMap.get("headPath").toString();
        }

        activeEngine();

        previewView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onGlobalLayout() {
        initVideoEngine();
        initCamera();
    }

    private void initVideoEngine(){
        faceVideoEngine = new FaceEngine();
        faceVideoEngineCOde = faceVideoEngine.init(this, DetectMode.ASF_DETECT_MODE_VIDEO, ConfigUtil.getFtOrient(this),
                16, 10, FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_AGE |FaceEngine.ASF_FACE_RECOGNITION|
                        FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_GENDER | FaceEngine.ASF_LIVENESS);
        Log.i("FacePreViewActivity", "initEngine:  init: " + faceVideoEngineCOde);
        if (faceVideoEngineCOde == ErrorInfo.MOK) {
            Log.i("FacePreViewActivity", "initEngine:  init success ");
        }
    }

    private void unDestroyEngine() {
        if (faceVideoEngineCOde == 0) {
            faceVideoEngineCOde = faceVideoEngine.unInit();
            Log.i("FacePreViewActivity", "unInitEngine: " + faceVideoEngineCOde);
        }
    }

    /**
     * 切换相机。注意：若切换相机发现检测不到人脸，则极有可能是检测角度导致的，需要销毁引擎重新创建或者在设置界面修改配置的检测角度
     *
     * @param
     */
//    public void switchCamera() {
//        if (cameraHelper != null) {
//            boolean success = cameraHelper.switchCamera();
//            if (!success) {
//                Log.i("FacePreViewActivity","switch camera failed ");
//            } else {
//                Log.i("FacePreViewActivity","camera switched, if no face detected, please change face detect degree in homepage ");
//            }
//        }
//    }

    /**
     * 激活引擎
     *
     * @param
     */
    public void activeEngine() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) {
                RuntimeABI runtimeABI = FaceEngine.getRuntimeABI();
                Log.i( "FacePreViewActivity","FacePreViewActivity subscribe: getRuntimeABI() " + runtimeABI);

                long start = System.currentTimeMillis();
                int activeCode = FaceEngine.activeOnline(FacePreViewActivity.this, Constant.APP_ID, Constant.SDK_KEY);
                Log.i("FacePreViewActivity", "FacePreViewActivity subscribe cost: " + (System.currentTimeMillis() - start));
                emitter.onNext(activeCode);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override public void onSubscribe(Disposable d) { }

                    @Override
                    public void onNext(Integer activeCode) {
                        if (activeCode == ErrorInfo.MOK) {
                            Log.i("FacePreViewActivity","FacePreViewActivity active is success ");
                        } else if (activeCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
                            Log.i("FacePreViewActivity","FacePreViewActivity active is ALREADY ");
                        } else {
                            Log.i("FacePreViewActivity","FacePreViewActivity active is failed code = " + activeCode);
                        }

                        ActiveFileInfo activeFileInfo = new ActiveFileInfo();
                        int res = FaceEngine.getActiveFileInfo(FacePreViewActivity.this, activeFileInfo);
                        if (res == ErrorInfo.MOK) {
                            Log.i("FacePreViewActivity",activeFileInfo.toString());
                        }

                        initImageEngine();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("FacePreViewActivity","look at error message = " + e.getMessage().toString());
                    }

                    @Override
                    public void onComplete() {
                    }
                });

    }

    private void initImageEngine() {
        faceEngine = new FaceEngine();
        faceEngineCode = faceEngine.init(this, DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority.ASF_OP_ALL_OUT,
                16, 10, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT |
                        FaceEngine.ASF_AGE | FaceEngine.ASF_GENDER | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_LIVENESS);
            Log.i("FacePreViewActivity","initEngine: init: " + faceEngineCode);

        if (faceEngineCode == ErrorInfo.MOK) {
            Log.i("FacePreViewActivity"," init engine is success");
            process();
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
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object value) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void processImage(){
        if (mBitmap == null) {
            mBitmap = BitmapFactory.decodeFile(headPath);
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
        int detectCode = faceEngine.detectFaces(bgr24, width, height, FaceEngine.CP_PAF_BGR24, DetectModel.RGB, faceInfoList);
        if (detectCode == ErrorInfo.MOK) {
            Log.i("FacePreViewActivity"," image is success ");
        }

        if(faceInfoList.size() > 0){
            FaceFeature[] faceFeatures = new FaceFeature[faceInfoList.size()];
            int[] extractFaceFeatureCodes = new int[faceInfoList.size()];

            for (int i = 0; i < faceInfoList.size(); i++) {
                faceFeatures[i] = new FaceFeature();
                //从图片解析出人脸特征数据
                long frStartTime = System.currentTimeMillis();
                extractFaceFeatureCodes[i] = faceEngine.extractFaceFeature(bgr24, width, height, FaceEngine.CP_PAF_BGR24, faceInfoList.get(i), faceFeatures[i]);

                if (extractFaceFeatureCodes[i] != ErrorInfo.MOK) {
                    Log.i("FacePreViewActivity"," extract failed, code is " + extractFaceFeatureCodes[i]);
                } else {

                    imageFace = faceFeatures[i];
                    Log.i("FacePreViewActivity","processImage: fr costTime = " + (System.currentTimeMillis() - frStartTime));
                }
            }
        }
    }

    private boolean flag = true;

    private void initCamera() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        CameraListener cameraListener = new CameraListener() {
            @Override
            public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
                Log.i("FacePreViewActivity", "onCameraOpened: " + cameraId + "  " + displayOrientation + " " + isMirror);
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
                Log.i("FacePreViewActivity", "code id = " + code);
                Log.i("FacePreViewActivity", "size = " + faceInfoList.size());
                if (code == ErrorInfo.MOK && faceInfoList.size() > 0) {
                    code = faceVideoEngine.process(nv21, previewSize.width, previewSize.height, FaceEngine.CP_PAF_NV21, faceInfoList, processMask);
                    Log.i("FacePreViewActivity", "process code = " + code);
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
                            Log.i("FacePreViewActivity", "failed = " + fcCode);
                        }else{
                            FaceSimilar faceSimilar = new FaceSimilar();
                            faceVideoEngine.compareFaceFeature(imageFace,faceFeature, CompareModel.LIFE_PHOTO,faceSimilar);
                            Log.i("FacePreViewActivity", "success = " + faceSimilar.getScore() );
                            if(faceSimilar.getScore() >= 0.8){
                                if(flag){
                                    flag = false;
                                    if (cameraHelper != null) {
                                        cameraHelper.release();
                                        cameraHelper = null;
                                    }
                                    unInitEngine();
                                    unDestroyEngine();
                                    Intent intent = new Intent(FacePreViewActivity.this,IdCardBackInfoActivity.class);
                                    Bundle bundle = new Bundle();
                                    if(hashMap == null){
                                        ToastUtils.showShort("请先录入身份证信息!");
                                        return;
                                    }
                                    bundle.putSerializable("data",hashMap);
                                    intent.putExtra("bundle",bundle);
                                    ActivityUtils.startActivity(intent);
                                    finish();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCameraClosed() {
                Log.i("FacePreViewActivity", "onCameraClosed: ");
            }

            @Override
            public void onCameraError(Exception e) {
                Log.i("FacePreViewActivity", "onCameraError: " + e.getMessage());
            }

            @Override
            public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {
                if (drawHelper != null) {
                    drawHelper.setCameraDisplayOrientation(displayOrientation);
                }
                Log.i("FacePreViewActivity", "onCameraConfigurationChanged: " + cameraID + "  " + displayOrientation);
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

    @Override
    protected void onDestroy() {
        if (cameraHelper != null) {
            cameraHelper.release();
            cameraHelper = null;
        }
        unInitEngine();
        unDestroyEngine();
        Log.i("FacePreViewActivity", "onDestroy");
        super.onDestroy();
    }

    private void unInitEngine() {
        if (faceEngine != null) {
            faceEngineCode = faceEngine.unInit();
            faceEngine = null;
            Log.i("FacePreViewActivity","unInitEngine: " + faceEngineCode);
        }
    }
}
