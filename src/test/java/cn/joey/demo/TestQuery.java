package cn.joey.demo;


import cn.joey.entity.Document;
import cn.joey.solr.core.Condition;
import cn.joey.solr.core.Item;
import cn.joey.solr.core.Joey;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestQuery {

	public static void main(String[] args) {
		List<Condition> q = new ArrayList<>();
		q.add(new Condition("doctitle", new String[]{"鸥涌综合楼停车场车位11号小额"}));

		Map<String, String> param = new HashMap<>();
		//param.put("defType", "edismax");
		//param.put("mm", "90%");

		Item item = new Item();
		item.setQ(q);
		item.setParam(param);
		Joey.search(Document.class, item).forEach(obj->{
			System.out.println(obj);
		});
		System.out.println(item.getPagination().getTotalSize());
	}

	@Test
	public void test() {
		System.out.println("\"ddd\"".replace("\"", ""));
	}

}
