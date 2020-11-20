package com.clone.baemin.token;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.clone.baemin.jwt.JwtComponent;
import com.clone.baemin.jwt.JwtComponent.TOKEN_TYPE;
import com.clone.baemin.jwt.domain.Jwt;
import com.clone.baemin.member.MemberLoginDto;
import com.fasterxml.jackson.databind.ObjectMapper;

//@ExtendWith(SpringExtension.class)	// 언제 사용??
@SpringBootTest
@AutoConfigureMockMvc	// mockMvc 사용 하려면 꼭 추가
@TestMethodOrder(OrderAnnotation.class)	// 순서 정할때 사용
public class Token_Test {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private WebApplicationContext wac;
	
	// 토큰용
	@Autowired
	private JwtComponent jwtComponent;
	
	/*
	 * @BeforeEach public void setup() { this.mockMvc =
	 * MockMvcBuilders.webAppContextSetup(wac) .addFilter(new
	 * CharacterEncodingFilter("utf-8", true)) .alwaysDo(print()).build(); }
	 */
	
	// ================================== DB에 유저가 존재 할때 => Token 확인 후 정보 ==================================
	@Test
	@Order(1)
	public void email_조회_성공() throws Exception {
		
		String email = "test01@naver.com";
		String password = "qweasd1!";
				
		// MEMBER 로그인 => db에 있는 유저 정보 (실제 디비에 있는 값을 넣는다)
		MemberLoginDto loginDto = MemberLoginDto.builder()
												.email(email)
												.password(password)
												.build()
												;
		
		// 로그인한 유저의 이메일로 토큰 생성
		Jwt fullToken = jwtComponent.makeJwt(loginDto.getEmail());
		String accessToken = fullToken.getAccessToken();
		System.out.println(accessToken);
		// accessToken에서 맴버 이메일 꺼내옴
		String findEmail = jwtComponent.extractUsername(accessToken, TOKEN_TYPE.ACCESS_TOKEN);
		
		// 이메일이 db정보와 같다면
		if(findEmail.equals(email)) {
			// 헤더에 토큰을 담아 보낸다 => org.springframework.http.HttpHeaders;
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
			System.out.println("===================" + httpHeaders);
			this.mockMvc.perform(
							get("/baemin/member/" + findEmail)
							.headers(httpHeaders)
							.contentType(MediaType.APPLICATION_JSON)
							//.content(objectMapper.writeValueAsString(loginDto)) // body 값
							)
							.andDo(MockMvcResultHandlers.print())
							.andExpect(status().isOk())
							;
		}
		
	}
	
	// ================================== 만료 확인후 재발급 ==================================
	static Stream<Arguments> TokenExpiredTest() {
		return Stream.of(
				Arguments.of(4000, 4000),	// access 만료아님 refresh 만료아님 => 재발급 X
				Arguments.of(4000, 7000),	// access 만료아님 refresh만료 => 재발급 X
				Arguments.of(5000, 5000),	// access 만료 refresh만료 => 다시 로그인 필요
				Arguments.of(6000, 5000),	// access 만료 refresh만료 => 다시 로그인 필요
				Arguments.of(6000, 2000)	// access 만료 refresh만료아님 => 재발급 O
				
		);
				
	}
	@ParameterizedTest
	@MethodSource("TokenExpiredTest")
	public void member_refreshToken_테스트(int accessTime, int refreshTime) throws Exception {
			
		String email = "test01@naver.com";
		String password = "qweasd1!";
		
		// MEMBER 로그인 => db에 있는 유저 정보 (실제 디비에 있는 값을 넣는다)
		MemberLoginDto loginDto = MemberLoginDto.builder()
												.email(email)
												.password(password)
												.build()
												;
		
		// 로그인한 유저의 이메일로 토큰 생성
		Jwt fullToken = jwtComponent.makeJwt(loginDto.getEmail());
		String accessToken = fullToken.getAccessToken();
		String refreshToken = fullToken.getRefreshToken();
		// accessToken에서 맴버 이메일 꺼내옴
		String findEmail = jwtComponent.extractUsername(accessToken, TOKEN_TYPE.ACCESS_TOKEN);
		
		// 이메일이 db정보와 같다면
		if(findEmail.equals(email)) {
			
			Thread.sleep(accessTime);
			// accessToken 만료 확인
			Boolean verifyAccess = jwtComponent.isTokenExpired(accessToken, TOKEN_TYPE.ACCESS_TOKEN);
			
			// accessToken 만료 verifyAccess = true
			if(verifyAccess) {
				Thread.sleep(refreshTime);
				// refreshToken 만료 확인
				Boolean verifyRefresh = jwtComponent.isTokenExpired(refreshToken, TOKEN_TYPE.REFRESH_TOKEN);
				
				// refreshToken 만료 => true => accessToken 재발급 실패
				if(verifyRefresh) {
					// 헤더에 토큰을 담아 보낸다 => org.springframework.http.HttpHeaders;
					HttpHeaders httpHeaders = new HttpHeaders();
					httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer "+ refreshToken);
					
	        		this.mockMvc.perform(
	    					post("/baemin/member/get_accesstoken")
	    					.headers(httpHeaders)
	    					.contentType(MediaType.APPLICATION_JSON)
	    					)
	    					.andDo(MockMvcResultHandlers.print())
	    					.andExpect(status().isBadRequest())
	    					;
					
				// refreshToken 만료 X => false => accessToken 재발급 OK
				}else {
					// 헤더에 토큰을 담아 보낸다 => org.springframework.http.HttpHeaders;
					HttpHeaders httpHeaders = new HttpHeaders();
					httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer "+ refreshToken);
					
		     	       this.mockMvc.perform(
			   					post("/baemin/member/get_accesstoken")
			   					.headers(httpHeaders)
			   					.contentType(MediaType.APPLICATION_JSON)
			   					)
			   					.andDo(MockMvcResultHandlers.print())
			   					.andExpect(status().isOk())
			   					;
				}
				
				
			}else {
				System.out.println("accessToken 만료 안됐음");
			}
			
		}
		
		
		
	}

}
