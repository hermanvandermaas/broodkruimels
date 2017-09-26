package nl.waywayway.broodkruimels;

import android.util.*;
import java.io.*;
import java.util.concurrent.*;
import okhttp3.*;

public class DownloadJsonString
{
	private String url;

	public DownloadJsonString(String url)
	{
		this.url = url;
	}

	public String download()
	{
		OkHttpClient mClient = new OkHttpClient.Builder()
			.readTimeout(30, TimeUnit.SECONDS)
			.build();

		Request mRequest = new Request.Builder()
			.url(url)
			.build();

		try
		{
			Response mResponse = mClient
				.newCall(mRequest)
				.execute();

			if (!mResponse.isSuccessful())
			{
				throw new IOException("Unexpected code in DownloadJsonString: " + mResponse);
			}

			Log.i("HermLog", "Gedownload");

			return mResponse.body().string();
		}
		catch (IOException e)
		{
			Log.i("HermLog", "Exception in DownloadJsonString");
		}

		return "Fout in DownloadJsonString!";
	}
}
