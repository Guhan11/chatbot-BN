package com.chatbot.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.chatbot.dto.CreatedUpdatedDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {

//	@SuppressWarnings("unused")
//	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@SequenceGenerator(name = "usersequence", sequenceName = "userseq", initialValue = 10000001, allocationSize = 1)
	@Column(name = "userid")
	private Long id;
	@Column(name = "username")
	private String userName;
	@Column(name = "email")
	private String email;
	@Column(name = "password")
	private String password;
	@Column(name = "age")
	private Integer age;
	@Column(name = "gender")
	private String gender;
	@Column(name = "phone")
	private Long phone;
	@Column(name = "imagename")
	private String imageName;
	@Lob
	@Column(name = "imagedata")
	private byte[] imageData;

//	_____________________

	@Column(name = "role")
	private String role;
	@Column(name = "provider")
	private String provider;
	@Column(name = "otp")
	private String otp;
	@Column(name = "otpverified")
	private String otpVerified;

	@Column(name = "lastname")
	private String lastName;
	@Column(name = "firstname")
	private String firstName;
	@Column(name = "token")
	private String token;
	@Column(name = "loginstatus")
	private boolean loginStatus;
	@Column(name = "active")
	private boolean isActive;

	@Embedded
	private CreatedUpdatedDate commonDate = new CreatedUpdatedDate();

	private Date accountRemovedDate;
}
