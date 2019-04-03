package cn.joey.demo;

import java.util.ArrayList;
import java.util.List;
import cn.joey.entity.Document;
import cn.joey.solr.core.Condition;
import cn.joey.solr.core.Joey;

public class TestDemo {
	public static void main(String[] args) {
		List<Condition> fq = new ArrayList<>();
		fq.add(new Condition("chnldesc", new String[] {"公告"}, true, false));
		List<Document> list = Joey.search(Document.class, fq, false);
		list.forEach(obj->{
			System.out.println(obj);
		});
	}
}
