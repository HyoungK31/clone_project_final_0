package com.clone.baemin.memberTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.clone.baemin.member.MemberJoinDto;
import com.clone.baemin.member.MemberLoginDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc	// mockMvc 사용 하려면 꼭 추가
@TestMethodOrder(OrderAnnotation.class)	// 순서 정할때 사용
public class MemberController_Test {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	// ====================== 회원가입 Test ===================================
		// 1. 회원가입 성공
		@Test
		@Order(1)
		public void 회원가입_테스트() throws JsonProcessingException, Exception {
			MemberJoinDto join = MemberJoinDto.builder()
											.email("test01@naver.com")
											.password("qweasd1!")
											.memberName("홍길동")
											.memberPhone("01077777777")
											.build();
			this.mockMvc.perform(
						post("/baemin/member/join")
						.contentType( MediaType.APPLICATION_JSON)
						.content( objectMapper.writeValueAsString(join))
					)
					.andDo(print())
					.andExpect(status().isCreated());
		}
		// 2. 회원가입 실패(Dto 유효성 검사 통과 못할 때) => 이메일 형식 X, 중복된 아이디
		@ParameterizedTest
		@ValueSource(strings = {"test01naver.com", "test01@naver.com"})
		@Order(2)
		public void 회원가입_실패_테스트(String email) throws JsonProcessingException, Exception {
			MemberJoinDto join = MemberJoinDto.builder()
											.email(email)
											.password("qweasd1!")
											.memberName("홍길동")
											.memberPhone("01077777777")
											.build()
											;
			this.mockMvc.perform(
						post("/baemin/member/join")
						.contentType( MediaType.APPLICATION_JSON)
						.content( objectMapper.writeValueAsString(join))
					)
					.andDo(print())
					.andExpect(status().isBadRequest());
		}
		
		
	// ====================== 로그인 Test ====================================
		// 1. 로그인 성공
		@Test
		@Order(3)
		public void 로그인_성공_테스트() throws Exception {
			MemberLoginDto login = MemberLoginDto.builder()
												.email("test01@naver.com")
												.password("qweasd1!")
												.build()
												;
			this.mockMvc.perform(
						post("/baemin/member/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(login))
					)
					.andDo(print())
					.andExpect(status().isOk());
					
		}
		// 2. 로그인 실패( 없는 이메일, 틀린 비밀번호, MemberLoginDto 유효성 테스트 이메일형식, 비밀번호패턴 )
		static Stream<Arguments> loginFailParams() {
			return Stream.of(
					Arguments.of("test07@naver.com", "qweasd1!"),
					Arguments.of("test01@naver.com", "!1asdqwe"),
					Arguments.of("test01naver.com", "!1asdqwe"),
					Arguments.of("test01@naver.com", "qweasd11")
					);
		}
		@ParameterizedTest
		@MethodSource("loginFailParams")
		@Order(4)
		public void 로그인_실패_테스트(String email, String password) throws Exception {
			MemberLoginDto loginDto = MemberLoginDto.builder()
												.email(email)
												.password(password)
												.build()
												;
			this.mockMvc.perform(
					post("/baemin/member/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(loginDto))
				)
				.andDo(print())
				.andExpect(status().isBadRequest());
		}
	// ====================== 카테고리 목록 Test ===============================
		// 1. 성공 => 한식, 중식, 양식, 분식
		// 2. 실패 => 일식
}
