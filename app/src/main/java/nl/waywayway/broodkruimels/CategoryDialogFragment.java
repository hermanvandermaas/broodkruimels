package nl.waywayway.broodkruimels;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v7.app.*;
import android.support.v7.preference.*;
import android.util.*;
import java.util.*;
import org.json.*;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import java.net.*;

public class CategoryDialogFragment extends DialogFragment
{
	public static final String KEY_PREF_CATEGORIES = "pref_categories";
	private Context mContext;
	private List<CategoryItem> categoryList;
	private String[] categoryNameArray;
	private Integer[] categoryNumberArray;
	private boolean[] categoryCheckedArray;
	private ArrayList<Integer> mSelectedItems;

	public interface DownloadCategories
	{
		//ggg
	}
	
	public void setCategoryList(List<CategoryItem> categoryList)
	{
		this.categoryList = categoryList;
	}

	// code binnen onAttach wordt pas uitgevoerd als dit fragment aan
	// de parent activity is gekoppeld, zodat voor deze code
	// 'context' beschikbaar is
	@Override
	public void onAttach(Context context)
	{
		Log.i("HermLog", "CategoryDialogFragment: onAttach()");

		super.onAttach(context);
		mContext = context;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// Als savedInstanceState opgeslagen, bv na schermrotatie, herstel oude toestand
		// zo niet, haal gekozen categorieen op uit SharedPreferences
		if (savedInstanceState != null)
		{
			Log.i("HermLog", "CategoryDialogFragment: restore savedInstanceState");
			super.onCreateDialog(savedInstanceState);
			categoryNameArray = savedInstanceState.getStringArray("savedCategoryArray");
			categoryNumberArray = (Integer[]) savedInstanceState.getSerializable("savedNumberArray");
			mSelectedItems = savedInstanceState.getIntegerArrayList("savedSelectedItems");
		}
		else
		{
			// mSelectedItems is een ArrayList met de categorienummers uit WordPress,
			// mSelectedItems is niet het volgnummer 'which' van de lijst in de dialog
			mSelectedItems = restoreCategories();
			categoryNameArray = makeCategoryArray((ArrayList<CategoryItem>) categoryList);
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder
			.setTitle(R.string.dialog_category_title)
			.setMultiChoiceItems(categoryNameArray, categoryCheckedArray, new DialogInterface.OnMultiChoiceClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked)
				{
					if (isChecked)
					{
						// If the user checked the item, add it to the selected items
						mSelectedItems.add(categoryNumberArray[which]);
						Log.i("HermLog", "mSelectedItems na add: " + Arrays.toString(mSelectedItems.toArray()));
					}
					else if (mSelectedItems.contains(categoryNumberArray[which]))
					{
						// Else, if the item is already in the array, remove it
						mSelectedItems.remove(categoryNumberArray[which]);
						Log.i("HermLog", "mSelectedItems na remove: " + Arrays.toString(mSelectedItems.toArray()));
					}
				}
			})
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					// FIRE ZE MISSILES!
					// :-)

					Collections.sort(mSelectedItems);
					Log.i("HermLog", "mSelectedItems gesorteerd: " + mSelectedItems.toString());
					ArrayList<Integer> savedCategories = restoreCategories();
					Log.i("HermLog", "savedCategories gesorteerd: " + savedCategories.toString());
					
