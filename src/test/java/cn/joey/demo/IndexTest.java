package cn.joey.demo;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import cn.joey.solr.core.DataImportParam;
import cn.joey.solr.core.Joey;

public class IndexTest {
	public static void main(String[] args) {
        String baseSolrUrl = "http://localhost:8983/solr/document";

        int total = 5000; //记录数
        int pageSize = 1000; //每页大小
        int pages = 0; //页数

        //计算页数
        pages = total % pageSize == 0 ? total / pageSize : (total / pageSize + 1);

        //开始时间
        LocalDateTime startTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        //分页创建索引
        for(int pageNo = 1; pageNo <= pages; pageNo++) {
        	LocalDateTime now = LocalDateTime.now();
            int startIndex = (pageNo - 1)*pageSize;
            System.out.println("页号：" + pageNo + ", 记录数：" + startIndex);
            System.out.println("分页：" + pageSize + ", 总页数：" + pages);
            System.out.println("开始时间：" + startTime.format(formatter));
            System.out.println("当前时间：" + now.format(formatter));
            System.out.println();
            //分页参数
            Map<String, Object> extra = new HashMap<>();
            extra.put("startIndex", startIndex);
            extra.put("pageSize", pageSize);
            try {
                DataImportParam param = new DataImportParam("full-import", "document", "document_page");
                String result = Joey.dataImport(baseSolrUrl, param, extra);
                Long time = System.currentTimeMillis();
                String status = "busy";
                do {
                    result = Joey.statusImport(baseSolrUrl, time);
                    //获取status
                    Type type = new TypeToken<Map<String, Object>>(){}.getType();
                    Map<String, Object> map = new Gson().fromJson(result, type);
                    status = (String)map.get("status");
                    //睡眠1秒钟
                    Thread.sleep(1000);
                } while (status.equals("busy"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
	}
}
