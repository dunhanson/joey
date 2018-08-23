package cn.joey.solr.core;

public class Basic {
    private String collection;
    private boolean cluster;
    private String baseSolrUrl;
    private String zkHost;
    private int zkConnectTimeout;
    private int zkClientTimeout;
    private boolean underlineConvertEnable;
    private boolean highlightEnable;
    private String highlightFieldName;
    private String highlightSimplePre;
    private String highlightSimplePost;
    private String dataimportEntity;
    private boolean showTime;

    public Basic() {

    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public boolean isCluster() {
        return cluster;
    }

    public void setCluster(boolean cluster) {
        this.cluster = cluster;
    }

    public String getBaseSolrUrl() {
        return baseSolrUrl;
    }

    public void setBaseSolrUrl(String baseSolrUrl) {
        this.baseSolrUrl = baseSolrUrl;
    }

    public String getZkHost() {
        return zkHost;
    }

    public void setZkHost(String zkHost) {
        this.zkHost = zkHost;
    }

    public int getZkConnectTimeout() {
        return zkConnectTimeout;
    }

    public void setZkConnectTimeout(int zkConnectTimeout) {
        this.zkConnectTimeout = zkConnectTimeout;
    }

    public int getZkClientTimeout() {
        return zkClientTimeout;
    }

    public void setZkClientTimeout(int zkClientTimeout) {
        this.zkClientTimeout = zkClientTimeout;
    }

    public boolean isUnderlineConvertEnable() {
        return underlineConvertEnable;
    }

    public void setUnderlineConvertEnable(boolean underlineConvertEnable) {
        this.underlineConvertEnable = underlineConvertEnable;
    }

    public boolean isHighlightEnable() {
        return highlightEnable;
    }

    public void setHighlightEnable(boolean highlightEnable) {
        this.highlightEnable = highlightEnable;
    }

    public String getHighlightFieldName() {
        return highlightFieldName;
    }

    public void setHighlightFieldName(String highlightFieldName) {
        this.highlightFieldName = highlightFieldName;
    }

    public String getHighlightSimplePre() {
        return highlightSimplePre;
    }

    public void setHighlightSimplePre(String highlightSimplePre) {
        this.highlightSimplePre = highlightSimplePre;
    }

    public String getHighlightSimplePost() {
        return highlightSimplePost;
    }

    public void setHighlightSimplePost(String highlightSimplePost) {
        this.highlightSimplePost = highlightSimplePost;
    }

    public String getDataimportEntity() {
        return dataimportEntity;
    }

    public void setDataimportEntity(String dataimportEntity) {
        this.dataimportEntity = dataimportEntity;
    }

    public boolean isShowTime() {
        return showTime;
    }

    public void setShowTime(boolean showTime) {
        this.showTime = showTime;
    }

    @Override
    public String toString() {
        return "SolrInfo{" +
                "collection='" + collection + '\'' +
                ", cluster=" + cluster +
                ", baseSolrUrl='" + baseSolrUrl + '\'' +
                ", zkHost='" + zkHost + '\'' +
                ", zkConnectTimeout=" + zkConnectTimeout +
                ", zkClientTimeout=" + zkClientTimeout +
                ", underlineConvertEnable='" + underlineConvertEnable + '\'' +
                ", highlightEnable='" + highlightEnable + '\'' +
                ", highlightFieldName='" + highlightFieldName + '\'' +
                ", highlightSimplePre='" + highlightSimplePre + '\'' +
                ", highlightSimplePost='" + highlightSimplePost + '\'' +
                ", dataimportEntity='" + dataimportEntity + '\'' +
                ", showTime='" + showTime + '\'' +
                '}';
    }
}

