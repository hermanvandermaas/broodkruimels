package nl.waywayway.broodkruimels;

import android.content.*;
import android.content.res.*;
import android.os.*;
import android.support.v7.preference.*;
import android.text.format.*;
import android.util.*;
import android.view.*;
import android.widget.*;

public class TimePreference extends Preference
{
	private TimePicker mTimePicker = null;
	private int mTime; // De tijd in hele minuten na middernacht
    private int mDialogLayoutResId = R.layout.preferences_timepicker_dialog;

	// 4 constructors voor verschillende API levels,
	// die elkaar in volgorde aanroepen,
	// beginnend met de constructor met de minste parameters

	public TimePreference(Context context)
	{
		this(context, null);
	}

	public TimePreference(Context context, AttributeSet attrs)
	{
		this(context, attrs, R.attr.preferenceStyle);
	}

	public  TimePreference(Context context, AttributeSet attrs, int defStyleAttr)
	{
		this(context, attrs, defStyleAttr, defStyleAttr);
	}

	public  TimePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
	}
	
	// Getter en setter voor de tijd
	public int getTime()
	{
		Log.i("HermLog", "getTime(): " + mTime);

		return  mTime;
	}

	public void setTime(int time)
	{
		mTime = time;

		// Save to Shared Preferences
		persistInt(time);

		Log.i("HermLog", "setTime(): " + time);
	}
	
	private void onBindDialogView(View view)
	{
		Log.i("HermLog", "onBindDialogView()");

		mTimePicker = (TimePicker) view.findViewById(R.id.preferences_timepicker);

		if (mTimePicker == null)
		{
			throw new IllegalStateException("Dialog view must contain a TimePicker with id 'preferences_timepicker'");
		}

		// Get the time from the related Preference
		Integer minutesAfterMidnight = null;
		TimePreference preference = (TimePreference) findPreferenceInHierarchy("pref_notify_time");
		minutesAfterMidnight = preference.getTime();

		// Set the time to the TimePicker
		if (minutesAfterMidnight != null)
		{
			int hours = minutesAfterMidnight / 60;
			int minutes = minutesAfterMidnight % 60;
			boolean is24hour = DateFormat.is24HourFormat(getContext());

			mTimePicker.setIs24HourView(is24hour);
			
			if (Build.VERSION.SDK_INT >= 23)
			{
				mTimePicker.setHour(hours);
				mTimePicker.setMinute(minutes);
			}
			else
			{
				mTimePicker.setCurrentHour(hours);
				mTimePicker.setCurrentMinute(minutes);
			}

			Log.i("HermLog", "onBindDialogView(): timepicker gezet op: " + hours + ":" + minutes);
		}
	}

	private void onDialogClosed(boolean positiveResult)
	{
		Log.i("HermLog", "onDialogClosed()");

		if (positiveResult)
		{
			Log.i("HermLog", "onDialogClosed() : positiveresult");

			// Get the current values from the TimePicker
			int hours;
			int minutes;
			if (Build.VERSION.SDK_INT >= 23)
			{
				hours = mTimePicker.getHour();
				minutes = mTimePicker.getMinute();
			}
			else
			{
				hours = mTimePicker.getCurrentHour();
				minutes = mTimePicker.getCurrentMinute();
			}

			// Generate value to save
			int minutesAfterMidnight = (hours * 60) + minutes;

			// Save the value
			TimePreference timePreference = (TimePreference) findPreferenceInHierarchy("pref_notify_time");
			
			// This allows the client to ignore the user value.
			if (timePreference.callChangeListener(minutesAfterMidnight))
			{
				// Save the value
				timePreference.setTime(minutesAfterMidnight);

				Log.i("HermLog", "onDialogClosed: bewaarde tijd in minuten na 00:00 : " + minutesAfterMidnight);
			}
		}
	}
}
