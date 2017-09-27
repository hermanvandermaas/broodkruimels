package nl.waywayway.broodkruimels;

import android.content.*;
import android.util.*;
import com.google.gson.*;
import com.google.gson.reflect.*;
import java.lang.reflect.*;
import java.util.*;
import org.json.*;

// Hulp class voor CategoryDialogFragment, om gekozen categorieen 
// op te slaan in en op te halen uit SharedPreferences
// de lijst van integers wordt opgeslagen als simpel (JSON) array in String vorm, bv. [2,5,67]

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
		
		ArrayList<Integer> savedCategoriesList = new ArrayList<Integer>();
		
		if (savedCategoriesString.isEmpty())
			return savedCategoriesList;

		Gson gson = new Gson();
		Type listType = new TypeToken<ArrayList<Integer>>(){}.getType();
		savedCategoriesList = gson.fromJson(savedCategoriesString, listType);
		Log.i("HermLog", "savedCategoriesList: " + Arrays.toString(savedCategoriesList.toArray()));
		
		Collections.sort(savedCategoriesList);
		return savedCategoriesList;
	}
}
