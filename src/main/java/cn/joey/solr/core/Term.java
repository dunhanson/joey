package cn.joey.solr.core;

import java.util.List;

public class Term {
    private List<Condition> q;
    private List<Condition> fq;
    private List<Sort> sort;
    private Pagination pagination;

    public Term() {
        pagination = new Pagination(1, 30);
    }

    public Term(List<Condition> q, List<Condition> fq, List<Sort> sort, Pagination pagination) {
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

    @Override
    public String toString() {
        return "QueryTerm{" +
                "q=" + q +
                ", fq=" + fq +
                ", sort=" + sort +
                ", pagination=" + pagination +
                '}';
    }
}
