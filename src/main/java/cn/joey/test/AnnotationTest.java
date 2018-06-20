package cn.joey.test;

import cn.joey.annotation.JoeyField;
import java.lang.reflect.Field;

public class AnnotationTest {
    public static void main(String[] args) {
        Class<AnnotationObject> clazz = AnnotationObject.class;
        Field[] fileds = clazz.getDeclaredFields();
        for(Field field : fileds) {
            //System.out.println(field.getAnnotation(JoeyField.class).value());
            System.out.println(field.getAnnotation(JoeyField.class));
        }
    }
}
