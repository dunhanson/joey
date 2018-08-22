package cn.joey.solr.core;

import cn.joey.solr.annotation.Collection;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

public class SolrClientStore {
    //SolrClient对象仓库
    private static Map<String, SolrClient> store = new Hashtable<>();

    /**
     * 获取SolrClient对象
     * @param <T>
     * @return
     */
    public synchronized static <T> SolrClient getSolrClient(SolrInfo solrInfo) {
        SolrClient client = store.get(solrInfo.getCollection());
        //判断SolrClient是否有效
        if(isValid(client)) {
            return client;
        } else if(solrInfo.isCluster()) { //集群模式
            //创建CloudSolrClient对象
            CloudSolrClient cloudSolrClient = new CloudSolrClient.Builder().withZkHost(solrInfo.getZkHost()).build();
            if(solrInfo.getZkClientTimeout() > 0) { //zk客户端超时时间
                cloudSolrClient.setZkClientTimeout(solrInfo.getZkClientTimeout());
            }
            if(solrInfo.getZkConnectTimeout() > 0) { //zk连接超时时间
                cloudSolrClient.setZkConnectTimeout(solrInfo.getZkConnectTimeout());
            }
            //设置Collection名称
            cloudSolrClient.setDefaultCollection(solrInfo.getCollection());
            //转SolrClient对象
            client = cloudSolrClient;
        } else { // 单机模式
            //创建SolrClient对象
            client = new HttpSolrClient.Builder(solrInfo.getBaseSolrUrl()).build();
        }
        //存入仓库对象
        store.put(solrInfo.getCollection(), client);
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

    public static void destory() {
        store.keySet().forEach(key->{
            try {
                store.get(key).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
