package cn.joey.demo;

import java.util.List;
import cn.joey.entity.Document;
import cn.joey.solr.core.Joey;

public class TestDemo {
	public static void main(String[] args) {
		List<Document> list = Joey.search(Document.class);
		list.forEach(obj->{
			System.out.println(obj);
		});
	}
}
