package nl.waywayway.broodkruimels;

import android.preference.*;

public class MyPreferenceFragment extends PreferenceFragment
{
	public TimePreference getTimePreference()
	{
		TimePreference prefTime = (TimePreference) findPreference("prefKey");
		return prefTime;
	}
	
	
}
