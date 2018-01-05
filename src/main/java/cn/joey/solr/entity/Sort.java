package cn.joey.solr.entity;

/**
 * 排序字段
 * @author dunhanson
 * @since 2017-12-27
 */
public class Sort {
	private String name;			//字段名称
	private boolean ascend;	//升序规则
	
	public Sort() {

	}

	public Sort(String name) {
		this.name = name;
	}

	public Sort(String name, boolean ascend) {
		this.name = name;
		this.ascend = ascend;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAscend() {
		return ascend;
	}

	public void setAscend(boolean ascend) {
		this.ascend = ascend;
	}
		
}
