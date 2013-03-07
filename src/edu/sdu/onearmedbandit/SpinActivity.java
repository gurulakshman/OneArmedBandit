package edu.sdu.onearmedbandit;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SpinActivity extends Activity
{
    TextView versionText;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spinactivity);

        versionText = (TextView) findViewById(R.id.textView01);

        setTitle(this.titleText());



    }




    public String titleText()
    {
        return String.format("%1$s (%2$s)",
                getString(R.string.app_name),
                getString(R.string.bi_versionname));
    }
}

