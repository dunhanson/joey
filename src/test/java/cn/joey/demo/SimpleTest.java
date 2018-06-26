package cn.joey.demo;

import cn.joey.entity.Contact;
import cn.joey.solr.core.Joey;

import java.util.Arrays;
import java.util.List;

public class SimpleTest {
    public static void main(String[] args) {
        MathOperation addtion = (int a, int b) -> a + b;
    }
}

interface MathOperation {
    int operation(int a, int b);
}
