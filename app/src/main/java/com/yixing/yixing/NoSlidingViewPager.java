package com.yixing.yixing;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by 我心永恒 on 2017/10/19.
 */

public class NoSlidingViewPager extends ViewPager {

    public NoSlidingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoSlidingViewPager(Context context) {
        super(context);
    }

    /*
     * 表示把滑动事件传递给下一个view
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        return false;
    }
    /*
     * 可以啥都不做
     */
    public boolean onTouchEvent(MotionEvent arg0) {
        return false;
    }
}
