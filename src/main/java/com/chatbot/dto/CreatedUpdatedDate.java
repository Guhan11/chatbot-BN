/*
* ========================================================================
* This file is the intellectual property of GSM Outdoors.it
* may not be copied in whole or in part without the express written 
* permission of GSM Outdoors. 
* ========================================================================
* Copyrights(c) 2023 GSM Outdoors. All rights reserved.
* ========================================================================
*/
package com.chatbot.dto;

import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatedUpdatedDate {
	private Date createdDate;
	private Date updatedDate;

	@PrePersist
	public void onSave() {
		Date currentDate = new Date();
		this.createdDate = currentDate;
		this.updatedDate = currentDate;
	}

	@PostLoad
	public void onUpdate() {
		Date currentDate = new Date();
		this.updatedDate = currentDate;
	}

}
