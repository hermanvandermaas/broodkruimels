package nl.waywayway.broodkruimels;

import android.util.*;
import com.google.gson.*;
import com.google.gson.reflect.*;
import java.lang.reflect.*;
import java.util.*;

// Singleton voor parsen van Json array String naar ArrayList<T>

public class JsonToArrayListParser
{
	private static JsonToArrayListParser instance;

	private JsonToArrayListParser(){}

	public static JsonToArrayListParser getJsonToArrayListParser()
	{
		if (instance == null)
			instance = new JsonToArrayListParser();

		return instance;
	}
	
	public <T> ArrayList<T> parse(String jsonString, String rootElement, ArrayList<T> arrayList, Type type)
	{
		Log.i("HermLog", "JsonToArrayListParser.parse()");

		Gson gson = new Gson();

		JsonArray jsonArray = gson.fromJson(jsonString, JsonObject.class).getAsJsonArray(rootElement);
		ArrayList<T> newItems = gson.fromJson(jsonArray, type);
		arrayList.addAll(newItems);
		Log.i("HermLog", "arrayList size: " + arrayList.size());
		
		return arrayList;
	}
}
