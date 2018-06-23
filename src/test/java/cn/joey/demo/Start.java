package cn.joey.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cn.joey.entity.Contact;
import cn.joey.solr.core.Joey;
import cn.joey.solr.core.Condition;
import cn.joey.solr.core.Pagination;
import cn.joey.solr.core.Sort;

public class Start {
	public static void main(String[] args) {
		query();
	}

	/**
	 * 查询
	 */
	public static void query() {
		//条件
		List<Condition> q = new ArrayList<Condition>();
		q.add(new Condition("company_name", new String[]{"厦门"}));
		//过滤
		List<Condition> fq = new ArrayList<Condition>();
		fq.add(new Condition("province", new String[]{"福建"}));
		//排序
		List<Sort> sort = new ArrayList<>();
		sort.add(new Sort("id", false));
		//分页
		Pagination pagination = new Pagination(1, 30);
		//查询
		Joey joey = new Joey(Contact.class, q, fq, sort, pagination);
		List<Contact> result = joey.search();
		result.forEach(obj -> {
			System.out.println(obj);
		});
		//参数
		System.out.println("q:" + joey.getQStr());
		System.out.println("fq:" + joey.getFQStr());
		System.out.println("sort:" + joey.getSortStr());
	}

	/**
	 * 全量更新索引（带参数）
	 */
	public static void fullImport() {
		Map<String, Object> param = new HashMap<>();
		param.put("index", 0);
		param.put("size", 10000);
		System.out.println(new Joey(cn.joey.entity.Contact.class).fullImport(param));
	}

	/**
	 * 增量更新索引
	 */
	public static void deltaImport() {
		Map<String, Object> param = new HashMap<>();
		System.out.println(new Joey(Contact.class).deltaImport());
	}

}
