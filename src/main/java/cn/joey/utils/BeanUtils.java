package cn.joey.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanUtils {
	private static Logger logger = LoggerFactory.getLogger(BeanUtils.class);
	
	/**
	 * 对象转map集合
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> toMap(Object obj) {
		//返回Map集合
		Map<String, Object> map = new HashMap<>();
		//对象属性
		Field[] fields = obj.getClass().getDeclaredFields();
		//遍历属性
		if(fields != null && fields.length > 0) {
			for(int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				field.setAccessible(true);
				Object value;
				try {
					value = field.get(obj);
					//属性不为空则存入map集合
					if(value != null) {
						map.put(field.getName(), value);
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
					e.printStackTrace();
					return new HashMap<>();
				}
			}
		}
		return map;
	}
}
