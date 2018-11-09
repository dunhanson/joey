package cn.joey.demo;

import cn.joey.entity.Document;
import cn.joey.solr.core.Joey;

public class MapTest {
	public static void main(String[] args) {
		System.out.println(Joey.statusImport(Document.class, System.currentTimeMillis()));;
	}
}
