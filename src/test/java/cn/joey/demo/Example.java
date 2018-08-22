package cn.joey.demo;

import java.util.*;

import cn.joey.entity.Contact;
import cn.joey.entity.DesignedProject;
import cn.joey.solr.core.Joey;
import cn.joey.solr.core.Condition;
import cn.joey.solr.core.Pagination;
import cn.joey.solr.core.Sort;

public class Example {
	public static void main(String[] args) {
        designProject();
	}


	public static void designProject() {
        //query 1
        List<DesignedProject> result = new Joey(DesignedProject.class).search();
        result.forEach(obj -> {
            System.out.println(obj);
        });
    }

	public static void query1() {
		//query 1
		List<Contact> result = new Joey(Contact.class).search();
		result.forEach(obj -> {
			System.out.println(obj);
		});
	}

	public static void query2() {
		//query 2
		List<Contact> result = new Joey(Contact.class, new Pagination(1, 30)).search();
		result.forEach(obj -> {
			System.out.println(obj);
		});
	}

	public static void query3() {
		//---查询条件---
		List<Condition> q = new ArrayList<>();
		q.add(new Condition("company_name", new String[]{"厦门"}));
		//---查询结果---
		List<Contact> result = new Joey(Contact.class, q, true).search();
		//---遍历结果---
		result.forEach(obj -> {
			System.out.println(obj);
		});
	}

	public static void query4() {
		//---查询条件---
		List<Condition> q = new ArrayList<>();
		q.add(new Condition("company_name", new String[]{"厦门"}));
		//---分页对象---
		Pagination pagination = new Pagination(1, 30);
		//---查询结果---
		List<Contact> result = new Joey(Contact.class, q, true, pagination).search();
		//---遍历结果---
		result.forEach(obj -> {
			System.out.println(obj);
		});
	}

	public static void query5() {
		//---查询条件---
		List<Condition> q = new ArrayList<>();
		q.add(new Condition("company_name", new String[]{"厦门"}));
		//---过滤条件---
		List<Condition> fq = new ArrayList<>();
		fq.add(new Condition("province", new String[]{"福建"}));
		//---排序条件---
		List<Sort> sort = new ArrayList<>();
		sort.add(new Sort("id", false));
		//---分页对象--
		Pagination pagination = new Pagination(1, 30);
		//---查询结果---
		List<Contact> result = new Joey(Contact.class, q, fq, sort, pagination).search();
		//---遍历结果---
		result.forEach(obj -> {
			System.out.println(obj);
		});
	}

	/**
	 * 全量更新索引（带参数）
	 */
	public static void fullImport() {
		Map<String, Object> param = new HashMap<>();
		param.put("index", 0);
		param.put("size", 10000);
		System.out.println(new Joey(Contact.class).fullImport(param));
	}

	/**
	 * 增量更新索引
	 */
	public static void deltaImport() {
		Map<String, Object> param = new HashMap<>();
		System.out.println(new Joey(Contact.class).deltaImport());
	}

}
