/**
 * 
 */
package me.ericmiles.mobiletrans.rest;

import me.ericmiles.mobiletrans.Constants;
import me.ericmiles.mobiletrans.operations.Operation;
import me.ericmiles.mobiletrans.operations.Operation.OperationResponse.Status;
import me.ericmiles.mobiletrans.operations.OperationIntentFactory;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * @author emiles
 * 
 */
public class SendFailedMessageService extends IntentService {

	private static final String TAG = SendFailedMessageService.class.getSimpleName();

	public SendFailedMessageService() {
		super("SendFailedMessageService");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		// we could do all sorts of things here, but all we're going to do is
		// notify
		// the calling activity that the call failed
		try {

			// we're gonna cheat massively here, but I want to ensure that the
			// error activity has finished up before we send this result
			// broadcast
			Thread.sleep(1000);

			Class<? extends Operation.OperationResponse> clazz = ((Operation.OperationRequest) intent
					.getParcelableExtra(Constants.REST_REQUEST)).getResponseType();
			Operation.OperationResponse response = clazz.newInstance();
			response.status = Status.FAILED;
			response.errorMsg = "Sorry!  Something went terribly wrong";

			// create the response broadcast and send on
			final Intent forward = OperationIntentFactory.getInstance(getApplicationContext()).createIntent(response,
					new Bundle());
			sendOrderedBroadcast(forward, Constants.PERMISSION);
		} catch (Exception e) {
			Log.e(TAG, "oops", e);
		}

	}

}
