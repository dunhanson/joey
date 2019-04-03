package cn.joey.demo;

import cn.joey.solr.core.DataImportParam;
import cn.joey.solr.core.Joey;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class TestQuery {
	public static void main(String[] args) throws IOException {
		String baseSolrUrl = "http://localhost:8983/solr/document";
		Integer total = 10000;
		Integer pageSize = 1000;
		String command = "full-import";
		String core = "document";
		String entity = "document_page";
		createIndex(baseSolrUrl, total, pageSize, command, core, entity);
	}
	
	public static void createIndex(String baseSolrUrl, Integer total, Integer pageSize, String command, String core, String entity) {
		
	  //页数
	  int pages = 0;
	
	  //计算页数
	  pages = total % pageSize == 0 ? total / pageSize : (total / pageSize + 1);
	
	  //开始时间
	  LocalDateTime startTime = LocalDateTime.now();
	  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	  //分页创建索引
	  for(int pageNo = 1; pageNo <= pages; pageNo++) {
	
	      //分页开始记录
	      LocalDateTime now = LocalDateTime.now();
	      int startIndex = (pageNo - 1)*pageSize;
	
	      //分页参数
	      Map<String, Object> extra = new HashMap<>();
	      extra.put("startIndex", startIndex);
	      extra.put("pageSize", pageSize);
	
	      //创建索引
	      Joey.dataImport(baseSolrUrl, new DataImportParam(command, core, entity), extra);
	
	      //检查所以创建状态
	      Long time = System.currentTimeMillis();
	      String status = "";
	      do {
	          //查询solr状态
	          String result = Joey.statusImport(baseSolrUrl, time);
	          try {
				Thread.sleep(1000);
	          } catch (InterruptedException e) {
				e.printStackTrace();
	          }
	          //获取status
	          status = new JSONObject(result).getString("status");
	      } while (status.equals("busy"));
	
	      //检查索引创建数量
	      int numFound = 0;
	      do {
	          Map<String, Object> param = new HashMap<>();
	          param.put("_", System.currentTimeMillis());
	          param.put("q", "*:*");
	          String result = Joey.search(baseSolrUrl, param);
	          numFound = new JSONObject(result).getJSONObject("response").getInt("numFound");
	          try {
				Thread.sleep(1000);
	          } catch (InterruptedException e) {
				e.printStackTrace();
	          }
	      } while (numFound != startIndex + pageSize);
	
	      //输出日志
	      System.out.println("页号：" + pageNo + ", 记录数：" + startIndex);
	      System.out.println("分页：" + pageSize + ", 总页数：" + pages);
	      System.out.println("开始时间：" + startTime.format(formatter));
	      System.out.println("当前时间：" + now.format(formatter));
	      System.out.println("已建索引：" + numFound);
	      System.out.println();
	  }
	  
	}
}
