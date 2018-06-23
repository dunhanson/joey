package cn.joey.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cn.joey.solr.core.Joey;
import cn.joey.solr.entity.Condition;

public class Start {
	public static void main(String[] args) {
		query();
	}

	public static void query() {
		Joey joey = new Joey(Contact.class);
		List<Condition> q = new ArrayList<Condition>();
		q.add(new Condition("company_name", new String[]{"电子科技"}));
		List<Contact> result = joey.search();
		System.out.println(result.size());
		result.forEach(obj -> {
			System.out.println(obj);
		});
		System.out.println("q:" + joey.getQStr());
		System.out.println("fq:" + joey.getFQStr());
		System.out.println("sort:" + joey.getSortStr());
	}

	public static void dataImport() {
		Map<String, Object> param = new HashMap<>();
		param.put("index", 0);
		param.put("size", 100);
		System.out.println(new Joey(Contact.class).fullImport());
	}

}
