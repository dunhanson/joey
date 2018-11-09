package cn.joey.demo;

import cn.joey.utils.HttpUtils;
import org.json.JSONObject;
import java.io.IOException;

public class TestQuery {
	public static void main(String[] args) throws IOException {
        String url = "http://localhost:8983/solr/document/select?_=1541729147484&q=*:*";
		String result = HttpUtils.httpGet(url);
		System.out.println(new JSONObject(result).getJSONObject("response").getInt("numFound"));
	}
}
