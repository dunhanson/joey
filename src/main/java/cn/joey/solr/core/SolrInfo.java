package cn.joey.solr.core;

public class SolrInfo {
    private String collection;
    private boolean cluster;
    private String baseSolrUrl;
    private String zkHost;
    private int zkConnectTimeout;
    private int zkClientTimeout;

    public SolrInfo() {

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

    @Override
    public String toString() {
        return "SolrInfo{" +
                "collection='" + collection + '\'' +
                ", cluster=" + cluster +
                ", baseSolrUrl='" + baseSolrUrl + '\'' +
                ", zkHost='" + zkHost + '\'' +
                ", zkConnectTimeout=" + zkConnectTimeout +
                ", zkClientTimeout=" + zkClientTimeout +
                '}';
    }
}

