package nl.waywayway.broodkruimels;

import android.content.*;
import android.content.res.*;
import android.support.v7.preference.*;
import android.util.*;
import android.view.*;
import android.widget.*;

public class TimePreference extends DialogPreference
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

		/*		
		 setPositiveButtonText("Ok");
		 setNegativeButtonText("Annuleren");
		 */
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

	@Override
    public int getDialogLayoutResource()
	{
        return mDialogLayoutResId;
    }

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index)
	{
		//  Default  value  from  attribute.  Fallback  value  is  set  to  0.
		return a.getInt(index,  0);
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue)
	{
		// Read the value. Use the default value if it is not possible.
		setTime(restorePersistedValue ?
				getPersistedInt(mTime) : (int) defaultValue);
	}

	@Override
    protected View onCreateDialogView()
	{
        mTimePicker = new TimePicker(getContext());

        return(mTimePicker);
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
