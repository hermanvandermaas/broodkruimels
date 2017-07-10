package nl.waywayway.broodkruimels;

import android.content.*;
import android.support.v7.preference.*;
import android.util.*;

public class TimePreference extends Preference
{
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
	
	
}
