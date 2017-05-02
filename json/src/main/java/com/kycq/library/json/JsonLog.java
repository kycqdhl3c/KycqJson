package com.kycq.library.json;

public class JsonLog {
	/** 无提示级别 */
	public static final int NONE = 0;
	/** 错误级别 */
	public static final int ERROR = 1;
	/** 警告级别 */
	public static final int WARNING = 2;
	
	/** 调试级别 */
	static volatile int DEBUG_LEVEL = WARNING;
	
	public static void setDebugLevel(int debugLevel) {
		DEBUG_LEVEL = debugLevel;
	}
	
	public static void w(String tag, String msg) {
		if (DEBUG_LEVEL >= WARNING) {
			System.out.println(tag + " # " + msg);
		}
	}
	
	public static void e(String tag, String msg) {
		if (DEBUG_LEVEL >= ERROR) {
			System.err.println(tag + " # " + msg);
		}
	}
	
}
