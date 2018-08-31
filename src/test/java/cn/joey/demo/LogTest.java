package cn.joey.demo;

import java.util.logging.Logger;
import cn.joey.utils.LogUtils;

public class LogTest {
	private static Logger logger = LogUtils.getLogger(Thread.currentThread().getStackTrace()[1].getClass());
	
	public static void main(String[] args) {
        logger.info("哈哈哈");
	}
}
