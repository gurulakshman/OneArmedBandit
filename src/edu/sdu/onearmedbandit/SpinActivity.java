package edu.sdu.onearmedbandit;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
    private TextView tView;
    private ArrayList<Drawable> fruits;
    private Reel[] reels;
    private Random gen;
    private Handler spinTaskHandler;
    private int spinTaskMsgCount;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setTitle(titleText());
        gen = new Random();
        spinTaskMsgCount = 0;

        // Get interface elements
        reels = new Reel[3];
        for (int i=0; i<reels.length; i++) {reels[i] = new Reel();}
        reels[0].view = (ImageView) findViewById(R.id.reel0);
        reels[1].view = (ImageView) findViewById(R.id.reel1);
        reels[2].view = (ImageView) findViewById(R.id.reel2);
        tView = (TextView) findViewById(R.id.textView);
        bStart = (Button) findViewById(R.id.button_start);

        // Attach button listeners
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

        // Set up message handler
        spinTaskHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    case 0:
                    case 1:
                    case 2:
                        Log.v(TAG, "Stopped (" + msg.what + ")");
                        if (spinTaskMsgCount == 2) //All three are done
                        {
                            spinTaskMsgCount = 0;
                            if (isWinner())
                            {
                                tView.setText("You WIN!");
                            }
                            else
                            {
                                tView.setText("You LOOSE!");
                            }
                            bStart.setClickable(true);
                        }
                        else
                        {
                            spinTaskMsgCount++;
                        }
                        break;

                    default:
                        break;
                }
            }
        };
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.button_start:
                SpinTask[] spinners = new SpinTask[reels.length];
                bStart.setClickable(false);
                tView.setText("");

                for (int i=0; i<reels.length; i++)
                {
                    spinners[i] = new SpinTask();
                    spinners[i].execute(i);
                }

                break;
            default:
                break;
        }
    }

    private class SpinTask extends AsyncTask<Integer, Integer, Integer>
    {
        protected void onPreExecute()
        {
            int runtime = gen.nextInt(7000) + 3000;

            spinTaskHandler.postDelayed(
                new Runnable()
                {
                    public void run()
                    {
                        SpinTask.this.cancel(true);
                    }
                },
                runtime);
        }

        protected Integer doInBackground(Integer... i)
        {
            for (int k=0; k<30; k++)
            {
                try {
                    Thread.sleep(350);
                } catch (InterruptedException e) {
                    Log.i(TAG, "", e);
                }

                reels[i[0]].idx = (reels[i[0]].idx + 1) % fruits.size();
                publishProgress(i[0]);

                if (isCancelled()) {break;}
            }

            return i[0];
        }

        protected void onProgressUpdate(Integer... i)
        {
            reels[i[0]].view.setImageDrawable(fruits.get(reels[i[0]].idx));
        }

        protected void onPostExecute(Integer i)
        {
            spinTaskHandler.sendEmptyMessage(i);
        }

        protected void onCancelled(Integer i)
        {
            spinTaskHandler.sendEmptyMessage(i);
        }
    }

    private boolean isWinner()
    {
        boolean win = false;
        int t = reels[0].idx;

        for (int i=0; i<reels.length; i++)
        {
            if (t != reels[i].idx)
            {
                win = false;
                break;
            }
            else
            {
                t = reels[i].idx;
                win = true;
            }
        }

        return win;
    }

    private String titleText()
    {
        return String.format("%1$s (%2$s)",
                getString(R.string.app_name),
                getString(R.string.bi_versionname));
    }
}

