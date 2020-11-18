package com.clone.baemin.memberTest;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.clone.baemin.member.Member;
import com.clone.baemin.member.MemberJoinDto;

@SpringBootTest
public class Member_Test {
	
	@Autowired
	Validator validator;
	private Errors errors;
	
	// ============================== Member ===================================
	
	/**
	 * member 도메인 builder 테스트
	 */
	@Test
	void member_builder_테스트() {
		Member member = Member.builder().memberName("홍길동")
					                    .email("test01@naver.com")
					                    .memberPhone("01077777777")
					                    .password("qweasd!")
					                    .build()
					                    ;
		assertThat(member).isNotNull();
	}
	
	/**
	 *  member 도메인 객체 생성 테스트
	 */
	@Test
	void member_생성() {
		String memberName = "홍길동";
		String email = "test01@naver.com";
		String password = "qweasd!";
		String memberPhone = "010-7777-7777";
		
		Member member = new Member();
		member.setMemberName(memberName);
		member.setEmail(email);
		member.setMemberPhone(memberPhone);
		member.setPassword(password);
		assertThat(member).isNotNull();
		assertThat(member.getEmail()).isEqualTo(email);
		assertThat(member.getPassword()).isEqualTo(password);
	}
	
	// ============================== MemberJoinDto ===================================	
	// MemberJoinDto 도메인 builder 테스트
	@Test
	void memberJoinDto_builder_테스트() {
		MemberJoinDto join = MemberJoinDto.builder().email("test01@naver.com")
													.memberName("홍길동")
													.password("qweasd!")
													.memberPhone("010-7777-7777")
													.build();
		assertThat(join).isNotNull();
	}
	
	// =================================MemberJoinDto Validator 유효성 테스트=======================================
	private MemberJoinDto join = new MemberJoinDto();
	private BindingResult bindingResult = new BindException(join, "join");
	
    @BeforeEach
    public void before() {
    	join.setMemberName("홍길동");
    	join.setEmail("test01@naver.com");
    	join.setMemberPhone("01077777777");
    	join.setPassword("qweasd1!");
    }
	
    // 이메일 형식 실패 테스트
	@ParameterizedTest
	@ValueSource(strings = {"test01naver.com", "test01", "test01@navercom", "  ", "", "test01@naver.com"})
	public void email_테스트 (String email) {
		//String email = "test01naver.com";
				
		join.setEmail(email);
		validator.validate(join, bindingResult);
		assertEquals(bindingResult.getFieldError().getDefaultMessage(), "email 형식이 아닙니다");
	}
	
	// 이름 유효성 test
	@ParameterizedTest
	@ValueSource(strings = {"홍", "홍길동동동동", "", "홍길동"})
	public void name_테스트 (String name) {
		join.setMemberName(name);
		validator.validate(join, bindingResult);
		assertEquals(bindingResult.getFieldError().getDefaultMessage(), "크기가 2에서 5 사이여야 합니다");
	}
	// 영어 또는 한글 만 입력 가능
	@ParameterizedTest
	@ValueSource(strings = {"   ", "!@3"})
	public void 이름_공백_패턴_테스트(String name) {
		join.setMemberName(name);
		validator.validate(join, bindingResult);
		assertEquals(bindingResult.getFieldError().getDefaultMessage(), "영어 또는 한글로 입력해주세요");
	}
	
	// password 유효성 test
	// 최소8자리에 숫자,문자,특수문자 각각 1개 포함
	@ParameterizedTest
	@ValueSource(strings = {"홍", "dljqlkjf", "홍길동", "1qpdq", ".qpdiuyrw", "", "   ", "qweasd1!"})
	public void password_테스트 (String password) {
		join.setPassword(password);
		validator.validate(join, bindingResult);
		assertEquals(bindingResult.getFieldError().getDefaultMessage(), "비밀번호를 다시 입력해 주세요");
	}
	
	// phone 유효성 test
	@ParameterizedTest
	@ValueSource(strings = {"010-9999-7777", "rkqpdjvqad", "01077777777", "     "})
	public void phone_테스트(String phone) {
		join.setMemberPhone(phone);
		validator.validate(join, bindingResult);
		
		assertEquals(bindingResult.getFieldError().getDefaultMessage(), "숫자만 입력해 주세요");
	}
	// phone null test
}
