/**
 * 
 */
package me.ericmiles.mobiletrans.activities;

import me.ericmiles.mobiletrans.Constants;
import me.ericmiles.mobiletrans.operations.Operation;
import me.ericmiles.mobiletrans.operations.Operation.OperationRequest;
import me.ericmiles.mobiletrans.operations.OperationIntentFactory;
import me.ericmiles.mobiletrans.operations.TimeoutOperation;
import me.ericmiles.mobiletrans.rest.SendFailedMessageService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/**
 * Generic error activity for our demo
 * 
 * @author emiles
 * 
 */
public class ErrorActivity extends Activity {

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
						Operation.OperationRequest request = (OperationRequest) extras
								.getParcelable(Constants.REST_REQUEST);
						Intent intent = OperationIntentFactory.getInstance(getApplicationContext()).createIntent(
								request);

						// this is a cheat, but trying to show that a subsequent
						// retry CAN work (i can't do this programmatically in
						// the backend with mocky
						if (request instanceof TimeoutOperation.Request) {
							((TimeoutOperation.Request) request).retry = true;
						}

						// we're going to attempt to refire the original request
						startService(intent);
						finish();
					}
				}).setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// we're just going to kill this error activity and send a failed message to the waiting
						// activity
						Intent forward = new Intent(ErrorActivity.this, SendFailedMessageService.class);
						forward.putExtras(extras);
						startService(forward);
						finish();
					}
				}).create();
	}

}
