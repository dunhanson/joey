package cn.joey.solr.core;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.StringUtils;
import cn.joey.solr.entity.Condition;
import cn.joey.solr.entity.Pagination;
import cn.joey.solr.entity.Sort;
import cn.joey.utils.HttpUtils;

/**
 * Solr通用查询对象
 * @author dunhanson
 * @since 2017-12-27
 */
public class Query<T> {
	private final String QUOTE = "\"";						
	private final String COLON = ":";								
	private final String OR = "OR";									
	private final String AND = "AND";								
	private final String SPACE = " ";					
	private final String STAR = "*";							
	private final String DOT = ".";
	private final String TO = "TO";								
	private final String ASC = "ASC";							
	private final String DESC = "DESC";		
	private final String TRUE = "true";
	private final String FALSE = "false";
	private final String LEFT_PARENTHESIS = "(";
	private final String RIGHT_PARENTHESIS = ")";
	private final String LEFT_BRACKET = "[";
	private final String RIGHT_BRACKET = "]";
	private final String COLLECTION = "collection";
	private final String CLASSPATH = "classpath";
	private final String BASESOLR_URL = "baseSolrUrl";
	private final String ID = "id";
	private final String ID_FIELDNAME = "idFieldName";
	private final String HIGHLIGHT_ENABLE = "highlightEnable";
	private final String UPPERCONVERT_ENABLE = "upperConvertEnable";
	private final String HIGHLIGHT_FIELDNAME = "highlightFieldName";
	private final String HIGHTLIGHT_SIMPLEPRE = "highlightSimplePre";
	private final String HIGHLIGHT_SIMPLEPOST = "highlightSimplePost";
	private final String CLUSTER = "cluster";
	private final String ZKHOST = "zkHost";
	private final String CONFIG_FILENAME = "joey-solr.ini";
	private final String SHOW_TIME = "showTime";
	private final static String IMPORT_ENTITY = "importEntity";
	private final static String FULL_IMPORT = "full-import";
	private final static String DELTA_IMPORT = "delta-import";	
	private final static String ZKCLIENT_TIMEOUT = "zkClientTimeout";	
	private QueryResponse response;
	private Properties properties;
	private List<Condition> q = new ArrayList<>();
	private List<Condition> fq = new ArrayList<>();
	private List<Sort> sort = new ArrayList<>();
	private Pagination pagination = new Pagination(0, 30);
	private Class<T> clazz;
	private String baseSolrUrl;
	private boolean highlightEnable;
	private String highlightFieldName;
	private String highlightSimplePre;
	private String highlightSimplePost;
	private boolean upperConvertEnable;
	private boolean cluster;
	private String collection;
	private String zkHost;
	private boolean showTime;
	private List<T> result = new ArrayList<>();
	private Map<String, String> notHighlightValue = new HashMap<>();
	private int zkClientTimeout;
	
	/**
	 * 构造方法
	 * @param q 查询条件
	 * @param fq 过滤条件
	 * @param sort 排序条件
	 * @param pagination 分页对象
	 * @param clazz Class对象
	 */
	public Query(List<Condition> q, List<Condition> fq, List<Sort> sort, Pagination pagination, Class<T> clazz) {
		this.q = q;
		this.fq = fq;
		this.sort = sort;
		this.pagination = pagination;	
		this.clazz = clazz;
		init();
	}
	
	/**
	 * 构造方法
	 * @param clazz Class对象
	 */
	public Query(Class<T> clazz) {
		this.clazz = clazz;
		init();
	}		
	
	/**
	 * 构造方法
	 * @param clazz Class对象
	 */
	public Query(Pagination pagination, Class<T> clazz) {
		this.pagination = pagination;
		this.clazz = clazz;
		init();
	}	
	
