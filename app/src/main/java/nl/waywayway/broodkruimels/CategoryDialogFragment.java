package nl.waywayway.broodkruimels;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v7.app.*;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class CategoryDialogFragment extends DialogFragment
{
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		builder
			.setMessage(R.string.dialog_category_title)
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
