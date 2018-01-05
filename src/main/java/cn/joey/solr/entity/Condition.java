package cn.joey.solr.entity;

/**
 * 搜索条件
 * @author dunhanson
 * @since 2017-12-27
 */
public class Condition {
	private String name;					//字段名称
	private String[] values;				//字段指
	private boolean fuzzy	;			//模糊查询
	private boolean or;					//并且
	
	public Condition() {
		
	}

	public Condition(String name, String[] values) {
		super();
		this.name = name;
		this.values = values;
	}

	public Condition(String name, String[] values, boolean fuzzy, boolean or) {
		this.name = name;
		this.values = values;
		this.fuzzy = fuzzy;
		this.or = or;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isFuzzy() {
		return fuzzy;
	}

	public void setFuzzy(boolean fuzzy) {
		this.fuzzy = fuzzy;
	}

	public String[] getValues() {
		return values;
	}

	public void setValues(String[] values) {
		this.values = values;
	}

	public boolean isOr() {
		return or;
	}

	public void setOr(boolean or) {
		this.or = or;
	}


}
