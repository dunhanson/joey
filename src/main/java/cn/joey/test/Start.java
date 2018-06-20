package cn.joey.test;

import java.util.ArrayList;
import java.util.List;
import cn.joey.solr.core.Query;
import cn.joey.solr.entity.Condition;
import cn.joey.solr.entity.Pagination;
import cn.joey.solr.entity.Sort;

public class Start {
	public static void main(String[] args) {
		nzjxm();
	}
	
	public static void contact() {
		//查询条件
		List<Condition> q = new ArrayList<>();
		//q.add(new Condition("company_name", new String[] {"厦门"}));
		//过滤条件
		List<Condition> fq = new ArrayList<>();
		//fq.add(new Condition("province", new String[] {"福建"}));
		//排序条件
		List<Sort> sort = new ArrayList<>();
		//sort.add(new Sort("id"));
		//分页对象
		Pagination pagination = new Pagination(1, 30);
		//查询对象
		Query<Contact> query = new Query<Contact>(q, fq, sort, pagination, Contact.class);	
		//打印参数
		System.out.println("q : " + query.getQStr());
		System.out.println("fq : " + query.getFQStr());
		System.out.println("sort : " + query.getSortStr());				
		//查询
		query.search();
		//分页信息
		System.out.println(pagination);
	}
	
	public static void nzjxm() {
		//查询条件
		List<Condition> q = new ArrayList<>();
		//过滤条件
		List<Condition> fq = new ArrayList<>();
		//fq.add(new Condition("page_time", new String[] {"2018-05-01 TO 2018-06-01"}, false, ));
		fq.add(new Condition("page_time", new String[] {"2018-05-01", "2018-06-01"}, Condition.Type.RANGE, true));
		//排序条件
		List<Sort> sort = new ArrayList<>();
		//分页对象
		Pagination pagination = new Pagination(1, 30);
		//查询对象
		Query<DesignedProject> query = new Query<DesignedProject>(q, fq, sort, pagination, DesignedProject.class);	
		//打印参数
		System.out.println("q : " + query.getQStr());
		System.out.println("fq : " + query.getFQStr());
		System.out.println("sort : " + query.getSortStr());				
		//查询
		query.search();
		//分页信息
		System.out.println(pagination);
		//输出内容
		query.getResult().forEach(obj -> {
			System.out.println(obj);
		});
	}
}
