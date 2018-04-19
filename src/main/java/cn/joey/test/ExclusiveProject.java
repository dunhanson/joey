package cn.joey.test;

public class ExclusiveProject {
	private String id;
	//项目编号
	private String xmCode;
	//项目名称
	private String xmName;
	//版本跟进
	private String version;
	//项目区域
	private String province;
	private String city;
	//业主类型
	private String ownerType;
	//项目类型
	private String pType;
	//项目子类型
	private String pTypeSub;
	//项目阶段
	private String projectPhase;
	//项目规模
	private String projectSize;
	//装修类别
	private String decorateSituation;
	//更新时间
	private String updateDate;
	
	public ExclusiveProject() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getXmCode() {
		return xmCode;
	}

	public void setXmCode(String xmCode) {
		this.xmCode = xmCode;
	}

	public String getXmName() {
		return xmName;
	}

	public void setXmName(String xmName) {
		this.xmName = xmName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(String ownerType) {
		this.ownerType = ownerType;
	}

	public String getpType() {
		return pType;
	}

	public void setpType(String pType) {
		this.pType = pType;
	}

	public String getProjectPhase() {
		return projectPhase;
	}

	public void setProjectPhase(String projectPhase) {
		this.projectPhase = projectPhase;
	}

	public String getProjectSize() {
		return projectSize;
	}

	public void setProjectSize(String projectSize) {
		this.projectSize = projectSize;
	}

	public String getDecorateSituation() {
		return decorateSituation;
	}

	public void setDecorateSituation(String decorateSituation) {
		this.decorateSituation = decorateSituation;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	

	public String getpTypeSub() {
		return pTypeSub;
	}

	public void setpTypeSub(String pTypeSub) {
		this.pTypeSub = pTypeSub;
	}

	@Override
	public String toString() {
		return "ExclusiveProject [id=" + id + ", xmCode=" + xmCode + ", xmName=" + xmName + ", version=" + version
				+ ", province=" + province + ", city=" + city + ", ownerType=" + ownerType + ", pType=" + pType
				+ ", pTypeSub=" + pTypeSub + ", projectPhase=" + projectPhase + ", projectSize=" + projectSize
				+ ", decorateSituation=" + decorateSituation + ", updateDate=" + updateDate + "]";
	}

}
