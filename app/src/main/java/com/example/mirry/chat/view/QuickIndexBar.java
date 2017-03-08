package com.example.mirry.chat.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.mirry.chat.R;

/**
 * 快速定位联系人
 * Created by Mirry on 2017/3/7.
 */

public class QuickIndexBar extends View {

    private Paint mPaint;
    private static final String[] LETTERS = new String[]{
            "A","B","C","D","E","F","G","H","I","J","K","L", "M",
            "N", "O", "P","Q","R","S","T","U","V","W","X","Y","Z"
    };
    private float cellWidth;
    private float cellHeight;
    private int touchIndex = -1;

    /**
     * 暴露字母监听
     */
    public interface OnLetterUpdateListener{
        void onLetterUpdate(String letter);
    }

    public OnLetterUpdateListener getListener() {
        return listener;
    }

    public void setListener(OnLetterUpdateListener listener) {
        this.listener = listener;
    }

    private OnLetterUpdateListener listener;

    public QuickIndexBar(Context context) {
        this(context,null);
    }

    public QuickIndexBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public QuickIndexBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(getResources().getColor(R.color.transparent));
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);   //抗锯齿
        mPaint.setColor(getResources().getColor(R.color.darkBlue));
        mPaint.setTextSize(40);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制文本内容A-Z
        for(int i = 0 ; i < LETTERS.length; i++) {
            String text =  LETTERS[i];
            int x = (int)(cellWidth/2.0f - mPaint.measureText(text)/2.0f);

            Rect bounds = new Rect();
            mPaint.getTextBounds(text,0,text.length(),bounds);
            int textHeight = bounds.height();
            int y = (int)(cellHeight/2.0f + textHeight/2.0f + i * cellHeight);

            //根据当前按下的字母设置画笔颜色
            mPaint.setColor(touchIndex == i ? getResources().getColor(R.color.colorAccent) : getResources().getColor(R.color.darkBlue));
            canvas.drawText(text, x, y, mPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //获取单元格的宽高
        cellWidth = getMeasuredWidth();
        float mHeight = getMeasuredHeight();

        cellHeight = mHeight/LETTERS.length;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = -1;
        switch (MotionEventCompat.getActionMasked(event)){
            case MotionEvent.ACTION_DOWN:
                setBackgroundColor(getResources().getColor(R.color.barBlue));
                index = (int) (event.getY()/cellHeight);
                if (index >=0 && index < LETTERS.length && index != touchIndex){
                    if(listener != null){
                        listener.onLetterUpdate(LETTERS[index]);
                    }
                    touchIndex = index;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                index = (int) (event.getY()/cellHeight);
                if (index >=0 && index < LETTERS.length && index != touchIndex){
                    if(listener != null){
                        listener.onLetterUpdate(LETTERS[index]);
                    }
                    touchIndex = index;
                }
                break;
            case MotionEvent.ACTION_UP:
                setBackgroundColor(getResources().getColor(R.color.transparent));
                touchIndex = -1;
                break;
        }
        invalidate(); //重绘
        return true;
    }
}
