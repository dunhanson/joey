package cn.joey.solr.core;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.joey.solr.annotation.Collection;
import cn.joey.solr.annotation.Column;
import cn.joey.utils.BeanUtils;
import cn.joey.utils.HttpUtils;

/**
 * Solr通用查询对象
 * @author dunhanson
 * @since 2018-06-20
 */
public class Joey<T> {
    private static final String QUOTE = "\"";
    private static final String COLON = ":";
    private static final String OR = "OR";
    private static final String AND = "AND";
    private static final String SPACE = " ";
    private static final String STAR = "*";
    private static final String DOT = ".";
    private static final String TO = "TO";
    private static final String ASC = "ASC";
    private static final String DESC = "DESC";
    private static final String TRUE = "true";
    private static final String LEFT_PARENTHESIS = "(";
    private static final String RIGHT_PARENTHESIS = ")";
    private static final String LEFT_BRACKET = "[";
    private static final String RIGHT_BRACKET = "]";
    private static final String FULL_IMPORT = "full-import";
    private static final String DELTA_IMPORT = "delta-import";
    private static final String CONFIG_FILENAME = "joey.properties";
    private static final String COLLECTION = "collection";
    private static final String CLASSPATH = "classpath";
    private static final String BASESOLR_URL = "baseSolrUrl";
    private static final String DATAIMPORT_ENTITY = "dataimportEntity";
    private static final String CLUSTER = "cluster";
    private static final String ZKHOST = "zkHost";
    private static final String SHOW_TIME = "showTime";
    private static final String ZKCLIENT_TIMEOUT = "zkClientTimeout";
    private static final String ZKCONNECT_TIMEOUT = "zkConnectTimeout";
    private static final String UNDERLINE_CONVERT_ENABLE = "underlineConvertEnable";
    private static final String HIGHLIGHT_ENABLE = "highlightEnable";
    private static final String HIGHLIGHT_FIELDNAME = "highlightFieldName";
    private static final String HIGHTLIGHT_SIMPLEPRE = "highlightSimplePre";
    private static final String HIGHLIGHT_SIMPLEPOST = "highlightSimplePost";
    private static Logger logger = LoggerFactory.getLogger(Joey.class);
    private static Properties properties = null;

    private Joey() {

    }

    public static <T> List<T> search(Class<T> clazz) {
        Item item = new Item(null, null, null, new Pagination(1, 30));
        return getResult(clazz, item, getBasic(clazz));
    }

    public static <T> List<T> search(Class<T> clazz, Item item) {
        return getResult(clazz, item, getBasic(clazz));
    }

    public static <T> List<T> search(Class<T> clazz, Pagination pagination) {
    	if(pagination == null) pagination = new Pagination(1, 30);
        Item item = new Item(null, null, null, pagination);
        return getResult(clazz, item, getBasic(clazz));
    }

    public static <T> List<T> search(Class<T> clazz, List<Condition> condition, boolean isQ) {
        List<Condition> q = null;
        List<Condition> fq = null;
        if(isQ) {
            q = condition;
        } else {
            fq = condition;
        }
        Item item = new Item(q, fq, null, new Pagination(1, 30));
        return getResult(clazz, item, getBasic(clazz));
    }

    public static <T> List<T> search(Class<T> clazz, List<Condition> condition, boolean isQ, Pagination pagination) {
        List<Condition> q = null;
        List<Condition> fq = null;
        if(pagination == null) pagination = new Pagination(1, 30);
        if(isQ) {
            q = condition;
        } else {
            fq = condition;
        }
        Item item = new Item(q, fq, null, pagination);
        return getResult(clazz, item, getBasic(clazz));
    }

    public static <T> List<T> search(Class<T> clazz, List<Condition> q, List<Condition> fq) {
        Item item = new Item(q, fq, null, new Pagination(1, 30));
        return getResult(clazz, item, getBasic(clazz));
    }

    public static <T> List<T> search(Class<T> clazz, List<Condition> condition, boolean isQ, List<Sort> sort) {
        List<Condition> q = null;
        List<Condition> fq = null;
        if(isQ) {
            q = condition;
        } else {
            fq = condition;
        }
        Item item = new Item(q, fq, sort, new Pagination(1, 30));
        return getResult(clazz, item, getBasic(clazz));
    }

