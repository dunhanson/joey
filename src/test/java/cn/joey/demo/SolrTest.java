package cn.joey.demo;

import cn.joey.entity.BaseEntity;
import cn.joey.utils.SolrUtils;

public class SolrTest {
	public static void main(String[] args) {
		BaseEntity entity = new BaseEntity();
		String baseUrl = "http://47.99.61.86:8983/solr/document";
	    entity.setBaseSolrUrl(baseUrl);
	    entity.setEntity("document");
	    entity.setCore("document");
	    entity.setPageNo(4294);
	    entity.setPageSize(10000);
	    entity.setTotal(42942716);
	    entity.setCommand("full-import");
	    SolrUtils.createIndex(entity);
	    //System.out.println(SolrUtils.getNumFound(baseUrl));
	    System.out.println("finish...");
	}

}
