package it.shopme.common.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "customer")
public class Customer {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "first_name", length = 45, nullable = false)
	private String firstName;
	
	@Column(name = "last_name", length = 45, nullable = false)
	private String lastName;
	
	@Column(length = 64, nullable = false)
	private String email;
	
	@Column(length = 64, nullable = false)
	private String password;
	
	@Column(name = "phone_number", length = 15, nullable = false)
	private String phoneNumber;
	
	@Column(name = "address_line_1", length = 64, nullable = false)
	private String addressLine1;
	
	@Column(name = "address_line_2", length = 64)
	private String addressLine2;
	
	@ManyToOne
	@JoinColumn(name = "country_id")
	private Country country;
	
	@Column(length = 45, nullable = false)	
	private String state;
	
	@Column(length = 45, nullable = false)
	private String city;
	
	@Column(name = "postal_code", length = 64, nullable = false)
	private String postalCode;
	
	@Column(name = "created_time")
	private Date createdTime;
	
	private boolean enabled;
	
	@Column(name = "verification_code", length = 64)
	private String verificationCode;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public boolean isEnable() {
		return enabled;
	}

	public void setEnable(boolean enable) {
		this.enabled = enable;
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	public String getFullName() {
		return this.firstName+" "+this.lastName;
	}
	
	@Override
	public String toString() {
		return "Customer [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", password=" + password + ", phoneNumber=" + phoneNumber + ", addressLine1=" + addressLine1
				+ ", addressLine2=" + addressLine2 + ", country=" + country + ", state=" + state + ", city=" + city
				+ ", postalCode=" + postalCode + ", createdTime=" + createdTime + ", enable=" + enabled
				+ ", verificationCode=" + verificationCode + "]";
	}
}
