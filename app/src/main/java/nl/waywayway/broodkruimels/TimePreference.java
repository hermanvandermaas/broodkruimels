package nl.waywayway.broodkruimels;

import android.content.*;
import android.support.v7.preference.*;
import android.util.*;

public class TimePreference extends DialogPreference
{
	private  int  mTime;
	private  int  mDialogLayoutResId  =  R.layout.preferences_timepicker_dialog;

	public TimePreference(Context context)
	{
		this(context, null);
	}

	public TimePreference(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
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
