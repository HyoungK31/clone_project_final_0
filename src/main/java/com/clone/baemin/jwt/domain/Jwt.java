package com.clone.baemin.jwt.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder			// 빌더 넣어야 JwtComponent에서 토큰 생성할때 builder로 생성할수 있음
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class Jwt {
	
	private String accessToken;
	private String refreshToken;
	
}
