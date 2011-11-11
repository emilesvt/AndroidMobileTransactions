/**
 * 
 */
package me.ericmiles.mobiletrans.operations;

import me.ericmiles.mobiletrans.R;
import android.content.Context;

/**
 * @author emiles
 *
 */
public class UrlFactoryDevelopment implements UrlFactory {
	
	private Context context;
	
	public UrlFactoryDevelopment(Context context) {
		this.context = context;
	}

	@Override
	public String getUrl(me.ericmiles.mobiletrans.operations.LoginOperation.Request request) {
		return context.getString(R.string.url_login_operation);
	}

	@Override
	public String getUrl(me.ericmiles.mobiletrans.operations.LogoutOperation.Request request) {
		return context.getString(R.string.url_logout_operation);
	}

	@Override
	public String getUrl(me.ericmiles.mobiletrans.operations.TimeoutOperation.Request request) {
		return null;
	}
	
	
}
