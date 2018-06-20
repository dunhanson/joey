package cn.joey.solr.core;

import cn.joey.solr.annotation.JoeyCollection;
import cn.joey.solr.annotation.JoeyField;
import cn.joey.solr.annotation.JoeyID;
import cn.joey.solr.entity.Condition;
import cn.joey.solr.entity.Pagination;
import cn.joey.solr.entity.Sort;
import cn.joey.utils.HttpUtils;
import com.google.common.base.Joiner;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.StringUtils;
import java.io.InputStream;
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
public class Joey {
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
	private final String FULL_IMPORT = "full-import";
	private final String DELTA_IMPORT = "delta-import";
	private String collection;
	private String baseSolrUrl;
	private boolean cluster;
	private String zkHost;
	private int zkClientTimeout;
	private boolean highlight;
	private String highlightFieldName;
	private String highlightSimplePre;
	private String highlightSimplePost;
	private boolean underlineConvertEnable;
	private String entity;
	private QueryResponse response;
	private List<Condition> q;
	private List<Condition> fq;
	private List<Sort> sort;
	private Pagination pagination;

	public Joey() {

	}

	public <T> List<T> search(List<Condition> q, Class<T> clazz) {
		return search(q, null, null, new Pagination(1, 30), clazz);
	}

	public <T> List<T> search(List<Condition> q, Pagination pagination, Class<T> clazz) {
		return search(q, null, null, pagination, clazz);
	}

	public <T> List<T> search(List<Condition> q, List<Condition> fq, Class<T> clazz) {
		return search(q, fq, null, pagination, clazz);
	}

	public <T> List<T> search(List<Condition> q, List<Condition> fq, Pagination pagination, Class<T> clazz) {
		return search(q, fq, null, pagination, clazz);
	}

	public <T> List<T> search(Pagination pagination, Class<T> clazz) {
		return search(null, null, null, pagination, clazz);
	}

	public <T> List<T> search(Class<T> clazz) {
		return search(null, null, null, new Pagination(1, 30), clazz);
	}

	/**
	 * 全文检索
	 */
	public <T> List<T> search(List<Condition> q, List<Condition> fq, List<Sort> sort, Pagination pagination, Class<T> clazz) {
		SolrClient client = null;
		try {
			this.q = q;
			this.fq = fq;
			this.sort = sort;
			this.pagination = pagination;
			init(clazz);
			if(cluster) {//集群模式
				CloudSolrClient cloudSolrClient = new CloudSolrClient.Builder().withZkHost(zkHost).build();
				if(zkClientTimeout > 0) {
					cloudSolrClient.setZkClientTimeout(zkClientTimeout);
				}
				cloudSolrClient.setDefaultCollection(collection);
				client = cloudSolrClient;
			} else {//单机模式
				client = new HttpSolrClient.Builder(baseSolrUrl).build();
			}
			SolrQuery query = new SolrQuery();
			//查询参数
			query.set("q", getQStr());
			query.set("fq", getFQStr());
			query.set("sort", getSortStr());
			query.setStart(pagination.getStartNum());
			query.setRows(pagination.getPageSize());
			if(highlight) {//高亮设置
				query.setHighlight(highlight);
				query.addHighlightField(highlightFieldName);
				query.setHighlightSimplePre(highlightSimplePre);
				query.setHighlightSimplePost(highlightSimplePost);
			}
			response = client.query(query);
			return toEntities(clazz);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			close(client);
		}
	}

	/**
	 * 初始化参数
	 * @param clazz
	 * @param <T>
	 */
	public <T> void init(Class<T> clazz) {
		JoeyCollection collection = clazz.getAnnotation(JoeyCollection.class);
		this.baseSolrUrl = collection.baseSolrUrl();
		this.cluster = collection.cluster();
		this.zkHost = collection.zkHost();
		this.zkClientTimeout = collection.zkClientTimeout();
		this.collection = collection.value();
		this.underlineConvertEnable = collection.underlineConvertEnable();
		this.highlight = collection.highlight();
		this.highlightFieldName = collection.highlightFieldName();
		this.highlightSimplePre = collection.highlightSimplePre();
		this.highlightSimplePost = collection.highlightSimplePost();
		this.entity = collection.entity();
	}

