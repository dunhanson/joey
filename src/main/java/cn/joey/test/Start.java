package cn.joey.test;

import java.util.ArrayList;
import java.util.List;
import cn.joey.solr.core.Query;
import cn.joey.solr.entity.Condition;
import cn.joey.solr.entity.Pagination;
import cn.joey.solr.entity.Sort;

public class Start {
	public static void main(String[] args) {
		//q
		List<Condition> q = new ArrayList<>();
		q.add(new Condition("company_name", new String[] {"厦门"}));
		//fq
		List<Condition> fq = new ArrayList<>();
		fq.add(new Condition("province", new String[] {"福建"}));
		//sort
		List<Sort> sort = new ArrayList<>();
		sort.add(new Sort("id"));
		//分页对象
		Pagination pagination = new Pagination(1, 30);
		//查询对象
		Query<Contact> query = new Query<Contact>(q, fq, sort, pagination, Contact.class);
		System.out.println("q : " + query.getQStr());
		System.out.println("fq : " + query.getFQStr());
		System.out.println("sort : " + query.getSortStr());			
		//查询
		query.search();
		List<Contact> list = query.getResult();
		for (Contact contact : list) {
			System.out.println(contact);
		}
		System.out.println("size：" + list.size());
	}
}
