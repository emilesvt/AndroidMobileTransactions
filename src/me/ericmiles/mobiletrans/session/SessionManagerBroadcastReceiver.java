/**
 * 
 */
package me.ericmiles.mobiletrans.session;

import me.ericmiles.mobiletrans.Constants;
import me.ericmiles.mobiletrans.R;
import me.ericmiles.mobiletrans.activities.MainActivity;
import me.ericmiles.mobiletrans.operations.LoginOperation;
import me.ericmiles.mobiletrans.operations.LogoutOperation;
import me.ericmiles.mobiletrans.operations.OperationIntentFactory;
import me.ericmiles.mobiletrans.util.Utils;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

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

		boolean authenticated = true;

		SessionManager manager = SessionManager.getInstance(context.getApplicationContext());

		if (intent.getCategories().contains(Utils.escapeType(LoginOperation.Response.class))) {
			LoginOperation.Response response = intent.getParcelableExtra(Constants.REST_RESPONSE);

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
			// let's kill our triggers
			manager.setSessionId(null);
			killSessionTriggers(context);
			// let's also notify the user via a status bar notification they've
			// been logged out
			notifyOfLogout(context);
		}

		// if we're authenticated and there was a response object
		// we're going to assume that this was a valid call and we
		// want to reset our session timeout triggers
		if (intent.getParcelableExtra(Constants.REST_RESPONSE) != null && authenticated) {
			resetSessionTriggers(context);
		}
	}

	private void notifyOfLogout(Context context) {
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(android.R.drawable.stat_sys_warning, "You have been logged out",
				System.currentTimeMillis());
		notification.defaults |= Notification.DEFAULT_ALL;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// forward to login
		Intent notificationIntent = new Intent(context, MainActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

		RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification);
		contentView.setImageViewResource(R.id.image, R.drawable.ic_launcher);
		contentView.setTextViewText(R.id.text, "You have been logged out, please log back in");
		notification.contentView = contentView;
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		notification.contentIntent = contentIntent;
		mNotificationManager.notify(1, notification);
	}

	private void killSessionTriggers(Context context) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(getLogoutIntent(context));
	}

	private void resetSessionTriggers(Context context) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		// arbitrarily setting session timeout to 1 mins
		// should get this from the server, but we're doing this for ease of
		// demonstration
		long logoutTimeMillis = System.currentTimeMillis() + (1 * 60 * 1000);

		// create one intent for eventual logout broadcast
		alarmManager.set(AlarmManager.RTC, logoutTimeMillis, getLogoutIntent(context));
	}

	private PendingIntent getLogoutIntent(Context context) {
		return PendingIntent.getService(
				context,
				0,
				OperationIntentFactory.getInstance(context.getApplicationContext()).createIntent(
						new LogoutOperation.Request()), 0);
	}

}
