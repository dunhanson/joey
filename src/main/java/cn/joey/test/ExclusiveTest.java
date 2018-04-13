package cn.joey.test;

import java.util.ArrayList;
import java.util.List;
import cn.joey.solr.core.Query;
import cn.joey.solr.entity.Condition;
import cn.joey.solr.entity.Pagination;

public class ExclusiveTest {
	public static void main(String[] args) {
		Pagination pagination = new Pagination(0, 30);
		List<Condition> q = new ArrayList<>();
		q.add(new Condition("xm_name", new String[] {"飞机"}, true, true));
		Query<ExclusiveProject> query = new Query<ExclusiveProject>(ExclusiveProject.class);
		query.setPagination(pagination);
		query.setQ(q);
		query.search();
		List<ExclusiveProject> list = query.getResult();
		for (ExclusiveProject exclusiveProject : list) {
			System.out.println(exclusiveProject);
		}
		System.out.println(query.getNotHighlightValue());
	}
}
