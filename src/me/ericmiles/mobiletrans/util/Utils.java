/**
 * 
 */
package me.ericmiles.mobiletrans.util;

/**
 * @author emiles
 *
 */
public class Utils {
	
	@SuppressWarnings("rawtypes")
	public static String escapeType(Class clazz) {
		return clazz.getName().replace("$", ".");
	}

}
