package cn.joey.demo;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import cn.joey.solr.core.Joey;

public class HttpTest {
	public static void main(String[] args) {
		System.out.println(getNumFound("http://120.55.57.11:8983/solr/document"));;
	}
	
    /**
     * 获取当前总索引数
     * @param baseSolrUrl
     * @return
     */
    public static int getNumFound(String baseSolrUrl) {
        Map<String, Object> param = new HashMap<>();
        param.put("_", System.currentTimeMillis());
        param.put("q", "*:*");
        String result = Joey.search(baseSolrUrl, param);
        return new JSONObject(result).getJSONObject("response").getInt("numFound");
    }
}
