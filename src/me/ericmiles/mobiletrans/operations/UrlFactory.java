/**
 * 
 */
package me.ericmiles.mobiletrans.operations;

import android.content.Context;

/**
 * @author emiles
 * 
 */
public class UrlFactory {
	private Context context;

	public UrlFactory(Context context) {
		this.context = context;
	}

	public String getUrl(Operation.OperationRequest request) {
		int id = request.getUrlResourceId();

		// we're REALLY going to cheat here as we need to prove a point for this
		// demo
		// but i normally wouldn't put this logic here
		if (request instanceof TimeoutOperation.Request) {
			// if this is a retry, we want to get the real url
			// if not, let's get a fake one that will timeout
			if (!((TimeoutOperation.Request) request).retry) {
				return "http://127.0.0.1/";
			}
		}

		return context.getString(id);
	}
}
