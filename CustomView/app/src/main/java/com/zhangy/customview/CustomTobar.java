package com.zhangy.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 1、定义属性  /values/attrs.xml目录
 * zhangy
 * Created by zhangy on 2017/12/7.
 */

public class CustomTobar extends RelativeLayout {

    //Tobar上的元素
    private Button mLeftButton,mRightButton;
    private TextView mTextView;

    /**
     * LayoutParams:用于子View向父View传达自己意愿的一个东西。相当于一个Layout的信息包
     * 封装了Layout的位置，宽和高的一些数据。
     * 假设在屏幕上一块区域是由一个Layout占领的，如果将一个View添加到一个Layout中，
     * 最好告诉Layout用户（要添加进去的View）期望的布局方式，也就是将一个认可的layoutParams传递进去。
     */
    //布局属性,用来控制组件元素在ViewGroup中的位置
    private LayoutParams mLeftParams, mTitlepParams, mRightParams;;

    //接口
    private tobOnClickListener listener;

    //标题的属性值
    private String mTitle;
    private float mTitleSize;
    private int mTitleColor;

    //左按钮的属性
    private String mLeftTitle;
    private Drawable mLeftTitlebackground;
    private int mLeftTitleColor;

    //右按钮的属性
    private String mRightTitle;
    private Drawable mRightTitlebackground;
    private int mRightTitleColor;

    public CustomTobar(Context context) {
        this(context,null);
    }

    public CustomTobar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomTobar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //获取attrs里面的资源文件
        TypedArray a=context.obtainStyledAttributes(attrs,R.styleable.TopBar);

        //从TyoeArray中取出对应的值来为要设置的属性赋值
        mTitle=a.getString(R.styleable.TopBar_tb_title);
        mTitleColor=a.getColor(R.styleable.TopBar_tb_titleTextColor,0);
        mTitleSize=a.getDimension(R.styleable.TopBar_tb_titleTextSize,10);
        mLeftTitle=a.getString(R.styleable.TopBar_tb_leftText);
        mLeftTitlebackground=a.getDrawable(R.styleable.TopBar_tb_leftbackground);
        mLeftTitleColor=a.getColor(R.styleable.TopBar_tb_leftTextColor,0);
        mRightTitle=a.getString(R.styleable.TopBar_tb_rightText);
        mRightTitleColor=a.getColor(R.styleable.TopBar_tb_rightTextColor,0);
        mRightTitlebackground=a.getDrawable(R.styleable.TopBar_tb_rightbackground);

        // 获取完TypedArray的值后，一般要调用
        // recyle方法来避免重新创建的时候的错误
        a.recycle();

        mLeftButton=new Button(context);
        mRightButton=new Button(context);
        mTextView=new TextView(context);

        //为创建的元素进行赋值
        mLeftButton.setTextColor(mLeftTitleColor);
        mLeftButton.setBackground(mLeftTitlebackground);
        mLeftButton.setText(mLeftTitle);

        mRightButton.setText(mRightTitle);
        mRightButton.setBackground(mRightTitlebackground);
        mRightButton.setTextColor(mRightTitleColor);

        mTextView.setText(mTitle);
        mTextView.setTextColor(mTitleColor);
        mTextView.setTextSize(mTitleSize);
        //居中显示
        mTextView.setGravity(Gravity.CENTER);

        mLeftParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        /**
         * 动态设置布局的属性，不同的条件下设置不同的布局排列方式
         * RelativeLayout中该控件放在相关联的兄弟节点的左边
         */
        mLeftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, TRUE);
        //添加到ViewGroup中
        addView(mLeftButton,mLeftParams);

        mRightParams=new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mRightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,TRUE);
        addView(mRightButton,mRightParams);

        mTitlepParams=new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mTitlepParams.addRule(RelativeLayout.CENTER_IN_PARENT,TRUE);
        addView(mTextView,mTitlepParams);

        mRightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.RightOnClick();
            }
        });

        mLeftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.LeftOnClick();
            }
        });
    }

    public void setTobOnClickListener(tobOnClickListener listener){
        this.listener=listener;
    }

    /**
     * 设置按钮的显示与否 通过id区分按钮，flag区分是否显示
     *
     * @param id   id
     * @param flag 是否显示
     */
    public void setButtonVisable(int id, boolean flag) {
        if (flag) {
            if (id == 0) {
                mLeftButton.setVisibility(View.VISIBLE);
            } else {
                mRightButton.setVisibility(View.VISIBLE);
            }
        } else {
            if (id == 0) {
                mLeftButton.setVisibility(View.GONE);
            } else {
                mRightButton.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 定义接口，实现回调机制
     *
     */
    public interface tobOnClickListener{

        void LeftOnClick();

        void RightOnClick();
    }
}