	/**
	 * 获取q (查询条件)
	 */
	public String getQStr(){
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
	public String getFQStr(){
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
	public String getSortStr(){
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
	 * 转换成实体对象集合
	 * @return 实体对象集合
	 */
	private <T> List<T> toEntities(Class<T> clazz){
		List<T> entitys = new ArrayList<>();
		SolrDocumentList documentList = response.getResults();
		for (SolrDocument document : documentList) {
			entitys.add(toEntity(document, clazz));
		}
		pagination.setTotalSize(documentList.getNumFound());
		return entitys;
	}

	/**
	 * SolrDocument转实体对象
	 * @param document SolrDocument对象
	 * @return 实体对象
	 */
	private <T> T toEntity(SolrDocument document, Class<T> clazz) {
		T entity;
		try {
			entity = clazz.newInstance();
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {//遍历字段
				field.setAccessible(true);
				String name = field.getName();
				Object value = document.get(name);
				if(underlineConvertEnable) {//启用大写转换（userName ---> user_name）
					name = toLowerUnderline(field.getName());
					value = document.get(name);
				} else if(field.getAnnotation(JoeyField.class) != null) {//注解
					name = field.getAnnotation(JoeyField.class).value();
					value = document.get(name);
				}
				//高亮字段
				if(highlight && highlightFieldName.equalsIgnoreCase(name)) {
					String highlightFieldValue = getHighlightingFieldValue(document, response, getIdFileName(clazz));
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
	private <T> void setValue(Field field, Object value, T entity) {
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
	private String getHighlightingFieldValue(SolrDocument document, QueryResponse response, String idFileName) {
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
	 * 获取ID字段名
	 * @return
	 */
	private <T> String getIdFileName(Class<T> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		for(Field field : fields) {
			JoeyID joeyID = field.getAnnotation(JoeyID.class);
			if(joeyID == null) {
				continue;
			}
			return joeyID.value();
		}
		return "id";
	}

	/**
	 * 全量更新索引
	 * @return
	 */
	public <T> String fullImport(Class<T> clazz) {
		try {
			init(clazz);
			return dataImport(baseSolrUrl, entity, FULL_IMPORT, new HashMap<>());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 全量更新索引
	 * @param clazz
	 * @param param 自定义参数
	 * @param <T>
	 * @return
	 */
	public <T> String fullImport(Class<T> clazz, Map<String, Object> param) {
		try {
			//初始化参数
			init(clazz);
			//执行更新索引并返回结果
			return dataImport(baseSolrUrl, entity, FULL_IMPORT, param);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 增量更新索引
	 * @param clazz
	 * @param param 自定义参数
	 * @param <T>
	 * @return
	 */
	public <T> String deltaImport(Class<T> clazz, Map<String, Object> param) {
		try {
			//初始化参数
			init(clazz);
			//执行更新索引并返回结果
			return dataImport(baseSolrUrl, entity, DELTA_IMPORT, param);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 增量更新索引
	 * @return
	 */
	public <T> String deltaImport(Class<T> clazz) {
		try {
			init(clazz);
			//执行更新索引并返回结果
			return dataImport(baseSolrUrl, entity, DELTA_IMPORT, new HashMap<>());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 更新索引
	 * @param baseSolrUrl solr地址
	 * @param entity 实体名
	 * @param importType 导入类型
	 * @return
	 */
	public String dataImport(String baseSolrUrl, String entity, String importType, Map<String, Object> param) {
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
	 * Map转URL参数
	 * @param map
	 * @param isURLEncoder
	 * @return
	 */
	public static String mapToFormData(Map<String, Object> map, boolean isURLEncoder) {
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
}
