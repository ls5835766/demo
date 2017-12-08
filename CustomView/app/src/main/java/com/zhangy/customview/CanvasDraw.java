package com.zhangy.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * zhangy
 *  canvas一些方法的总结的用法
 * Created by zhangy on 2017/12/6.
 */

public class CanvasDraw extends View {


    public CanvasDraw(Context context) {
        this(context,null);
    }

    public CanvasDraw(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CanvasDraw(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int baseLineX=0;
        int baseLineY=200;

        //画基线
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        canvas.drawLine(baseLineX,baseLineY,3000,baseLineY,paint);//画了一条线从点坐标为(0,200)到点坐标为(3000,200)的一条直线

        //利用canvas画出文字,利用canvas.drawText以（0，200）为原点画出文字
        paint.setColor(Color.GREEN);
        canvas.drawText("zhangy",baseLineX,baseLineY,paint);

        //绘制矩形父控件的长度和宽度   左上右下
        paint.setColor(Color.YELLOW);
        canvas.drawRect(0,0,getMeasuredWidth(),getMeasuredHeight(),paint);
    }
}
