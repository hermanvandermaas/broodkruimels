package nl.waywayway.broodkruimels;

import android.app.*;
import android.content.*;
import android.support.annotation.*;
import android.util.*;
import android.view.*;

// Singleton voor bepalen breedte van de app: 'narrow' of 'wide'
// en andere meetwaarden
// grenswaarde staat in xml bestand resources / values / integers

public class AppWidthFinder
{
	private static AppWidthFinder instance;
	
	private AppWidthFinder(){}
	
	public static AppWidthFinder getAppWidthFinder()
	{
		if(instance == null)
			instance = new AppWidthFinder();
		
		return instance;
	}
	
	private int getWidth(View view)
	{
		// Vind breedte van de app in dp
		// dp = pixels / logical density
		int viewWidth = view.getWidth();
		float logicalDensity = getLogicalDensity(view);
		int appWidthDp = Math.round(viewWidth / logicalDensity);

		Log.i("HermLog", "AppWidthDp: " + appWidthDp);
		
		return appWidthDp;
	}
	
	public String getWidthString(View view, int listviewMaxWidth)
	{
		// Bepaal of de weergave van de app smal of breed is
		if (getWidth(view) <= listviewMaxWidth)
			return "narrow";
		else
			return "wide";
	}
	
	public int getWidthInt(View view)
	{
		return getWidth(view);
	}
	
	public float getLogicalDensity(View view)
	{
		float logicalDensity = view.getContext().getResources().getDisplayMetrics().density;
		Log.i("HermLog", "logicalDensity: " + logicalDensity);
		
		return logicalDensity;
	}
}
