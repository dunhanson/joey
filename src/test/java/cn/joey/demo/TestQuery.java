package cn.joey.demo;


import cn.joey.entity.Document;
import cn.joey.solr.core.Condition;
import cn.joey.solr.core.Item;
import cn.joey.solr.core.Joey;
import cn.joey.solr.core.Sort;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestQuery {

	public static void main(String[] args) {
		List<Condition> q = new ArrayList<>();
		//q.add(new Condition("", new String[]{"鸥涌", "综合楼", "停车场", "车位", "11号", "小额"}));

		List<Condition> fq = new ArrayList<>();
		Condition docchannel = new Condition();
		docchannel.setName("docchannel");
		docchannel.setValues(new String[]{"52", "101"});
		docchannel.setInnerOr(true);
		fq.add(docchannel);
		fq.add(new Condition("publishtime", new String[]{"2020-01-01T00:00:00Z", "2020-01-08T23:59:59Z"}, Condition.Type.RANGE, true));


		List<Sort> sorts = new ArrayList<>();
		sorts.add(new Sort("publishtime", false));

		Map<String, String> param = new HashMap<>();
		param.put("mm", "95%");
		param.put("defType", "edismax");
		param.put("ps", "2");
		param.put("qf", "doctitle dochtmlcon");
		param.put("fl", "id,doctitle,publishtime");
		param.put("pf", "doctitle^1.0 dochtmlcon^0.6");
		param.put("q", "\"鸥\" \"涌\" \"综合楼\" \"停车场\" \"车位\" \"11\" \"号\" \"小额\"");

		Item item = new Item();
		item.setQ(q);
		item.setFq(fq);
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
