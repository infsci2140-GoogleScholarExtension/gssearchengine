package edu.pitt.sis.infsci2140.gssearchengine.utils.conf;

import java.util.HashMap;

/**
 * 
 * @author hongzhang
 *
 * A wrapper for various utensils that come in handy.
 */
public class ScholarUtils {
	/**
	 * LOG_LEVELS = { 'error': 1,
     *                'warn':  2,
     *                'info':  3,
     *                'debug': 4 
     *               }
	 */
	private static HashMap<String, Integer> LOG_LEVELS = null;
	
	// would have problem in mutilthreading program
	public static int getLevel(String level) {
		if (LOG_LEVELS == null) {
			LOG_LEVELS.put("error", 1);
			LOG_LEVELS.put("warn",  2);
			LOG_LEVELS.put("info",  3);
			LOG_LEVELS.put("debug", 4);
		}
		return LOG_LEVELS.get(level);
	}
	
	public static void log(String level, String msg) {
		if (!LOG_LEVELS.containsKey(level)) return;
		if (LOG_LEVELS.get(level) > ScholarConf.LOG_LEVEL) return;
		
		System.err.println(level.toUpperCase() + "  " + msg);
	}
	
	// cannot init
	private ScholarUtils() {}
}
