package cn.joey.demo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.zip.CheckedInputStream;

import cn.joey.entity.Document;
import cn.joey.solr.core.Condition;
import cn.joey.solr.core.Joey;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TestDemo {

	//{"天津":[],"北京":[],"上海":[],"广东":["广州"]}
	//{"广东":["广州","深圳"],"北京":[],"天津":[]}
	//{"广东":["广州"]}
	public static void main(String[] args) {
		List<Condition> fq = new ArrayList<>();

		String json = "{\"广东\":[\"广州\",\"深圳\"],\"北京\":[],\"天津\":[]}";
		Type type = new TypeToken<Map<String, String[]>>(){}.getType();
		Map<String, String[]> map = new Gson().fromJson(json, type);

		List<String> provinceList = new ArrayList<>();
		List<Condition> conditions = new ArrayList<>();
		map.keySet().forEach(province->{
			String[] citys = map.get(province);
			if(citys == null || citys.length == 0) {
				provinceList.add(province);
			} else {
				Condition cityCondition = new Condition();
				cityCondition.setName("city");
				cityCondition.setValues(citys);
				cityCondition.setInnerOr(true);

				Condition provinceCondition = new Condition();
				provinceCondition.setValues(new String[]{province});
				provinceCondition.setName("province");
				provinceCondition.setConditions(Arrays.asList(new Condition[]{cityCondition}));
				provinceCondition.setOr(true);
				conditions.add(provinceCondition);
			}
		});

		Condition condition = new Condition();
		condition.setName("province");
		condition.setValues(provinceList.toArray(new String[provinceList.size()]));
		condition.setConditions(conditions);
		condition.setInnerOr(true);
		condition.setOr(false);
		fq.add(condition);

		fq.add(new Condition("docchannel", new String[]{"102", "52", "51"}));

		Joey.search(Document.class, fq, false).forEach(obj->{
			System.out.println(obj);
		});

	}
}
