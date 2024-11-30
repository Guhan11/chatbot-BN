package com.chatbot.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "country")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountryVO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@SequenceGenerator(name = "countrysequence", sequenceName = "countryseq", initialValue = 10000001, allocationSize = 1)
	@Column(name = "countryid")
	private Long id;
	@Column(name = "country")
	private String country;
	@Column(name = "countrycode")
	private String countryCode;
	@Column(name = "orgid")
	private Long orgId;
}
