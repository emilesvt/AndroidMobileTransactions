/**
 * 
 */
package me.ericmiles.mobiletrans.activities;

import me.ericmiles.mobiletrans.Constants;
import me.ericmiles.mobiletrans.operations.Operation;
import me.ericmiles.mobiletrans.operations.Operation.OperationRequest;
import me.ericmiles.mobiletrans.operations.OperationIntentFactory;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author emiles
 * 
 */
public class ConnectionErrorActivity extends Activity {

	private Bundle extras;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Be sure to call the super class.
		super.onCreate(savedInstanceState);
		extras = getIntent().getExtras();
		showDialog(1);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		return new AlertDialog.Builder(ConnectionErrorActivity.this)
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
						// TODO: create negative response and broadcast
						finish();
					}
				}).create();
	}

}
