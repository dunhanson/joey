package cn.joey.demo;

import cn.joey.entity.DesignedProject;
import cn.joey.solr.core.Query;

public class TestDemo {
    public static void main(String[] args) {
        Query.search(DesignedProject.class).forEach(obj->{
            System.out.println(obj);
        });
    }
}
