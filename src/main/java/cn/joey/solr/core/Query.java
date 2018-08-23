package cn.joey.solr.core;

import cn.joey.solr.annotation.Collection;
import cn.joey.solr.annotation.JoeyField;
import cn.joey.utils.HttpUtils;
import com.google.common.base.Joiner;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Solr通用查询对象
 * @author dunhanson
 * @since 2018-06-20
 */
public class Query<T> {
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
    private static Logger logger = LoggerFactory.getLogger(Query.class);
    private static Properties properties = null;

    private Query() {

    }

    public static <T> List<T> search(Class<T> clazz) {
        Term term = new Term(null, null, null, new Pagination(1, 30));
        return getResult(clazz, term, getSolrInfo(clazz));
    }

    public static <T> List<T> search(Class<T> clazz, Term term) {
        return getResult(clazz, term, getSolrInfo(clazz));
    }

    public static <T> List<T> search(Class<T> clazz, Pagination pagination) {
        Term term = new Term(null, null, null, pagination);
        return getResult(clazz, term, getSolrInfo(clazz));
    }

    public static <T> List<T> search(Class<T> clazz, List<Condition> condition, boolean isQ) {
        List<Condition> q = null;
        List<Condition> fq = null;
        if(isQ) {
            q = condition;
        } else {
            fq = condition;
        }
        Term term = new Term(q, fq, null, new Pagination(1, 30));
        return getResult(clazz, term, getSolrInfo(clazz));
    }

    public static <T> List<T> search(Class<T> clazz, List<Condition> condition, boolean isQ, Pagination pagination) {
        List<Condition> q = null;
        List<Condition> fq = null;
        if(isQ) {
            q = condition;
        } else {
            fq = condition;
        }
        Term term = new Term(q, fq, null, pagination);
        return getResult(clazz, term, getSolrInfo(clazz));
    }

    public static <T> List<T> search(Class<T> clazz, List<Condition> q, List<Condition> fq) {
        Term term = new Term(q, fq, null, new Pagination(1, 30));
        return getResult(clazz, term, getSolrInfo(clazz));
    }

    public static <T> List<T> search(Class<T> clazz, List<Condition> condition, boolean isQ, List<Sort> sort) {
        List<Condition> q = null;
        List<Condition> fq = null;
        if(isQ) {
            q = condition;
        } else {
            fq = condition;
        }
        Term term = new Term(q, fq, sort, new Pagination(1, 30));
        return getResult(clazz, term, getSolrInfo(clazz));
    }

    public static <T> List<T> search(Class<T> clazz, List<Condition> q, List<Condition> fq, List<Sort> sort) {
        Term term = new Term(q, fq, sort, new Pagination(1, 30));
        return getResult(clazz, term, getSolrInfo(clazz));
    }

    public static <T> List<T> search(Class<T> clazz, List<Condition> q, List<Condition> fq,
        List<Sort> sort, Pagination pagination) {
        return getResult(clazz, new Term(q, fq, sort, pagination), getSolrInfo(clazz));
    }

    public static <T> List<T> search(Class<T> clazz, List<Condition> condition, boolean isQ,
        List<Sort> sort, Pagination pagination) {
        List<Condition> q = null;
        List<Condition> fq = null;
        if(isQ) {
            q = condition;
        } else {
            fq = condition;
        }
        return getResult(clazz, new Term(q, fq, sort, pagination), getSolrInfo(clazz));
    }

    /**
     * 获取查询结果
     * @param clazz
     * @param term
     * @param info
     * @param <T>
     * @return
     */
    private static <T> List<T> getResult(Class<T> clazz, Term term, Basic info) {
        //设置参数
        SolrQuery query = new SolrQuery();
        query.set("q", getQStr(term.getQ()));
        query.set("fq", getFQStr(term.getFq()));
        query.set("sort", getSortStr(term.getSort()));
        query.setStart(term.getPagination().getStartNum());
        query.setRows(term.getPagination().getPageSize());
        //高亮设置
        if(info.isHighlightEnable()) {
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
            logger.info("Q:" + getQStr(term.getQ()));
            logger.info("FQ:" + getFQStr(term.getFq()));
            logger.info("Sort:" + getSortStr(term.getSort()));
            logger.info("QTime():" + response.getQTime() + "ms");
            logger.info("ElapsedTime:" + response.getElapsedTime() + "ms");
        }
        //返回实体对象集合
        return toEntities(clazz, term.getPagination(), info, response);
    }

