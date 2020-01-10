package cn.joey.solr.core;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * 搜索条件
 * @author dunhanson
 * @since 2017-12-27
 */
@Getter
@Setter
public class Condition {
	private String name;                 //字段名称
	private String[] values;             //字段指
	private boolean fuzzy;               //模糊查询
	private boolean or;                  //并且
	private boolean range;		         //范围查询
	private boolean innerOr;             //多值并且
	private boolean innerFuzzy;          //多值模糊
	private List<Condition> conditions;  //内部条件
	//内部类，查询类型
	public enum Type {
		FUZZY, OR, RANGE, INNER_OR, INNER_FUZZY
	}

	public Condition() {

	}

	public Condition(String name, String[] values) {
		this.name = name;
		this.values = values;
	}

	public Condition(String name, String[] values, Type type, boolean bool) {
		this.name = name;
		this.values = values;
		switch (type) {
			case FUZZY:
				this.fuzzy = bool;
				break;
			case OR:
				this.or = bool;
				break;
			case RANGE:
				this.range = bool;
				break;
			case INNER_OR:
				this.innerOr = bool;
				break;
			case INNER_FUZZY:
				this.innerFuzzy = bool;
				break;
		}
	}

	public Condition(String name, String[] values, boolean or) {
		this.name = name;
		this.values = values;
		this.or = or;
	}

	public Condition(String name, String[] values, boolean fuzzy, boolean or) {
		this.name = name;
		this.values = values;
		this.fuzzy = fuzzy;
		this.or = or;
	}
}
