package nl.waywayway.broodkruimels;

import android.os.*;
import android.support.v7.preference.*;
import android.text.format.*;
import android.util.*;
import android.view.*;
import android.widget.*;

public class TimePreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat
{
	/**
	 * The TimePicker widget
	 */
	private TimePicker mTimePicker;

	/**
	 * Creates a new Instance of the TimePreferenceDialogFragment and stores the key of the
	 * related Preference
	 *
	 * @param key The key of the related Preference
	 * @return A new Instance of the TimePreferenceDialogFragment
	 */
	public static TimePreferenceDialogFragmentCompat newInstance(String key)
	{
		final TimePreferenceDialogFragmentCompat
			fragment = new TimePreferenceDialogFragmentCompat();
		final Bundle b = new Bundle(1);
		b.putString(ARG_KEY, key);
		fragment.setArguments(b);

		return fragment;
	}

	@Override
	protected void onBindDialogView(View view)
	{
		Log.i("HermLog", "onBindDialogView()");
		
		super.onBindDialogView(view);

		mTimePicker = (TimePicker) view.findViewById(R.id.preferences_timepicker);

		if (mTimePicker == null)
		{
			throw new IllegalStateException("Dialog view must contain a TimePicker with id 'preferences_timepicker'");
		}

		// Get the time from the related Preference
		Integer minutesAfterMidnight = null;
		DialogPreference preference = getPreference();
		if (preference instanceof TimePreference)
		{
			minutesAfterMidnight = ((TimePreference) preference).getTime();
		}

		// Set the time to the TimePicker
		if (minutesAfterMidnight != null)
		{
			int hours = minutesAfterMidnight / 60;
			int minutes = minutesAfterMidnight % 60;
			boolean is24hour = DateFormat.is24HourFormat(getContext());

			mTimePicker.setIs24HourView(is24hour);
			mTimePicker.setCurrentHour(hours);
			mTimePicker.setCurrentMinute(minutes);
			
			Log.i("HermLog", "onBindDialogView(): timepicker gezet op: " + hours + ":" + minutes);
		}
	}

	/**
	 * Called when the Dialog is closed.
	 *
	 * @param positiveResult Whether the Dialog was accepted or canceled.
	 */
	@Override
	public void onDialogClosed(boolean positiveResult)
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
			DialogPreference preference = getPreference();
			if (preference instanceof TimePreference)
			{
				TimePreference timePreference = ((TimePreference) preference);
				
				// This allows the client to ignore the user value.
				if (timePreference.callChangeListener(minutesAfterMidnight))
				{
					// Save the value
					timePreference.setTime(minutesAfterMidnight);
					
					Log.i("HermLog", "onDialogClosed: bewaarde tijd: " + minutesAfterMidnight);
				}
			}
		}
	}
}
