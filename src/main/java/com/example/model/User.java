/**
 * 
 */
package com.example.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Swati
 *
 */
@Entity
@Table(name = "user")
public class User implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private int id;
	
	@Column(name="name", nullable=false, unique=false)
	private String name;
	
	@Column(name="email", nullable=false, unique=true)
	@Email(message="Please provide a valid email id")
	@NotEmpty(message= "Please provide an email")
	private String email;
	
	@Column(name="pincode", nullable=false, unique=false)
	private int pincode;
	
	@Column(name="userid",unique=true)
	private String userId;
	
	@Column(name="expiry_time")
	private Date expiryTime;
	
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="temperature")
	private Temperature temperature;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getPincode() {
		return pincode;
	}
	public void setPincode(int pincode) {
		this.pincode = pincode;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Date getExpiryTime() {
		return expiryTime;
	}
	public void setExpiryTime(Date expiryTime) {
		this.expiryTime = expiryTime;
	}
	public Temperature getTemperature() {
		return temperature;
	}
	public void setTemperature(Temperature temperature) {
		this.temperature = temperature;
	}
	@Override
	public String toString(){
		return "Name: "+ this.name + " Email: "+this.email + " Pincode: "+this.pincode +" UserId: "+this.userId;
	}
	

}
