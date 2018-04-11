package cn.joey.test;

import java.util.List;
import cn.joey.solr.core.Query;
import cn.joey.solr.entity.Pagination;

public class ExclusiveTest {
	public static void main(String[] args) {
		Query<ExclusiveProject> query = new Query<ExclusiveProject>(new Pagination(0, 30), ExclusiveProject.class);
		query.search();
		List<ExclusiveProject> list = query.getResult();
		for (ExclusiveProject exclusiveProject : list) {
			System.out.println(exclusiveProject);
		}
	}
}
