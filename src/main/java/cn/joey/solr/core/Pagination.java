package cn.joey.solr.core;

/**
 * 分页对象
 * @author dunhanson
 * @since 2017-12-14
 */
public class Pagination {
	private int pageNo;		//页号
	private int pageSize;	//每页大小
	private int startNum;	//开始记录数	
	private long totalSize;	//总记录数
	
	public Pagination() {
		
	}

	public Pagination(int pageNo) {
		this.pageNo = pageNo;
		this.pageSize = 30;
		this.startNum = (this.pageNo - 1) * this.pageSize;
	}
	
	public Pagination(int pageNo, int pageSize) {
		this.pageNo = pageNo == 0 ? 1 : pageNo;
		this.pageSize = pageSize;
		this.startNum = (this.pageNo - 1) * this.pageSize;
	}
	/**
	 * 当startNum为0时使用pageNo计算开始记录数，否则直接把startNum作为开始记录数
	 * @param pageNo
	 * @param startNum
	 * @param pageSize
	 */
	public Pagination(int pageNo, int startNum, int pageSize) {
		this.pageNo = pageNo == 0 ? 1 : pageNo;
		this.pageSize = pageSize;
		if(startNum > 0) {
			this.startNum = startNum;
		} else {
			this.startNum = (this.pageNo - 1) * this.pageSize;
		}
	}
	
	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	public int getStartNum() {
		return startNum;
	}

	public void setStartNum(int startNum) {
		this.startNum = startNum;
	}

	@Override
	public String toString() {
		return "Pagination{" +
				"pageNo=" + pageNo +
				", pageSize=" + pageSize +
				", startNum=" + startNum +
				", totalSize=" + totalSize +
				'}';
	}


}
