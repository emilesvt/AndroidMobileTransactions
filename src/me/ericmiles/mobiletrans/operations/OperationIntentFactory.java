/**
 * 
 */
package me.ericmiles.mobiletrans.operations;

import me.ericmiles.mobiletrans.rest.RestDelegateService;
import android.content.Context;
import android.content.Intent;

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
		intent.putExtra(RestDelegateService.REQUEST, request);
		return intent;
	}

	private Intent createIntent() {
		return new Intent(context, RestDelegateService.class);
	}
}
