package nl.waywayway.broodkruimels;

import android.app.*;
import android.content.*;
import android.support.annotation.*;
import android.util.*;
import android.view.*;

// Bepaal breedte van de app: 'narrow' of 'wide'
// grenswaarde staat in xml bestand resources / values / integers
// Singleton

public class AppWidthFinder
{
	private View view;
	private float logicalDensity;
	int appWidthDp;
	
	private static AppWidthFinder instance;
	
	private AppWidthFinder(){}
	
	public static AppWidthFinder getAppWidthFinder()
	{
		if(instance == null)
			instance = new AppWidthFinder();
		
		return instance;
	}
	
	private int getWidth()
	{
		// Vind breedte van de app in dp
		// dp = pixels / logical density
		// de gemeten breedte is de breedte van de hoogste view in het xml layout bestand
		int viewWidth = view.getWidth();
		getLogicalDensity(null);
		appWidthDp = Math.round(viewWidth / logicalDensity);

		Log.i("HermLog", "AppWidthDp: " + appWidthDp);
		
		return appWidthDp;
	}
	
	public String getWidthString(View view, int listviewMaxWidth)
	{
		this.view = view;
		
		// Bepaal of de weergave van de app smal of breed is
		if (getWidth() <= listviewMaxWidth)
			return "narrow";
		else
			return "wide";
	}
	
	public int getWidthInt(View view)
	{
		this.view = view;
		return getWidth();
	}
	
	public float getLogicalDensity(@Nullable View view)
	{
		if (context != null) 
			this.activity = (Activity) context;
		DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
		logicalDensity = displayMetrics.density;
		
		Log.i("HermLog", "logicalDensity: " + logicalDensity);
		
		return logicalDensity;
	}
}
