package com.wintone.site.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wintone.site.R;

/**
 * create by ths on 2020/6/22
 */
public class PopWindowLayout extends PopupWindow {

    private Context mContext;

    private View mMenuView;
    private TextView selectLocal;
    private TextView selectPhoto;
    private TextView cancelImage;

    private PopWindowLayout.OpenWindowListener mOpenPictureListener;

    public PopWindowLayout(Context context){

        super(context);

        this.mContext = context;

        initView();

        initListener();
    }

    private void initView(){
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.pop_feedback_layout,null);
        selectLocal = (TextView)mMenuView.findViewById(R.id.selectLocal);
        selectPhoto = (TextView)mMenuView.findViewById(R.id.selectPhoto);
        cancelImage = (TextView)mMenuView.findViewById(R.id.cancelImage);
        this.setContentView(mMenuView);
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setAnimationStyle(R.style.pop_down_to_up);
        ColorDrawable cd = new ColorDrawable(0x80000000);
        this.setBackgroundDrawable(cd);
    }

    private void initListener(){
        mMenuView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = mMenuView.findViewById(R.id.popLayout).getTop();
                int y=(int) event.getY();
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if(y<height){
                        dismiss();
                    }
                }
                return false;
            }
        });

        selectLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOpenPictureListener.fromLocalImage();
                dismiss();
            }
        });

        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOpenPictureListener.fromPhotoImage();
                dismiss();
            }
        });

        cancelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setOpenWindowListener(PopWindowLayout.OpenWindowListener listener){
        this.mOpenPictureListener = listener;
    }

    public interface OpenWindowListener{
        void fromLocalImage();
        void fromPhotoImage();
    }
}
