package nl.waywayway.broodkruimels;

import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v7.preference.*;
import android.util.*;
import java.util.*;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener
{
	public static final String FRAGMENT_FILENAME_PREF_NOTIFY_CATEGORIES = "notify_categories";
	public static final String FRAGMENT_KEY_PREF_NOTIFY_CATEGORIES = "pref_notify_categories";
	Context mContext;
	List<CategoryItem> categoryList;

	public void setCategoryList(List<CategoryItem> categoryList)
	{
		this.categoryList = categoryList;
	}

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
	{
        setPreferencesFromResource(R.xml.preferences, rootKey);
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
		//setTimePickerPreferenceClickAction();
	}

	@Override
	public boolean onPreferenceTreeClick(Preference preference)
	{
		switch (preference.getKey())
		{
			case SettingsActivity.KEY_PREF_NOTIFY_TIME:
				showTimePickerDialog(preference);
				break;
			case SettingsActivity.KEY_PREF_NOTIFY_CATEGORIES:
				showCategoryDialog(preference);
				break;
		}

		return super.onPreferenceTreeClick(preference);
	}

	private void showTimePickerDialog(Preference preference)
	{
		DialogFragment newFragment = new TimePickerFragment();
		newFragment.show(getFragmentManager(), "timePicker");

		// Toast.makeText(mContext, preference.getKey(), Toast.LENGTH_SHORT).show();
	}
	
	private void showCategoryDialog(Preference preference)
	{
		CategoryDialogFragment categoryDialog = new CategoryDialogFragment();
		categoryDialog.setCategoryList((ArrayList<CategoryItem>) categoryList);
		categoryDialog.setPrefFilename(this.FRAGMENT_FILENAME_PREF_NOTIFY_CATEGORIES);
		categoryDialog.setPrefKey(this.FRAGMENT_KEY_PREF_NOTIFY_CATEGORIES);
		categoryDialog.show(getFragmentManager() , "pref_category");
	}

	// Lees wel/niet instelling
	private Boolean getBooleanPref(String key)
	{
		// Lees instelling voor wel of geen melding
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
		return sharedPref.getBoolean(key, false);
	}

	// In switch preference bij summary 'aan' of 'uit' vermelden
	private void setPrefNotifySummary(String prefKey)
	{
		SwitchPreferenceCompat prefNotify = (SwitchPreferenceCompat) getPreferenceManager().findPreference(prefKey);
		Boolean prefNotifyIsSetTo = getBooleanPref(prefKey);

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
		Preference prefTime = getPreferenceManager().findPreference(prefKey);

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

	// Stel alarm voor notification in
	private void setPrefNotifyAlarm(Context context)
	{
		if (!getBooleanPref(SettingsActivity.KEY_PREF_NOTIFY))
		{
			Log.i("HermLog", "setPrefNotifyAlarm(): Instelling is false: return");
			return;
		}

		// Lees ingestelde tijd, als geen ingestelde tijd is gevonden, neem default
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		int prefTimeDefault = getActivity().getResources().getInteger(R.integer.preferences_time_default);
		int minutesAfterMidnight = sharedPref.getInt(SettingsActivity.KEY_PREF_NOTIFY_TIME, prefTimeDefault);

		Log.i("HermLog", "setPrefNotifyAlarm(): minutesAfterMidnight: " + minutesAfterMidnight);

		// Stel alarm in
		MyAlarm mAlarm = new MyAlarm(context);
		mAlarm.setAlarm(minutesAfterMidnight);

		// Alarm opnieuw instellen na opnieuw opstarten apparaat
		// (alle alarmen worden standaard gewist bij opstarten apparaat)
		// Boot receiver in manifest bestand geeft broadcast van opnieuw 
		// opstarten door aan class BootReceiver
		// Onderstaande code houdt 'boot receiver' geregistreerd
		// in het systeem, tot ongedaan maken registratie
		ComponentName receiver = new ComponentName(context, BootReceiver.class);
		PackageManager pm = context.getPackageManager();

		pm.setComponentEnabledSetting(
			receiver,
			PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
			PackageManager.DONT_KILL_APP);
	}

	// Verwijder alarm voor notification
	private void cancelPrefNotifyAlarm(Context context)
	{
		Log.i("HermLog", "cancelPrefNotifyAlarm()");

		MyAlarm mAlarm = new MyAlarm(context);
		mAlarm.cancelAlarm();

		// Onderstaande code maakt registratie 'boot receiver'
		// in het systeem ongedaan
		ComponentName receiver = new ComponentName(context, BootReceiver.class);
		PackageManager pm = context.getPackageManager();

		pm.setComponentEnabledSetting(
			receiver,
			PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
			PackageManager.DONT_KILL_APP);
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
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) 
	{
		switch (key)
		{
			case SettingsActivity.KEY_PREF_NOTIFY:
				cancelPrefNotifyAlarm(mContext);
				setPrefNotifyAlarm(mContext);
				setPrefNotifySummary(key);
				break;
			case SettingsActivity.KEY_PREF_NOTIFY_TIME:
				cancelPrefNotifyAlarm(mContext);
				setPrefNotifyAlarm(mContext);
				setPrefTimeSummary(key);
				break;
		}
	}
}


