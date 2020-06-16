package com.wintone.site.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wintone.site.R;
import com.wintone.site.utils.PhoneUtils;


/**
 *
 */
public class LabelTextRow extends LinearLayout {

    private static final int INVALID = -1;

    protected TextView value;

    protected TextView label;

    private Paint paint;

    private int dividerIndent; // 分隔线缩进值

    private boolean drawBottomDivider = true;
    private boolean drawTopDivider = false;

    public LabelTextRow(Context context) {
        this(context, null);
    }

    public LabelTextRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 设置布局文件
        View.inflate(context, R.layout.base_label_text_row, this);

        // 初始化布局参数
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setMinimumHeight(50);
        int padding = 10;
        setPadding(padding, 0, padding, 0);
        setBackgroundResource(R.drawable.selector_lv_item);

        // 初始化控件
        label =  findViewById(R.id.row_label);
        value = findViewById(R.id.row_value);

        // xml属性为控件赋值
        if (attrs != null) {
            TypedArray styled = getContext().obtainStyledAttributes(attrs, R.styleable.LabelIconRow);

            CharSequence text = styled.getText(R.styleable.LabelIconRow_labelText);
            label.setText(text);

            int defaultColor = getResources().getColor(R.color.jx_value_text);
            int color = styled.getColor(R.styleable.LabelIconRow_labelValueColor, defaultColor);
            value.setTextColor(color);
            value.setPadding(0,0,15,0);
            CharSequence valueText = styled.getText(R.styleable.LabelIconRow_labelValue);
            value.setText(valueText);

            boolean alignRight = styled.getBoolean(R.styleable.LabelIconRow_labelValueRight, true);
            if (!alignRight) {
                value.setGravity(Gravity.LEFT);
            }

            boolean drawArrows = styled.getBoolean(R.styleable.LabelIconRow_labelArrows, false);
            if (drawArrows) {
                value.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.right_arrow, 0);
            }

            int labelWidth = styled.getDimensionPixelSize(R.styleable.LabelIconRow_labelExactlyWidth, INVALID);
            if (labelWidth != INVALID) {
                label.getLayoutParams().width = labelWidth;
            }

            float textSize = styled.getDimension(R.styleable.LabelIconRow_labelTextSize, INVALID);
            if(textSize != INVALID) {
                label.setTextSize(textSize);
                value.setTextSize(textSize);
            }

            textSize = styled.getDimension(R.styleable.LabelIconRow_labelValueSize, INVALID);
            if(textSize != INVALID){
                value.setTextSize(textSize);
            }

            int labelColor = styled.getColor(R.styleable.LabelIconRow_labelTextColor, INVALID);
            if(labelColor != INVALID) {
                label.setTextColor(labelColor);
            }

            int valueColor = styled.getColor(R.styleable.LabelIconRow_labelValueColor, INVALID);
            if(labelColor != INVALID) {
                value.setTextColor(valueColor);
            }

            int drawableLeft = styled.getResourceId(R.styleable.LabelIconRow_labelIcon, INVALID);
            if(drawableLeft != INVALID){
                label.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(drawableLeft),null,null,null);
            }

            int scaleLeft = styled.getResourceId(R.styleable.LabelIconRow_labelScaleIcon, INVALID);
            if(scaleLeft != INVALID && !isInEditMode()){
                Drawable left = context.getResources().getDrawable(scaleLeft);
                final int size = com.wintone.site.utils.PhoneUtils.dpToPx(context, 20);
                left.setBounds(0, 0, size, size);
                label.setCompoundDrawables(left, null, null, null);
            }

            drawBottomDivider = styled.getBoolean(R.styleable.LabelIconRow_labelBottomDivider, true);
            drawTopDivider = styled.getBoolean(R.styleable.LabelIconRow_labelTopDivider, false);
            dividerIndent = styled.getDimensionPixelSize(R.styleable.LabelIconRow_labelDividerIndent, 0);

            styled.recycle();
        }

        initPaint();

        // 不设置背景的情况下，继承自ViewGroup的容器不会调用onDraw方法，调用下面的方法使其调用onDraw()方法
        setWillNotDraw(!(drawBottomDivider || drawTopDivider));

    }

    /**
     * 设置值控件所占的权重比
     */
    public void setViewWeight(int labelWeight, int valueWeight) {
        ((LayoutParams) label.getLayoutParams()).weight = labelWeight;
        ((LayoutParams) value.getLayoutParams()).weight = valueWeight;
    }


    public TextView getLabelView() {
        return label;
    }

    public TextView getValueView() {
        return value;
    }

    public void setLabel(String text) {
        label.setText(text);
    }

    public void setText(CharSequence text) {
        value.setText(text);
    }


    public CharSequence getText() {
        return value.getText();
    }

    public String getLabel() {
        return label.getText().toString();
    }

    /**
     * 设置分隔线缩进值
     * @param indent
     */
    public void setDividerIndent(int indent) {
        dividerIndent = indent;
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.text_dark_gray));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawBottomDivider) {
            int y = getHeight() - 1;
            canvas.drawLine(dividerIndent, y, getRight(), y, paint);
        }
        if (drawTopDivider) {
            canvas.drawLine(0, 0, getRight(), 0, paint);
        }
    }

    public void setLabelIcon(int resId){
        Drawable left = getResources().getDrawable(resId);
        final int size = PhoneUtils.dpToPx(getContext(), 20);
        left.setBounds(0, 0, size, size);
        label.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
    }
}
