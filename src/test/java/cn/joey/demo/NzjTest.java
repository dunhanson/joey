package cn.joey.demo;

import cn.joey.entity.DesignedProject;
import cn.joey.solr.core.Condition;
import cn.joey.solr.core.Joey;
import cn.joey.solr.core.Pagination;
import cn.joey.solr.core.Sort;
import com.ctc.wstx.dtd.ConcatModel;

import java.util.ArrayList;
import java.util.List;

public class NzjTest {
    public static void main(String[] args) {
        List<Condition> fq = new ArrayList<>();

        fq.add(new Condition("page_time", new String[]{"2019-05-04", "2019-05-05"}, Condition.Type.RANGE, true));
        fq.add(new Condition("area", new String[]{""}));

        // 排序
        List<Sort> sorts = new ArrayList<>();
        Sort sort = new Sort();
        sort.setName("page_time");
        sort.setAscend(false);
        sorts.add(sort);
        Pagination pagination = new Pagination(1, 30);
        List<DesignedProject> result = Joey.search(DesignedProject.class, null, fq, sorts, pagination);

    }
}
