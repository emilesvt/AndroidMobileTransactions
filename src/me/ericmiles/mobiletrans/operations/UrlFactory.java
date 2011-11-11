/**
 * 
 */
package me.ericmiles.mobiletrans.operations;


/**
 * @author emiles
 *
 */
public interface UrlFactory {
	public String getUrl(LoginOperation.Request request);
	public String getUrl(LogoutOperation.Request request);
	public String getUrl(TimeoutOperation.Request request);
}