    public static <T> List<T> search(Class<T> clazz, List<Condition> q, List<Condition> fq, List<Sort> sort) {
        Item item = new Item(q, fq, sort, new Pagination(1, 30));
        return getResult(clazz, item, getBasic(clazz));
    }

    public static <T> List<T> search(Class<T> clazz, List<Condition> q, List<Condition> fq,
        List<Sort> sort, Pagination pagination) {
    	if(pagination == null) pagination = new Pagination(1, 30);
        return getResult(clazz, new Item(q, fq, sort, pagination), getBasic(clazz));
    }
    
    public static <T> List<T> search(Class<T> clazz, List<Condition> q, List<Condition> fq, String fqStr,
            List<Sort> sort, Pagination pagination) {
        	if(pagination == null) pagination = new Pagination(1, 30);
            return getResult(clazz, new Item(q, fq,fqStr, sort, pagination), getBasic(clazz));
    }

    public static <T> List<T> search(Class<T> clazz, List<Condition> condition, boolean isQ,
        List<Sort> sort, Pagination pagination) {
    	if(pagination == null) pagination = new Pagination(1, 30);
        List<Condition> q = null;
        List<Condition> fq = null;
        if(isQ) {
            q = condition;
        } else {
            fq = condition;
        }
        return getResult(clazz, new Item(q, fq, sort, pagination), getBasic(clazz));
    }
    
    
    public static String search(String completeUrl) {
    	return HttpUtils.httpGet(completeUrl);
    }

    public static String search(String baseSolrUrl, Map<String, Object> param) {
    	String url = baseSolrUrl + "/select";
    	return HttpUtils.httpGet(url, param);
    }
    
