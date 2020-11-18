package com.clone.baemin.member;

import com.clone.baemin.jwt.domain.Jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@NoArgsConstructor @AllArgsConstructor
@Setter @Getter
@ToString
public class TokenDto {
	
	
	private Long id;
	
	private String email;
	private String name;
	private Jwt tokens;

}
