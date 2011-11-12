package me.ericmiles.mobiletrans.activities;

import me.ericmiles.mobiletrans.Constants;
import me.ericmiles.mobiletrans.R;
import me.ericmiles.mobiletrans.operations.LoginOperation;
import me.ericmiles.mobiletrans.operations.OperationIntentFactory;
import me.ericmiles.mobiletrans.session.SessionManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getSimpleName();

	private BroadcastReceiver receiver;
	private IntentFilter filter;

	private EditText userId;
	private EditText password;
	private Button show;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final OperationIntentFactory factory = OperationIntentFactory.getInstance(getApplicationContext());

		userId = (EditText) findViewById(R.id.userId);
		password = (EditText) findViewById(R.id.password);
		show = (Button) findViewById(R.id.show);
		show.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(MainActivity.this,
						"Current Session Id is " + SessionManager.getInstance(getApplicationContext()).getSessionId(),
						Toast.LENGTH_LONG).show();
			}
		});

		final Button button = (Button) findViewById(R.id.login);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				LoginOperation.Request request = new LoginOperation.Request();
				request.userId = userId.getText().toString();
				request.password = password.getText().toString();

				Intent intent = factory.createIntent(request);
				startService(intent);

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(button.getWindowToken(), 0);

			}
		});

		receiver = new MyReceiver();

		// we want to only receive broadcasts we can handle
		filter = factory.createIntentFilter(LoginOperation.Response.class);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "Received intent " + intent);
			final LoginOperation.Response response = intent.getParcelableExtra(Constants.REST_RESPONSE);
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setTitle("Login Response");
					builder.setMessage(response.status.name());
					builder.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (response.status == LoginOperation.Response.Status.SUCCESS) {
								Intent forward = new Intent(MainActivity.this, SecondaryActivity.class);
								startActivity(forward);
								finish();
							}
						}
					});

					builder.create().show();
				}
			});
		}

	}
}