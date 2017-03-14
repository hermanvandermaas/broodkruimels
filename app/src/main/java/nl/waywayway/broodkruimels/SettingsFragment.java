package nl.waywayway.broodkruimels;

import android.os.*;
import android.support.v7.preference.*;

public class SettingsFragment extends PreferenceFragmentCompat
{
    @Override
    public void onCreatePreferences(Bundle bundle, String s)
	{
        addPreferencesFromResource(R.xml.preferences);
    }
}


