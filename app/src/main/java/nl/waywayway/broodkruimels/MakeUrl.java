package nl.waywayway.broodkruimels;

import android.util.*;

/** Maak URL voor downloaden data
 Query string heeft de vorm:
 ?s=0&n=40&c=3,406,15
 waarin:
 s=eerste op te halen item in de op datum gesorteerde lijst met alle items,
 let op: het eerste item is item 0
 n=aantal op te halen items binnen de lijst met alle items, inclusief item nummer "s"
 c=komma gescheiden lijst met categorieen waarvan items worden opgehaald

 Endless scrolling:
 als er al eerder gedownloade data in de List<E> staan, begin nieuwe download bij eerstvolgende item
 */

public class MakeUrl
{
	String url;
	int startItem;
	int itemsPerPage;
	String categoriesParameter;

	public MakeUrl(String url, int startItem, int itemsPerPage, String categoriesParameter)
	{
		this.url = url;
		this.startItem = startItem;
		this.itemsPerPage = itemsPerPage;
		this.categoriesParameter = categoriesParameter;
	}
	
	public String make()
	{
		String mUrl = url
			+ "s="
			+ Integer.toString(startItem)
			+ "&n="
			+ Integer.toString(itemsPerPage)
			+ categoriesParameter;

		Log.i("HermLog", "mUrl: " + mUrl);

		return mUrl;
	}
}
