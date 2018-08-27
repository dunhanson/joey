package cn.joey.demo;

import java.util.*;
import cn.joey.entity.DesignedProject;
import cn.joey.entity.Document;
import cn.joey.solr.core.Condition;
import cn.joey.solr.core.Pagination;
import cn.joey.solr.core.Joey;
import cn.joey.solr.core.Sort;
import cn.joey.solr.core.Store;

/**
 * 搜索条件
 * @author dunhanson
 * @since 2018-08-22
 */
public class Example {
	public static void main(String[] args) {
        search();
	}


	public static void search() {
        List<Condition> q = new ArrayList<>();
        List<Condition> fq = new ArrayList<>();
        List<Sort> sort = new ArrayList<>();
        Pagination pagination = new Pagination(800, 30);
        Joey.search(Document.class, q, fq, sort, pagination).forEach(obj->{
            System.out.println(obj);
        });
        Store.getInstance().destory();
        System.out.println(pagination);
    }

	/**
	 * 全量更新索引（带参数）
	 */
	public static void fullImport() {
		Map<String, Object> param = new HashMap<>();
		param.put("index", 0);
		param.put("size", 10000);
		String result = Joey.fullImport(DesignedProject.class, param);
		System.out.println(result);
	}

	/**
	 * 增量更新索引
	 */
	public static void deltaImport() {
		Map<String, Object> param = new HashMap<>();
        String result = Joey.deltaImport(DesignedProject.class, param);
        System.out.println(result);
	}

}
