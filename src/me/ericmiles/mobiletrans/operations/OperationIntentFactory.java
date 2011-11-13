/**
 * 
 */
package me.ericmiles.mobiletrans.operations;

import me.ericmiles.mobiletrans.Constants;
import me.ericmiles.mobiletrans.rest.RestDelegateService;
import me.ericmiles.mobiletrans.util.Utils;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

/**
 * @author 94728
 * 
 */
public final class OperationIntentFactory {

	@SuppressWarnings("unused")
	private static final String TAG = OperationIntentFactory.class.getSimpleName();

	private static OperationIntentFactory _instance;

	private Context context;

	public OperationIntentFactory(Context applicationContext) {
		this.context = applicationContext;
	}

	public static final OperationIntentFactory getInstance(Context applicationContext) {
		if (_instance == null) {
			synchronized (OperationIntentFactory.class) {
				if (_instance == null) {
					_instance = new OperationIntentFactory(applicationContext);
				}
			}
		}
		return _instance;
	}

	public Intent createIntent(Operation.OperationRequest request) {
		Intent intent = createIntent();
		intent.putExtra(Constants.REST_REQUEST, request);
		return intent;
	}

	private Intent createIntent() {
		return new Intent(context, RestDelegateService.class);
	}

	public Intent createIntent(Operation.OperationResponse response, Bundle extras) {
		Intent intent = new Intent(Constants.ACTION_REST_RESULT);
		intent.addCategory(Utils.escapeType(response.getClass()));
		intent.putExtras(extras);
		intent.putExtra(Constants.REST_RESPONSE, response);
		return intent;
	}

	public Intent createIntent(Throwable e, Bundle extras) {
		Intent intent = new Intent(Constants.ACTION_REST_RESULT);
		intent.addCategory(Utils.escapeType(e.getClass()));
		intent.putExtras(extras);
		intent.putExtra(Constants.REST_EXCEPTION, e);
		return intent;
	}

	public IntentFilter createIntentFilter(Class<? extends Operation.OperationResponse> clazz) {
		IntentFilter filter = new IntentFilter(Constants.ACTION_REST_RESULT);
		filter.addCategory(Utils.escapeType(clazz));
		return filter;
	}
}
