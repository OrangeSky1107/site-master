package com.wintone.site.ui.activity;

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
import android.os.Bundle;
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
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.wintone.site.R;
import com.wintone.site.SiteApplication;
import com.wintone.site.ui.base.activity.BaseActivity;
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

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FacePreViewActivity extends BaseActivity implements ViewTreeObserver.OnGlobalLayoutListener{

    private static final String IMAGE_PATH = "/storage/emulated/0/Android/data/com.tomcat.ocr.idcard/cache/";

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

        initImageEngine();

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

        if(faceVideoEngineCOde == 90115){
            SiteApplication.getInstance().activeEngine();

            initVideoEngine();
        }
    }

    private void unDestroyEngine() {
        if (faceVideoEngineCOde == 0) {
            faceVideoEngineCOde = faceVideoEngine.unInit();
            Log.i("FacePreViewActivity", "unInitEngine: " + faceVideoEngineCOde);
        }
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

        if(faceVideoEngineCOde == 90115){
            SiteApplication.getInstance().activeEngine();

            initImageEngine();
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

                                    String faceImgPath = saveCurrentPreView(nv21,camera);

                                    hashMap.put("faceImg",faceImgPath);

                                    Intent intent = new Intent(FacePreViewActivity.this,IdCardBackInfoActivity.class);
                                    Bundle bundle = new Bundle();
                                    if(hashMap == null){
                                        ToastUtils.showShort("请先录入身份证信息!");
                                        return;
                                    }
                                    bundle.putSerializable("data",hashMap);
                                    intent.putExtra("bundle",bundle);
                                    flag = false;
                                    if (cameraHelper != null) {
                                        cameraHelper.release();
                                        cameraHelper = null;
                                    }
                                    unInitEngine();
                                    unDestroyEngine();
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
            cameraHelper.stop();
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

    private String saveCurrentPreView(byte[] data, Camera camera){
        Camera.Size previewSize = cameraHelper.previewSize;//获取尺寸,格式转换的时候要用到
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
        Integer integer = (Integer) SPUtils.getShare(SiteApplication.getInstance(), Constant.CAMERA_SWITCH,1);
        Bitmap rotationMap = null;
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

}