    /**
     * 初始化参数
     * @param clazz
     */
    private static <T> Basic getSolrInfo(Class<T> clazz) {
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
            solrInfo.setCollection(getCollection(clazz, properties));
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
        return null;
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
                if(values != null && values.length > 0) {
                    qStr.append(LEFT_PARENTHESIS);
                    for(int j = 0; j < values.length; j++) {
                        qStr.append(getFuzzyStr(name, values[j], fuzzy));
                        if(j < values.length - 1) {
                            qStr.append(SPACE + OR + SPACE);
                        }
                    }
                    qStr.append(RIGHT_PARENTHESIS);
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
     * 获取范围查询字符串
     * @param name 字段名
     * @param values 字段值
     * @return 查询字符串
     */
    private static String getTOStr(String name, String[] values, boolean innerFuzzy, boolean InnerOr) {
        StringBuffer str = new StringBuffer();
        str.append(name + COLON + "[");
        str.append(QUOTE + values[0] + QUOTE);
        str.append(SPACE + TO + SPACE);
        str.append(QUOTE + values[1] + QUOTE);
        str.append("]");
        return str.toString();
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
            value = STAR + value + STAR;
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
                } else if(field.getAnnotation(JoeyField.class) != null) {//注解
                    name = field.getAnnotation(JoeyField.class).value();
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
            if(value == null || StringUtils.isEmpty((String) value)) {
                return;
            }
            Class<?> clazz = field.getType();
            if(clazz == String.class) {
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
        Map<String, List<String>> map = highlighting.get(id);
        if(map != null){
            List<String> list = map.get(highlightFieldName);
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
            JoeyField joeyField = field.getAnnotation(JoeyField.class);
            if(joeyField != null && joeyField.isID()) {
                return joeyField.value();
            }
        }
        return "id";
    }

    /**
     * Map转URL参数
     * @param map
     * @param isURLEncoder
     * @return
     */
    private static String mapToFormData(Map<String, Object> map, boolean isURLEncoder) {
        String formData = "";
        try {
            if (map != null && map.size() > 0) {
                formData = Joiner.on("&").withKeyValueSeparator("=").join(map);
                if (isURLEncoder) {
                    formData = URLEncoder.encode(formData, "UTF-8");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return formData;
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
     * 更新索引
     * @param baseSolrUrl solr地址
     * @param entity 实体名
     * @param importType 导入类型
     * @return
     */
    private static String dataImport(String baseSolrUrl, String entity, String importType, Map<String, Object> param) {
        try {
            param.put("entity", entity);
            param.put("command", importType);
            //执行更新索引并返回结果
            return HttpUtils.httpPost(baseSolrUrl + "/dataimport", mapToFormData(param, false));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 全量更新索引
     * @return
     */
    public static <T> String fullImport(Class<T> clazz) {
        try {
            Basic info = getSolrInfo(clazz);
            return dataImport(info.getBaseSolrUrl(), info.getDataimportEntity(), FULL_IMPORT, new HashMap<>());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 全量更新索引
     * @param param 自定义参数
     * @param <T>
     * @return
     */
    public static <T> String fullImport(Class<T> clazz, Map<String, Object> param) {
        try {
            //执行更新索引并返回结果
            Basic info = getSolrInfo(clazz);
            return dataImport(info.getBaseSolrUrl(), info.getDataimportEntity(), FULL_IMPORT, param);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 增量更新索引
     * @param param 自定义参数
     * @param <T>
     * @return
     */
    public static <T> String deltaImport(Class<T> clazz, Map<String, Object> param) {
        try {
            //执行更新索引并返回结果
            Basic info = getSolrInfo(clazz);
            return dataImport(info.getBaseSolrUrl(), info.getDataimportEntity(), DELTA_IMPORT, param);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 增量更新索引
     * @return
     */
    public static <T> String deltaImport(Class<T> clazz) {
        try {
            //执行更新索引并返回结果
            Basic info = getSolrInfo(clazz);
            return dataImport(info.getBaseSolrUrl(), info.getDataimportEntity(), DELTA_IMPORT, new HashMap<>());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
