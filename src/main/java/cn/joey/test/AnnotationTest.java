package cn.joey.test;

import java.lang.reflect.Field;
import cn.joey.annotation.Column;

public class AnnotationTest {
	public static void main(String[] args) {
		Field[] filed = Contact.class.getDeclaredFields();
		for (Field field : filed) {
			Column column = field.getAnnotation(Column.class);
			System.out.println(column.value());
		}
	}
}