    /**
     * 获取查询结果
     * @param clazz
     * @param item
     * @param info
     * @param <T>
     * @return
     */
    private static <T> List<T> getResult(Class<T> clazz, Item item, Basic info) {
    	String q = getQStr(item.getQ());
    	String fq = getFQStr(item.getFq());
    	if(StringUtils.isEmpty(fq) && !StringUtils.isEmpty(item.getFqStr())){ 
    		fq =item.getFqStr();
    	}
    	String sort = getSortStr(item.getSort());
        //设置参数
        SolrQuery query = new SolrQuery();
        query.set("q", q);
        query.set("fq", fq);
        query.set("sort",sort);
        query.setStart(item.getPagination().getStartNum());
        query.setRows(item.getPagination().getPageSize());
        setParam(query, item.getParam());
        //高亮设置
        if(info.isHighlightEnable() && item.getParam() == null) {
            query.setHighlight(true);
            query.addHighlightField(info.getHighlightFieldName());
            query.setHighlightSimplePre(info.getHighlightSimplePre());
            query.setHighlightSimplePost(info.getHighlightSimplePost());
        }
        //获取SolrClient进行查询
        QueryResponse response = null;
        try {
            response = Store.getInstance().getSolrClient(info).query(query);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if(info.isShowTime()) {
        	logger.info("---------- Joey Start ----------");
            logger.info("Q:" + q);
            logger.info("FQ:" + fq);
            logger.info("Sort:" + sort);
            logger.info("QTime:" + response.getQTime() + "ms");
            logger.info("ElapsedTime:" + response.getElapsedTime() + "ms");
            logger.info("---------- Joey End ----------");
        }
        //返回实体对象集合
        return toEntities(clazz, item.getPagination(), info, response);
    }

    /**
     * 设置其它参数
     * @param param
     */
    private static void setParam(SolrQuery query, Map<String, String> param) {
    	if(param != null) {
        	param.keySet().forEach(key->{
        		query.set(key, param.get(key));
        	});	
    	}
	}

	/**
     * 初始化参数
     * @param clazz
     */
    public static <T> Basic getBasic(Class<T> clazz) {
        loadConfiguration();
        Basic solrInfo = new Basic();
        if(properties == null) {//注解方式
            Collection collection = clazz.getAnnotation(Collection.class);
            solrInfo.setCollection(collection.value());
            solrInfo.setCluster(collection.cluster());
            solrInfo.setBaseSolrUrl(collection.baseSolrUrl());
            solrInfo.setZkHost(collection.zkHost());
            solrInfo.setZkClientTimeout(collection.zkConnectTimeout());
            solrInfo.setZkConnectTimeout(collection.zkClientTimeout());
            solrInfo.setUnderlineConvertEnable(collection.underlineConvertEnable());
            solrInfo.setHighlightEnable(collection.highlightEnable());
            solrInfo.setHighlightFieldName(collection.highlightFieldName());
            solrInfo.setHighlightSimplePre(collection.highlightSimplePre());
            solrInfo.setHighlightSimplePost(collection.highlightSimplePost());
            solrInfo.setDataimportEntity(collection.dataimportEntity());
            solrInfo.setShowTime(collection.showTime());
        } else {//配置方式
            String collection = getCollection(clazz, properties);
            solrInfo.setCollection(collection);
            solrInfo.setCluster(getProperty(collection, CLUSTER, "false").equals(TRUE));
            solrInfo.setBaseSolrUrl(getProperty(collection, BASESOLR_URL));
            solrInfo.setZkHost(getProperty(collection, ZKHOST));
            solrInfo.setZkClientTimeout(Integer.parseInt(getProperty(collection, ZKCLIENT_TIMEOUT, "0")));
            solrInfo.setZkConnectTimeout(Integer.parseInt(getProperty(collection, ZKCONNECT_TIMEOUT, "0")));
            solrInfo.setUnderlineConvertEnable(getProperty(collection, UNDERLINE_CONVERT_ENABLE).equals(TRUE));
            solrInfo.setHighlightEnable(getProperty(collection, HIGHLIGHT_ENABLE).equals(TRUE));
            solrInfo.setHighlightFieldName(getProperty(collection, HIGHLIGHT_FIELDNAME, ""));
            solrInfo.setHighlightSimplePre(getProperty(collection, HIGHTLIGHT_SIMPLEPRE, ""));
            solrInfo.setHighlightSimplePost(getProperty(collection, HIGHLIGHT_SIMPLEPOST, ""));
            solrInfo.setDataimportEntity(getProperty(collection, DATAIMPORT_ENTITY, ""));
            solrInfo.setShowTime(getProperty(collection, SHOW_TIME).equalsIgnoreCase(TRUE));
        }
        return solrInfo;
    }

    /**
     * 获取collection
     * @param properties
     * @return
     */
    private static <T> String getCollection(Class<T> clazz, Properties properties) {
        String[] collectionArr = properties.getProperty(COLLECTION).split(",");
        for (String collection : collectionArr) {	//遍历nickname
            String key = collection + DOT + CLASSPATH;
            String classpath = properties.getProperty(key);
            if(classpath.equals(clazz.getCanonicalName())) {
                return collection;
            }
        }
        logger.error("未能匹配到collection");
        throw new RuntimeException("未能匹配到collection");
    }

    /**
     * 获取q (查询条件)
     */
    private static String getQStr(List<Condition> q){
        StringBuffer qStr = new StringBuffer();
        if(q == null || q.isEmpty()) {
            qStr.append(STAR + COLON + STAR);
        } else {
            for(int i = 0; i < q.size(); i++) {
                Condition condition = q.get(i);
                String name = condition.getName();
                String[] values = condition.getValues();
                boolean fuzzy = condition.isFuzzy();
                if(StringUtils.isEmpty(name)) {
                	return values[0];
                }
                if(values != null && values.length > 0) {
                    if(values.length == 1){
                    	 qStr.append(getFuzzyStr(name, values[0], fuzzy));
                    }else{
                    	 qStr.append(LEFT_PARENTHESIS);
                         for(int j = 0; j < values.length; j++) {
                             qStr.append(getFuzzyStr(name, values[j], fuzzy));
                             if(j < values.length - 1) {
                                 qStr.append(SPACE + OR + SPACE);
                             }
                         }
                         qStr.append(RIGHT_PARENTHESIS);
                    }
                }
                if(i < q.size() - 1) {
                    qStr.append(SPACE + (condition.isOr() ? OR : AND) + SPACE);
                }
            }
        }
        return qStr.toString();
    }

    /**
     * 获取fq（过滤条件）
     * @return 过滤条件字符串
     */
    private static String getFQStr(List<Condition> fq){
        StringBuffer fqStr = new StringBuffer();
        for(int i = 0; fq != null && i < fq.size(); i++) {
            Condition condition = fq.get(i);
            String name = condition.getName();
            String[] values = condition.getValues();
            boolean fuzzy = condition.isFuzzy();
            boolean or = condition.isOr();
            boolean innerFuzzy = condition.isInnerFuzzy();
            boolean innerOr = condition.isInnerOr();
            boolean isRange = condition.isRange();
            fqStr.append(LEFT_PARENTHESIS);
            if(values.length == 1) {//精准查询
                fqStr.append(getFuzzyStr(name, values[0], fuzzy));
            } else if(values.length == 2 && isRange) {
                fqStr.append(getRangeStr(name, values));
            } else {
                fqStr.append(getInnerStr(name, values, innerFuzzy, innerOr));
            }
            fqStr.append(RIGHT_PARENTHESIS);
            if(i < fq.size() - 1) {
                fqStr.append(SPACE + (or ? OR : AND) + SPACE);
            }
        }
        return fqStr.toString();
    }

    /**
     * 获取排序字符串
     * @return 排序字符串
     */
    private static String getSortStr(List<Sort> sort){
        StringBuffer sortStr = new StringBuffer();
        for(int i = 0; sort != null && i < sort.size(); i++) {
            Sort getSort = sort.get(i);
            sortStr.append(getSort.getName() + SPACE +(getSort.isAscend() ? ASC : DESC));
            if(i < sort.size() - 1) {
                sortStr.append(",");
            }
        }
        return sortStr.toString();
    }

    /**
     * 获取OR查询字符串
     * @param name
     * @param values
     * @return
     */
    private static String getInnerStr(String name, String[] values, boolean innerFuzzy, boolean innerOr) {
        String logic = innerOr ? OR : AND;
        StringBuffer str = new StringBuffer();
        for(int i = 0; i < values.length; i++) {
            str.append(getFuzzyStr(name, values[i], innerFuzzy));
            str.append(SPACE + logic + SPACE);
        }
        str = str.delete(str.lastIndexOf(logic) - 1, str.length());
        return str.toString();
    }

    /**
     * 获取范围查询字符串
     * @param name
     * @param values
     * @return
     */
    private static String getRangeStr(String name, String[] values) {
        StringBuffer str = new StringBuffer();
        str.append(name);
        str.append(COLON);
        str.append(LEFT_BRACKET);
        str.append(values[0]);
        str.append(SPACE + TO + SPACE);
        str.append(values[1]);
        str.append(RIGHT_BRACKET);
        return str.toString();
    }

    /**
     * 获取模糊查询字符串
     * @param name 字段名
     * @param value 字段值
     * @param fuzzy 模糊查询？
     * @return 查询字符串
     */
    private static String getFuzzyStr(String name, String value, boolean fuzzy) {
        if(fuzzy && !StringUtils.isEmpty(value)) {
            //value = STAR + value + STAR;
            return name + COLON + value;
        }
        return name + COLON + QUOTE + value + QUOTE;
    }

    /**
     * 转换成实体对象集合
     * @return 实体对象集合
     */
    private static <T> List<T> toEntities(Class<T> clazz, Pagination pagination, Basic info, QueryResponse response){
        List<T> entitys = new ArrayList<>();
        SolrDocumentList documentList = response.getResults();
        for (SolrDocument document : documentList) {
            entitys.add(toEntity(document, clazz, info, response));
        }
        pagination.setTotalSize(documentList.getNumFound());
        return entitys;
    }

    /**
     * SolrDocument转实体对象
     * @param document SolrDocument对象
     * @return 实体对象
     */
    private static <T> T toEntity(SolrDocument document, Class<T> clazz, Basic info, QueryResponse response) {
        T entity;
        try {
            entity = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {//遍历字段
                field.setAccessible(true);
                String name = field.getName();
                Object value = document.get(name);
                if(info.isUnderlineConvertEnable()) {//启用大写转换（userName ---> user_name）
                    name = toLowerUnderline(field.getName());
                    value = document.get(name);
                } else if(field.getAnnotation(Column.class) != null) {//注解
                    name = field.getAnnotation(Column.class).value();
                    value = document.get(name);
                }
                //高亮字段
                if(info.isHighlightEnable() && info.getHighlightFieldName().equalsIgnoreCase(name)) {
                    String highlightFieldValue = getHighlightingFieldValue(document,
                            response, getIdFileName(clazz), info.getHighlightFieldName());
                    if(StringUtils.isEmpty(highlightFieldValue) == false) {
                        value = highlightFieldValue;
                    }
                }
                setValue(field, value, entity);
            }
        } catch (Exception e) {
        	logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return entity;
    }

    /**
     * 反射属性赋值
     * @param field 字段
     * @param value 字段值
     * @param entity 实体对象
     */
    private static <T> void setValue(Field field, Object value, T entity) {
        try {
            if(value == null) {
                return;
            }
            Class<?> clazz = field.getType();
            if (value.getClass() == ArrayList.class) {
                field.set(entity, ((List)value).get(0));
            } else if(clazz == String.class) {
                field.set(entity, String.valueOf(value));
            } else if (clazz == Character.class || clazz == char.class) {
                field.set(entity, (char)value);
            }  else if (clazz == Long.class || clazz == long.class) {
                field.set(entity, Long.parseLong(value.toString()));
            } else if (clazz == Integer.class || clazz == int.class) {
                field.set(entity, Integer.parseInt(value.toString()));
            } else if (clazz == Short.class || clazz == short.class) {
                field.set(entity, Short.parseShort(value.toString()));
            } else if (clazz == Byte.class || clazz == byte.class) {
                field.set(entity, Byte.parseByte(value.toString()));
            } else if (clazz == Double.class || clazz == double.class) {
                field.set(entity, Double.parseDouble(value.toString()));
            } else if (clazz == Float.class || clazz == Float.class) {
                field.set(entity, Float.parseFloat(value.toString()));
            } else if (clazz == Boolean.class || clazz == boolean.class) {
                field.set(entity, Boolean.parseBoolean(value.toString()));
            } else if (clazz == Date.class && !(value instanceof String)) {
                field.set(entity, value);
            } else {
                field.set(entity, value);
            }
        } catch (Exception e) {
        	logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取高亮字段值
     * @param document
     * @return
     */
    private static String getHighlightingFieldValue(SolrDocument document,
        QueryResponse response, String idFileName, String highlightFieldName) {
        Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
        String id = (String) document.get(idFileName);
        if(highlighting != null && highlighting.get(id) != null){
            List<String> list = highlighting.get(id).get(highlightFieldName);
            if(list != null && list.size() > 0){
                return list.get(0);
            }
        }
        return null;
    }

    /**
     * 驼峰转小写下划线
     * @param str
     * @return
     */
    private static String toLowerUnderline(String str) {
        if(StringUtils.isEmpty(str)) {
            return "";
        }
        Pattern pattern = Pattern.compile("[A-Z]");
        Matcher matcher = pattern.matcher(str);
        int index = 0;
        while(matcher.find()) {
            String findStr = matcher.group(index);
            str = str.replace(findStr, "_" + findStr);
            //index ++;
        }
        return str.toLowerCase();
    }

    /**
     * 获取ID字段名
     * @return
     */
    private static <T> String getIdFileName(Class<T> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields) {
        	Column column = field.getAnnotation(Column.class);
            if(column != null && column.isID()) {
                return column.value();
            }
        }
        return "id";
    }
    
    /**
     * 读取配置文件
     * @return
     */
    private static void loadConfiguration(){
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try(InputStream in = contextClassLoader.getResourceAsStream(CONFIG_FILENAME)) {
            if(in != null) {
                properties = new Properties();
                properties.load(new InputStreamReader(in, "UTF-8"));
            }
        } catch (Exception e) {
        	logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取属性值
     * @param key
     * @return
     */
    private static String getProperty(String collection, String key) {
        key = collection + DOT + key;
        String value = properties.getProperty(key);
        if(value == null) {
            value = "";
        }
        return value;
    }

    /**
     * 获取属性值（默认值）
     * @param key
     * @param defaultValue
     * @return
     */
    private static String getProperty(String collection, String key, String defaultValue) {
        key = collection + DOT + key;
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * 查看创建索引状态
     * @param baseSolrUrl
     * @param times
     * @return
     */
    public static <T> String statusImport(String baseSolrUrl, Long times) {
        //封装参数
    	Map<String, Object> param = new HashMap<>();
    	param.put("_", times == null ? System.currentTimeMillis() : times);
        param.put("command", "status");
        param.put("indent", "on");
        param.put("wt", "json");
        //执行请求并返回结果
        return HttpUtils.httpGet(baseSolrUrl + "/dataimport", param);
    }
    
    /**
     * 查看创建索引状态
     * @param clazz
     * @param time
     * @return
     */
    public static <T> String statusImport(Class<T> clazz, Long time) {
    	return statusImport(getBasic(clazz).getBaseSolrUrl(), time);
    }    
    
    /**
     * 数据导入
     * @param baseSolrUrl
     * @param param
     * @param extra
     * @return
     */
    public static String dataImport(String baseSolrUrl, DataImportParam param, Map<String, Object> extra) {
        try {
        	//防止空指针异常
        	extra = extra == null ? new HashMap<>() : extra;
        	//转换map集合
        	Map<String, Object> mapParam = BeanUtils.toMap(param);
        	//追加额外参数
        	mapParam.putAll(extra);
        	//执行请求
        	long time = System.currentTimeMillis();
        	baseSolrUrl += "/dataimport?_=TIME&indent=on&wt=json";
        	baseSolrUrl = baseSolrUrl.replaceAll("TIME", String.valueOf(time));
            return HttpUtils.httpPost(baseSolrUrl, mapParam);
        } catch (Exception e) {
        	logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 数据导入
     * @param baseSolrUrl
     * @param param
     * @return
     */
    public static String dataImport(String baseSolrUrl, DataImportParam param) {
        return dataImport(baseSolrUrl, param, null);
    }
    
    /**
     * 全量更新索引
     * @param param 自定义参数
     * @param <T>
     * @return
     */
    public static <T> String fullImport(Class<T> clazz, Map<String, Object> param) {
        try {
            Basic info = getBasic(clazz);
            String command = FULL_IMPORT;
            String core = info.getCollection();
            String entity = info.getDataimportEntity();
            return dataImport(info.getBaseSolrUrl(), new DataImportParam(command, core, entity), param);
        } catch (Exception e) {
        	logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 全量更新索引
     * @return
     */
    public static <T> String fullImport(Class<T> clazz) {
        return fullImport(clazz, null);
    }

    /**
     * 增量更新索引
     * @param param 自定义参数
     * @param <T>
     * @return
     */
    public static <T> String deltaImport(Class<T> clazz, Map<String, Object> param) {
        try {
            Basic info = getBasic(clazz);
            String command = DELTA_IMPORT;
            String core = info.getCollection();
            String entity = info.getDataimportEntity();
            return dataImport(info.getBaseSolrUrl(), new DataImportParam(command, core, entity), param);
        } catch (Exception e) {
        	logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 增量更新索引
     * @return
     */
    public static <T> String deltaImport(Class<T> clazz) {
        return deltaImport(clazz, null);
    }
    
    /**
     * 删除单个索引
     * @param clazz
     * @param id
     */
    public static <T> void deleteIndex(Class<T> clazz, String id) {
    	try {
        	Basic info = getBasic(clazz);
			Store.getInstance().getSolrClient(info).deleteById(id);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
    }

    /**
     * 删除多个索引
     * @param clazz
     * @param ids
     */
    public static <T> void deleteIndex(Class<T> clazz, List<String> ids) {
    	try {
        	Basic info = getBasic(clazz);
			Store.getInstance().getSolrClient(info).deleteById(ids);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
    }
    
    /**
     * 更新单个索引
     * @param clazz
     * @param document
     */
    public static <T> void upadteIndex(Class<T> clazz, SolrInputDocument document) {
    	try {
        	SolrClient client = Store.getInstance().getSolrClient(getBasic(clazz));
			client.add(document);
			client.commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
    }
    
    /**
     * 更新多个索引
     * @param clazz
     * @param documents
     */
    public static <T> void upadteIndex(Class<T> clazz, List<SolrInputDocument> documents) {
    	try {
        	SolrClient client = Store.getInstance().getSolrClient(getBasic(clazz));
			client.add(documents);
			client.commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
    }

}
