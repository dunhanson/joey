package cn.joey.test;

import java.util.ArrayList;
import java.util.List;
import cn.joey.solr.core.Query;
import cn.joey.solr.entity.Condition;
import cn.joey.solr.entity.Pagination;

public class ExclusiveTest {
	public static void main(String[] args) {
		Pagination pagination = new Pagination(0, 30);
		List<Condition> fq = new ArrayList<>();
		fq.add(new Condition("owner_type", new String[] {"外资"}));
		fq.add(new Condition("project_size", new String[] {"大型","普通"}, true, true));
		Query<ExclusiveProject> query = new Query<ExclusiveProject>(ExclusiveProject.class);
		query.setPagination(pagination);
		query.setFq(fq);
		query.search();
		System.out.println(query.getFQStr());
		System.out.println(pagination);
	}
}
