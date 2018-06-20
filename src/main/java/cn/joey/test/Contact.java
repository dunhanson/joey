package cn.joey.test;

import cn.joey.solr.annotation.JoeyCollection;
import cn.joey.solr.annotation.JoeyField;
import cn.joey.solr.annotation.JoeyID;
import java.util.Date;

@JoeyCollection(
		value = "contact",
		baseSolrUrl = "http://192.168.2.131:8983/solr/contact",
		cluster = true,
		zkHost = "192.168.2.131:2181",
		zkClientTimeout = 20,
		entity = "c_contact_new",
		upperConvertEnable = true,
		highlight = true,
		highlightFieldName = "company_name",
		highlightSimplePre = "<font style=\"color:red\">",
		highlightSimplePost = "</font>"
)
public class Contact {
	private String id;
	private String companyName;
	private String contactPerson;
	private String position;
	private String phoneNo;
	private String mobileNo;
	private String companyAddr;
	private String province;
	private String city;
	private String email;
	private Date createTime;
	
	public Contact() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getCompanyAddr() {
		return companyAddr;
	}

	public void setCompanyAddr(String companyAddr) {
		this.companyAddr = companyAddr;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "Contact [id=" + id + ", companyName=" + companyName + ", contactPerson=" + contactPerson + ", position="
				+ position + ", phoneNo=" + phoneNo + ", mobileNo=" + mobileNo + ", companyAddr=" + companyAddr
				+ ", province=" + province + ", city=" + city + ", email=" + email + ", createTime=" + createTime + "]";
	}

}