	/**
	 * 全文检索
	 */
	public void search() {
		long starttime = System.currentTimeMillis();
		SolrClient client = null;
		try {
			if(cluster) {
				CloudSolrClient cloudSolrClient = new CloudSolrClient.Builder().withZkHost(zkHost).build();
				cloudSolrClient.setZkClientTimeout(zkClientTimeout);
				cloudSolrClient.setDefaultCollection(collection);
				client = cloudSolrClient;
			} else {
				client = new HttpSolrClient.Builder(baseSolrUrl).build();
			}
			SolrQuery query = new SolrQuery();
			query.set("q", getQStr());
			query.set("fq", getFQStr());
			query.set("sort", getSortStr());
			query.setStart(pagination.getStartNum());
			query.setRows(pagination.getPageSize());
			if(highlightEnable) {
				query.setHighlight(highlightEnable);
				query.addHighlightField(highlightFieldName);	
				query.setHighlightSimplePre(highlightSimplePre);
				query.setHighlightSimplePost(highlightSimplePost);						
			}		
			response = client.query(query);
			long endtime = System.currentTimeMillis();
			if(showTime) {
				System.out.println(clazz.getSimpleName() + "查询耗时：" + (endtime - starttime) + "毫秒");	
			}
			result = toEntities();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			close(client);
		}
	}
		
