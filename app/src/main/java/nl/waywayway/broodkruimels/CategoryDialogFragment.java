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
	private List<CategoryItem> categoryList;
	private String[] categoryNameArray;
	private Integer[] categoryNumberArray;
	private ArrayList<Integer> mSelectedItems;

	public void setCategoryList(List<CategoryItem> categoryList)
	{
		this.categoryList = categoryList;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		if (savedInstanceState != null)
		{
			Log.i("HermLog", "CategoryDialogFragment: restore savedInstanceState");
			super.onCreateDialog(savedInstanceState);
			categoryNameArray = savedInstanceState.getStringArray("savedCategoryArray");
		}
		else
		{
			categoryNameArray = makeCategoryArray((ArrayList<CategoryItem>) categoryList);
		}

		// mSelectedItems is een ArrayList met de categorienummers uit WordPress,
		// niet het volgnummer 'which' van de lijst in de dialog
		mSelectedItems = new ArrayList<>();

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder
			.setTitle(R.string.dialog_category_title)
			.setMultiChoiceItems(categoryNameArray, null,
			new DialogInterface.OnMultiChoiceClickListener()
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
				}
			})
			.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					// User cancelled the dialog
				}
			});

		return builder.create();
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		Log.i("HermLog", "CategoryDialogFragment: save savedInstanceState");
		super.onSaveInstanceState(outState);

		if (categoryNameArray != null)
		{
			outState.putStringArray("savedCategoryArray", categoryNameArray);
		}
	}

	private String[] makeCategoryArray(ArrayList<CategoryItem> categoryList)
	{
		// Maak ArrayList<String> van ArrayList<CategoryItem>
		// maak daarna String[] van ArrayList<String>
		// Filter niet gewenste categorieen er uit
		ArrayList<String> categoryNameArrayList = new ArrayList<String>();
		ArrayList<Integer> categoryNumberArrayList = new ArrayList<Integer>();
		int[] exclude_children = getResources().getIntArray(R.array.parent_categories_exclude_children);
		int[] exclude_categories = getResources().getIntArray(R.array.categories_exclude);

		for (CategoryItem item : categoryList)
		{
			String name = item.getName();
			Integer number = item.getNumber();

			if (!arrayContains(exclude_children, item.getParent())
				&& !arrayContains(exclude_categories, item.getNumber()))
			{
				categoryNameArrayList.add(name);
				categoryNumberArrayList.add(number);
			}
		}

		categoryNameArray = categoryNameArrayList.toArray(new String[0]);
		categoryNumberArray = categoryNumberArrayList.toArray(new Integer[0]);
		return categoryNameArray;
	}

	// Test of getal in getallen(int)array zit
	private boolean arrayContains(int[] array, int key)
	{  
		Arrays.sort(array);
		return Arrays.binarySearch(array, key) >= 0;  
	}  
}
