package com.zhangy.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * zhangy
 * Created by zhangy on 2017/12/7.
 */

public class CircleView extends View {

    private Paint mPaint;
    private float mCircleXY;
    private float mRadius;

    private int mMeasureHeigth;
    private int mMeasureWidth;


    public CircleView(Context context) {
        this(context,null);
    }

    public CircleView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initViews();

    }

    private void initViews() {
        float length=0;
        if(mMeasureHeigth>=mMeasureWidth){  //不管怎么样，都取小的
            length=mMeasureWidth;
        }else{
            length=mMeasureWidth;
        }

        mCircleXY = length / 2;
        mRadius=length/4;

        mPaint=new Paint();
        //用来防止边缘的锯齿
        mPaint.setAntiAlias(true);
        mPaint.setColor(getResources().getColor(android.R.color.holo_blue_light));

        //绘制弧线，需要指定其椭圆的外接矩形



    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMeasureHeigth=MeasureSpec.getSize(heightMeasureSpec);
        mMeasureWidth=MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(mMeasureWidth,mMeasureHeigth);
    }
}
