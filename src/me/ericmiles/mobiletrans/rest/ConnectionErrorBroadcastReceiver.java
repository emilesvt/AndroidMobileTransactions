/**
 * 
 */
package me.ericmiles.mobiletrans.rest;

import me.ericmiles.mobiletrans.activities.ConnectionErrorActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * @author emiles
 * 
 */
public class ConnectionErrorBroadcastReceiver extends BroadcastReceiver {

	private static final String TAG = ConnectionErrorBroadcastReceiver.class.getSimpleName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Received intent: " + intent);
		// do some inspection of data connections to see if we have one, that'll
		// determine
		// what activity we want to call
		// for this demo, always call connection timeout
		Intent forward = new Intent(context, ConnectionErrorActivity.class);
		forward.putExtras(intent.getExtras());
		forward.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(forward);
	}

}
