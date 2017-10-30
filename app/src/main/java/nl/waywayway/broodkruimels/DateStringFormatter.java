package nl.waywayway.broodkruimels;

import android.util.*;
import java.text.*;
import java.util.*;

// Singleton voor datum String opmaken

public class DateStringFormatter
{
	private static DateStringFormatter instance;

	private DateStringFormatter(){}

	public static DateStringFormatter getDateStringFormatter()
	{
		if(instance == null)
			instance = new DateStringFormatter();

		return instance;
	}
	
	public String formatDate(String mDateString, String dateFormat)
	{
		try
		{
			Date mDate = new SimpleDateFormat(dateFormat).parse(mDateString);
			String mFormattedDate = DateFormat.getDateInstance(DateFormat.LONG).format(mDate);
			return mFormattedDate;
		}
		catch (Exception e)
		{
			Log.i("HermLog", "DetailActivity: Date format exception in parseResult");
			e.printStackTrace();
		}

		return "";
	}
}
