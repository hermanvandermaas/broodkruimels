package nl.waywayway.broodkruimels;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v7.preference.*;
import android.text.format.*;
import android.util.*;
import android.widget.*;

import android.support.v4.app.DialogFragment;

public class TimePickerFragment extends DialogFragment
	implements TimePickerDialog.OnTimeSetListener
{
	private Context mContext;
	private int mTime; // De tijd in hele minuten na middernacht

	// code binnen onAttach wordt pas uitgevoerd als dit fragment aan
	// de parent activity is gekoppeld, zodat voor deze code
	// 'context' beschikbaar is
	@Override
	public void onAttach(Context context)
	{
		Log.i("HermLog", "TimePickerFragment: onAttach()");

		super.onAttach(context);
		mContext = context;
	}

	// Getter en setter voor de tijd
	public int getTime()
	{
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
		int prefDefault = mContext.getResources().getInteger(R.integer.preferences_time_default);
		mTime = sharedPref.getInt(SettingsActivity.KEY_PREF_NOTIFY_TIME, prefDefault);
		
		Log.i("HermLog", "getTime(): " + mTime);
		
		return  mTime;
	}

	public void setTime(int time)
	{
		mTime = time;

		// Save to Shared Preferences
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt(SettingsActivity.KEY_PREF_NOTIFY_TIME, time);
		editor.commit();

		Log.i("HermLog", "setTime(): " + time);
	}

	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		int minutesAfterMidnight = getTime();
		int hour = minutesAfterMidnight / 60;
		int minute = minutesAfterMidnight % 60;

		Log.i("HermLog", "onCreateDialog(), tijd: " + hour + ":" + minute);

		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(
			mContext, 
			this, 
			hour, 
			minute,
			DateFormat.is24HourFormat(mContext)
		);
	}

	@Override
	public void onTimeSet(TimePicker view, int hour, int minute)
	{
		int minutesAfterMidnight = (hour * 60) + minute;
		setTime(minutesAfterMidnight);
		
		Log.i("HermLog", "onTimeSet: bewaarde tijd: " + hour + ":" + minute);
    }
}