					// Alleen opnieuw downloaden als andere categorieen zijn gekozen
					// dan voor meest recente download
					if (!mSelectedItems.equals(savedCategories))
					{
						Log.i("HermLog", "Categorie selectie gewijzigd");

						if (saveCategories())
							Log.i("HermLog", "Categorieen opgeslagen");
						else
							Log.i("HermLog", "Fout: categorieen niet opgeslagen");

						((MainActivity) mContext).downloadFromCategories();
					}
					else
					{
						Log.i("HermLog", "Categorie selectie niet gewijzigd");
					}
				}
			})
			.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{

				}
			});

		return builder.create();
	}

	// Gekozen categorieen opslaan in SharedPreferences
	private Boolean saveCategories()
	{
		// Maak JSON string van ArrayList
		JSONArray categoriesJsonArray = new JSONArray(mSelectedItems);

		// Save to Shared Preferences
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(this.KEY_PREF_CATEGORIES, categoriesJsonArray.toString());

		Log.i("HermLog", "saveCategories(): " + categoriesJsonArray.toString());

		return editor.commit();
	}

	// Gekozen categorieen ophalen uit SharedPreferences
	// de default is: alle categorieen geselecteerd
	private ArrayList<Integer> restoreCategories()
	{
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
		String prefDefault = "";
		String savedCategoriesString = sharedPref.getString(this.KEY_PREF_CATEGORIES, prefDefault);
		Log.i("HermLog", "CategoryDialogFragment: savedCategoriesString: " + savedCategoriesString);

		// Maak JSONarray van string
		JSONArray categoriesJsonArray = null;

		try
		{
			categoriesJsonArray = new JSONArray(savedCategoriesString);
		}
		catch (JSONException e)
		{
			Log.i("HermLog", "JSON Exception in restoreCategories, categoriesJsonArray");
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

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		Log.i("HermLog", "CategoryDialogFragment: save savedInstanceState");
		super.onSaveInstanceState(outState);

		if (categoryNameArray != null
			&& categoryNumberArray != null
			&& mSelectedItems != null)
		{
			outState.putStringArray("savedCategoryArray", categoryNameArray);
			outState.putSerializable("savedNumberArray", categoryNumberArray);
			outState.putIntegerArrayList("savedSelectedItems", mSelectedItems);
		}
	}

	private String[] makeCategoryArray(ArrayList<CategoryItem> categoryList)
	{
		// Maak ArrayList<String> van ArrayList<CategoryItem>
		// maak daarna String[] van ArrayList<String>
		// Filter niet gewenste categorieen er uit
		// maak boolean[] voor aangevinkte categorieen
		ArrayList<String> categoryNameArrayList = new ArrayList<String>();
		ArrayList<Integer> categoryNumberArrayList = new ArrayList<Integer>();
		ArrayList<Boolean> categoryCheckedArrayList = new ArrayList<Boolean>();
		int[] exclude_children = getResources().getIntArray(R.array.parent_categories_exclude_children);
		int[] exclude_categories = getResources().getIntArray(R.array.categories_exclude);

		for (CategoryItem item : categoryList)
		{
			String name = item.getName();
			Integer number = item.getNumber();
			Boolean checked = mSelectedItems.size() == 0 ? true : mSelectedItems.contains(number);

			if (!arrayContains(exclude_children, item.getParent())
				&& !arrayContains(exclude_categories, item.getNumber()))
			{
				categoryNameArrayList.add(name);
				categoryNumberArrayList.add(number);
				categoryCheckedArrayList.add(checked);
			}
		}

		categoryNameArray = categoryNameArrayList.toArray(new String[0]);
		categoryNumberArray = categoryNumberArrayList.toArray(new Integer[0]);
		categoryCheckedArray = new boolean[categoryCheckedArrayList.size()];

		// Er is helaas pindakaas per se een boolean[] nodig in setMultiChoiceItems()
		// dus Boolean[] (variabele lengte) naar boolean[] (vaste lengte) omzetten, kan niet 
		// anders dan met een loop
		int i = 0;
		for (boolean yesOrNo : categoryCheckedArrayList)
		{
			categoryCheckedArray[i] = yesOrNo;
			i++;
		}

		// Log.i("HermLog", "categoryCheckedArray: " + Arrays.toString(categoryCheckedArray) + "  lengte: " + categoryCheckedArray.length);
		// Log.i("HermLog", "categoryNameArray: " + Arrays.toString(categoryNameArray) + "  lengte: " + categoryNameArray.length);
		// Log.i("HermLog", "categoryNumberArray: " + Arrays.toString(categoryNumberArray) + "  lengte: " + categoryNumberArray.length);

		return categoryNameArray;
	}

	// Test of getal in getallen(int)array zit
	private boolean arrayContains(int[] array, int key)
	{  
		Arrays.sort(array);
		return Arrays.binarySearch(array, key) >= 0;  
	}
}
