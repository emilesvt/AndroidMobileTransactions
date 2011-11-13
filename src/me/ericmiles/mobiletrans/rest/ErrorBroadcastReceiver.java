/**
 * 
 */
package me.ericmiles.mobiletrans.rest;

import me.ericmiles.mobiletrans.activities.ErrorActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * For this scenario, we're going to have 1 single error broadcast receiver,
 * that will take care of ALL errors.  You could/should have multiple receivers
 * for different error types.
 * 
 * @author emiles
 * 
 */
public class ErrorBroadcastReceiver extends BroadcastReceiver {

	private static final String TAG = ErrorBroadcastReceiver.class.getSimpleName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Received intent: " + intent);
		// for this demo, always call error activity
		Intent forward = new Intent(context, ErrorActivity.class);
		forward.putExtras(intent.getExtras());
		forward.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(forward);
	}

}
