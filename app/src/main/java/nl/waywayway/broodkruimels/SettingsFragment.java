package nl.waywayway.broodkruimels;

import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v7.preference.*;
import android.util.*;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener
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

	// In switch preference bij summary 'aan' of 'uit' vermelden
	private void setPrefNotifySummary(String prefKey)
	{
		SwitchPreferenceCompat prefNotify = (SwitchPreferenceCompat) getPreferenceManager().findPreference(prefKey);

		// Lees instelling voor wel of geen melding
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
		Boolean prefNotifyIsSetTo = sharedPref.getBoolean(prefKey, false);

		Log.i("HermLog", "prefNotifyIsSetTo: " + prefNotifyIsSetTo);

		// Lees de summary
		String prefNotifySumm = mContext.getResources().getString(R.string.pref_notify_summ);

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
		prefNotify.setSummary(prefNotifySumm + ": " + notifySetting);
	}
	
	// In time preference bij summary de ingestelde tijd vermelden
	private void setPrefTimeSummary(String prefKey)
	{
		TimePreference prefTime = (TimePreference) getPreferenceManager().findPreference(prefKey);

		// Lees ingestelde tijd, als geen ingestelde tijd is gevonden, neem default
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
		int prefTimeDefault = getActivity().getResources().getInteger(R.integer.preferences_time_default);
		int minutesAfterMidnight = sharedPref.getInt(prefKey, prefTimeDefault);
		int hours = minutesAfterMidnight / 60;
		int minutes = minutesAfterMidnight % 60;

		// Lees de summary, zet voorloopnullen in de tijd
		String prefTimeSumm = mContext.getResources().getString(R.string.pref_notify_time_summ);
		String timeFormatted = String.format("%02d:%02d", hours, minutes);
		
		Log.i("HermLog", "setPrefTimeSummary(): " + timeFormatted);

		// Geef de instelling met tekst weer in de preference summary
		prefTime.setSummary(prefTimeSumm + ": " + timeFormatted);
	}

	@Override
	public void onStart()
	{
		Log.i("HermLog", "SettingsFragment: onStart()");

		super.onStart();

		setPrefNotifySummary(SettingsActivity.KEY_PREF_NOTIFY);
		setPrefTimeSummary(SettingsActivity.KEY_PREF_NOTIFY_TIME);
	}

	@Override
	public void onResume()
	{
		super.onResume();

		getPreferenceManager().getSharedPreferences()
			.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause()
	{
		super.onPause();

		getPreferenceManager().getSharedPreferences()
			.unregisterOnSharedPreferenceChangeListener(this);
	}

	// Bij verandering in instelling, geef de nieuwe instelling direct weer in de summary
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) 
	{
		switch (key)
		{
			case SettingsActivity.KEY_PREF_NOTIFY:
				setPrefNotifySummary(key);
				break;
			case SettingsActivity.KEY_PREF_NOTIFY_TIME:
				setPrefTimeSummary(key);
				break;
		}
	}

	@Override
    public void onDisplayPreferenceDialog(Preference preference)
	{
        // Try if the preference is one of our custom Preferences
        DialogFragment dialogFragment = null;

        if (preference instanceof TimePreference)
		{
            // Create a new instance of TimePreferenceDialogFragment with the key of the related
            // Preference
            dialogFragment = TimePreferenceDialogFragmentCompat.newInstance(preference.getKey());
        }


        if (dialogFragment != null)
		{
            // The dialog was created (it was one of our custom Preferences), show the dialog for it
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(this.getFragmentManager(), "android.support.v7.preference" +
								".PreferenceFragment.DIALOG");
        }
		else
		{
            // Dialog creation could not be handled here. Try with the super method.
            super.onDisplayPreferenceDialog(preference);
        }

    }
}


