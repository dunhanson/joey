package cn.joey.solr.core;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.StringUtils;
import cn.joey.solr.entity.Condition;
import cn.joey.solr.entity.Pagination;
import cn.joey.solr.entity.Sort;

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
	private final String LEFT_PARENTHESIS = "(";
	private final String RIGHT_PARENTHESIS = ")";
	private final String NICKNAME = "nickname";
	private final String CLASSPATH = "classpath";
	private final String BASESOLR_URL = "baseSolrUrl";
	private final String ID = "id";
	private final String ID_FIELDNAME = "idFieldName";
	private final String HIGHLIGHT_ENABLE = "highlightEnable";
	private final String UPPERCONVERT_ENABLE = "upperConvertEnable";
	private final String HIGHLIGHT_FIELDNAME = "highlightFieldName";
	private final String HIGHTLIGHT_SIMPLEPRE = "highlightSimplePre";
	private final String HIGHLIGHT_SIMPLEPOST = "highlightSimplePost";
	private final String CONFIG_FILENAME = "joey-solr.ini";
	private QueryResponse response;
	private Properties properties;
	private List<Condition> q;
	private List<Condition> fq;
	private List<Sort> sort;
	private Pagination pagination;
	private Class<T> clazz;
	private String baseSolrUrl;
	private boolean highlightEnable = false;
	private String highlightFieldName;
	private String highlightSimplePre;
	private String highlightSimplePost;
	private boolean upperConvertEnable = false;
	private List<T> result;
	
	public Query(List<Condition> q, List<Condition> fq, List<Sort> sort, Pagination pagination, Class<T> clazz) {
		this.q = q;
		this.fq = fq;
		this.sort = sort;
		this.pagination = pagination;	
		this.clazz = clazz;
	}
	
	/**
	 * 初始化参数
	 */
	private void init() {
		loadConfiguration();
		if(properties != null) {
			baseSolrUrl = getProperty(BASESOLR_URL);
			highlightEnable = getProperty(HIGHLIGHT_ENABLE).equals(String.valueOf(true)) ? true : false;
			upperConvertEnable = getProperty(UPPERCONVERT_ENABLE).equals(String.valueOf(true)) ? true : false;
			highlightFieldName = getProperty(HIGHLIGHT_FIELDNAME, "");
			highlightSimplePre = getProperty(HIGHTLIGHT_SIMPLEPRE, "");
			highlightSimplePost = getProperty(HIGHLIGHT_SIMPLEPOST, "");			
		}
	}

	/**
	 * 全文检索
	 */
	public void search() {
		HttpSolrClient client = null;
		try {
			init();
			//client = new HttpSolrClient(baseSolrUrl);
			client = new HttpSolrClient.Builder(baseSolrUrl).build();
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
			if(values.length == 1) {//精准查询
				fqStr.append(getFuzzyStr(name, values[0], condition.isFuzzy()));	
			}else {//多值AND查询
				fqStr.append(getTOStr(name, values));	
			}
			if(i < fq.size() - 1) {
				fqStr.append(SPACE + (condition.isOr() ? OR : AND) + SPACE);
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
	public String getTOStr(String name, String[] values) {
		StringBuffer str = new StringBuffer();
		str.append(name + COLON + "[");
		str.append(QUOTE + values[0] + QUOTE);
		str.append(SPACE + TO + SPACE);
		str.append(QUOTE + values[1] + QUOTE);
		str.append("]");
		return str.toString();
	}
	
	/**
	 * 获取模糊查询字符串
	 * @param name 字段名
	 * @param value 字段值
	 * @param fuzzy 模糊查询？
	 * @return 查询字符串
	 */
	public String getFuzzyStr(String name, String value, boolean fuzzy) {
		if(fuzzy) {
			return name + COLON + value;
		}
		return name + COLON + QUOTE + value + QUOTE;
	}
	
	/**
	 * 转换成实体对象集合
	 * @return 实体对象集合
	 */
	public List<T> toEntities(){
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
	public T toEntity(SolrDocument document) {
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
					setValue(field, highlightFieldValue, entity);
				} else {//普通字段
					setValue(field, value, entity);	
				}
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
	public String getHighlightingFieldValue(SolrDocument document) {
		Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
		String idFileName = getProperty(ID_FIELDNAME);
		if(StringUtils.isEmpty(idFileName)) {
			idFileName = ID;
		}
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
	 * 反射属性赋值
	 * @param field 字段
	 * @param value 字段值
	 * @param entity 实体对象
	 */
	public void setValue(Field field, Object value, T entity) {
		try {
			if(value == null) {
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
			} else if (clazz == Date.class) {
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
	public String toLowerUnderline(String str) {
		if(StringUtils.isEmpty(str)) {
			return "";
		}
		Pattern pattern = Pattern.compile("[A-Z]");
		Matcher matcher = pattern.matcher(str);
		int index = 0;
		while(matcher.find()) {
			String findStr = matcher.group(index);
			str = str.replace(findStr, "_" + findStr);
			index ++;
		}		
		return str.toLowerCase();
	}	
	
	/**
	 * 获取nickname
	 * @param properties
	 * @param clazz
	 * @return
	 */
	public String getNickname(Properties properties, Class<T> clazz) {
		String[] nicknameArr = properties.getProperty(NICKNAME).split(",");
		for (String nickname : nicknameArr) {	//遍历nickname
			String key = nickname + DOT + CLASSPATH;
			String classpath = properties.getProperty(key);			
			if(classpath.equals(clazz.getCanonicalName())) {
				return nickname;
			}
		}
		return null;		
	}	
	
	/**
	 * 读取配置文件
	 * @param <T>
	 * @return
	 */
	public void loadConfiguration(){
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
	public String getProperty(String key) {
		key = getNickname(properties, clazz) + DOT + key;
		return properties.getProperty(key);
	}
	
	public String getProperty(String key, String defaultValue) {
		key = getNickname(properties, clazz) + DOT + key;
		return properties.getProperty(key, defaultValue);
	}
		
	/**
	 * 关闭HttpSolrClient资源
	 * @param client
	 */
	public void close(HttpSolrClient client) {
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
	public void close(InputStream in) {
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

	public void setResult(List<T> result) {
		this.result = result;
	}

}
