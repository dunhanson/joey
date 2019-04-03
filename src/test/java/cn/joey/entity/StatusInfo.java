package cn.joey.entity;

/**
 * 状态信息
 */
public class StatusInfo {
    private Integer pageNo;
    private Integer startIndex;
    private Integer pageSize;
    private Integer total;
    private String startTime;
    private String nowTime;
    private Integer alredyCreate;

    public StatusInfo() {

    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getNowTime() {
        return nowTime;
    }

    public void setNowTime(String nowTime) {
        this.nowTime = nowTime;
    }

    public Integer getAlredyCreate() {
        return alredyCreate;
    }

    public void setAlredyCreate(Integer alredyCreate) {
        this.alredyCreate = alredyCreate;
    }
}
