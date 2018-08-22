package cn.joey.entity;

public class DesignedProject {
    private Integer id;
    private String projectName;
    private String projectType;
    private String progress;
    private String projectFollow;
    private String projectInvestment;
    private String pageTime;
    private String projectDescription;
    private String area;

    public DesignedProject() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getProjectFollow() {
        return projectFollow;
    }

    public void setProjectFollow(String projectFollow) {
        this.projectFollow = projectFollow;
    }

    public String getProjectInvestment() {
        return projectInvestment;
    }

    public void setProjectInvestment(String projectInvestment) {
        this.projectInvestment = projectInvestment;
    }

    public String getPageTime() {
        return pageTime;
    }

    public void setPageTime(String pageTime) {
        this.pageTime = pageTime;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    @Override
    public String toString() {
        return "DesignedProject{" +
                "id=" + id +
                ", projectName='" + projectName + '\'' +
                ", projectType='" + projectType + '\'' +
                ", progress='" + progress + '\'' +
                ", projectFollow='" + projectFollow + '\'' +
                ", projectInvestment='" + projectInvestment + '\'' +
                ", pageTime='" + pageTime + '\'' +
                ", projectDescription='" + projectDescription + '\'' +
                ", area='" + area + '\'' +
                '}';
    }
}
