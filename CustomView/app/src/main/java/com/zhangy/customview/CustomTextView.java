package com.zhangy.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * zhangy
 * Created by zhangy on 2017/12/7.
 */

/**
 *
 *
 12-07 11:25:07.168 3642-3642/com.zhangy.customview D/CustomTextView: CustomTextView
 12-07 11:25:07.168 3642-3642/com.zhangy.customview D/CustomTextView: onFinishInflate
 12-07 11:25:07.188 3642-3642/com.zhangy.customview D/CustomTextView: onMeasure
 12-07 11:25:07.188 3642-3642/com.zhangy.customview D/CustomTextView: onMeasure
 12-07 11:25:07.228 3642-3642/com.zhangy.customview D/CustomTextView: onSizeChanged
 12-07 11:25:07.238 3642-3642/com.zhangy.customview D/CustomTextView: onMeasure
 12-07 11:25:07.238 3642-3642/com.zhangy.customview D/CustomTextView: onMeasure
 12-07 11:25:07.238 3642-3642/com.zhangy.customview D/CustomTextView: onDraw begin
 12-07 11:25:07.238 3642-3642/com.zhangy.customview D/CustomTextView: onDraw finsh
 *
 */


public class CustomTextView extends TextView{
    private static final String TAG=CustomTextView.class.getSimpleName();

    private Paint mPaint1;

    private Paint mPaint2;

    public CustomTextView(Context context) {
        this(context,null);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Log.d(TAG,"CustomTextView");
        mPaint1=new Paint();
        mPaint1.setColor(getResources().getColor(android.R.color.holo_blue_light));
        mPaint1.setStyle(Paint.Style.FILL);

        mPaint2=new Paint();
        mPaint2.setColor(Color.YELLOW);
        mPaint2.setStyle(Paint.Style.FILL);
    }

    /**
     * canvas.save()  保存画布当前状态
     * canvas.restore()  取出之前保存过的状态
     * canvas是对整个画布进行操作，如果不进行这两部，每一次的绘图都会在上一次的基础上进行绘制
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG,"onDraw begin");
        //在回调父类方法之前，实现自己的逻辑，对TextView来说即是绘制文本之前

        //绘制外层矩形
        canvas.drawRect(0,0,getMeasuredWidth(),getMeasuredHeight(),mPaint1);

        //绘制内层矩形
        canvas.drawRect(10,10,getMeasuredWidth()-10,getMeasuredHeight()-10,mPaint2);
//
        canvas.save();
        //绘制文字前平移10像素
        canvas.translate(10,0);
//        //父类完成的方法，绘制文本
        super.onDraw(canvas);
        canvas.restore();
        Log.d(TAG,"onDraw finsh");
        //在回调父类方法之前，实现自己的逻辑，对TextView来说即是绘制文本之后
    }

    /**
     * 在控件大小发生改变的时候调用，初始化调用一次
     * 也就是说onMeasure如果执行了，就必须会调用insizeChanged()方法
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG,"onSizeChanged");


    }

    /**
     * 当View中所有的子控件被映射成xml的时候触发
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d(TAG,"onFinishInflate");
    }

    /**
     * 在onsizeChanged（）方法前后各执行两次，共计4次
     * View放置到父容器时调用
     * 作用：测量View的大小，也可以通过下面方式，修改View的大小
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG,"onMeasure");
    }
}
