package nl.waywayway.broodkruimels;

import android.content.*;
import android.util.*;
import java.util.*;
import org.json.*;

// Hulp class voor CategoryDialogFragment, om gekozen categorieen 
// op te slaan in en op te halen uit SharedPreferences
// de lijst van integers wordt opgeslagen als simpel (JSON) array, bv. [2,5,67]

public class CategorySaveAndRestore
{
	private Context mContext;
	private String preferenceFileName;
	private String preferenceKey;

	public CategorySaveAndRestore(Context context, String preferenceFileName, String key)
	{
		this.mContext = context;
		this.preferenceFileName = preferenceFileName;
		this.preferenceKey = key;
	}

	// Gekozen categorieen opslaan in SharedPreferences
	public Boolean saveCategories(ArrayList<Integer> mSelectedItems)
	{
		// Maak JSON string van ArrayList
		JSONArray categoriesJsonArray = new JSONArray(mSelectedItems);

		// Opslaan in Shared Preferences
		SharedPreferences categoriesPref = mContext.getSharedPreferences(preferenceFileName, mContext.MODE_PRIVATE);
		SharedPreferences.Editor edit = categoriesPref.edit();
		edit.putString(preferenceKey, categoriesJsonArray.toString());

		Log.i("HermLog", "CategorySaveAndRestore: savedCategories(): " + categoriesJsonArray.toString());

		return edit.commit();
	}
	
	// Gekozen categorieen ophalen uit SharedPreferences
	// de default is: alle categorieen geselecteerd
	public ArrayList<Integer> restoreCategories()
	{
		SharedPreferences sharedPref = mContext.getSharedPreferences(preferenceFileName, mContext.MODE_PRIVATE);
		String prefDefault = "";
		String savedCategoriesString = sharedPref.getString(preferenceKey, prefDefault);
		Log.i("HermLog", "CategorySaveAndRestore: restoredCategoriesString: " + savedCategoriesString);

		// Maak JSONarray van string
		JSONArray categoriesJsonArray = null;

		try
		{
			categoriesJsonArray = new JSONArray(savedCategoriesString);
		}
		catch (JSONException e)
		{
			Log.i("HermLog", "JSON Exception in method restoreCategories, field categoriesJsonArray");
            e.printStackTrace();
        }

		ArrayList<Integer> savedCategoriesList = new ArrayList<Integer>();

		if (categoriesJsonArray != null)
		{
			int len = categoriesJsonArray.length();
			for (int i=0; i < len; i++)
			{
				String val = null;
				try
				{
					val = categoriesJsonArray.get(i).toString();
				}
				catch (JSONException e)
				{
					Log.i("HermLog", "JSON Exception in restoreCategories, savedCategoriesList");
					e.printStackTrace();
				}

				savedCategoriesList.add(Integer.valueOf(val));
			} 
		} 

		// Log.i("HermLog", "savedCategoriesList: " + savedCategoriesList.toString());
		// Log.i("HermLog", "savedCategoriesList.size(): " + savedCategoriesList.size());

		Collections.sort(savedCategoriesList);
		return savedCategoriesList;
	}
}
