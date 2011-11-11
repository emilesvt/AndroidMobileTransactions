/**
 * 
 */
package me.ericmiles.mobiletrans.session;

import me.ericmiles.mobiletrans.operations.LoginOperation;
import me.ericmiles.mobiletrans.operations.LogoutOperation;
import me.ericmiles.mobiletrans.operations.OperationIntentFactory;
import me.ericmiles.mobiletrans.rest.RestDelegateService;
import me.ericmiles.mobiletrans.util.Utils;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author 94728
 * 
 */
public class SessionManagerBroadcastReceiver extends BroadcastReceiver {

	private static final String TAG = SessionManagerBroadcastReceiver.class.getSimpleName();

	/**
	 * 
	 */
	public SessionManagerBroadcastReceiver() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Received intent " + intent);
		String action = intent.getAction();

		boolean authenticated = true;

		SessionManager manager = SessionManager.getInstance(context.getApplicationContext());

		if (RestDelegateService.ACTION_REST_RESULT.equals(action)) {
			if (intent.getCategories().contains(Utils.escapeType(LoginOperation.Response.class))) {
				LoginOperation.Response response = intent.getParcelableExtra(RestDelegateService.RESPONSE);

				Log.d(TAG,
						"Received broadcast in " + SessionManagerBroadcastReceiver.class.getName() + ": "
								+ response.toString());

				if (response.status == LoginOperation.Response.Status.SUCCESS) {
					manager.setSessionId(response.sessionId);
				} else {
					authenticated = false;
				}
			} else if (intent.getCategories().contains(Utils.escapeType(LogoutOperation.Response.class))) {
				authenticated = false;
				// let's actuall kill our triggers
				manager.setSessionId(null);
				killSessionTriggers(context);
				// let's also notify the user via a status bar notification they've been logged out
				// TODO:
			}

			// if we're authenticated and there was a response object
			// we're going to assume that this was a valid call and we
			// want to reset our session timeout triggers
			if (intent.getParcelableExtra(RestDelegateService.RESPONSE) != null && authenticated) {
				resetSessionTriggers(context);
			}

		}
	}

	private void killSessionTriggers(Context context) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(getLogoutIntent(context));
	}

	private void resetSessionTriggers(Context context) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		// arbitrarily setting session timeout to 2 mins
		// should get this from the server, but we're doing this for ease of
		// demonstration
		long logoutTimeMillis = System.currentTimeMillis() + (2 * 60 * 1000);

		// create one intent for eventual logout broadcast
		alarmManager.set(AlarmManager.RTC, logoutTimeMillis, getLogoutIntent(context));
	}

	private PendingIntent getLogoutIntent(Context context) {
		return PendingIntent.getBroadcast(
				context,
				0,
				OperationIntentFactory.getInstance(context.getApplicationContext()).createIntent(
						new LogoutOperation.Request()), 0);
	}

}
