package edu.sdu.opab13.onearmedbandit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;

public class SpinActivity extends Activity implements OnClickListener {

    private final static String mTAG = "SpinActivity";
    private final static int mNumReels = 3;
    private Button mBtnStart;
    private Button[] mBtnStop;
    private Reel[] reels;
    private Thread[] mSpinThreads;
    private boolean mAllRunning;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);
        setTitle(titleText());

        loadPref();

        mBtnStart = (Button) findViewById(R.id.button_start);
        mBtnStop = new Button[3];
        mBtnStop[0] = (Button) findViewById(R.id.button_stop0);
        mBtnStop[1] = (Button) findViewById(R.id.button_stop1);
        mBtnStop[2] = (Button) findViewById(R.id.button_stop2);
        mBtnStart.setOnClickListener(this);
        for (int i = 0; i < 3; i++) {
            mBtnStop[i].setOnClickListener(this);
        }

        // Construct map of fruit drawables
        TypedArray typedfruits = getResources().obtainTypedArray(R.array.fruits);
        HashMap fruits = new HashMap<Integer, Drawable>();
        for (int i = 0; i < typedfruits.length(); i++) {
            fruits.put(i, typedfruits.getDrawable(i));
        }
        typedfruits.recycle();

        // Message handler for UI thread
        Handler msgHandler = new SpinActivityHandler();

        // Set up three reels, each connected to an ImageView
        reels = new Reel[mNumReels];
        reels[0] = new Reel(0, (ImageView) findViewById(R.id.reel0), fruits, msgHandler);
        reels[1] = new Reel(1, (ImageView) findViewById(R.id.reel1), fruits, msgHandler);
        reels[2] = new Reel(2, (ImageView) findViewById(R.id.reel2), fruits, msgHandler);

        mAllRunning = false;

        // Set random start frame, just for looks.
        for (int i = 0; i < mNumReels; i++) {
            reels[i].shuffleFruits();
            reels[i].next(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_start:
                mBtnStart.setClickable(false);

                // One thread for each reel
                mSpinThreads = new Thread[mNumReels];
                for (int i = 0; i < mNumReels; i++) {
                    mSpinThreads[i] = new Thread(reels[i]);
                    mSpinThreads[i].start();
                }

                mAllRunning = true;

                break;
            case R.id.button_stop0:
                reels[0].stop();
                break;
            case R.id.button_stop1:
                reels[1].stop();
                break;
            case R.id.button_stop2:
                reels[2].stop();
                break;
            default:
                break;
        }
    }

    // Message handler to process messages passed to the UI thread.
    private class SpinActivityHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (Reel.Message.getMsg(msg.what)) {
                case Reel.Message.TEST:
                    Toast.makeText(getApplicationContext(), "TEST", Toast.LENGTH_SHORT).show();
                    break;
                case Reel.Message.REQUEST_NEXT_FRAME:
                    reels[Reel.Message.getId(msg.what)].next();
                    break;
                case Reel.Message.STOPPED:
                    Log.d(mTAG, "Stopped " + Reel.Message.getId(msg.what));

                    // Check result when all reels have stopped.
                    if (!reels[0].isRunning()
                        && !reels[1].isRunning()
                        && !reels[2].isRunning()
                        && mAllRunning) {
                        Log.d(mTAG, "All three reels stopped");
                        mAllRunning = false;

                        showResult();

                        mBtnStart.setClickable(true);
                    }

                    break;
                default:
                    break;
            }
        }
    }

    private void showResult() {
        int prize = 0;
        int bet = getBet();
        ArrayList equals = new ArrayList<Integer>();

        // Make a list of reels that have the same fruit key
        for (int i = 0; i < mNumReels; i++) {
            for (int j = 0; j < mNumReels; j++) {
                if (i != j
                    && reels[i].getCurrentKey() == reels[j].getCurrentKey()) {
                    equals.add(i);
                }
            }
        }

        if (equals.isEmpty()) {
            Log.d(mTAG, "Result: LOSS. (bet=" + bet + " prize=" + prize + ")");
            Toast.makeText(getApplicationContext(), "You loose " + bet + "€", Toast.LENGTH_SHORT).show();
        } else {
            if (equals.size() == 2) {
                prize = bet * 5;
            } else if (equals.size() == 3) {
                prize = bet * 50;
            }

            String text = "You win " + prize + "€ on reels";
            for (int i = 0; i < equals.size(); i++) {
                text = text.concat(String.format(" %d", ((Integer) equals.get(i)).intValue()));
                Log.d(mTAG, "eq: " + i);
            }

            Log.d(mTAG, "Result: WIN. (bet=" + bet + " prize=" + prize + ")");
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

    private String titleText() {
        return String.format("%s (%s)",
            getString(R.string.app_name),
            getString(R.string.bi_versionname));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /*
         * Because it's onlt ONE option in the menu.
         * In order to make it simple, We always start SetPreferenceActivity
         * without checking.
         */
        Intent intent = new Intent();
        intent.setClass(this, SettingsActivity.class);
        startActivityForResult(intent, 0);

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
        //super.onActivityResult(requestCode, resultCode, data);

        /*
         * To make it simple, always re-load Preference setting.
         */
        loadPref();
    }

    private void loadPref() {
        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String myListPreference = mySharedPreferences.getString("list_preference", "None selected");
    }

    private int getBet() {
        int res;
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            res = Integer.parseInt(p.getString("list_preference", ""));
        } catch (NumberFormatException e) {
            Log.i(mTAG, "", e);
            res = 0;
        }

        return res;
    }
}
