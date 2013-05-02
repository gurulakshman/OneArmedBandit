package edu.sdu.opab13.onearmedbandit;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import java.util.HashMap;
import java.util.Random;

public class Reel implements Runnable
{
    private final static String mTAG = "Reel";
    private int mReelId;
    private ImageView mView;
    private HashMap mFruits;
    private int mSize;
    private int mCurrFruitIdx;
    private Integer[] mShuffledKeys;
    private Handler mMsgHandler;
    private boolean mRunning;

    public static class Message
    {
        public final static int TEST = 1;
        public final static int REQUEST_NEXT_FRAME = 2;
        public final static int STOPPED = 3;

        private static int encodeMsg(int msg, int id)
        {
            return (id << 16) | msg;
        }

        public static int getMsg(int encodedMsg)
        {
            return encodedMsg & 0xFFFF;
        }

        public static int getId(int encodedMsg)
        {
            return encodedMsg >> 16;
        }
    }

    public Reel(int id, ImageView view, HashMap<Integer, Drawable> fruits, Handler handler)
    {
        mView = view;
        mFruits = fruits;
        mSize = mFruits.size();
        mCurrFruitIdx = 0;
        mShuffledKeys = new Integer[mSize];
        mFruits.keySet().toArray(mShuffledKeys);
        mMsgHandler = handler;
        mReelId = id;
        mRunning = false;
    }

    public void run()
    {
        synchronized(this)
        {
            mRunning = true;
        }
        Log.d(mTAG, "[" + mReelId + "] mRunning=" + mRunning);

        for (int k=0; k<30; k++)
        {
            if (!mRunning)
            {
                break;
            }

            try {
                Thread.sleep(350);
            } catch (InterruptedException e) {
                Log.i(mTAG, "", e);
            }

            // Request next frame on UI thread
            mMsgHandler.sendEmptyMessage(Message.encodeMsg(Message.REQUEST_NEXT_FRAME, mReelId));
        }

        synchronized(this)
        {
            mRunning = false;
        }
        Log.d(mTAG, "[" + mReelId + "] mRunning=" + mRunning);
        mMsgHandler.sendEmptyMessage(Message.encodeMsg(Message.STOPPED, mReelId));
    }

    public synchronized void stop()
    {
        mRunning = false;
    }

    public synchronized boolean isRunning()
    {
        return mRunning;
    }

    // Only call next() from UI thread
    public void next()
    {
        if (mRunning) // Only allow next() if running
        {
            mCurrFruitIdx = (mCurrFruitIdx + 1) % mSize;
            mView.setImageDrawable((Drawable) mFruits.get(mShuffledKeys[mCurrFruitIdx]));
        }
    }

    public void next(boolean force)
    {
        mCurrFruitIdx = (mCurrFruitIdx + 1) % mSize;
        mView.setImageDrawable((Drawable) mFruits.get(mShuffledKeys[mCurrFruitIdx]));
    }

    public int getCurrentKey()
    {
        return mShuffledKeys[mCurrFruitIdx];
    }

    public void shuffleFruits()
    {
        shuffleKeys();
    }

    private void shuffleKeys()
    {
        Random r = new Random();
        for (int i=mShuffledKeys.length-1; i>0; i--)
        {
            int idx = r.nextInt(i+1);
            int tmp = mShuffledKeys[i];
            mShuffledKeys[i] = mShuffledKeys[idx];
            mShuffledKeys[idx] = tmp;
        }
        mCurrFruitIdx = 0;
    }
}
