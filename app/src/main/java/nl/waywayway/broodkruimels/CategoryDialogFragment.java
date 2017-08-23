package nl.waywayway.broodkruimels;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v7.app.*;
import android.util.*;
import java.util.*;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class CategoryDialogFragment extends DialogFragment
{
	private List<CategoryItem> categoryList;
	private String[] categoryArray;
	private ArrayList mSelectedItems;

	public void setCategoryList(List<CategoryItem> categoryList)
	{
		this.categoryList = categoryList;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		if (savedInstanceState != null)
		{
			super.onCreateDialog(savedInstanceState);
			categoryArray = savedInstanceState.getStringArray("savedCategoryArray");
		}
		else
		{
			// categoryArray = new String[]{"Kruipen", "Lopen", "Rennen"};
			Log.i("HermLog", "categoryList is null? " + (categoryList == null));
			categoryArray = makeCategoryArray((ArrayList<CategoryItem>) categoryList);
		}

		mSelectedItems = new ArrayList();

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder
			.setTitle(R.string.dialog_category_title)
			.setMultiChoiceItems(categoryArray, null,
			new DialogInterface.OnMultiChoiceClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked)
				{
					if (isChecked)
					{
						// If the user checked the item, add it to the selected items
						mSelectedItems.add(which);
					}
					else if (mSelectedItems.contains(which))
					{
						// Else, if the item is already in the array, remove it
						mSelectedItems.remove(Integer.valueOf(which));
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
		super.onSaveInstanceState(outState);

		if (categoryArray != null)
		{
			outState.putStringArray("savedCategoryArray", categoryArray);
		}
	}

	private String[] makeCategoryArray(ArrayList<CategoryItem> categoryList)
	{
		// Maak ArrayList<String> van ArrayList<CategoryItem>
		// maak daarna String[] van ArrayList<String>
		ArrayList<String> categoryArrayList = new ArrayList<String>();

		for (CategoryItem item : categoryList)
		{
			String name = item.getName();
			Log.i("HermLog", name);
			Log.i("HermLog", "categoryArrayList is null? " + (categoryArrayList == null));
			categoryArrayList.add(name);
		}

		Log.i("HermLog", "categoryArrayList is null? " + (categoryArrayList == null));
		Log.i("HermLog", "categoryArray is null? " + (categoryArray == null));
		Log.i("HermLog", "(new String[0]) is null? " + ((new String[0]) == null));

		categoryArray = categoryArrayList.toArray(new String[0]);
		return categoryArray;
	}
}
