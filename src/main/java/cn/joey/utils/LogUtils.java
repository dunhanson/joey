package cn.joey.utils;

import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LogUtils {
	private static LogManager logManager = LogManager.getLogManager();
	private static String logfile = "logging.propreties";
	
	public static Logger getLogger(Class<?> clazz) {
		Thread thread = Thread.currentThread();
		ClassLoader classLoader = thread.getContextClassLoader();
		try (InputStream inputStream = classLoader.getResourceAsStream(logfile)) {
			if(inputStream == null) return null;
			logManager.readConfiguration(inputStream);
			logManager.addLogger(Logger.getLogger(clazz.getName()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return logManager.getLogger(clazz.getName());
	}
}
