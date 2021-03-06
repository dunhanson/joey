package cn.joey.entity;

import cn.joey.solr.annotation.Collection;
import cn.joey.solr.annotation.Column;
import java.util.Date;

@Collection(
		value = "contact",
		baseSolrUrl = "http://192.168.2.170:8983/solr/contact",
		cluster = true,
		zkHost = "192.168.2.170:2181",
		zkClientTimeout = 20,
		zkConnectTimeout = 20,
		dataimportEntity = "c_contact_new",
		underlineConvertEnable = false
)
public class Contact {
	@Column(value = "id", isID = true)
	private String id;
	@Column("company_name")
	private String companyName;
	@Column("contact_person")
	private String contactPerson;
	@Column("position")
	private String position;
	@Column("phone_no")
	private String phoneNo;
	@Column("mobile_no")
	private String mobileNo;
	@Column("company_addr")
	private String companyAddr;
	@Column("province")
	private String province;
	@Column("city")
	private String city;
	@Column("email")
	private String email;
	@Column("create_time")
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
