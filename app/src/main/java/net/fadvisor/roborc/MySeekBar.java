/**
 * Created by Fahad Alduraibi on 12/4/14.
 * fadvisor.net
 */
package net.fadvisor.roborc;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class MySeekBar extends SeekBar {
    public MySeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onTouchEvent(final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.d("tag", "UP");
            MainActivity.ResetSeekBar(this);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            Log.d("tag", Integer.toString(getProgress()));
            MainActivity.UpdateText(this, Integer.toString(getProgress()));
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d("tag", "DOWN ");
            MainActivity.UpdateText(this, Integer.toString(getProgress()));
        }
        return super.onTouchEvent(event);
    }
}