	/**
	 * 获取q (查询条件) 
	 */
	public String getQStr(){
		StringBuffer qStr = new StringBuffer();
		if(q == null || q.isEmpty()) {
			qStr.append(STAR + COLON + STAR);	
		}else {
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
	public String getFQStr(){
		StringBuffer fqStr = new StringBuffer();
		for(int i = 0; i < fq.size(); i++) {
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
	public String getSortStr(){
		StringBuffer sortStr = new StringBuffer();
		for(int i = 0; i < sort.size(); i++) {
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
	public String getTOStr(String name, String[] values, boolean innerFuzzy, boolean InnerOr) {
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
	private String getInnerStr(String name, String[] values, boolean innerFuzzy, boolean innerOr) {
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
	private String getRangeStr(String name, String[] values) {
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
	private String getFuzzyStr(String name, String value, boolean fuzzy) {
		if(fuzzy && !StringUtils.isEmpty(value)) {
			value = STAR + value + STAR;
			return name + COLON + value;
		}
		return name + COLON + QUOTE + value + QUOTE;
	}	
	
	/**
	 * 全量更新索引
	 * @param clazz
	 * @return
	 */
	public String fullImport() {	
		String result = "";
		try {
			String baseSolrUrl = getProperty(BASESOLR_URL);
			String entity = getProperty(IMPORT_ENTITY);
			result = dataImport(baseSolrUrl, entity, FULL_IMPORT);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}
	
	/**
	 * 增量更新索引
	 * @param clazz
	 * @return
	 */
	public String deltaImport() {	
		String result = "";
		try {
			String baseSolrUrl = getProperty(BASESOLR_URL);
			String entity = getProperty(IMPORT_ENTITY);
			result = dataImport(baseSolrUrl, entity, DELTA_IMPORT);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}	
	
	/**
	 * 更新索引
	 * @param <T>
	 */
	public String dataImport(String baseSolrUrl, String entity, String importType) {
		String result = "";
		try {
			result = HttpUtils.httpPost(baseSolrUrl + "/dataimport", "entity=" + entity + "&command=" + importType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}		
	
	/**
	 * 初始化参数
	 */
	private void init() {
		loadConfiguration();
		if(properties != null) {
			collection = getCollection(properties, clazz);
			baseSolrUrl = getProperty(BASESOLR_URL);
			highlightEnable = getProperty(HIGHLIGHT_ENABLE).equals(TRUE) ? true : false;
			upperConvertEnable = getProperty(UPPERCONVERT_ENABLE).equals(TRUE) ? true : false;
			highlightFieldName = getProperty(HIGHLIGHT_FIELDNAME, "");
			highlightSimplePre = getProperty(HIGHTLIGHT_SIMPLEPRE, "");
			highlightSimplePost = getProperty(HIGHLIGHT_SIMPLEPOST, "");	
			cluster = getProperty(CLUSTER, "false").equals(TRUE) ? true : false;	
			zkHost = getProperty(ZKHOST);
			showTime = getProperty(SHOW_TIME).equalsIgnoreCase(TRUE) ? true : false;
			zkClientTimeout = Integer.parseInt(getProperty(ZKCLIENT_TIMEOUT, "10"));
		}
	}	
	
	/**
	 * 转换成实体对象集合
	 * @return 实体对象集合
	 */
	private List<T> toEntities(){
		List<T> entitys = new ArrayList<>();
		SolrDocumentList documentList = response.getResults();
		pagination.setTotalSize(documentList.getNumFound());
		for (SolrDocument document : documentList) {
			entitys.add(toEntity(document));
		}
		return entitys;
	}	
	
	/**
	 * SolrDocument转实体对象
	 * @param document SolrDocument对象
	 * @return 实体对象
	 */
	private T toEntity(SolrDocument document) {
		T entity;
		try {
			entity = clazz.newInstance();
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {//遍历字段
				field.setAccessible(true);
				String name = field.getName();
				Object value = document.get(name);
				if(upperConvertEnable) {//启用大写转换（userName ---> user_name）
					name = toLowerUnderline(field.getName());
					value = document.get(name);
				}
				//高亮字段
				if(highlightEnable && highlightFieldName.equalsIgnoreCase(name)) {
					String highlightFieldValue = getHighlightingFieldValue(document);
					if(value != null) {
						//获取正常字段
						notHighlightValue.put((String) document.get(getIdFileName()), (String)value);					
					}
					if(StringUtils.isEmpty(highlightFieldValue) == false) {
						//设置高亮字段
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
	 * 获取高亮字段值
	 * @param document
	 * @return
	 */
	private String getHighlightingFieldValue(SolrDocument document) {
		Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
		String idFileName = getIdFileName();
		String id = (String) document.get(idFileName);
		Map<String, List<String>> map = highlighting.get(id);
		if(map != null){
			List<String> list = map.get(getProperty(HIGHLIGHT_FIELDNAME));
			if(list != null && list.size() > 0){
				return list.get(0);
			}
		}
		return null;
	}
	
	/**
	 * 获取ID字段名
	 * @return
	 */
	private String getIdFileName() {
		String idFileName = getProperty(ID_FIELDNAME);
		if(StringUtils.isEmpty(idFileName)) {
			idFileName = ID;
		}
		return idFileName;
	}
	
	/**
	 * 反射属性赋值
	 * @param field 字段
	 * @param value 字段值
	 * @param entity 实体对象
	 */
	private void setValue(Field field, Object value, T entity) {
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
	 * 驼峰转小写下划线
	 * @param str
	 * @return
	 */
	private String toLowerUnderline(String str) {
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
	 * 获取nickname
	 * @param properties
	 * @param clazz
	 * @return
	 */
	private String getCollection(Properties properties, Class<T> clazz) {
		String[] collectionArr = properties.getProperty(COLLECTION).split(",");
		for (String collection : collectionArr) {	//遍历nickname
			String key = collection + DOT + CLASSPATH;
			String classpath = properties.getProperty(key);			
			if(classpath.equals(clazz.getCanonicalName())) {
				return collection;
			}
		}
		return null;		
	}	
	
	/**
	 * 读取配置文件
	 * @param <T>
	 * @return
	 */
	private void loadConfiguration(){
		InputStream in = null;
		try {
			in = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_FILENAME);
			if(in != null) {
				properties = new Properties();		
				properties.load(new InputStreamReader(in, "UTF-8"));			
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			close(in);
		}
	}
	
	/**
	 * 获取属性值
	 * @param nickname 
	 * @param name
	 * @return
	 */
	private String getProperty(String key) {
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
	private String getProperty(String key, String defaultValue) {
		key = collection + DOT + key;
		return properties.getProperty(key, defaultValue);
	}
		
	/**
	 * 关闭HttpSolrClient资源
	 * @param client
	 */
	private void close(SolrClient client) {
		try {
			if(client != null){
				client.close();				
			}
		} catch (Exception e) {

		}
	}	
	
	/**
	 * 关闭InputStream资源
	 * @param in
	 */
	private void close(InputStream in) {
		try {
			if(in != null){
				in.close();				
			}
		} catch (Exception e) {

		}
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

	public String getBaseSolrUrl() {
		return baseSolrUrl;
	}

	public void setBaseSolrUrl(String baseSolrUrl) {
		this.baseSolrUrl = baseSolrUrl;
	}

	public Class<T> getClazz() {
		return clazz;
	}

	public void setClazz(Class<T> clazz) {
		this.clazz = clazz;
	}

	public List<T> getResult() {
		return result;
	}

	public Map<String, String> getNotHighlightValue() {
		return notHighlightValue;
	}

	public QueryResponse getResponse() {
		return response;
	}

	public void setResponse(QueryResponse response) {
		this.response = response;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public boolean isUpperConvertEnable() {
		return upperConvertEnable;
	}

	public void setUpperConvertEnable(boolean upperConvertEnable) {
		this.upperConvertEnable = upperConvertEnable;
	}

	public boolean isCluster() {
		return cluster;
	}

	public void setCluster(boolean cluster) {
		this.cluster = cluster;
	}

	public String getCollection() {
		return collection;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}

	public String getZkHost() {
		return zkHost;
	}

	public void setZkHost(String zkHost) {
		this.zkHost = zkHost;
	}

	public boolean isShowTime() {
		return showTime;
	}

	public void setShowTime(boolean showTime) {
		this.showTime = showTime;
	}

	public String getQUOTE() {
		return QUOTE;
	}

	public String getCOLON() {
		return COLON;
	}

	public String getOR() {
		return OR;
	}

	public String getAND() {
		return AND;
	}

	public String getSPACE() {
		return SPACE;
	}

	public String getSTAR() {
		return STAR;
	}

	public String getDOT() {
		return DOT;
	}

	public String getTO() {
		return TO;
	}

	public String getASC() {
		return ASC;
	}

	public String getDESC() {
		return DESC;
	}

	public String getTRUE() {
		return TRUE;
	}

	public String getFALSE() {
		return FALSE;
	}

	public String getLEFT_PARENTHESIS() {
		return LEFT_PARENTHESIS;
	}

	public String getRIGHT_PARENTHESIS() {
		return RIGHT_PARENTHESIS;
	}

	public String getCOLLECTION() {
		return COLLECTION;
	}

	public String getCLASSPATH() {
		return CLASSPATH;
	}

	public String getBASESOLR_URL() {
		return BASESOLR_URL;
	}

	public String getID() {
		return ID;
	}

	public String getID_FIELDNAME() {
		return ID_FIELDNAME;
	}

	public String getHIGHLIGHT_ENABLE() {
		return HIGHLIGHT_ENABLE;
	}

	public String getUPPERCONVERT_ENABLE() {
		return UPPERCONVERT_ENABLE;
	}

	public String getHIGHLIGHT_FIELDNAME() {
		return HIGHLIGHT_FIELDNAME;
	}

	public String getHIGHTLIGHT_SIMPLEPRE() {
		return HIGHTLIGHT_SIMPLEPRE;
	}

	public String getHIGHLIGHT_SIMPLEPOST() {
		return HIGHLIGHT_SIMPLEPOST;
	}

	public String getCLUSTER() {
		return CLUSTER;
	}

	public String getZKHOST() {
		return ZKHOST;
	}

	public String getCONFIG_FILENAME() {
		return CONFIG_FILENAME;
	}

	public String getSHOW_TIME() {
		return SHOW_TIME;
	}

	public static String getImportEntity() {
		return IMPORT_ENTITY;
	}

	public static String getFullImport() {
		return FULL_IMPORT;
	}

	public static String getDeltaImport() {
		return DELTA_IMPORT;
	}

	public void setResult(List<T> result) {
		this.result = result;
	}

	public void setNotHighlightValue(Map<String, String> notHighlightValue) {
		this.notHighlightValue = notHighlightValue;
	}
	
}
