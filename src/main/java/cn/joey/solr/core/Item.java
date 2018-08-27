package cn.joey.solr.core;

import java.util.List;
import java.util.Map;

public class Item {
    private List<Condition> q;
    private List<Condition> fq;
    private List<Sort> sort;
    private Map<String, String> param;
    private Pagination pagination;

    public Item() {
        pagination = new Pagination(1, 30);
    }

    public Item(List<Condition> q, List<Condition> fq, List<Sort> sort, Pagination pagination) {
        this.q = q;
        this.fq = fq;
        this.sort = sort;
        this.pagination = pagination;
    }

    public List<Condition> getQ() {
        return q;
    }

    public void setQ(List<Condition> q) {
        this.q = q;
    }

    public List<Condition> getFq() {
        return fq;
    }

    public void setFq(List<Condition> fq) {
        this.fq = fq;
    }

    public List<Sort> getSort() {
        return sort;
    }

    public void setSort(List<Sort> sort) {
        this.sort = sort;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public Map<String, String> getParam() {
		return param;
	}

	public void setParam(Map<String, String> param) {
		this.param = param;
	}

	@Override
	public String toString() {
		return "Item [q=" + q + ", fq=" + fq + ", sort=" + sort + ", param=" + param + ", pagination=" + pagination
				+ "]";
	}
}
