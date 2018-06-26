package cn.joey.solr.core;

import cn.joey.solr.annotation.Collection;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import java.util.Hashtable;
import java.util.Map;

public class SolrClientStore {
    //SolrClient对象仓库
    private static Map<String, SolrClient> store = new Hashtable<>();

    /**
     * 获取SolrClient对象
     * @param clazz
     * @param <T>
     * @return
     */
    public synchronized static <T> SolrClient getSolrClient(Class<T> clazz) {
        SolrClient client = store.get(clazz.getSimpleName());
        //判断SolrClient是否有效
        if(isValid(client)) {
            return client;
        }
        Collection collection = clazz.getAnnotation(Collection.class);
        if(collection.cluster()) {//集群模式
            //创建CloudSolrClient对象
            CloudSolrClient cloudSolrClient = new CloudSolrClient.Builder().withZkHost(collection.zkHost()).build();
            //zk客户端超时时间
            int zkClientTimeout = collection.zkClientTimeout();
            if(zkClientTimeout > 0) {
                cloudSolrClient.setZkClientTimeout(zkClientTimeout);
            }
            //zk连接超时时间
            int zkConnectTimeout = collection.zkConnectTimeout();
            if(zkConnectTimeout > 0) {
                cloudSolrClient.setZkConnectTimeout(zkConnectTimeout);
            }
            //设置Collection名称
            cloudSolrClient.setDefaultCollection(collection.value());
            //转SolrClient对象
            client = cloudSolrClient;
        } else {//单机模式
            //创建SolrClient对象
            client = new HttpSolrClient.Builder(collection.baseSolrUrl()).build();
        }
        //存入仓库对象
        store.put(clazz.getSimpleName(), client);
        return client;
    }

    /**
     * 判断SolrClient对象是否有效
     * @param client
     * @return
     */
    public static boolean isValid(SolrClient client) {
        try {
            client.ping().getQTime();
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
