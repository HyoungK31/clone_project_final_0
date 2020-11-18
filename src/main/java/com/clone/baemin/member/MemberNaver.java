package com.clone.baemin.member;

import javax.persistence.Entity;
import javax.persistence.Table;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


public class MemberNaver {
	
	private Long naverId;
	private String snsEmail;

}
