package edu.sdu.obap.onearmedbandit;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import edu.sdu.opab13.onearmedbandit.R;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.preferences);
    }
}
