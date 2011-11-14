/**
 * 
 */
package me.ericmiles.mobiletrans.activities;

import me.ericmiles.mobiletrans.Constants;
import me.ericmiles.mobiletrans.R;
import me.ericmiles.mobiletrans.operations.LogoutOperation;
import me.ericmiles.mobiletrans.operations.Operation.OperationResponse.Status;
import me.ericmiles.mobiletrans.operations.OperationIntentFactory;
import me.ericmiles.mobiletrans.operations.TimeoutOperation;
import me.ericmiles.mobiletrans.session.SessionManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * simple activity to allow the user to fire another network call, take a look
 * at the session id, or to signout.
 * 
 * @author emiles
 * 
 */
public class SecondaryActivity extends Activity {

	private Button show;
	private Button timeout;
	private Button logout;

	private BroadcastReceiver logoutReceiver;
	private IntentFilter logoutFilter;

	private BroadcastReceiver timeoutReceiver;
	private IntentFilter timeoutFilter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.secondary);

		final OperationIntentFactory factory = OperationIntentFactory.getInstance(getApplicationContext());

		show = (Button) findViewById(R.id.show);
		show.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(SecondaryActivity.this,
						"Current Session Id is " + SessionManager.getInstance(getApplicationContext()).getSessionId(),
						Toast.LENGTH_LONG).show();
			}
		});

		timeout = (Button) findViewById(R.id.fireTimeout);
		timeout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TimeoutOperation.Request request = new TimeoutOperation.Request();
				Intent intent = factory.createIntent(request);
				startService(intent);
				timeout.setEnabled(false);
			}
		});

		logout = (Button) findViewById(R.id.logout);
		logout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				LogoutOperation.Request request = new LogoutOperation.Request();
				Intent intent = factory.createIntent(request);
				startService(intent);
				logout.setEnabled(false);
			}
		});

		logoutReceiver = new LogoutBroadcastReceiver();
		logoutFilter = factory.createIntentFilter(LogoutOperation.Response.class);

		timeoutReceiver = new TimeoutBroadcastReceiver();
		timeoutFilter = factory.createIntentFilter(TimeoutOperation.Response.class);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(logoutReceiver);
		unregisterReceiver(timeoutReceiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(logoutReceiver, logoutFilter, Constants.PERMISSION, null);
		registerReceiver(timeoutReceiver, timeoutFilter, Constants.PERMISSION, null);
	}

	class LogoutBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// for demo purposes, i want to ignore programmatic logouts
			// so the user can check the session value
			if (!logout.isEnabled()) {
				LogoutOperation.Response response = intent.getParcelableExtra(Constants.REST_RESPONSE);
				if (response.status == Status.SUCCESS) {
					Intent forward = new Intent(SecondaryActivity.this, MainActivity.class);
					startActivity(forward);
					SecondaryActivity.this.finish();
				} else {
					logout.setEnabled(true);
				}
			}
		}

	}

	class TimeoutBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			TimeoutOperation.Response response = intent.getExtras().getParcelable(Constants.REST_RESPONSE);
			Toast.makeText(SecondaryActivity.this, "Operation result: " + response.status, Toast.LENGTH_LONG).show();
			timeout.setEnabled(true);
		}

	}

}
