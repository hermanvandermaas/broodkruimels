package nl.waywayway.broodkruimels;

import android.util.*;
import com.google.firebase.iid.*;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService
{
	@Override
	public void onTokenRefresh()
	{
		// Get updated InstanceID token.
		String refreshedToken = FirebaseInstanceId.getInstance().getToken();
		Log.i("HermLog", "Refreshed token: " + refreshedToken);

		// If you want to send messages to this application instance or
		// manage this apps subscriptions on the server side, send the
		// Instance ID token to your app server.
		sendRegistrationToServer(refreshedToken);
	}
	
	private void sendRegistrationToServer(String refreshedToken)
	{
		// Stuur recente Token naar de Firebase server
	}
}
