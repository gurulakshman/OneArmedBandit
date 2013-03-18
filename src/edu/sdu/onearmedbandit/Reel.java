package edu.sdu.onearmedbandit;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import java.util.HashMap;
import java.util.Random;

public class Reel
{
    private ImageView mView;
    private HashMap mFruits;
    private int mSize;
    private int mCurrFruitIdx;
    private Integer[] shuffledKeys;

    public Reel(ImageView view, HashMap<Integer, Drawable> fruits)
    {
        mView = view;
        mFruits = fruits;
        mSize = mFruits.size();
        mCurrFruitIdx = 0;
        shuffledKeys = new Integer[mSize];
        mFruits.keySet().toArray(shuffledKeys);
        shuffleKeys();
    }

    public void nextFrame()
    {
        mCurrFruitIdx = (mCurrFruitIdx + 1) % mSize;
        mView.setImageDrawable((Drawable) mFruits.get(shuffledKeys[mCurrFruitIdx]));
    }

    public int getKey()
    {
        return shuffledKeys[mCurrFruitIdx];
    }

    private void shuffleKeys()
    {
        Random r = new Random();
        for (int i=shuffledKeys.length-1; i>0; i--)
        {
            int idx = r.nextInt(i+1);
            int tmp = shuffledKeys[i];
            shuffledKeys[i] = shuffledKeys[idx];
            shuffledKeys[idx] = tmp;
        }
        mCurrFruitIdx = 0;
    }
}
