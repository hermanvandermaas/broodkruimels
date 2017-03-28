package nl.waywayway.broodkruimels;

import android.content.*;
import android.content.res.*;
import android.support.v7.preference.*;
import android.util.*;

public class TimePreference extends DialogPreference
{
	// De tijd in hele minuten na middernacht
	private int mTime;

	/**
     * Resource of the dialog layout
     */
    private int mDialogLayoutResId = R.layout.preferences_dialog_timepicker;
	
	// 4 constructors voor verschillende API levels,
	// die elkaar in volgorde aanroepen, beginnend met de constructor met de minste parameters

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
		return  mTime;
	}

	public void setTime(int time)
	{
		mTime = time;

		// Save to Shared Preferences
		persistInt(time);
	}

	@Override
    public int getDialogLayoutResource() {
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
}
