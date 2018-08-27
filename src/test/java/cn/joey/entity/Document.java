package cn.joey.entity;

import java.util.Date;

public class Document {
    private String id;
    private String chnldesc;
    private String city;
    private String docchannel;
    private String page_time;
    private String industry;
    private String province;
    private Date publishtime;
    private String qcodes;
    private String websourcename;
    private String doctitle;

    public Document() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChnldesc() {
        return chnldesc;
    }

    public void setChnldesc(String chnldesc) {
        this.chnldesc = chnldesc;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDocchannel() {
        return docchannel;
    }

    public void setDocchannel(String docchannel) {
        this.docchannel = docchannel;
    }


    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public Date getPublishtime() {
        return publishtime;
    }

    public void setPublishtime(Date publishtime) {
        this.publishtime = publishtime;
    }

    public String getQcodes() {
        return qcodes;
    }

    public void setQcodes(String qcodes) {
        this.qcodes = qcodes;
    }

    public String getWebsourcename() {
        return websourcename;
    }

    public void setWebsourcename(String websourcename) {
        this.websourcename = websourcename;
    }

    public String getDoctitle() {
        return doctitle;
    }

    public void setDoctitle(String doctitle) {
        this.doctitle = doctitle;
    }

	public String getPage_time() {
		return page_time;
	}

	public void setPage_time(String page_time) {
		this.page_time = page_time;
	}

	@Override
	public String toString() {
		return "Document [id=" + id + ", chnldesc=" + chnldesc + ", city=" + city + ", docchannel=" + docchannel
				+ ", page_time=" + page_time + ", industry=" + industry + ", province=" + province + ", publishtime="
				+ publishtime + ", qcodes=" + qcodes + ", websourcename=" + websourcename + ", doctitle=" + doctitle
				+ "]";
	}
	
	

}