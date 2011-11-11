/**
 * 
 */
package me.ericmiles.mobiletrans.activities;


import me.ericmiles.mobiletrans.R;
import me.ericmiles.mobiletrans.operations.LogoutOperation;
import me.ericmiles.mobiletrans.operations.LogoutOperation.Response.Status;
import me.ericmiles.mobiletrans.operations.OperationIntentFactory;
import me.ericmiles.mobiletrans.operations.TimeoutOperation;
import me.ericmiles.mobiletrans.rest.RestDelegateService;
import me.ericmiles.mobiletrans.session.SessionManager;
import me.ericmiles.mobiletrans.util.Utils;
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
		show = (Button) findViewById(R.id.show);
		show.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(SecondaryActivity.this,
						"Current Session Id is " + SessionManager.getInstance(getApplicationContext()).getSessionId(),
						Toast.LENGTH_LONG);
			}
		});
		
		timeout = (Button) findViewById(R.id.fireTimeout);
		timeout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TimeoutOperation.Request request = new TimeoutOperation.Request();
				Intent intent = OperationIntentFactory.getInstance(getApplicationContext()).createIntent(request);
				startService(intent);
				timeout.setEnabled(false);
			}
		});
		
		logout = (Button) findViewById(R.id.logout);
		logout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LogoutOperation.Request request = new LogoutOperation.Request();
				Intent intent = OperationIntentFactory.getInstance(getApplicationContext()).createIntent(request);
				startService(intent);
				logout.setEnabled(false);
			}
		});
		
		logoutReceiver = new LogoutBroadcastReceiver();
		logoutFilter = new IntentFilter();
		logoutFilter.addAction(RestDelegateService.ACTION_REST_RESULT);
		logoutFilter.addCategory(Utils.escapeType(LogoutOperation.Response.class));
		
		timeoutReceiver = new TimeoutBroadcastReceiver();
		timeoutFilter = new IntentFilter();
		timeoutFilter.addAction(RestDelegateService.ACTION_REST_RESULT);
		timeoutFilter.addCategory(Utils.escapeType(TimeoutOperation.Response.class));
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
		registerReceiver(logoutReceiver, logoutFilter, RestDelegateService.PERMISSION, null);
		registerReceiver(timeoutReceiver, timeoutFilter, RestDelegateService.PERMISSION, null);
	}
	
	class LogoutBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			LogoutOperation.Response response = intent.getParcelableExtra(RestDelegateService.RESPONSE);
			if(response.status == Status.SUCCESS) {
				Intent forward = new Intent(SecondaryActivity.this, MainActivity.class);
				startActivity(forward);
				SecondaryActivity.this.finish();
			} else {
				logout.setEnabled(true);
			}
		}
		
	}
	
	class TimeoutBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO:
			timeout.setEnabled(true);
		}
		
	}

}
