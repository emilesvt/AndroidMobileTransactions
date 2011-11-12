/**
 * 
 */
package me.ericmiles.mobiletrans.activities;

import me.ericmiles.mobiletrans.Constants;
import me.ericmiles.mobiletrans.operations.Operation;
import me.ericmiles.mobiletrans.operations.Operation.OperationRequest;
import me.ericmiles.mobiletrans.operations.Operation.OperationResponse.Status;
import me.ericmiles.mobiletrans.operations.OperationIntentFactory;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * @author emiles
 * 
 */
public class ErrorActivity extends Activity {

	private static final String TAG = ErrorActivity.class.getSimpleName();

	private Bundle extras;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		extras = getIntent().getExtras();
		showDialog(1);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		return new AlertDialog.Builder(ErrorActivity.this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Connection Issue")
				.setMessage(
						"A error occurred while attempting to communicate with the backend.  Do you want to attempt again?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Intent intent = OperationIntentFactory.getInstance(getApplicationContext()).createIntent(
								(OperationRequest) extras.getParcelable(Constants.REST_REQUEST));
						startService(intent);
						finish();
					}
				}).setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						try {

							// we could do all sorts of things here, but all we're going to do is notify
							// the calling activity that the call failed
							Class<? extends Operation.OperationResponse> clazz = ((Operation.OperationRequest) extras
									.getParcelable(Constants.REST_REQUEST)).getResponseType();
							Operation.OperationResponse response = clazz.newInstance();
							response.status = Status.FAILED;
							response.errorMsg = "Sorry!  Something went terribly wrong";

							// create the response broadcast and send on
							Intent forward = OperationIntentFactory.getInstance(getApplicationContext()).createIntent(
									response);
							sendOrderedBroadcast(forward, Constants.PERMISSION);
							finish();
						} catch (IllegalAccessException e) {
							Log.e(TAG, "oops", e);
						} catch (InstantiationException e) {
							Log.e(TAG, "oops", e);
						}
					}
				}).create();
	}

}
