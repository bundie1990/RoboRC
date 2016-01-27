/**
 * Created by Fahad Alduraibi on 12/4/14.
 * fadvisor.net
 */
package net.fadvisor.roborc;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class MySeekBar extends SeekBar {
    public MySeekBar(Context context) {
        super(context);
    }
    public MySeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MySeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        super.onTouchEvent(event);
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                break;
//            case MotionEvent.ACTION_MOVE:
//                break;
            case MotionEvent.ACTION_UP:
                MainActivity.ResetSeekBar(this);
                break;
            case MotionEvent.ACTION_CANCEL:
                MainActivity.ResetSeekBar(this);
                break;
        }
        return true;
    }
}