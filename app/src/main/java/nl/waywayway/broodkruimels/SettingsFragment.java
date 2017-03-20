package nl.waywayway.broodkruimels;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v7.preference.*;
import android.util.*;

public class SettingsFragment extends PreferenceFragmentCompat
{
	Context mContext;

    @Override
    public void onCreatePreferences(Bundle bundle, String s)
	{
        addPreferencesFromResource(R.xml.preferences);
    }

	// code binnen onAttach wordt pas uitgevoerd als dit fragment aan
	// de parent activity is gekoppeld, zodat voor deze code
	// 'context' beschikbaar is
	@Override
	public void onAttach(Context context)
	{
		Log.i("HermLog", "SettingsFragment: onAttach()");

		super.onAttach(context);
		mContext = context;
	}

	@Override
	public void onStart()
	{
		Log.i("HermLog", "SettingsFragment: onStart()");

		super.onStart();

		SwitchPreferenceCompat prefNotify = (SwitchPreferenceCompat) getPreferenceManager().findPreference("pref_notify");
		String prefNotifySummary = (String) prefNotify.getSummary();

		Log.i("HermLog", "prefNotifySummary: " + prefNotifySummary);

		// Lees instelling voor wel of geen melding
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
		Boolean prefNotifyIsSetTo = sharedPref.getBoolean(SettingsActivity.KEY_PREF_NOTIFY, false);

		Log.i("HermLog", "prefNotifyIsSetTo: " + prefNotifyIsSetTo);
		
		String on = mContext.getResources().getString(R.string.pref_notify_time_on);
		String off = mContext.getResources().getString(R.string.pref_notify_time_off);
		String notifySetting;

		if (prefNotifyIsSetTo == true)
		{
			notifySetting = on;
		}
		else
		{
			notifySetting = off;
		}

		// Geef de instelling met tekst weer in de preference summary
		// als 'aan' of 'uit'
		prefNotify.setSummary(prefNotifySummary + ": " + notifySetting);
	}
}


