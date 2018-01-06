package cn.joey.test;

import java.util.Date;

public class DesignedProject {
    private Long id;
    private String projectFollow;
    private String ownerType;
    private String projectType;
    private String subprojectType;
    private String projectInvestment;
    private String begintime;
    private String endtime;
    private Date crtime;
    private String status;
    private String progress;
    private String projectName;
    private String area;
    private String projectDescription;
    private String pageTime;
    
	public DesignedProject() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProjectFollow() {
		return projectFollow;
	}

	public void setProjectFollow(String projectFollow) {
		this.projectFollow = projectFollow;
	}

	public String getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(String ownerType) {
		this.ownerType = ownerType;
	}

	public String getProjectType() {
		return projectType;
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}

	public String getSubprojectType() {
		return subprojectType;
	}

	public void setSubprojectType(String subprojectType) {
		this.subprojectType = subprojectType;
	}

	public String getProjectInvestment() {
		return projectInvestment;
	}

	public void setProjectInvestment(String projectInvestment) {
		this.projectInvestment = projectInvestment;
	}

	public String getBegintime() {
		return begintime;
	}

	public void setBegintime(String begintime) {
		this.begintime = begintime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public Date getCrtime() {
		return crtime;
	}

	public void setCrtime(Date crtime) {
		this.crtime = crtime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProgress() {
		return progress;
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getProjectDescription() {
		return projectDescription;
	}

	public void setProjectDescription(String projectDescription) {
		this.projectDescription = projectDescription;
	}

	public String getPageTime() {
		return pageTime;
	}

	public void setPageTime(String pageTime) {
		this.pageTime = pageTime;
	}

	@Override
	public String toString() {
		return "DesignedProject [id=" + id + ", projectFollow=" + projectFollow + ", ownerType=" + ownerType
				+ ", projectType=" + projectType + ", subprojectType=" + subprojectType + ", projectInvestment="
				+ projectInvestment + ", begintime=" + begintime + ", endtime=" + endtime + ", crtime=" + crtime
				+ ", status=" + status + ", progress=" + progress + ", projectName=" + projectName + ", area=" + area
				+ ", projectDescription=" + projectDescription + ", pageTime=" + pageTime + "]";
	}

}