package nl.waywayway.broodkruimels;

import android.content.*;
import android.util.*;

// Maak kommagescheiden string van de categoriecijfers
// opgeslagen in SharedPreferences

public class CategoryGetter
{
	private Context context;
	private String filename;
	private String key;

	public CategoryGetter(Context context, String filename, String key)
	{
		this.context = context;
		this.filename = filename;
		this.key = key;
	}

	public String getCategories()
	{
		SharedPreferences sharedPref = context.getSharedPreferences(filename, context.MODE_PRIVATE);
		String prefDefault = "";
		String savedCategoriesString = sharedPref.getString(key, prefDefault);
		Log.i("HermLog", "CategoryGetter: savedCategoriesString: " + savedCategoriesString);
		String commaSeparatedList = savedCategoriesString.replaceAll("\\[|\\]", "").replaceAll("\\s", "");
		// Log.i("HermLog", "CommaSeparatedList: " + commaSeparatedList);

		return commaSeparatedList;
	}
}
