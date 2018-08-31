package cn.joey.demo;

import java.util.HashMap;
import java.util.Map;

public class TestDemo {
	public static void main(String[] args) {
		Map<String, String> map = new HashMap<>();
		map.put("a", "aaa");
		map.put("b", "bbb");
		map.put("c", "ccc");
		StringBuffer sb = new StringBuffer();
		map.forEach((k, v)->{
			sb.append(k);
			sb.append("=");
			sb.append(v);
			sb.append("&");
		});
		sb.deleteCharAt(sb.lastIndexOf("&"));
		System.out.println(sb);;
	}
}
