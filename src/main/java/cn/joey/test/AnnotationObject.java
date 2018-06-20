package cn.joey.test;

import cn.joey.annotation.JoeyField;

public class AnnotationObject {
    @JoeyField("id")
    private Integer id;
    @JoeyField("name")
    private String name;
    @JoeyField("value")
    private String value;
    private String age;

    public AnnotationObject() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
