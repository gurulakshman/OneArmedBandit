package edu.sdu.onearmedbandit;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class SpinActivity extends Activity
{
    private ImageView[] reel;
    String fruit_strings[];
    TextView textv;
    Resources res;

    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spinactivity);
        setTitle(this.titleText());
        res = getResources();

        textv = (TextView) findViewById(R.id.textView);

        // Set ImageViews for the three reels
        reel = new ImageView[3];
        reel[0] = (ImageView) findViewById(R.id.reel0);
        reel[1] = (ImageView) findViewById(R.id.reel1);
        reel[2] = (ImageView) findViewById(R.id.reel2);





        TypedArray fruits = res.obtainTypedArray(R.array.fruits);

        for (int i=0; i<reel.length; i++)
        {
            reel[i].setImageDrawable(fruits.getDrawable(i));
        }

        textv.setText(String.format("Fruit array length: %1$d", fruits.length()));
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

