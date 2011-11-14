/**
 * 
 */
package me.ericmiles.mobiletrans.rest;

import java.util.Collections;

import me.ericmiles.mobiletrans.Constants;
import me.ericmiles.mobiletrans.operations.Operation;
import me.ericmiles.mobiletrans.operations.OperationIntentFactory;
import me.ericmiles.mobiletrans.operations.UrlFactory;
import me.ericmiles.mobiletrans.session.SessionManager;

import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * @author 94728
 * 
 */
public class RestDelegateService extends IntentService {

	private static final String TAG = RestDelegateService.class.getSimpleName();

	private RestTemplate template = null;
	private UrlFactory urlFactory = null;

	/**
	 * @param name
	 */
	public RestDelegateService() {
		super("RestDelegateService");
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// we'll need to do all this to setup the 5 second connection timeout
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

		HttpParams params = new BasicHttpParams();
		ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager(params, schemeRegistry);
		ConnManagerParams.setMaxTotalConnections(params, 100);
		ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(5));
		HttpConnectionParams.setConnectionTimeout(params, 5 * 1000);

		DefaultHttpClient httpClient = new DefaultHttpClient(connectionManager, null);

		template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));

		// setting read timeout to 5 seconds
		((HttpComponentsClientHttpRequestFactory) template.getRequestFactory()).setReadTimeout(5 * 1000);

		urlFactory = new UrlFactory(getApplicationContext());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void onHandleIntent(Intent intent) {
		OperationIntentFactory factory = OperationIntentFactory.getInstance(getApplicationContext());

		// get out all the stuff we need to make the call
		Operation.OperationRequest request = intent.getParcelableExtra(Constants.REST_REQUEST);

		// if this is an authenticated request, let's put the session id in real
		// quick
		if (request instanceof Operation.AuthenticatedOperationRequest) {
			((Operation.AuthenticatedOperationRequest) request).sessionId = SessionManager.getInstance(
					getApplicationContext()).getSessionId();
		}

		// create our result broadcast intent
		Intent result = null;

		// need to specify this will be json
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		// create our request entity
		HttpEntity requestEntity = new HttpEntity(request, headers);

		// make our call
		try {
			ResponseEntity r = template.exchange(urlFactory.getUrl(request), request.getHttpMethod(), requestEntity,
					request.getResponseType());
			result = factory.createIntent((Operation.OperationResponse) r.getBody(), intent.getExtras());
			result.putExtra(Constants.HTTP_RESPONSE_CODE, r.getStatusCode().value());
		} catch (RestClientException e) {
			result = factory.createIntent(e.getRootCause(), intent.getExtras());
		} catch (Exception e) {
			result = factory.createIntent(e, intent.getExtras());
		}

		// if this is an exception toting Intent, we need to ensure at least one
		// BroadcastReceiver is registered
		// to handle it
		if (result.getSerializableExtra(Constants.REST_EXCEPTION) != null) {
			ensureReceivable(result);
		}

		Log.d(TAG, "Firing result intent " + result.toString());

		// you could even send an Ordered broadcast, but I think the use of a
		// strongly typed
		// category will alleviate the need to "chain" broadcast receivers like
		// that
		sendBroadcast(result, Constants.PERMISSION);
	}

	private void ensureReceivable(Intent result) {
		PackageManager manager = getApplicationContext().getPackageManager();
		if (manager.queryBroadcastReceivers(result, 0).size() == 0) {
			// clear all categories and add a default exception category
			result.getCategories().clear();
			result.addCategory(Constants.DEFAULT_EXCEPTION);
		}
	}

}
