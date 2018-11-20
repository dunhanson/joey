package cn.joey.demo;

import org.json.JSONObject;

import cn.joey.entity.Document;
import cn.joey.solr.core.Joey;

public class SolrTest {
	public static void main(String[] args) {
		upadteIndexByHttp();
	}
	
    public static void upadteIndexByHttp() {
        try {
            //JSONObject json = null;
            String status = null;
            //int i = 0;
            Joey.deltaImport(Document.class);
            long time = System.currentTimeMillis();
            String result = "";
            do {
                //查询solr状态
                result = Joey.statusImport(Document.class, time);
                //获取status
                status = new JSONObject(result).getString("status");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (status.equals("busy"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
