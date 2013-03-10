package edu.sdu.onearmedbandit;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
    private Random gen;
    private Handler stopSpinTask;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spinactivity);
        setTitle(titleText());
        gen = new Random();
        stopSpinTask = new Handler();

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
                    new SpinTask().execute(i);
                }
                break;
            default:
                break;
        }
    }

    private class SpinTask extends AsyncTask<Integer, Integer, Void>
    {
        protected void onPreExecute()
        {
            int runtime = gen.nextInt(5000) + 5000;

            stopSpinTask.postDelayed(
                new Runnable()
                {
                    public void run()
                    {
                        SpinTask.this.cancel(true);
                    }
                },
                runtime);
        }

        protected Void doInBackground(Integer... i)
        {
            for (int k=0; k<20; k++)
            {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Log.i(TAG, "", e);
                }

                reels[i[0]].idx = (reels[i[0]].idx + 1) % fruits.size();
                publishProgress(i[0]);

                if (isCancelled()) {break;}
            }

            return (Void) null;
        }

        protected void onProgressUpdate(Integer... i)
        {
            // Update images on the i[0]'th reel
            reels[i[0]].view.setImageDrawable(fruits.get(reels[i[0]].idx));
        }

        protected void onPostExecute(Integer i)
        {

        }
    }

    private String titleText()
    {
        return String.format("%1$s (%2$s)",
                getString(R.string.app_name),
                getString(R.string.bi_versionname));
    }
}

