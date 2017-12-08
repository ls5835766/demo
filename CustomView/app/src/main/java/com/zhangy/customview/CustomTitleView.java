package com.zhangy.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * 自定义View
 * zhangy
 * Created by zhangy on 2017/12/6.
 */

public class CustomTitleView  extends View{

    /**
     * 文本
     */
    private String mTitleText;

    /**
     * 文本颜色
     */
    private int mTitleTextColor;

    /**
     * 文本大小
     */
    private int mTitleTextSize;

    /**
     * 画笔和控制文本的范围
     */
    private Paint mPaint;
    private Rect mBound;


    /**
     * 我们重写了3个构造方法，默认的布局文件调用的是两个参数的构造方法，
     * 所以记得让所有的构造调用我们的三个参数的构造，我们在三个参数的构造中获得自定义属性。
     * @param context
     */
    public CustomTitleView(Context context) {
        this(context,null);
    }

    public CustomTitleView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomTitleView(Context context, AttributeSet attrs,int defStyle) {
        super(context, attrs, defStyle);

        /**
         * 获得我们自定义的属性
         */
        TypedArray a=context.getTheme().obtainStyledAttributes(attrs,R.styleable.CustomTitleView,defStyle,0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++)
        {
            int attr = a.getIndex(i);
            switch (attr)
            {
                case R.styleable.CustomTitleView_titleText:
                    mTitleText = a.getString(attr);
                    break;
                case R.styleable.CustomTitleView_titleTextColor:
                    // 默认颜色设置为黑色
                    mTitleTextColor = a.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.CustomTitleView_titleTextSize:
                    // 默认设置为16sp，TypeValue也可以把sp转化为px
                    mTitleTextSize = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
                    break;
            }

        }
        a.recycle();
        /**
         * 获得画笔
         */
        mPaint = new Paint();
        //给画笔赋值
        mPaint.setTextSize(mTitleTextSize);
        mBound=new Rect();
        //得到文本的边界，上下左右，提取到bounds中
        mPaint.getTextBounds(mTitleText,0,mTitleText.length(),mBound);
    }


    /**
     *
     * View的绘制
     * @param canvas  在Canvaas对象上绘制所需要的图形
     *                 Canvas相当于是一个画板
     *                 使用Paint在上面作画就可以了
     */
    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(Color.YELLOW);
        //绘制矩形
        canvas.drawRect(0,0,getMeasuredWidth(),getMeasuredHeight(),mPaint);

        mPaint.setColor(mTitleTextColor);
        //绘制文本
        canvas.drawText(mTitleText, getWidth() / 2 - mBound.width() / 2, getHeight() / 2 + mBound.height() / 2, mPaint);
    }

    /**
     * View的测量
     * 如果布局设置成wrapcontent,系统帮我们测量的结果就是MATCH_PARENT的长度。
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension(measureWidth(widthMeasureSpec),measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int widthMeasureSpec){

        int widthMode=MeasureSpec.getMode(widthMeasureSpec);
        int widthSize=MeasureSpec.getSize(widthMeasureSpec);

        /**
         * 定义改过之后的宽和高
         */
        int width = 0;

        //测量值等于精确模式（占据父View的大小）
        if(widthMode == MeasureSpec.EXACTLY){
            width=widthSize;
        }else{
            if(widthMode==MeasureSpec.AT_MOST){
                width=getPaddingLeft()+getPaddingRight()+mBound.width();
            }
        }
        return width;
    }

    private int measureHeight(int heightMeasureSpec){

        int heightMode=MeasureSpec.getMode(heightMeasureSpec);
        int heightSize=MeasureSpec.getSize(heightMeasureSpec);
        /**
         * 定义改过之后的宽和高
         */
        int height = 0;

        //测量值等于精确模式（占据父View的大小）
        if(heightMode == MeasureSpec.EXACTLY){
            height=heightSize;
        }else{
            if(heightMode==MeasureSpec.AT_MOST){
                height=getPaddingTop()+getPaddingBottom()+mBound.height();
            }
        }
        return height;
    }

    /**
     * ViewGroup的测量
     * 1、当ViewGroup的大小为wrap_content的时候，ViewGroup就会对子View进行遍历，获得所有子View的大小，来决定自己的大小
     * 2、其他的模式下会通过指定的大小来设置自己的大小
     */


    /**
     * ViewGroup的绘制
     * 1、ViewGroup根本就不需要绘制，如果不是指定了背景颜色，那么ViewGroup的onDraw（）方法就不会被调用
     * 但是，ViewGroup的dispatchDraw()方法会来绘制其子View，其过程同样是遍历所有子View,并调用子View的绘制方法来完成绘制工作。
     */


}
