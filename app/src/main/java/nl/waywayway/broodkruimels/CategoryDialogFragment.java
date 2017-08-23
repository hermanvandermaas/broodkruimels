package nl.waywayway.broodkruimels;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v7.app.*;
import java.util.*;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class CategoryDialogFragment extends DialogFragment
{
	private List<CategoryItem> categoryList;
	private String[] mCategoryArray;
	private ArrayList mSelectedItems;
	
	public void setCategoryList(ArrayList categoryList)
	{
		this.categoryList = categoryList;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		mCategoryArray = new String[]{"Kruipen", "Lopen", "Rennen"};
		mSelectedItems = new ArrayList();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder
			.setTitle(R.string.dialog_category_title)
			.setMultiChoiceItems(mCategoryArray, null,
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
}
