package nl.waywayway.broodkruimels;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v7.app.*;
import android.util.*;
import android.widget.*;
import java.util.*;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class CategoryDialogFragment extends DialogFragment
{
	private Context mContext;
	private String prefFilename;
	private String prefKey;
	private CategorySaveAndRestore catSaveRestore;
	private DownloadCategories mDownloadCategories;
	private List<CategoryItem> categoryList;
	private String[] categoryNameArray;
	private Integer[] categoryNumberArray;
	private boolean[] categoryCheckedArray;
	private ArrayList<Integer> mSelectedItems;

	public interface DownloadCategories
	{
		public void downloadFromCategories();
	}

	public void setCategoryList(List<CategoryItem> categoryList)
	{
		this.categoryList = categoryList;
	}

	public void setPrefFilename(String filename)
	{
		this.prefFilename = filename;
	}

	public void setPrefKey(String key)
	{
		this.prefKey = key;
	}

	// code binnen onAttach wordt pas uitgevoerd als dit fragment aan
	// de parent activity is gekoppeld, zodat voor deze code
	// 'context' beschikbaar is
	@Override
	public void onAttach(Context context)
	{
		Log.i("HermLog", "CategoryDialogFragment: onAttach(), to: " + ((Activity) context).getLocalClassName());

		super.onAttach(context);
		mContext = context;

		// Maak referentie naar in dit fragment als interface
		// gedefinieerde method geimplementeerd in gekoppelde Activity,
		// om die method vanuit dit fragment aan te kunnen roepen

		if (context instanceof DownloadCategories)
		{
            mDownloadCategories = (DownloadCategories) context;
        }
		else
		{
            throw new RuntimeException(context.toString() + " must implement DownloadCategories");
        }
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		Log.i("HermLog", "CategoryDialogFragment: onCreateDialog()");
		
		// Als savedInstanceState opgeslagen, bv na schermrotatie, herstel oude toestand
		// zo niet, haal gekozen categorieen op uit SharedPreferences
		if (savedInstanceState != null)
		{
			Log.i("HermLog", "CategoryDialogFragment: restore savedInstanceState");
			super.onCreateDialog(savedInstanceState);
			categoryNameArray = savedInstanceState.getStringArray("savedCategoryArray");
			categoryNumberArray = (Integer[]) savedInstanceState.getSerializable("savedNumberArray");
			mSelectedItems = savedInstanceState.getIntegerArrayList("savedSelectedItems");
			prefFilename = savedInstanceState.getString("savedPrefFilename");
			prefKey = savedInstanceState.getString("savedPrefKey");
			catSaveRestore = new CategorySaveAndRestore(mContext, prefFilename, prefKey);
			Log.i("HermLog", "mSelectedItems na restore savedInstanceState: " + Arrays.toString(mSelectedItems.toArray()));
		}
		else
		{
			// mSelectedItems is een ArrayList met de categorienummers uit WordPress,
			// mSelectedItems bevat niet de oplopende volgnummers 'which' van de lijst in de dialog
			catSaveRestore = new CategorySaveAndRestore(mContext, prefFilename, prefKey);
			mSelectedItems = catSaveRestore.restoreCategories();
			categoryNameArray = makeCategoryArray((ArrayList<CategoryItem>) categoryList);
		}
		
		Log.i("HermLog", "CategoryDialogFragment: prefFilename: " + prefFilename);
		Log.i("HermLog", "CategoryDialogFragment: prefKey: " + prefKey);

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
						Toast.makeText(mContext, "Klik", Toast.LENGTH_SHORT).show();
					}
					else if (mSelectedItems.contains(categoryNumberArray[which]))
					{
						// Else, if the item is already in the array, remove it
						mSelectedItems.remove(categoryNumberArray[which]);
						Log.i("HermLog", "mSelectedItems na remove: " + Arrays.toString(mSelectedItems.toArray()));
						Toast.makeText(mContext, "Klik", Toast.LENGTH_SHORT);
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
					ArrayList<Integer> savedCategories = catSaveRestore.restoreCategories();
					Log.i("HermLog", "savedCategories gesorteerd: " + savedCategories.toString());

					// Alleen opnieuw downloaden als andere categorieen zijn gekozen
					// dan voor meest recente download
					if (!mSelectedItems.equals(savedCategories))
					{
						Log.i("HermLog", "Categorie selectie gewijzigd");

						if (catSaveRestore.saveCategories(mSelectedItems))
							Log.i("HermLog", "Categorieen opgeslagen");
						else
							Log.i("HermLog", "Fout: categorieen niet opgeslagen");

						mDownloadCategories.downloadFromCategories();
						// ((MainActivity) mContext).downloadFromCategories();
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
					// Doe niets Tammo
				}
			});

		return builder.create();
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		Log.i("HermLog", "CategoryDialogFragment: save savedInstanceState");
		super.onSaveInstanceState(outState);

		if (categoryNameArray != null
			&& categoryNumberArray != null
			&& mSelectedItems != null
			&& prefFilename != null
			&& prefKey != null)
		{
			outState.putStringArray("savedCategoryArray", categoryNameArray);
			outState.putSerializable("savedNumberArray", categoryNumberArray);
			outState.putIntegerArrayList("savedSelectedItems", mSelectedItems);
			outState.putString("savedPrefFilename", prefFilename);
			outState.putString("savedPrefKey", prefKey);
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
		// dus Boolean[] (variabele lengte) naar primitive boolean[] (vaste lengte) omzetten, kan niet 
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
