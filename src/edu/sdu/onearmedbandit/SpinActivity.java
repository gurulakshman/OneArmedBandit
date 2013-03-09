package edu.sdu.onearmedbandit;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Random;

public class SpinActivity extends Activity implements OnClickListener
{
    private final static String TAG = "edu.sdu.onearmedbandit";
    private Button bStart;
    private Button[] bStop;
    private TextView tViewone;
    private ArrayList<Drawable> fruits;
    private Reel[] reels;

    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spinactivity);
        setTitle(titleText());

        Log.v(TAG, "Initializing ...");

        // Get interface elements
        reels = new Reel[3];
        for (int i=0; i<reels.length; i++) {reels[i] = new Reel();}
        bStop = new Button[3];
        reels[0].view = (ImageView) findViewById(R.id.reel0);
        reels[1].view = (ImageView) findViewById(R.id.reel1);
        reels[2].view = (ImageView) findViewById(R.id.reel2);
        tViewone = (TextView) findViewById(R.id.textView);
        bStart = (Button) findViewById(R.id.button_start);
        bStop[0] = (Button) findViewById(R.id.button_stop_0);
        bStop[1] = (Button) findViewById(R.id.button_stop_1);
        bStop[2] = (Button) findViewById(R.id.button_stop_2);

        // Attach button listeners
        for (int i=0; i<bStop.length; i++) {bStop[i].setOnClickListener(this);}
        bStart.setOnClickListener(this);

        // Construct array of fruit drawables
        TypedArray typedfruits = getResources().obtainTypedArray(R.array.fruits);
        fruits = new ArrayList<Drawable>();
        for (int i=0; i<typedfruits.length(); i++)
        {
            fruits.add(typedfruits.getDrawable(i));
        }
        fruits.trimToSize();

        // Set random start images
        Random gen = new Random();
        for (int i=0; i<reels.length; i++)
        {
            int r = gen.nextInt(fruits.size());
            if (i>1 && r==reels[i-2].idx)
                {r = (r+3)%fruits.size();} //Ensure they are not all the same
            reels[i].view.setImageDrawable(fruits.get(r));
            reels[i].idx = r;
        }

        Log.v(TAG, "Done.");
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.button_start:
                for (int i=0; i<reels.length; i++)
                {
                    spin(i);
                }
                break;
            case R.id.button_stop_0:
                break;
            default:
                break;
        }
    }

    private void spin(int i)
    {
        reels[i].idx = (reels[i].idx + 1) % fruits.size();
        reels[i].view.setImageDrawable(fruits.get(reels[i].idx));
    }
//    new Thread(new Runnable() {
//        public void run() {
//            Bitmap b = loadImageFromNetwork("http://example.com/image.png");
//            mImageView.setImageBitmap(b);
//        }
//    }).start();


    private String titleText()
    {
        return String.format("%1$s (%2$s)",
                getString(R.string.app_name),
                getString(R.string.bi_versionname));
    }
}

