package cn.joey.solr.core;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Store {
    private static volatile Store instance = null;
    private Map<String, SolrClient> store = new ConcurrentHashMap<>();
    private Logger logger = LoggerFactory.getLogger(Store.class);

    private Store() {

    }

    /**
     * 获取SolrClientStore实例
     * @return
     */
    public static Store getInstance() {
        if(instance == null) {
            synchronized (Store.class) {
                if(instance == null) {
                    instance = new Store();
                }
            }
        }
        return instance;
    }

    /**
     * 获取SolrClient对象
     * @return
     */
    public SolrClient getSolrClient(Basic basic) {
        SolrClient client = store.get(basic.getCollection());
        //判断SolrClient是否有效
        if(client == null || !isValid(client)) {
            return newSolrClient(basic);
        }
        return client;
    }

    /**
     * 创建SolrClient对象
     * @param basic
     * @return
     */
    public SolrClient newSolrClient(Basic basic) {
        String collection = basic.getCollection();
        String zkHost = basic.getZkHost();
        int zkClientTimeout = basic.getZkClientTimeout();
        int zkConnectTimeout = basic.getZkConnectTimeout();
        if(basic.isCluster()) { //集群模式
            CloudSolrClient cloudSolrClient = new CloudSolrClient.Builder().withZkHost(zkHost).build();
            if(zkClientTimeout > 0) cloudSolrClient.setZkClientTimeout(zkClientTimeout);
            if(zkConnectTimeout > 0) cloudSolrClient.setZkConnectTimeout(zkConnectTimeout);
            cloudSolrClient.setDefaultCollection(collection);
            store.put(collection, cloudSolrClient);
        } else { //单机模式
            store.put(collection, new HttpSolrClient.Builder(basic.getBaseSolrUrl()).build());
        }
        return store.get(collection);
    }

    /**
     * 判断SolrClient对象是否有效
     * @param client
     * @return
     */
    public boolean isValid(SolrClient client) {
        try {
            client.ping().getQTime();
        } catch (Exception e) {
            logger.warn(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 销毁所有资源
     */
    public void destory() {
        store.keySet().forEach(key->{
            try {
            	SolrClient client = store.get(key);
            	client.close();	
            } catch (IOException e) {
                logger.warn(e.getMessage());
            }
        });
    }
}
