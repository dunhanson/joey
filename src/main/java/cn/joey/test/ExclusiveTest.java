package cn.joey.test;

import java.util.List;
import cn.joey.solr.core.Query;

public class ExclusiveTest {
	public static void main(String[] args) {
		/*
		List<Condition> q = new ArrayList<>();
		List<Condition> fq = new ArrayList<>();
		List<Sort> sort = new ArrayList<>();
		Pagination pagination = new Pagination(0, 30);
		*/
		Query<ExclusiveProject> query = new Query<ExclusiveProject>(ExclusiveProject.class);
		query.search();
		List<ExclusiveProject> list = query.getResult();
		for (ExclusiveProject exclusiveProject : list) {
			System.out.println(exclusiveProject);
		}
	}
}
