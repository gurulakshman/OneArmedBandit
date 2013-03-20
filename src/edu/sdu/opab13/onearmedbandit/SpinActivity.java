package edu.sdu.opab13.onearmedbandit;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.HashMap;

public class SpinActivity extends Activity implements OnClickListener
{
    private final static String mTAG = "edu.sdu.opab13.onearmedbandit";
    private Button mBtnStart;
    private Button[] mBtnStop;
    private Reel[] reels;
    private SpinAnimTask[] mSpinAnimTasks;

    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.main);
        setTitle(titleText());

        mBtnStart = (Button) findViewById(R.id.button_start);
        mBtnStop = new Button[3];
        mBtnStop[0] = (Button) findViewById(R.id.button_stop0);
        mBtnStop[1] = (Button) findViewById(R.id.button_stop1);
        mBtnStop[2] = (Button) findViewById(R.id.button_stop2);
        mBtnStart.setOnClickListener(this);
        for (int i=0; i<3; i++) {mBtnStop[i].setOnClickListener(this);}

        // Construct map of fruit drawables
        TypedArray typedfruits = getResources().obtainTypedArray(R.array.fruits);
        HashMap fruits = new HashMap<Integer, Drawable>();
        for (int i=0; i<typedfruits.length(); i++)
        {
            fruits.put(i, typedfruits.getDrawable(i));
        }
        typedfruits.recycle();

        // Set up three reels, each connected to an ImageView
        reels = new Reel[3];
        reels[0] = new Reel((ImageView) findViewById(R.id.reel0), fruits);
        reels[1] = new Reel((ImageView) findViewById(R.id.reel1), fruits);
        reels[2] = new Reel((ImageView) findViewById(R.id.reel2), fruits);

        // Set random start frame, just for looks.
        for (int i=0; i<reels.length; i++)
        {
            reels[i].shuffleFruits();
            reels[i].next();
        }
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.button_start:
                mBtnStart.setClickable(false);
                mSpinAnimTasks = new SpinAnimTask[reels.length];
                for (int i=0; i<reels.length; i++)
                {
                    reels[i].shuffleFruits();
                    mSpinAnimTasks[i] = new SpinAnimTask();

                    // Concurrent or sequential?
                    // See http://www.jayway.com/2012/11/28/is-androids-asynctask-executing-tasks-serially-or-concurrently/
                    mSpinAnimTasks[i].execute(i);
                }
                break;
            case R.id.button_stop0:
                mSpinAnimTasks[0].cancel(true);
                break;
            case R.id.button_stop1:
                mSpinAnimTasks[1].cancel(true);
                break;
            case R.id.button_stop2:
                mSpinAnimTasks[2].cancel(true);
            default:
                break;
        }
    }

    private class SpinAnimTask extends AsyncTask<Integer, Integer, Integer>
    {
        protected Integer doInBackground(Integer... p)
        {
            for (int k=0; k<30; k++)
            {
                try {
                    Thread.sleep(350);
                } catch (InterruptedException e) {
                    Log.i(mTAG, "", e);
                }

                publishProgress(p[0]);

                if (isCancelled()) {break;}
            }
            return p[0];
        }

        protected void onProgressUpdate(Integer... p)
        {
            reels[p[0]].next();
        }

        protected void onPostExecute(Integer p)
        {
            end(p);
        }

        protected void onCancelled(Integer p)
        {
            end(p);
        }

        private void end(Integer p)
        {
            if ((mSpinAnimTasks[0].getStatus() == AsyncTask.Status.FINISHED || mSpinAnimTasks[0].isCancelled())
                    && (mSpinAnimTasks[1].getStatus() == AsyncTask.Status.FINISHED || mSpinAnimTasks[1].isCancelled())
                    && (mSpinAnimTasks[2].getStatus() == AsyncTask.Status.FINISHED || mSpinAnimTasks[2].isCancelled()))
                // FIXME shiiiat man (msg handler?)
            {
                CharSequence text = "";
                if (isWinner())
                {
                    text = "Win!";
                }
                else
                {
                    text = ":(";
                }
                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                toast.show();
                mBtnStart.setClickable(true);
            }
        }
    }

    private boolean isWinner()
    {
        boolean win = false;
        for (int i=0; i<reels.length-1; i++)
        {
            if (reels[i].getCurrentKey() != reels[i+1].getCurrentKey())
            {
                win = false;
                break;
            }
            else
            {
                win = true;
            }
        }
        return win;
    }

    private String titleText()
    {
        return String.format("%s (%s)",
                getString(R.string.app_name),
                getString(R.string.bi_versionname));
    }
}

