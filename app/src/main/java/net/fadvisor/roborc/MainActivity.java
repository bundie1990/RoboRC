/**
 * Created by Fahad Alduraibi on 12/4/14.
 * fadvisor.net
 */

package net.fadvisor.roborc;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    private static TextView tv1;
    private static TextView tv2;

    private static MySeekBar sb1;
    private static MySeekBar sb2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final View rl2 = findViewById(R.id.rl2);

        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);

        sb1 = (MySeekBar) findViewById(R.id.sb1);
        sb2 = (MySeekBar) findViewById(R.id.sb2);

        // When the layout rl2 is created rotate it and swap height and width values
        rl2.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                int w = rl2.getWidth();
                int h = rl2.getHeight();

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(h, w);
                rl2.setLayoutParams(params);
                rl2.setRotation(270.0f);
                rl2.setTranslationX((w - h) / 2);
                rl2.setTranslationY((h - w) / 2);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
                    rl2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    rl2.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    public static void SeekBarUpdated(View v, String text) {
        if (v.getId() == R.id.sb1) {
            tv1.setText(text);
        } else if (v.getId() == R.id.sb2) {
            tv2.setText(text);
        }
    }

    public static void ResetSeekBar(final View v) {
        final MySeekBar tempsb;
        if (v.getId() == R.id.sb1) {
            tempsb = sb1;

        } else {
            tempsb = sb2;
        }

        ValueAnimator anim = ValueAnimator.ofInt(tempsb.getProgress(), 50);
        anim.setDuration(100);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animProgress = (Integer) animation.getAnimatedValue();
                tempsb.setProgress(animProgress);
                if (animProgress == 50) {
                    SeekBarUpdated(v, "50");
                }
            }
        });
        anim.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
