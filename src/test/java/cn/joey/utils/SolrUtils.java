package cn.joey.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.joey.entity.BaseEntity;
import cn.joey.entity.StatusInfo;
import cn.joey.solr.core.DataImportParam;
import cn.joey.solr.core.Joey;

public class SolrUtils {
    public static Logger logger = LoggerFactory.getLogger(SolrUtils.class);
    public static boolean enabled = true;
    public static StatusInfo info = new StatusInfo();


    /**
     * 创建索引
     * @param baseEntity
     */
    public static void createIndex(BaseEntity baseEntity) {

        //基本参数
        int pageNo = baseEntity.getPageNo() == null ? 1 : baseEntity.getPageNo();
        int pages = 0;
        int total = baseEntity.getTotal();
        int pageSize = baseEntity.getPageSize();
        String baseSolrUrl = baseEntity.getBaseSolrUrl();
        String command = baseEntity.getCommand();
        String core = baseEntity.getCore();
        String entity = baseEntity.getEntity();

        //计算页数
        pages = total % pageSize == 0 ? total / pageSize : (total / pageSize + 1);

        //开始时间
        LocalDateTime startTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        //信息
        StringBuffer stringBuffer = new StringBuffer();

        //分页创建索引
        for(int i = pageNo; i <= pages; i++) {

            //检查索引创建数量
            //int startNumFound = getNumFound(baseSolrUrl);

            //启动开关
            if(!enabled) {
                break;
            }

            //清空记录
            stringBuffer.setLength(0);

            //分页开始记录
            LocalDateTime now = LocalDateTime.now();
            int startIndex = (i - 1)*pageSize;

            //分页参数
            Map<String, Object> extra = new HashMap<>();
            extra.put("startIndex", startIndex);
            extra.put("pageSize", pageSize);

            //创建索引
            Joey.dataImport(baseSolrUrl, new DataImportParam(command, core, entity), extra);

            //检查所以创建状态
            Long time = System.currentTimeMillis();
            String status = "";
            do {
                //查询solr状态
                String result = Joey.statusImport(baseSolrUrl, time);
                //获取status
                status = new JSONObject(result).getString("status");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (status.equals("busy"));

            //检查索引创建数量
            /*
            int nowNumFound = 0;
            do {
                //如果开始查询的索引总记录等于执行创建索引后查询的索引总记录
                nowNumFound = getNumFound(baseSolrUrl);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("nowNumFound:" + nowNumFound);
                System.out.println("startNumFound:" + startNumFound);
            } while (nowNumFound == startNumFound);
            */

            int nowNumFound = getNumFound(baseSolrUrl);
            //当前时间
            String nowTime = now.format(formatter);
            //输出日志
            stringBuffer.append("\n当前页：" + i + "\n");
            stringBuffer.append("开始数：" + startIndex + "\n");
            stringBuffer.append("每页数：" + pageSize + "\n");
            stringBuffer.append("总页数：" + pages + "\n");
            stringBuffer.append("开始时间：" + startTime.format(formatter) + "\n");
            stringBuffer.append("当前时间：" + nowTime + "\n");
            stringBuffer.append("已建索引：" + nowNumFound + "\n");
            logger.info(stringBuffer.toString());

            //设置信息
            info.setPageNo(i);
            info.setPageSize(pageSize);
            info.setTotal(total);
            info.setStartTime(now.format(formatter));
            info.setNowTime(nowTime);
            info.setStartIndex(nowNumFound);
        }

    }

    /**
     * 获取当前总索引数
     * @param baseSolrUrl
     * @return
     */
    public static int getNumFound(String baseSolrUrl) {
        Map<String, Object> param = new HashMap<>();
        param.put("_", System.currentTimeMillis());
        param.put("q", "*:*");
        String result = Joey.search(baseSolrUrl, param);
        return new JSONObject(result).getJSONObject("response").getInt("numFound");
    }



}
