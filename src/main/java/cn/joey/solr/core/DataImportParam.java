package cn.joey.solr.core;

import cn.joey.solr.constant.DataImportConstant;

/**
 * 导入参数对象
 * @author dunhanson
 * @since 2018-11-08
 */
public class DataImportParam {
	private String command;   //命令
	private Boolean verbose;  //详细
	private Boolean clean;    //清除
	private Boolean commit;   //提交
	private Boolean optimize; //优化
	private String core;      //核心
	private String entity;    //实体
	private String name;      //名称
	
	public void init() {
		this.verbose = false;
		this.clean = false;
		this.commit = true;
		this.optimize = false;
		this.name = DataImportConstant.DATAIMPORT;
	}
	
	public DataImportParam() {
		this.init();
	}
	
	public DataImportParam(String command, String core, String entity) {
		this.init();
		this.command = command;
		this.core = core;
		this.entity = entity;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public Boolean getVerbose() {
		return verbose;
	}

	public void setVerbose(Boolean verbose) {
		this.verbose = verbose;
	}

	public Boolean getClean() {
		return clean;
	}

	public void setClean(Boolean clean) {
		this.clean = clean;
	}

	public Boolean getCommit() {
		return commit;
	}

	public void setCommit(Boolean commit) {
		this.commit = commit;
	}

	public Boolean getOptimize() {
		return optimize;
	}

	public void setOptimize(Boolean optimize) {
		this.optimize = optimize;
	}

	public String getCore() {
		return core;
	}

	public void setCore(String core) {
		this.core = core;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "DataImport [command=" + command + ", verbose=" + verbose + ", clean=" + clean + ", commit=" + commit
				+ ", optimize=" + optimize + ", core=" + core + ", entity=" + entity + ", name=" + name + "]";
	}

}
